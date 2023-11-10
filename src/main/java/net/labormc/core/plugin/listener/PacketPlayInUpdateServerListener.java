package net.labormc.core.plugin.listener;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import net.labormc.cloudapi.network.client.types.MinecraftServer;
import net.labormc.cloudapi.network.client.types.ProxyServer;
import net.labormc.cloudapi.network.protocol.listener.IPacketHandler;
import net.labormc.cloudapi.network.protocol.listener.IPacketListener;
import net.labormc.cloudapi.network.protocol.packets.server.PacketPlayInUpdateServerInfo;
import net.labormc.core.plugin.CorePlugin;
import net.labormc.core.plugin.events.CloudProxyUpdateEvent;
import net.labormc.core.plugin.events.CloudServerUpdateEvent;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCore
 * Author  : Ryixz (Dominik Auer)
 */
@RequiredArgsConstructor
public class PacketPlayInUpdateServerListener implements IPacketListener {

    private final CorePlugin plugin;

    @IPacketHandler
    public void on(PacketPlayInUpdateServerInfo packet, ChannelHandlerContext ctx) {
        if (packet.getObject() instanceof MinecraftServer minecraftServer) {
            this.plugin.getServer().getPluginManager().callEvent(new CloudServerUpdateEvent(minecraftServer));
            return;
        }
        if (packet.getObject() instanceof ProxyServer proxyServer) {
            this.plugin.getServer().getPluginManager().callEvent(new CloudProxyUpdateEvent(proxyServer));
        }
    }
}
