package com.demonwav.ectotokens.config.tokens;

import com.demonwav.ectotokens.config.Config;

import lombok.Data;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Logger;

@Data
public class MobsConfig implements Config {

    // Passive Mobs
    private Mob bat;
    private Mob chicken;
    private Mob cow;
    private Mob mooshroom;
    private Mob pig;
    private Mob rabbit;
    private Mob sheep;
    private Mob squid;
    private Mob villager;
    private Mob babyVillager;

    // Neutral Mobs
    private Mob caveSpider;
    private Mob enderman;
    private Mob spider;
    private Mob zombiePigman;
    private Mob babyZombiePigman;

    // Hostile Mobs
    private Mob blaze;
    private Mob chickenJockey;
    private Mob creeper;
    private Mob poweredCreeper;
    private Mob guardian;
    private Mob elderGuardian;
    private Mob endermite;
    private Mob ghast;
    private Mob magmaCube;
    private Mob silverfish;
    private Mob skeleton;
    private Mob slime;
    private Mob spiderJockey;
    private Mob witch;
    private Mob witherSkeleton;
    private Mob zombie;
    private Mob babyZombie;
    private Mob zombieVillager;

    // Tameable Mobs
    private Mob donkey;
    private Mob horse;
    private Mob mule;
    private Mob ocelot;
    private Mob skeletonHorse;
    private Mob wolf;

    // Utility Mobs
    private Mob ironGolem;
    private Mob snowGolem;

    // Boss Mobs
    private Mob enderDragon;
    private Mob wither;

    // Unused Mobs
    private Mob giant;
    private Mob killerBunny;
    private Mob zombieHorse;

    @Override
    public boolean validate(Logger logger) {
        boolean result = true;
        Class<MobsConfig> clazz = MobsConfig.class;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // ignore transient fields, if needed in the future
            if (!Modifier.isTransient(field.getModifiers()) && field.getType() == Mob.class) {
                try {
                    Mob mob = (Mob) field.get(this);
                    result &= mob.validate(logger);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    @Data
    public static class Mob implements Config {
        private long overworld;
        private long nether;
        private long end;

        @Override
        public boolean validate(Logger logger) {
            boolean result = true;

            if (overworld < 0) {
                logger.severe("Overworld token count must not be negative!");
                result = false;
            }
            if (nether < 0) {
                logger.severe("Nether token count must not be negative!");
                result = false;
            }
            if (end < 0) {
                logger.severe("End token count must not be negative!");
                result = false;
            }

            return result;
        }
    }
}
