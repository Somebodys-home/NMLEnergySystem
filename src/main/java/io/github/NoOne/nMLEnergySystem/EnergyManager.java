package io.github.NoOne.nMLEnergySystem;

import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import io.github.NoOne.nMLPlayerStats.statSystem.StatChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class EnergyManager {
    private static NMLEnergySystem nmlEnergySystem;
    private static ProfileManager profileManager;
    private static final ArrayList<UUID> ongoingEnergyPauses = new ArrayList<>();

    public EnergyManager(NMLEnergySystem nmlEnergySystem) {
        this.nmlEnergySystem = nmlEnergySystem;
        profileManager = nmlEnergySystem.getProfileManager();
    }

    public void startEnergyRegenTask() {
        new BukkitRunnable() {
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {

                    // energy cannot be added if either their energy regen is paused, or they're sprinting in survival or adv. mode
                    if ((player.isSprinting() && player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) ||
                        ongoingEnergyPauses.contains(player.getUniqueId())) {
                        continue;
                    }

                    UUID uuid = player.getUniqueId();
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
        Bukkit.getPluginManager().callEvent(new StatChangeEvent(player, "currentenergy", amount));
    }

    public static void useEnergy(Player player, double amount) {
        if (canHaveEnergyRemoved(player)) {
            Bukkit.getPluginManager().callEvent(new StatChangeEvent(player, "currentenergy", -amount));
        }
    }

    public static void pauseEnergyRegen(Player player) {
        ongoingEnergyPauses.add(player.getUniqueId());
    }

    public static void resumeEnergyRegen(Player player) {
        ongoingEnergyPauses.remove(player.getUniqueId());
    }

    public static void updateEnergyBar(Player player) {
        double currentEnergy = profileManager.getPlayerProfile(player.getUniqueId()).getStats().getCurrentEnergy();
        double maxEnergy = profileManager.getPlayerProfile(player.getUniqueId()).getStats().getMaxEnergy();
        double currentEnergyPercent = Math.min(currentEnergy / maxEnergy, 1);

        player.setFoodLevel((int) (currentEnergyPercent * 20));
    }

    public static boolean canHaveEnergyRemoved(Player player) {
        return (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) && !ongoingEnergyPauses.contains(player.getUniqueId());
    }
}