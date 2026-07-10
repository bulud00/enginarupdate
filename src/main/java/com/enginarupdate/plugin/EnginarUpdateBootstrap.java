package com.enginarupdate.plugin;

import com.enginarupdate.plugin.commands.EngCountCommand;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * Gold Ore (restricted outside Mesa), Diamond Ore, and Ancient Debris generation values
 * are updated at the source via a bundled datapack ("enginarupdate_oregen"), derived from
 * the real Paper 26.1.2 vanilla worldgen data (configured_feature/placed_feature/biome
 * JSON files extracted from the server jar). The "/engcount" command is also registered
 * here, via Brigadier.
 */
public class EnginarUpdateBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(
                LifecycleEvents.DATAPACK_DISCOVERY.newHandler(event -> {
                    try {
                        URI uri = Objects.requireNonNull(
                                getClass().getResource("/enginarupdate_oregen")
                        ).toURI();
                        event.registrar().discoverPack(uri, "oregen");
                    } catch (URISyntaxException | IOException e) {
                        throw new RuntimeException(e);
                    }
                })
        );

        context.getLifecycleManager().registerEventHandler(
                LifecycleEvents.COMMANDS.newHandler(event ->
                        event.registrar().register(
                                "engcount",
                                "Shows how many of a given block type are in your current chunk (OP only)",
                                new EngCountCommand()
                        )
                )
        );
    }
}
