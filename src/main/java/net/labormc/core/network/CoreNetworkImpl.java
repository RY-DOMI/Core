package net.labormc.core.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.RequiredArgsConstructor;
import net.labormc.cloudapi.network.protocol.codec.PacketDecoder;
import net.labormc.cloudapi.network.protocol.codec.PacketEncoder;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCore
 * Author  : Ryixz (Dominik Auer)
 */
@RequiredArgsConstructor
public class CoreNetworkImpl implements Runnable {

    private final String hostName;
    private final int port;

    private final Object object;
    private final Class clazz;

    private final Runnable runnable;


    @Override
    public void run() {
        System.out.println("Trying to connect to cloud...");

        final boolean epoll = Epoll.isAvailable();
        final EventLoopGroup eventLoopGroup = epoll ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        try {

            final Bootstrap bootstrap = new Bootstrap()
                    .group(eventLoopGroup)
                    .channel(epoll ? EpollSocketChannel.class : NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.config().setOption(ChannelOption.IP_TOS, 0x18);

                            socketChannel.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                                    .addLast(new PacketDecoder())
                                    .addLast(new LengthFieldPrepender(4))
                                    .addLast(new PacketEncoder())
                                    .addLast(new CoreNetworkHandler(object, clazz, runnable));
                        }
                    }).option(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture channelFuture = null;
            channelFuture = bootstrap.connect(this.hostName, this.port);

            channelFuture.channel().closeFuture().addListener(future -> {
                System.exit(0);
                eventLoopGroup.shutdownGracefully();
            }).sync();
        } catch (Exception ex) {
            Logger.getLogger(CoreNetworkImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
