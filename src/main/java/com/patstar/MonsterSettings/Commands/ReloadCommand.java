package com.patstar.MonsterSettings.Commands;

import com.patstar.MonsterSettings.MonsterSettings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ReloadCommand implements TabExecutor {

    private final MonsterSettings plugin;

    public ReloadCommand(MonsterSettings plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {

        if (!sender.hasPermission("monstersettings.reload")) { return true; }

        // Check arguments
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {

            // Reload the configuration
            plugin.reloadConfig();

            // Send confirmation in console
            plugin.getLogger().info("MonsterSettings configuration reloaded successfully.");

            // Notify the player
            if (sender instanceof Player) {
                sender.sendMessage("MonsterSettings configuration reloaded successfully.");
            }

            return true;
        }

        // Show correct usage after incorrect arguments
        if (sender instanceof Player) {
            sender.sendMessage("Usage: /monstersettings reload");
        } else {
            plugin.getLogger().info("Usage: /monstersettings reload");
        }

        return true;
    }

    @Override
    public List < String > onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {

        List < String > suggestions = new ArrayList < > ();

        if (sender instanceof Player player) {

            if (player.hasPermission("monstersettings.reload")) {
                if (args.length == 1) {
                    if ("reload".startsWith(args[0].toLowerCase())) {
                        suggestions.add("reload");
                    }
                }
            }
        }

        return suggestions;
    }
}
