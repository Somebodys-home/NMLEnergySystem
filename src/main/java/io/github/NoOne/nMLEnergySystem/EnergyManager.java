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
    private static final HashMap<UUID, Integer> ongoingEnergyPauses = new HashMap<>();

    public EnergyManager(NMLEnergySystem nmlEnergySystem) {
        EnergyManager.nmlEnergySystem = nmlEnergySystem;
        profileManager = EnergyManager.nmlEnergySystem.getProfileManager();
    }

    public void energyRegenServerTask() {
        new BukkitRunnable() {
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();

                    if (player.isSprinting() || ongoingEnergyPauses.containsKey(uuid)) continue;

                    double currentEnergy = profileManager.getPlayerProfile(uuid).getStats().getCurrentEnergy();
                    double maxEnergy = profileManager.getPlayerProfile(uuid).getStats().getMaxEnergy();

                    if (currentEnergy < maxEnergy) {
                        addEnergy(player, (maxEnergy / 15)); // how long in seconds it takes to regen to full energy
                    }
                }
            }
        }.runTaskTimer(nmlEnergySystem, 0, 20);
    }

    public void pauseEnergyRegenServerTask() {
        new BukkitRunnable() {
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();

                    if (ongoingEnergyPauses.containsKey(uuid)) {
                        int pauseTimer = ongoingEnergyPauses.get(uuid);

                        if (pauseTimer <= 0) {
                            ongoingEnergyPauses.remove(uuid);
                        } else {
                            ongoingEnergyPauses.put(uuid, pauseTimer - 1);
                        }
                    }
                }
            }
        }.runTaskTimer(nmlEnergySystem, 0, 1);
    }

    public static void addEnergy(Player player, double amount) {
        profileManager.getPlayerProfile(player.getUniqueId()).getStats().add2Stat("currentenergy", amount);
        updateEnergyBar(player);
    }

    public static void useEnergy(Player player, double amount) {
        profileManager.getPlayerProfile(player.getUniqueId()).getStats().removeFromStat("currentenergy", amount);
        updateEnergyBar(player);
    }

    public static void pauseRegen(Player player) {
        ongoingEnergyPauses.put(player.getUniqueId(), Integer.MAX_VALUE);
    }

    public static void resumeRegen(Player player) {
        ongoingEnergyPauses.put(player.getUniqueId(), 0);
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