package com.enginarupdate.plugin.beacon;

import io.papermc.paper.event.player.PlayerChangeBeaconEffectEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Custom Beacon effect range: tier 1->30, 2->45, 3->60, 4->75 blocks
 * (vanilla: 20/30/40/50). No pyramid material restriction — vanilla's own
 * tier logic (getTier()) is used as-is.
 */
public class BeaconListener implements Listener {

    private static final int[] RANGE_BY_TIER = {30, 45, 60, 75};

    private final Set<Location> trackedBeacons = ConcurrentHashMap.newKeySet();

    public void start(Plugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                refreshAllBeacons();
            }
        }.runTaskTimer(plugin, 40L, 40L);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.BEACON) {
            trackedBeacons.add(event.getBlock().getLocation());
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.BEACON) {
            trackedBeacons.remove(event.getBlock().getLocation());
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for (BlockState state : event.getChunk().getTileEntities()) {
            if (state instanceof Beacon) {
                trackedBeacons.add(state.getLocation());
            }
        }
    }

    @EventHandler
    public void onChangeEffect(PlayerChangeBeaconEffectEvent event) {
        if (event.getBeacon().getState() instanceof Beacon beacon) {
            applyRange(beacon);
        }
    }

    private void refreshAllBeacons() {
        for (Location loc : trackedBeacons) {
            World world = loc.getWorld();
            if (world == null || !world.isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
                continue;
            }
            Block block = loc.getBlock();
            if (block.getType() != Material.BEACON) {
                trackedBeacons.remove(loc);
                continue;
            }
            if (block.getState() instanceof Beacon beacon) {
                applyRange(beacon);
            }
        }
    }

    private void applyRange(Beacon beacon) {
        int tier = beacon.getTier();
        if (tier <= 0) {
            return;
        }
        int index = Math.min(tier, RANGE_BY_TIER.length) - 1;
        beacon.setEffectRange(RANGE_BY_TIER[index]);
        beacon.update();
    }
}
