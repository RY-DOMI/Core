package net.labormc.core.proxy.listener;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.network.protocol.listener.IPacketHandler;
import net.labormc.cloudapi.network.protocol.listener.IPacketListener;
import net.labormc.cloudapi.network.protocol.packets.server.PacketPlayOutUnregisterServer;
import net.labormc.core.CoreAPI;
import net.labormc.core.proxy.CoreProxy;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCore
 * Author  : Ryixz (Dominik Auer)
 */
@RequiredArgsConstructor
public class PacketPlayOutUnregisterServerListener implements IPacketListener {

    private final CoreProxy proxy;

    @IPacketHandler
    public void on(PacketPlayOutUnregisterServer packet, ChannelHandlerContext ctx) {
        this.proxy.getProxy().getServers().remove(packet.getName());
        CloudAPI.getInstance().getServerRegistry().unregisterServer(CloudAPI.getInstance().getServerRegistry().getServer(packet.getName()));

        this.proxy.getProxy().getPlayers().forEach(player -> {
            if (player.hasPermission("labormc.server.info")) {
                player.sendMessage(TextComponent.fromLegacyText(CoreAPI.getInstance().getPrefix() + "Der §bServer §8(§b§l" + packet.getName()
                        + "§8) §7wurde §cgestoppt§8."));
            }
        });
    }
}
