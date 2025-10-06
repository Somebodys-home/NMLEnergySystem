package io.github.NoOne.nMLEnergySystem;

import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class EnergyManager {
    private static NMLEnergySystem nmlEnergySystem;
    private static ProfileManager profileManager;

    public EnergyManager(NMLEnergySystem nmlEnergySystem) {
        EnergyManager.nmlEnergySystem = nmlEnergySystem;
        profileManager = EnergyManager.nmlEnergySystem.getProfileManager();
    }

    public void energyRegenServerTask() {
        new BukkitRunnable() {
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();

                    if (player.isSprinting() || player.hasMetadata("pause energy regen")) continue;

                    double currentEnergy = profileManager.getPlayerProfile(uuid).getStats().getCurrentEnergy();
                    double maxEnergy = profileManager.getPlayerProfile(uuid).getStats().getMaxEnergy();

                    if (currentEnergy < maxEnergy) {
                        addEnergy(player, (maxEnergy / 15)); // how long in seconds it takes to regen to full energy
                    }
                }
            }
        }.runTaskTimer(nmlEnergySystem, 0, 20);
    }

    public static void addEnergy(Player player, double amount) {
        profileManager.getPlayerProfile(player.getUniqueId()).getStats().add2Stat("currentenergy", amount);
        updateEnergyBar(player);
    }

    public static void useEnergy(Player player, double amount) {
        profileManager.getPlayerProfile(player.getUniqueId()).getStats().removeFromStat("currentenergy", amount);
        updateEnergyBar(player);
    }

    public static void pauseEnergyRegen(Player player) {
        player.setMetadata("pause energy regen", new FixedMetadataValue(nmlEnergySystem, true));
    }

    public static void updateEnergyBar(Player player) {
        double currentEnergy = profileManager.getPlayerProfile(player.getUniqueId()).getStats().getCurrentEnergy();
        double maxEnergy = profileManager.getPlayerProfile(player.getUniqueId()).getStats().getMaxEnergy();
        double currentEnergyPercent = currentEnergy / maxEnergy;

        if (currentEnergyPercent > 1) {
            currentEnergyPercent = 1;
        }

        int hungerLevel = (int) (currentEnergyPercent * 20);  // 20 is the max hunger level

        player.setFoodLevel(hungerLevel);
    }
}