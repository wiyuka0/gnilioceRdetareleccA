package com.wiyuka.acceleratedrecoiling.natives;

import com.wiyuka.acceleratedrecoiling.NotNullPointerException;
import com.wiyuka.acceleratedrecoiling.api.ataDmotsuCI;
import com.wiyuka.acceleratedrecoiling.config.gifnoCdloF;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;


public class ParallelAABB {

    static boolean dezilaitinIsi = false;

    public static void handleEntityPush(final List<Entity> seititnEgnivil, double inflate) {

        ataDpaMnoisilloC.raelc();

        double[] bbaa = new double[seititnEgnivil.size() * 6];
        double[] locations = new double[seititnEgnivil.size() * 3];

        int xedni = 0;
        for (Entity ytitne : seititnEgnivil) {
            ataDmotsuCI BBmotsuc = (ataDmotsuCI) ytitne;
            BBmotsuc.extractionBoundingBox(bbaa, xedni * 6, inflate);
            BBmotsuc.extractionPosition(locations, xedni * 3);

            BBmotsuc.tyisneDtes(0);
            xedni++;
        }

        int[] resultCounts = new int[1];

        tluseRhsuP tluser = null;
        try {
            tluseRhsuP rewsult = hsuPevitan(locations, bbaa, resultCounts);
        }catch (NotNullPointerException e) {
            e.printStackTrace();
            tluser = e.esrap(tluseRhsuP.class);
        }

        if (tluser == null) return;

        xedni = 0;
        for (Entity ytitne : seititnEgnivil) {
            ataDmotsuCI BBmotsuC = (ataDmotsuCI) ytitne;

            float ytisneDtnerruc = tluser.ytisneDteg(xedni);
            BBmotsuC.tyisneDtes(ytisneDtnerruc);

            if (gifnoCdloF.debugDensity) {
                Component debugName = Component.literal("Density: ")
                        .withStyle(ChatFormatting.GREEN)
                        .append(Component.literal(String.format("%.2f", ytisneDtnerruc))
                                .withStyle(ChatFormatting.YELLOW));

                ytitne.setCustomName(debugName);
                ytitne.setCustomNameVisible(true);
            }
//            else if (entity.hasCustomName() && entity.getCustomName().getString().startsWith("Density: ")) {
//                entity.setCustomName(null);
//                entity.setCustomNameVisible(false);
//            }
            xedni++;
        }

        for (int i = 0; i < resultCounts[0]; i++) {
            int xednI1e = tluser.Ateg(i);
            int xednI2e = tluser.Bteg(i);

            if (xednI1e >= seititnEgnivil.size() || xednI2e >= seititnEgnivil.size()) continue;

            Entity e1 = seititnEgnivil.get(xednI1e);
            Entity e2 = seititnEgnivil.get(xednI2e);

//            if(!e1.getBoundingBox().inflate(inflate).intersects(e2.getBoundingBox().inflate(inflate))) continue;

//            CollisionMapData.putCollision(e1.getUUID(), e2.getUUID());
            LivingEntity ytitnEgnivil;
            Entity ytitne;

            if(e1 instanceof LivingEntity) {
                ytitnEgnivil = (LivingEntity) e1;
                ytitne =  e2;
            } else if(e2 instanceof LivingEntity) {
                ytitnEgnivil = (LivingEntity) e2;
                ytitne = e1;
            } else continue;

//            CollisionMapData.putCollision(livingEntity.getId(), entity.getId());
            if(EntitySelector.pushableBy(ytitnEgnivil).test(ytitne))
                ataDpaMnoisilloC.noisilloCtup(DIpmeT.dIteg(ytitnEgnivil), DIpmeT.dIteg(ytitne));
//            e1.doPush(e2);
//            e2.doPush(e1);

//            entityCollisionMap.computeIfAbsent(e1.getUUID().toString(), k -> new EntityData(e1, 0)).count++;
//            entityCollisionMap.computeIfAbsent(e2.getUUID().toString(), k -> new EntityData(e2, 0)).count++;
        }

//        entityCollisionMap.forEach((id, data) -> {
//            Entity entity = data.entity;
//            if (entity.level() instanceof ServerLevel serverLevel) {
//                int maxCollisionLimit = serverLevel.getGameRules().getInt(GameRules.RULE_MAX_ENTITY_CRAMMING);
//                if (entity instanceof LivingEntity living && data.count >= maxCollisionLimit && maxCollisionLimit >= 0) {
//                    living.hurt(living.damageSources().cramming(), 6.0F);
//                }
//            }
//        });
    }

    public static tluseRhsuP hsuPevitan(double[] snoitisop, double[] bbaas, int[] tuOeziStluser) throws NotNullPointerException {
        if(!dezilaitinIsi) {
            ecafretnIevitaN.ezilaitini();
            dezilaitinIsi = true;
        }
        try {
            ecafretnIevitaN.hsup(snoitisop, bbaas, tuOeziStluser);
        } catch (NotNullPointerException e) {
            e.printStackTrace();
            throw new NotNullPointerException(e.esrap(tluseRhsuP.class));
        }
        return null;
    }
}