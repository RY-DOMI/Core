package net.labormc.core.plugin.listener;

import lombok.RequiredArgsConstructor;
import net.labormc.core.plugin.CorePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCore
 * Author  : Ryixz (Dominik Auer)
 */
@RequiredArgsConstructor
public class PlayerJoinListener implements Listener {

    private final CorePlugin plugin;

    @EventHandler
    public void on(PlayerJoinEvent event) {
        this.plugin.updatePlayerCount(false);
    }
}
