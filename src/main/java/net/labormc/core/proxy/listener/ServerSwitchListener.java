package net.labormc.core.proxy.listener;

import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.cloudplayer.CloudPlayer;
import net.labormc.cloudapi.network.client.types.MinecraftServer;
import net.labormc.cloudapi.network.protocol.packets.cloudplayer.PacketPlayInCloudPlayerConnected;
import net.labormc.cloudapi.network.protocol.packets.cloudplayer.PacketPlayInCloudPlayerUpdate;
import net.labormc.core.CoreAPI;
import net.labormc.core.proxy.CoreProxy;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.LinkedHashMap;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCore
 * Author  : Ryixz (Dominik Auer)
 */
public class ServerSwitchListener implements Listener {

    private final CoreProxy proxy;

    public ServerSwitchListener(CoreProxy proxy) {
        this.proxy = proxy;
        this.proxy.getProxy().getPluginManager().registerListener(this.proxy, this);
    }

    @EventHandler
    public void on(ServerSwitchEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        CoreAPI.getInstance().getCloudPlayer(player.getUniqueId(), (cloudPlayer, throwable) -> {
            final MinecraftServer server = CloudAPI.getInstance().getServerRegistry()
                    .getServer(player.getServer().getInfo().getName(), MinecraftServer.class);
            if (cloudPlayer == null) {
                final CloudPlayer cp = new CloudPlayer(player.getUniqueId(), player.getName(), server.getUniqueId(),
                        this.proxy.getProxyServer().getUniqueId());
                CoreAPI.getInstance().sendPacketAsync(new PacketPlayInCloudPlayerConnected(cp));
                return;
            }
            cloudPlayer.setServerUniqueId(server.getUniqueId());
            CoreAPI.getInstance().sendPacketAsync(new PacketPlayInCloudPlayerUpdate(cloudPlayer));
        });

        this.proxy.setTabList(event.getPlayer(), new LinkedHashMap<>());
    }
}
