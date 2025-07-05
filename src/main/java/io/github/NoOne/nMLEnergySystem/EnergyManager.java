package io.github.NoOne.nMLEnergySystem;

import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class EnergyManager {
    private NMLEnergySystem nmlEnergySystem;
    private static ProfileManager profileManager;

    public EnergyManager(NMLEnergySystem nmlEnergySystem) {
        this.nmlEnergySystem = nmlEnergySystem;
        profileManager = this.nmlEnergySystem.getProfileManager();
    }

    public static void addEnergy(Player player, double amount) {
        double currentEnergy = profileManager.getPlayerProfile(player.getUniqueId()).getStats().getCurrentEnergy();
        double maxEnergy = profileManager.getPlayerProfile(player.getUniqueId()).getStats().getMaxEnergy();

        if ((amount + currentEnergy) > maxEnergy) {
            amount = maxEnergy - currentEnergy;
        }

        double edited = currentEnergy + amount;

        profileManager.getPlayerProfile(player.getUniqueId()).getStats().setCurrentEnergy(edited);
        updateEnergyBar(player);
    }

    public static void useEnergy(Player player, double amount) {
        double currentEnergy = profileManager.getPlayerProfile(player.getUniqueId()).getStats().getCurrentEnergy();
        double maxEnergy = profileManager.getPlayerProfile(player.getUniqueId()).getStats().getMaxEnergy();

        if (amount > maxEnergy) {
            amount = maxEnergy;
        }

        double edited = currentEnergy - amount;

        profileManager.getPlayerProfile(player.getUniqueId()).getStats().setCurrentEnergy(edited);
        updateEnergyBar(player);
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

    public void energyRegenServerTask() {
        new BukkitRunnable() {
            public void run(){
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.isSprinting()) continue;
                    double currentEnergy = profileManager.getPlayerProfile(player.getUniqueId()).getStats().getCurrentEnergy();
                    double maxEnergy = profileManager.getPlayerProfile(player.getUniqueId()).getStats().getMaxEnergy();

                    if (currentEnergy < maxEnergy) {
                        addEnergy(player, (maxEnergy / 15)); // how long in seconds it takes to regen to full energy
                    }
                }
            }
        }.runTaskTimer(nmlEnergySystem, 0, 20);
    }
}