package net.labormc.core.plugin.listener;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import net.labormc.cloudapi.network.protocol.listener.IPacketHandler;
import net.labormc.cloudapi.network.protocol.listener.IPacketListener;
import net.labormc.cloudapi.network.protocol.packets.server.PacketPlayOutUpdateSign;
import net.labormc.core.plugin.CorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCore
 * Author  : Ryixz (Dominik Auer)
 */
@RequiredArgsConstructor
public class PacketPlayOutUpdateSignListener implements IPacketListener {

    private final CorePlugin plugin;

    @IPacketHandler
    public void on(PacketPlayOutUpdateSign packet, ChannelHandlerContext ctx) {
        Location location = new Location(Bukkit.getWorld("Lobby"), packet.getX(), packet.getY(), packet.getZ());
        this.plugin.getSignManager().updateSign(
                packet.getServerName(),
                packet.getGameState(),
                packet.getOnlinePlayers(),
                packet.getMaxPlayers(),
                location,
                packet.getLines());
    }
}
