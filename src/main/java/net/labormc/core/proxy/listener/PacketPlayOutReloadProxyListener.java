package net.labormc.core.proxy.listener;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import net.labormc.cloudapi.network.protocol.listener.IPacketHandler;
import net.labormc.cloudapi.network.protocol.listener.IPacketListener;
import net.labormc.cloudapi.network.protocol.packets.server.PacketPlayOutRegisterServer;
import net.labormc.cloudapi.network.protocol.packets.server.PacketPlayOutReloadProxy;
import net.labormc.core.proxy.CoreProxy;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCore
 * Author  : Ryixz (Dominik Auer)
 */
@RequiredArgsConstructor
public class PacketPlayOutReloadProxyListener implements IPacketListener {

    private final CoreProxy proxy;

    @IPacketHandler
    public void on(PacketPlayOutReloadProxy packet, ChannelHandlerContext ctx) {
        this.proxy.loadConfig();
    }
}
