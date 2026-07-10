package com.enginarupdate.plugin;

import com.enginarupdate.plugin.beacon.BeaconListener;
import com.enginarupdate.plugin.enchant.ElytraMendingBlockListener;
import com.enginarupdate.plugin.enchant.MendingListener;
import com.enginarupdate.plugin.end.EndCrystalListener;
import com.enginarupdate.plugin.end.EndTeleportTask;
import com.enginarupdate.plugin.loot.LootTableListener;
import com.enginarupdate.plugin.mobs.EndermanBlazeLootListener;
import com.enginarupdate.plugin.mobs.EndermanBlockPickupListener;
import com.enginarupdate.plugin.mobs.MobHealthListener;
import com.enginarupdate.plugin.mobs.ShulkerBulletListener;
import com.enginarupdate.plugin.mobs.SkeletonDamageListener;
import com.enginarupdate.plugin.mobs.WitchCombatTask;
import com.enginarupdate.plugin.mobs.WitchLootListener;
import com.enginarupdate.plugin.recipes.RecipeManager;
import com.enginarupdate.plugin.worldgen.NetherGoldOreDropListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class EnginarUpdatePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();

        // Creature updates
        pm.registerEvents(new MobHealthListener(), this);
        pm.registerEvents(new SkeletonDamageListener(), this);
        pm.registerEvents(new EndermanBlazeLootListener(), this);
        pm.registerEvents(new EndermanBlockPickupListener(), this);
        pm.registerEvents(new ShulkerBulletListener(), this);
        pm.registerEvents(new WitchLootListener(), this);
        WitchCombatTask.start(this);

        // Recipes and loot
        RecipeManager.apply(this);
        pm.registerEvents(new LootTableListener(), this);

        // Beacon
        BeaconListener beaconListener = new BeaconListener();
        pm.registerEvents(beaconListener, this);
        beaconListener.start(this);

        // The End dimension
        pm.registerEvents(new EndCrystalListener(this), this);
        EndTeleportTask.start(this);

        // Mending
        pm.registerEvents(new MendingListener(), this);
        pm.registerEvents(new ElytraMendingBlockListener(), this);

        // World generation (Gold Ore/Diamond Ore/Ancient Debris worldgen settings are
        // applied via the "enginarupdate_oregen" datapack through EnginarUpdateBootstrap)
        pm.registerEvents(new NetherGoldOreDropListener(), this);

        getLogger().info("EnginarUpdate-v1.1 enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("EnginarUpdate-v1.1 disabled.");
    }
}
