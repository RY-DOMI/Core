package net.labormc.core.network.listener;

import io.netty.channel.ChannelHandlerContext;
import net.labormc.cloudapi.network.protocol.listener.IPacketHandler;
import net.labormc.cloudapi.network.protocol.listener.IPacketListener;
import net.labormc.cloudapi.network.protocol.packets.api.PacketPlayOutAPIResponse;
import net.labormc.core.CoreAPI;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCore
 * Author  : Ryixz (Dominik Auer)
 */
public class PacketPlayOutAPIResponseListener implements IPacketListener {

    @IPacketHandler
    public void on(PacketPlayOutAPIResponse packet, ChannelHandlerContext ctx) {
        CoreAPI.getInstance().getMap().put(packet.getUnqiueId(), packet.getObject());
    }

}
