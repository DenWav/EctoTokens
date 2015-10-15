package com.demonwav.ectotokens.events;

import com.demonwav.ectotokens.EctoTokens;
import com.demonwav.ectotokens.TokensManager;
import com.demonwav.ectotokens.config.tokens.MobsConfig;
import com.demonwav.ectotokens.gui.ActionBarManager;
import com.demonwav.ectotokens.util.StringUtil;

import lombok.AllArgsConstructor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Horse;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.lang.reflect.Field;

@AllArgsConstructor
public class KillEvent implements Listener {

    private EctoTokens plugin;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKillEvent(EntityDeathEvent event) {
        if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();
            if (damageEvent.getDamager() instanceof OfflinePlayer) {
                Entity entity = damageEvent.getEntity();

                String name = null;
                long amount = 0;
                OfflinePlayer player = (OfflinePlayer) damageEvent.getDamager();
                World.Environment environment = event.getEntity().getWorld().getEnvironment();

                switch (event.getEntityType()) {
                    // Normal cases
                    case BAT:
                    case COW:
                    case PIG:
                    case SHEEP:
                    case SQUID:
                    case ENDERMAN:
                    case BLAZE:
                    case ENDERMITE:
                    case GHAST:
                    case SILVERFISH:
                    case SLIME:
                    case WITCH:
                    case OCELOT:
                    case WOLF:
                    case WITHER:
                    case GIANT:
                        name = event.getEntityType().name().toLowerCase();
                        amount = getAmount(name, environment);
                        break;
                    case VILLAGER:
                        Villager villager = (Villager) entity;
                        if (villager.isAdult()) {
                            name = event.getEntityType().name().toLowerCase();
                            amount = getAmount(name, environment);
                        } else {
                            name = "BABY_VILLAGER";
                            amount = getAmount("babyVillager", environment);
                        }
                        break;
                    case CHICKEN:
                        if (entity.getPassenger() != null) {
                            name = "CHICKEN_JOCKEY";
                            amount = getAmount("chickenJockey", environment);
                        } else {
                            name = entity.getType().name();
                            amount = getAmount("chicken", environment);
                        }
                        break;
                    case MUSHROOM_COW:
                        name = "MOOSHROOM";
                        amount = getAmount("mooshroom", environment);
                        break;
                    case ZOMBIE:
                        Zombie zombie = (Zombie) entity;
                        if (zombie.isVillager()) {
                            name = "ZOMBIE_VILLAGER";
                            amount = getAmount("zombieVillager", environment);
                        } else if (zombie.getVehicle() != null && zombie.getVehicle().getType() == EntityType.CHICKEN) {
                            name = "CHICKEN_JOCKEY";
                            amount = getAmount("chickenJockey", environment);
                        } else if (zombie.isBaby()) {
                            name = "BABY_ZOMBIE";
                            amount = getAmount("babyZombie", environment);
                        } else {
                            name = entity.getType().name();
                            amount = getAmount("zombie", environment);
                        }
                        break;
                    case CAVE_SPIDER:
                        name = entity.getType().name();
                        amount = getAmount("caveSpider", environment);
                        break;
                    case SPIDER:
                        if (entity.getPassenger() != null) {
                            name = "SPIDER_JOCKEY";
                            amount = getAmount("spiderJockey", environment);
                        } else {
                            name = entity.getType().name();
                            amount = getAmount("spider", environment);
                        }
                        break;
                    case PIG_ZOMBIE:
                        PigZombie pigZombie = (PigZombie) entity;
                        if (pigZombie.isBaby()) {
                            name = "BABY_ZOMBIE_PIGMAN";
                            amount = getAmount("babyZombiePigman", environment);
                        } else {
                            name = "ZOMBIE_PIGMAN";
                            amount = getAmount("zombiePigman", environment);
                        }
                        break;
                    case CREEPER:
                        Creeper creeper = (Creeper) entity;
                        if (creeper.isPowered()) {
                            name = "POWERED_CREEPER";
                            amount = getAmount("poweredCreeper", environment);
                        } else {
                            name = entity.getType().name();
                            amount = getAmount("creeper", environment);
                        }
                        break;
                    case GUARDIAN:
                        Guardian guardian = (Guardian) entity;
                        if (guardian.isElder()) {
                            name = "ELDER_GUARDIAN";
                            amount = getAmount("elderGuardian", environment);
                        } else {
                            name = entity.getType().name();
                            amount = getAmount("guardian", environment);
                        }
                        break;
                    case MAGMA_CUBE:
                        name = entity.getType().name();
                        amount = getAmount("magmaCube", environment);
                        break;
                    case SKELETON:
                        Skeleton skeleton = (Skeleton) entity;
                        if (skeleton.getSkeletonType() == Skeleton.SkeletonType.WITHER) {
                            name = "WITHER_SKELETON";
                            amount = getAmount("witherSkeleton", environment);
                        } else if (skeleton.getVehicle() != null && skeleton.getVehicle().getType() == EntityType.SPIDER) {
                            name = "SPIDER_JOCKEY";
                            amount = getAmount("spiderJockey", environment);
                        } else {
                            name = entity.getType().name();
                            amount = getAmount("skeleton", environment);
                        }
                        break;
                    case HORSE:
                        Horse horse = (Horse) entity;
                        switch (horse.getVariant()) {
                            case HORSE:
                                name = "HORSE";
                                amount = getAmount("horse", environment);
                                break;
                            case DONKEY:
                                name = "DONKEY";
                                amount = getAmount("donkey", environment);
                                break;
                            case MULE:
                                name = "MULE";
                                amount = getAmount("mule", environment);
                                break;
                            case UNDEAD_HORSE:
                                name = "ZOMBIE_HORSE";
                                amount = getAmount("zombieHorse", environment);
                                break;
                            case SKELETON_HORSE:
                                name = "SKELETON_HORSE";
                                amount = getAmount("skeletonHorse", environment);
                                break;
                        }
                        break;
                    case IRON_GOLEM:
                        name = entity.getType().name();
                        amount = getAmount("ironGolem", environment);
                        break;
                    case SNOWMAN:
                        name = "SNOW_GOLEM";
                        amount = getAmount("snowGolem", environment);
                        break;
                    case ENDER_DRAGON:
                        name = entity.getType().name();
                        amount = getAmount("enderDragon", environment);
                        break;
                    case RABBIT:
                        Rabbit rabbit = (Rabbit) entity;
                        if (rabbit.getRabbitType() == Rabbit.Type.THE_KILLER_BUNNY) {
                            name = "KILLER_BUNNY";
                            amount = getAmount("killerBunny", environment);
                        } else {
                            name = entity.getType().name();
                            amount = getAmount("rabbit", environment);
                        }
                        break;
                }

                if (name != null) {
                    TokensManager.getInstance().modifyBalance(player, amount, getDescription(name, event.getEntity().getWorld().getEnvironment()));
                    String actionBar = "Killed " + WordUtils.capitalizeFully(name.replaceAll("_", " "));
                    ActionBarManager.getInstance().setActionBarMomentarily(
                        player.getPlayer(),
                        StringUtil.tokensReceivedTextVar(
                            actionBar,
                            amount,
                            plugin.getMainConfig().getGui().getActionBar().getTokensReceived()
                        ),
                        20
                    );
                }
            }
        }
    }

    private long getAmount(String name, World.Environment environment) {
        MobsConfig.Mob mob = null;

        Class<MobsConfig> configClass = MobsConfig.class;
        Field field;
        try {
            field = configClass.getDeclaredField(name);
            if (field.getType() == MobsConfig.Mob.class) {
                field.setAccessible(true);
                mob = (MobsConfig.Mob) field.get(plugin.getTokensConfig().getMobs());
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        if (mob == null)
            return 0;

        switch (environment) {
            case NORMAL:
                return mob.getOverworld();
            case NETHER:
                return mob.getNether();
            case THE_END:
                return mob.getEnd();
            default:
                return 0;
        }
    }

    private String getDescription(String name, World.Environment environment) {
        return "KILL - Entity:" + name + " World Type:" + environment;
    }
}
