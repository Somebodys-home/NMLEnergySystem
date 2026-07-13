package io.github.NoOne.nMLEnergySystem;

import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class NMLEnergySystem extends JavaPlugin {
    private ProfileManager profileManager;

    @Override
    public void onEnable() {
        profileManager = JavaPlugin.getPlugin(NMLPlayerStats.class).getProfileManager();

        EnergyManager energyManager = new EnergyManager(this);
        energyManager.startEnergyRegenTask();

        getServer().getPluginManager().registerEvents(new EnergyListener(this), this);
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }
}
