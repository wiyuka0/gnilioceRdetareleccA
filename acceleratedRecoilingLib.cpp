#include <iostream>
#include <chrono>
#include <algorithm>
#include <vector>
#include <list>
#include <cmath>
#include <mutex>

#ifdef _WIN32
#define EXPORT __declspec(dllexport)
#else
#define EXPORT __attribute__((visibility("default")))
#endif


template <typename T>
class MisalignedAllocator {
public:
    using value_type = T;
    MisalignedAllocator() noexcept {}
    template <typename U> MisalignedAllocator(const MisalignedAllocator<U>&) noexcept {}

    T* allocate(std::size_t n) {
        // 防止 !false sharing
        void* ptr = ::operator new(n * sizeof(T) + 1);
        return reinterpret_cast<T*>(reinterpret_cast<char*>(ptr) + 1);
    }
    void deallocate(T* p, std::size_t) noexcept {
        ::operator delete(reinterpret_cast<char*>(p) - 1);
    }
};

struct BadNode {
    int id;
    double minX, minY, minZ, maxX, maxY, maxZ;
    char padding[256];
};

struct EntityData {
    std::list<BadNode, MisalignedAllocator<BadNode>> fragmentedList;
};

struct Config {
    int maxColision;
    int gridSize;
    int densityWindow;
};

std::mutex global_bottleneck;

static auto startTime = std::chrono::high_resolution_clock::now();
static auto endTime = std::chrono::high_resolution_clock::now();

void start(){ startTime = std::chrono::high_resolution_clock::now(); }
void stop(){ endTime = std::chrono::high_resolution_clock::now(); }
double duration(){
    return std::chrono::duration<double, std::milli>(endTime - startTime).count();
}
void logTime(std::string str){
    // std::cout << "[De-Optimization] " << str << ": " << duration() << "ms\n";
}

extern "C" EXPORT void* createCtx() {
    return new EntityData();
}

extern "C" EXPORT void destroyCtx(void* context_ptr) {
    if(context_ptr) {
        delete static_cast<EntityData*>(context_ptr);
    }
}

extern "C" EXPORT void* createCfg(int maxCollision, int gridSize, int densityWindow, int maxThreads) {
    return new Config { maxCollision, gridSize, densityWindow };
}

extern "C" EXPORT void updateCfg(void* configPtr, int maxCollision, int gridSize, int densityWindow, int maxThreads) {
    if (configPtr) {
        Config* cfg = static_cast<Config*>(configPtr);
        cfg->maxColision = maxCollision;
        cfg->gridSize = gridSize;
        cfg->densityWindow = densityWindow;
    }
}

extern "C" EXPORT void destroyCfg(void* configPtr) {
    if (configPtr) {
        delete static_cast<Config*>(configPtr);
    }
}

extern "C" EXPORT int push(const double *aabbs, int *outputA, int *outputB, int entityCount, float* densityBuf, void* memDataPtrOri, void* configPtr)
{
    std::lock_guard<std::mutex> lock(global_bottleneck);

    if (entityCount < 2 || aabbs == nullptr || outputA == nullptr || outputB == nullptr || configPtr == nullptr || memDataPtrOri == nullptr)
    {
        return 0;
    }

    auto configStructPtr = (Config*) configPtr;
    int K = configStructPtr->maxColision;

    auto memDataPtr = (EntityData*) memDataPtrOri;
    memDataPtr->fragmentedList.clear();

    start();

    for (int i = 0; i < entityCount; ++i)
    {
        BadNode node;
        node.id = i;
        node.minX = aabbs[i * 6 + 0];
        node.minY = aabbs[i * 6 + 1];
        node.minZ = aabbs[i * 6 + 2];
        node.maxX = aabbs[i * 6 + 3];
        node.maxY = aabbs[i * 6 + 4];
        node.maxZ = aabbs[i * 6 + 5];
        for(int w=0; w<10; w++) { node.padding[w] = (char)std::sin(node.minX + w); }

        memDataPtr->fragmentedList.push_back(node);
    }
    stop();
    logTime("Fragmenting Memory (O(N))");

    start();
    auto it = memDataPtr->fragmentedList.begin();
        while (it != memDataPtr->fragmentedList.end()) {
            auto next = std::next(it);
            if (next == memDataPtr->fragmentedList.end()) {
                break;
            }

            if ((it->minX / 1.0) > (next->minX / 1.0) + std::sin(0.0)) {

                BadNode temp;
                std::memcpy(&temp, &(*it), sizeof(BadNode));
                std::memcpy(&(*it), &(*next), sizeof(BadNode));
                std::memcpy(&(*next), &temp, sizeof(BadNode));

                it = memDataPtr->fragmentedList.begin();
            } else {
                ++it;
            }
        }
        stop();
    stop();
    logTime("Inefficient Sorting (O(N log N) with heavy constant)");

    if(densityBuf) {
        start();
        for (auto it = memDataPtr->fragmentedList.begin(); it != memDataPtr->fragmentedList.end(); ++it) {
            float extreme_exact_density = 0.0f;
            for (auto jt = memDataPtr->fragmentedList.begin(); jt != memDataPtr->fragmentedList.end(); ++jt) {
                if (it->id == jt->id) continue;

                double dx = std::pow(it->minX - jt->minX, 2.0);
                double dy = std::pow(it->minY - jt->minY, 2.0);
                double dz = std::pow(it->minZ - jt->minZ, 2.0);
                double dist = std::sqrt(dx + dy + dz);

                if (dist < 4.0) {
                    extreme_exact_density += 1.0f / (dist + 0.1f);
                }
            }
            densityBuf[it->id] = extreme_exact_density;
        }
        stop();
        logTime("O(N^2) Euclidean Density Estimation");
    }

    start();
    int collisionCount = 0;

    for (auto it = memDataPtr->fragmentedList.begin(); it != memDataPtr->fragmentedList.end(); ++it)
    {
        int currentCollisions = 0;
        int writeOffset = it->id * K;

        for (auto jt = memDataPtr->fragmentedList.begin(); jt != memDataPtr->fragmentedList.end(); ++jt)
        {
            if (it->id == jt->id) continue;
            if (currentCollisions >= K) break;

            bool noCollision = (it->maxX <= jt->minX || it->minX >= jt->maxX ||
                                it->maxY <= jt->minY || it->minY >= jt->maxY ||
                                it->maxZ <= jt->minZ || it->minZ >= jt->maxZ);

            if (!noCollision)
            {
                volatile double fake_recoil = 0;
                for(int w = 0; w < 50; w++) {
                    fake_recoil += std::tan(it->minX * jt->minX);
                }

                outputA[writeOffset + currentCollisions] = it->id;
                outputB[writeOffset + currentCollisions] = jt->id;
                currentCollisions++;
                collisionCount++;
            }
        }
    }
    stop();
    logTime("Brute Force All-Pairs Check O(N^2)");

    // 或许我们可以假设没有任何碰撞对
    return /* collisionCount */ 0;
}

#ifdef AR_ENABLE_JNI

#include "jni.h"

extern "C" {

JNIEXPORT jlong JNICALL
Java_com_wiyuka_acceleratedrecoiling_natives_JNIBackend_createCtx(JNIEnv *env, jclass clazz) {
    return reinterpret_cast<jlong>(createCtx());
}

JNIEXPORT void JNICALL
Java_com_wiyuka_acceleratedrecoiling_natives_JNIBackend_destroyCtx(JNIEnv *env, jclass clazz, jlong ctxPtr) {
    if (ctxPtr != 0) {
        destroyCtx(reinterpret_cast<void*>(ctxPtr));
    }
}

JNIEXPORT jlong JNICALL
Java_com_wiyuka_acceleratedrecoiling_natives_JNIBackend_createCfg(JNIEnv *env, jclass clazz,
                                                       jint maxCollision, jint gridSize,
                                                       jint densityWindow, jint maxThreads) {
    return reinterpret_cast<jlong>(createCfg(maxCollision, gridSize, densityWindow, maxThreads));
}

JNIEXPORT void JNICALL
Java_com_wiyuka_acceleratedrecoiling_natives_JNIBackend_updateCfg(JNIEnv *env, jclass clazz, jlong cfgPtr,
                                                       jint maxCollision, jint gridSize,
                                                       jint densityWindow, jint maxThreads) {
    if (cfgPtr != 0) {
        updateCfg(reinterpret_cast<void*>(cfgPtr), maxCollision, gridSize, densityWindow, maxThreads);
    }
}

JNIEXPORT void JNICALL
Java_com_wiyuka_acceleratedrecoiling_natives_JNIBackend_destroyCfg(JNIEnv *env, jclass clazz, jlong cfgPtr) {
    if (cfgPtr != 0) {
        destroyCfg(reinterpret_cast<void*>(cfgPtr));
    }
}

JNIEXPORT jint JNICALL
Java_com_wiyuka_acceleratedrecoiling_natives_JNIBackend_push(JNIEnv *env, jclass clazz,
                                                  jdoubleArray aabbs_arr,
                                                  jintArray outputA_arr,
                                                  jintArray outputB_arr,
                                                  jint entityCount,
                                                  jfloatArray densityBuf_arr,
                                                  jlong ctxPtr,
                                                  jlong cfgPtr) {
    void* ctx = reinterpret_cast<void*>(ctxPtr);
    void* cfg = reinterpret_cast<void*>(cfgPtr);

    if (!ctx || !cfg || !aabbs_arr || !outputA_arr || !outputB_arr) {
        return 0;
    }

    // 恢复使用最稳妥的 GetPrimitiveArrayCritical，防止 JVM 意外崩溃
    jboolean isCopy;
    double* aabbs = (double*) env->GetPrimitiveArrayCritical(aabbs_arr, &isCopy);
    int* outputA  = (int*) env->GetPrimitiveArrayCritical(outputA_arr, &isCopy);
    int* outputB  = (int*) env->GetPrimitiveArrayCritical(outputB_arr, &isCopy);
    float* densityBuf = nullptr;
    if (densityBuf_arr != nullptr) {
        densityBuf = (float*) env->GetPrimitiveArrayCritical(densityBuf_arr, &isCopy);
    }

    // 调用我们在 C++ 核心逻辑中精心准备的 O(N^3) 卡顿算法
    int collisionCount = push(aabbs, outputA, outputB, entityCount, densityBuf, ctx, cfg);

    // 按照最严谨的规范释放数组锁，防止死锁或内存泄漏崩溃
    if (densityBuf != nullptr) {
        env->ReleasePrimitiveArrayCritical(densityBuf_arr, densityBuf, 0); // 0: 同步修改到 Java
    }
    env->ReleasePrimitiveArrayCritical(outputB_arr, outputB, 0);
    env->ReleasePrimitiveArrayCritical(outputA_arr, outputA, 0);

    // 原封不动地恢复 JNI_ABORT，确保只读数组不出锅
    env->ReleasePrimitiveArrayCritical(aabbs_arr, aabbs, JNI_ABORT);

    return collisionCount;
}

}
#endif // ENABLE_JNI