package com.patstar.MonsterSettings;

import com.patstar.MonsterSettings.Commands.ReloadCommand;
import com.patstar.MonsterSettings.Listeners.EnemyListener;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MonsterSettings extends JavaPlugin {


    @Override
    public void onLoad() {
        // Plugin load success message
        getLogger().info("MonsterSettings has been loaded successfully");
    }

    @Override
    public void onEnable() {
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
