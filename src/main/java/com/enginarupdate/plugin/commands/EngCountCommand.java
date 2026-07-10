package com.enginarupdate.plugin.commands;

import com.enginarupdate.plugin.lang.Lang;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * /engcount {block_id} — OP only. Counts how many of the given block type are in the
 * chunk the calling player is currently standing in.
 */
public class EngCountCommand implements BasicCommand {

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(Component.text("This command can only be used by players."));
            return;
        }
        if (!player.isOp()) {
            player.sendMessage(Component.text(Lang.pick(player,
                    "You do not have permission to use this command.",
                    "Bu komutu kullanma izniniz yok.")));
            return;
        }
        if (args.length < 1 || args[0].isBlank()) {
            player.sendMessage(Component.text(Lang.pick(player,
                    "Usage: /engcount {block_id}",
                    "Kullanım: /engcount {block_id}")));
            return;
        }

        Material material = Material.matchMaterial(args[0]);
        if (material == null || !material.isBlock()) {
            player.sendMessage(Component.text(Lang.pick(player,
                    "Invalid block id: " + args[0],
                    "Geçersiz blok id: " + args[0])));
            return;
        }

        int count = countBlocksInChunk(player.getChunk(), material);
        player.sendMessage(Component.text(Lang.pick(player,
                "There are " + count + " " + material.name() + " block(s) in your current chunk.",
                "Bulunduğun chunk'ta " + material.name() + " bloğundan " + count + " tane var.")));
    }

    private int countBlocksInChunk(Chunk chunk, Material material) {
        World world = chunk.getWorld();
        int minY = world.getMinHeight();
        int maxY = world.getMaxHeight();
        int count = 0;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = minY; y < maxY; y++) {
                    if (chunk.getBlock(x, y, z).getType() == material) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
