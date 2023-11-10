package net.labormc.core.proxy.listener;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.network.client.types.MinecraftServer;
import net.labormc.cloudapi.network.protocol.listener.IPacketHandler;
import net.labormc.cloudapi.network.protocol.listener.IPacketListener;
import net.labormc.cloudapi.network.protocol.packets.server.PacketPlayOutRegisterServer;
import net.labormc.core.CoreAPI;
import net.labormc.core.proxy.CoreProxy;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCore
 * Author  : Ryixz (Dominik Auer)
 */
@RequiredArgsConstructor
public class PacketPlayOutRegisterServerListener implements IPacketListener {

    private final CoreProxy proxy;

    @IPacketHandler
    public void on(PacketPlayOutRegisterServer packet, ChannelHandlerContext ctx) {
        final MinecraftServer server = (MinecraftServer) packet.getObject();
        CloudAPI.getInstance().getServerRegistry().registerServer(server);

        this.proxy.getProxy().getServers().put(server.getName(),
                this.proxy.getProxy().constructServerInfo(server.getName(), server.getAddress(), "", false));

        this.proxy.getProxy().getPlayers().forEach(player -> {
            if (player.hasPermission("labormc.server.info")) {
                player.sendMessage(TextComponent.fromLegacyText(CoreAPI.getInstance().getPrefix() + "Der §bServer §8(§b§l" + server.getName()
                        + "§8) §7wurde §agestartet§8."));
            }
        });
    }
}
