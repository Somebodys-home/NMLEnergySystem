package io.github.NoOne.nMLEnergySystem;

import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.statSystem.ResetStatsEvent;
import io.github.NoOne.nMLPlayerStats.statSystem.StatChangeEvent;
import io.github.NoOne.nMLPlayerStats.statSystem.Stats;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class EnergyListener implements Listener {
    private NMLPlayerStats nmlPlayerStats;

    public EnergyListener(NMLPlayerStats nmlPlayerStats) {
        this.nmlPlayerStats = nmlPlayerStats;
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        var stats = nmlPlayerStats.getProfileManager().getPlayerProfile(player.getUniqueId()).getStats();

        if (player.isSprinting()) {
            event.setCancelled(true);
            return;
        }

        int foodLevelChange = event.getFoodLevel() - player.getFoodLevel();
        double energyChange = foodLevelChange * (stats.getMaxEnergy() / 20);

        stats.setCurrentEnergy(stats.getCurrentEnergy() + energyChange);
    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Stats stats = nmlPlayerStats.getProfileManager().getPlayerProfile(player.getUniqueId()).getStats();

        if (player.isSprinting() && player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
            EnergyManager.useEnergy(player, stats.getMaxEnergy() / 600);
        }
    }

    @EventHandler
    public void resetEnergy(ResetStatsEvent event) {
        event.getPlayer().setFoodLevel(20);
    }
}
