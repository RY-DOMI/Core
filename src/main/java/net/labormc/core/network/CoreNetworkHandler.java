package net.labormc.core.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.network.connection.Connection;
import net.labormc.cloudapi.network.connection.ConnectionTypes;
import net.labormc.cloudapi.network.protocol.IPacket;
import net.labormc.cloudapi.network.protocol.packets.channel.PacketPlayInClientConnect;
import net.labormc.cloudapi.server.ServerConfig;
import net.labormc.core.CoreAPI;

import java.util.UUID;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCore
 * Author  : Ryixz (Dominik Auer)
 */
@RequiredArgsConstructor
public class CoreNetworkHandler extends SimpleChannelInboundHandler<IPacket> {

    private final Object object;
    private final Class clazz;

    private final Runnable runnable;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IPacket packet) throws Exception {
        CloudAPI.getInstance().getPacketRegistry().callIncoming(ctx, packet);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        CloudAPI.getInstance().getConnectionRegistry().registerConnection(new Connection(UUID.randomUUID(),
                "CLOUD", ConnectionTypes.MASTER, ctx));

        final ServerConfig config = CoreAPI.getInstance().getServerConfig();
        System.out.println("Connected to Cloud @" + config.getHostName() + ":" + config.getPort());
        System.out.println("Activate channel...");

        ctx.writeAndFlush(new PacketPlayInClientConnect(this.clazz, this.object));

        if (runnable != null)
            runnable.run();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    }
}
