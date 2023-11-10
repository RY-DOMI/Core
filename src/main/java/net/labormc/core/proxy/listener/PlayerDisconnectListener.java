package net.labormc.core.proxy.listener;

import net.labormc.cloudapi.network.protocol.packets.cloudplayer.PacketPlayInCloudPlayerDisconnected;
import net.labormc.core.CoreAPI;
import net.labormc.core.proxy.CoreProxy;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCore
 * Author  : Ryixz (Dominik Auer)
 */
public class PlayerDisconnectListener implements Listener {

    private final CoreProxy proxy;

    public PlayerDisconnectListener(CoreProxy proxy) {
        this.proxy = proxy;
        this.proxy.getProxy().getPluginManager().registerListener(this.proxy, this);
    }

    @EventHandler
    public void on(PlayerDisconnectEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        CoreAPI.getInstance().getCloudPlayer(player.getUniqueId(), (cloudPlayer, throwable) -> {
            if (throwable != null) {
                System.out.println(throwable.getMessage());
                return;
            }
            if (cloudPlayer == null) {
                return;
            }
            CoreAPI.getInstance().sendPacketAsync(new PacketPlayInCloudPlayerDisconnected(cloudPlayer.getUuid()));
        });
        this.proxy.updatePlayerCount(true);
    }
}
