package org.happydev.test;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class TestDumper extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("TestDumper enabled (Java 17 / Spigot 1.20.1)");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!command.getName().equalsIgnoreCase("testdump")) {
            return false;
        }

        // Permisos (la consola siempre pasa)
        if (!(sender instanceof ConsoleCommandSender) &&
                !sender.hasPermission("testdumper.use")) {
            sender.sendMessage("§cNo tienes permiso para usar este comando.");
            return true;
        }

        sender.sendMessage("§aIniciando test de MySQL y dump a JSON...");

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                DumpService.run(this);
                sender.sendMessage("§aDump completado. Revisa plugins/TestDumper/dump.json");
            } catch (Exception e) {
                sender.sendMessage("§cError al realizar el dump. Revisa la consola.");
                getLogger().severe("Dump failed:");
                e.printStackTrace();
            }
        });

        return true;
    }
}
