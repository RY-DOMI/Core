package net.labormc.core.plugin.listener;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import net.labormc.cloudapi.network.protocol.listener.IPacketHandler;
import net.labormc.cloudapi.network.protocol.listener.IPacketListener;
import net.labormc.cloudapi.network.protocol.packets.server.PacketPlayInDeleteServer;
import net.labormc.core.plugin.CorePlugin;
import net.labormc.core.plugin.events.CloudServerDeleteEvent;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCore
 * Author  : Ryixz (Dominik Auer)
 */
@RequiredArgsConstructor
public class PacketPlayInDeleteServerListener implements IPacketListener {

    private final CorePlugin plugin;

    @IPacketHandler
    public void on(PacketPlayInDeleteServer packet, ChannelHandlerContext ctx) {
        this.plugin.getServer().getPluginManager().callEvent(new CloudServerDeleteEvent(packet.getName(),
                packet.getTemplateName()));
    }
}
