package io.github.NoOne.nMLEnergySystem;

import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class NMLEnergySystem extends JavaPlugin {
    private NMLEnergySystem instance;
    private NMLPlayerStats nmlPlayerStats;
    private ProfileManager profileManager;
    private EnergyManager energyManager;

    @Override
    public void onEnable() {
        instance = this;

        Plugin plugin = Bukkit.getPluginManager().getPlugin("NMLPlayerStats");
        if (plugin instanceof NMLPlayerStats statsPlugin) {
            nmlPlayerStats = statsPlugin;
            profileManager = nmlPlayerStats.getProfileManager();
        } else {
            getLogger().severe("Failed to find NMLPlayerStats! Disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        energyManager = new EnergyManager(this);
        energyManager.energyRegenServerTask();
        energyManager.pauseEnergyRegenServerTask();

        getServer().getPluginManager().registerEvents(new EnergyListener(nmlPlayerStats), this);
    }

    public NMLEnergySystem getInstance() {
        return instance;
    }

    public NMLPlayerStats getNmlPlayerStats() {
        return nmlPlayerStats;
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public EnergyManager getEnergyManager() {
        return energyManager;
    }
}
