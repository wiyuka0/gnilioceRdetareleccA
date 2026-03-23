package com.wiyuka.acceleratedrecoiling.mixin;


import net.minecraft.world.level.EntityGetter;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(value = EntityGetter.class)
public interface EntityGetterMixin {

//    @WrapOperation(
//            method = "getEntityCollisions",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/world/level/EntityGetter;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;")
//    )
//    default List<Entity> getEntityCollisions(EntityGetter instance, Entity entity, AABB aabb, Predicate<? super Entity> predicate, Operation<List<Entity>> original) {
//        if(FoldConfig.enableEntityGetterOptimization && !(entity instanceof Player) && entity != null)
//            return CollisionMapData.replace1(entity, entity.level()).stream().filter(predicate).collect(Collectors.toList());
//        else
//            return original.call(instance, entity, aabb, predicate);
//    }
}
