package io.github.NoOne.nMLEnergySystem;

import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import io.github.NoOne.nMLPlayerStats.statSystem.ResetStatsEvent;
import io.github.NoOne.nMLPlayerStats.statSystem.StatChangeEvent;
import io.github.NoOne.nMLPlayerStats.statSystem.Stats;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class EnergyListener implements Listener {
    private ProfileManager profileManager;

    public EnergyListener(NMLEnergySystem nmlEnergySystem) {
        this.profileManager = nmlEnergySystem.getProfileManager();
    }

    @EventHandler
    public void onEnergyStatChange(StatChangeEvent event) {
        Player player = event.getPlayer();

        if (event.getStat().equals("currentenergy")) {
            EnergyManager.updateEnergyBar(player);
        } else if (event.getStat().equals("maxenergy")) {
            Stats stats = profileManager.getPlayerProfile(player.getUniqueId()).getStats();

            if (stats.getCurrentEnergy() > stats.getMaxEnergy()) {
                Bukkit.getPluginManager().callEvent(new StatChangeEvent(player, "currentenergy", stats.getMaxEnergy() - stats.getCurrentEnergy()));
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.isSprinting()) {
            event.setCancelled(true);
            return;
        }

        Stats stats = profileManager.getPlayerProfile(player.getUniqueId()).getStats();
        int foodLevelChange = event.getFoodLevel() - player.getFoodLevel();
        double energyChange = foodLevelChange * (stats.getMaxEnergy() / 20);

        Bukkit.getPluginManager().callEvent(new StatChangeEvent(player, "currentenergy", energyChange));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Stats stats = profileManager.getPlayerProfile(player.getUniqueId()).getStats();

        if (player.isSprinting()) {
            EnergyManager.useEnergy(player, stats.getMaxEnergy() / 600);
        }
    }

    @EventHandler
    public void resetEnergy(ResetStatsEvent event) {
        event.getPlayer().setFoodLevel(20);
    }
}
