package com.patstar.MonsterSettings.Listeners;

import com.patstar.MonsterSettings.MonsterSettings;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.jetbrains.annotations.NotNull;

public class EnemyListener implements Listener {

    private final MonsterSettings plugin;

    public EnemyListener(MonsterSettings plugin) {
        this.plugin = plugin;
    }

    private boolean preventDamage(@NotNull ConfigurationSection config, @NotNull Entity target) {
        if (!config.getBoolean("damage-pets", true) && target instanceof Tameable) {
            return ((Tameable) target).isTamed();
        }
        if (!config.getBoolean("damage-animals", true) && target instanceof Animals) {
            return true;
        }
        if (!config.getBoolean("damage-villagers", true) && target instanceof Villager) {
            return true;
        }
        if (!config.getBoolean("damage-traders", true) && target instanceof WanderingTrader) {
            return true;
        }
        if (!config.getBoolean("damage-monsters", true) && target instanceof Enemy) {
            return true;
        }
        if (!config.getBoolean("damage-players", true) && target instanceof Player) {
            return true;
        }
        return false;
    }


    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if(event.getEntityType() == EntityType.ENDERMAN && !plugin.getConfigForSubsection("enderman", "block-interaction", true)) {
            event.setCancelled(true);
            return;
        }

        if(event.getEntityType() == EntityType.RAVAGER && !plugin.getConfigForSubsection("ravager", "destroy-blocks", true)) {
            event.setCancelled(true);
            return;
        }

        if(!plugin.getConfig().getBoolean("trample-crops") && event.getEntity() instanceof Enemy && event.getBlockData() instanceof Ageable) {
            event.setCancelled(true);
            return;
        }

        if(!plugin.getConfigForEnemy(event.getEntity(), "trample-crops", true) && event.getBlockData() instanceof Ageable) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        if(event.getEntityType() == EntityType.CREEPER && !plugin.getConfigForSubsection("creeper", "destroy-blocks", true)) {
            event.blockList().clear();
            return;
        }

        if(event.getEntityType() == EntityType.FIREBALL && !plugin.getConfigForSubsection("ghast", "projectile-destroy-blocks", true)) {
            Projectile fireball = (Projectile) event.getEntity();
            if (fireball.getShooter() instanceof Ghast) {
                event.blockList().clear();
                return;
            }
        }

        if(event.getEntityType() == EntityType.WITHER_SKULL && !plugin.getConfigForSubsection("wither", "projectile-destroy-blocks", true)) {
            Projectile wither_skull = (Projectile) event.getEntity();
            if (wither_skull.getShooter() instanceof Wither) {
                event.blockList().clear();
                return;
            }
        }

        if(event.getEntityType() == EntityType.WITHER && !plugin.getConfigForSubsection("wither", "spawn-destroy-blocks", true)) {
            event.blockList().clear();
            return;
        }

        if(event.getEntityType() == EntityType.ENDER_DRAGON && !plugin.getConfigForSubsection("ender_dragon", "destroy-blocks", true)) {
            event.blockList().clear();
            return;
        }

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (preventDamage(plugin.getConfig(), event.getEntity())) {
            event.setCancelled(true);
            return;
        }

        ConfigurationSection subconfig = plugin.getConfigSectionForEnemy(event.getDamager());
        if (subconfig != null && preventDamage(subconfig, event.getEntity())) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent event) {
        if (event.getPotion().getShooter() instanceof Enemy attacker) {
            ConfigurationSection subconfig = plugin.getConfigSectionForEnemy(attacker);
            if (subconfig != null) {
                event.getAffectedEntities().removeIf(target -> preventDamage(subconfig, target));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event) {
        if (plugin.getConfigForEnemy(event.getEntity(), "disable-aggression", false)) {
            event.setCancelled(true);
            return;
        }
        ConfigurationSection subconfig = plugin.getConfigSectionForEnemy(event.getEntity());
        if (event.getTarget() != null && subconfig != null && preventDamage(subconfig, event.getTarget())) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntitySpawn(CreatureSpawnEvent event) {
        if (plugin.getConfigForEnemy(event.getEntity(), "disable-spawn", false)) {
            event.setCancelled(true);
            return;
        }
    }
}
