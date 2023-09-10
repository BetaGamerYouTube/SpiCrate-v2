package de.beta.spicrate.database;

import de.beta.spicrate.SpiCrate;
import org.bukkit.Bukkit;

public class DatabaseConnectionHolder {

    private static int taskId;

    public static void startRunnable() {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(SpiCrate.getInstance(), new Runnable() {
            @Override
            public void run() {
                SpiCrate.getInstance().getCrateManager().prepareStatement("SELECT * FROM `crates`");
            }
        }, 1200L, 1200L);
    }

    public static void stopRunnable() {
        Bukkit.getScheduler().cancelTask(taskId);
    }
}
