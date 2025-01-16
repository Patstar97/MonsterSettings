package com.patstar.MonsterSettings;

import com.patstar.MonsterSettings.Commands.ReloadCommand;
import com.patstar.MonsterSettings.Listeners.EnemyListener;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MonsterSettings extends JavaPlugin {

    public static final int PLUGIN_ID = 24468;

    @Override
    public void onLoad() {
        // Plugin load success message
        getLogger().info("MonsterSettings has been loaded successfully");
    }

    @Override
    public void onEnable() {
        // Register bStats
        Metrics metrics = new Metrics(this, PLUGIN_ID);

        // generate chart for all global options
        for (String option: List.of("trample-crops", "damage-pets", "damage-animals", "damage-villagers", "damage-traders", "damage-monsters", "damage-players")) {
            metrics.addCustomChart(new SimplePie(option + "-all", () -> Boolean.toString(getConfig().getBoolean(option, true))));
        }

        // generate chart for each common option for monsters
        for (String option: List.of("trample-crops", "damage-pets", "damage-animals", "damage-villagers", "damage-traders", "damage-monsters", "damage-players", "disable-aggression", "disable-spawn")) {
            metrics.addCustomChart(new AdvancedPie(option + "-entities", () -> {
                Map<String, Integer> map = new HashMap<>();
                for (String entity: getConfig().getKeys(false)) {
                    Boolean defaultValue = getDefaultForOption(option);
                    if (defaultValue != null && !defaultValue.equals(getConfigForSubsection(entity, option, defaultValue))) {
                        map.put(entity, 1);
                    }
                }
                return map;
            }));
        }

        // create chart for entities that have changed values
        metrics.addCustomChart(new AdvancedPie("changed_entities", () -> {
            Map<String, Integer> map = new HashMap<>();
            for (String entity: getConfig().getKeys(false)) {
                if (getConfig().getConfigurationSection(entity) != null) {
                    for (String option : getConfig().getConfigurationSection(entity).getKeys(false)) {
                        Boolean defaultValue = getDefaultForOption(option);
                        if (defaultValue != null && !defaultValue.equals(getConfigForSubsection(entity, option, defaultValue))) {
                            map.put(entity, 1);
                            break;
                        }
                    }
                }
            }
            return map;
        }));

        // create chart for entities that have changed values with amount of changes
        metrics.addCustomChart(new DrilldownPie("changed_entities-amount", () -> {
            Map<String, Map<String, Integer>> map = new HashMap<>();
            for (String entity: getConfig().getKeys(false)) {
                Map<String, Integer> entry = new HashMap<>();
                if (getConfig().getConfigurationSection(entity) != null) {
                    for (String option : getConfig().getConfigurationSection(entity).getKeys(false)) {
                        Boolean defaultValue = getDefaultForOption(option);
                        if (defaultValue != null && !defaultValue.equals(getConfigForSubsection(entity, option, defaultValue))) {
                            entry.put(option, 1);
                        }
                    }
                    if (!entry.isEmpty()) {
                        map.put(entity, entry);
                    }
                }
            }
            return map;
        }));

        // Plugin startup success message
        getLogger().info("MonsterSettings has been enabled successfully");
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        // register listener
        getServer().getPluginManager().registerEvents(new EnemyListener(this), this);

        // Register commands
        getCommand("monstersettings").setExecutor(new ReloadCommand(this));
        getCommand("monstersettings").setTabCompleter(new ReloadCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin disable success message
        getLogger().info("MonsterSettings has been disabled successfully");
    }

    @Nullable
    public Boolean getDefaultForOption(String option) {
        return switch (option) {
            case "trample-crops", "damage-pets", "damage-animals", "damage-villagers", "damage-traders",
                 "damage-monsters", "damage-players", "destroy-blocks", "move-blocks", "projectile-destroy-blocks",
                 "spawn-destroy-blocks" -> true;
            case "disable-aggression", "disable-spawn" -> false;
            default -> null;
        };
    }

    @Nullable
    public ConfigurationSection getConfigSectionForEnemy(@NotNull Entity entity) {
        if (entity instanceof Enemy) {
            return getConfig().getConfigurationSection(entity.getType().name().toLowerCase());
        }
        return null;
    }

    public boolean getConfigForEnemy(@NotNull Entity entity, @NotNull String option, boolean defaultValue) {
        ConfigurationSection section = getConfigSectionForEnemy(entity);
        if (section != null) {
            return section.getBoolean(option, defaultValue);
        }
        return defaultValue;
    }

    public boolean getConfigForSubsection(@NotNull String subsection, @NotNull String option, boolean defaultValue) {
        ConfigurationSection section = getConfig().getConfigurationSection(subsection);
        if (section != null) {
            return section.getBoolean(option, defaultValue);
        }
        return defaultValue;
    }
}
