package net.labormc.core.proxy.listener;

import net.labormc.cloudapi.cloudplayer.CloudPlayer;
import net.labormc.cloudapi.network.protocol.packets.cloudplayer.PacketPlayInCloudPlayerConnected;
import net.labormc.core.CoreAPI;
import net.labormc.core.proxy.CoreProxy;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.LinkedHashMap;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCore
 * Author  : Ryixz (Dominik Auer)
 */
public class PostLoginListener implements Listener {

    private final CoreProxy proxy;

    public PostLoginListener(CoreProxy proxy) {
        this.proxy = proxy;
        this.proxy.getProxy().getPluginManager().registerListener(this.proxy, this);
    }

    @EventHandler
    public void on(PostLoginEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        this.proxy.setTabList(player, new LinkedHashMap<>());
        this.proxy.updatePlayerCount(false);

        final CloudPlayer cloudPlayer = new CloudPlayer(player.getUniqueId(), player.getName(), null,
                this.proxy.getProxyServer().getUniqueId());
        CoreAPI.getInstance().sendPacketAsync(new PacketPlayInCloudPlayerConnected(cloudPlayer));
    }
}
