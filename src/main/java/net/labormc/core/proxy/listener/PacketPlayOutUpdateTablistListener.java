package net.labormc.core.proxy.listener;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import net.labormc.cloudapi.network.protocol.listener.IPacketHandler;
import net.labormc.cloudapi.network.protocol.listener.IPacketListener;
import net.labormc.cloudapi.network.protocol.packets.tablist.PacketPlayOutUpdateTablist;
import net.labormc.core.proxy.CoreProxy;

import java.util.HashMap;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCore
 * Author  : Ryixz (Dominik Auer)
 */
@RequiredArgsConstructor
public class PacketPlayOutUpdateTablistListener implements IPacketListener {

    private final CoreProxy proxy;

    @IPacketHandler
    public void on(PacketPlayOutUpdateTablist packet, ChannelHandlerContext ctx) {
        this.proxy.getProxy().getPlayers().forEach(player -> this.proxy.setTabList(player, new HashMap<>()));
    }
}
