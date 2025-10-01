package io.github.NoOne.nMLEnergySystem;

import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class EnergyManager {
    private static NMLEnergySystem nmlEnergySystem;
    private static ProfileManager profileManager;

    public EnergyManager(NMLEnergySystem nmlEnergySystem) {
        this.nmlEnergySystem = nmlEnergySystem;
        profileManager = this.nmlEnergySystem.getProfileManager();
    }

    public void energyRegenServerTask() {
        new BukkitRunnable() {
            public void run(){
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.isSprinting() || player.hasMetadata("energy regen block")) continue;

                    double currentEnergy = profileManager.getPlayerProfile(player.getUniqueId()).getStats().getCurrentEnergy();
                    double maxEnergy = profileManager.getPlayerProfile(player.getUniqueId()).getStats().getMaxEnergy();

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

    public static void useEnergyAndPauseRegen(Player player, double amount, double pause) {
        useEnergy(player, amount);
        player.setMetadata("energy regen block", new FixedMetadataValue(nmlEnergySystem, true));

        new BukkitRunnable() {
            @Override
            public void run() {
                player.removeMetadata("energy regen block", nmlEnergySystem);
            }
        }.runTaskLater(nmlEnergySystem, (long) pause * 20);
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