package net.labormc.core.proxy.listener;

import net.labormc.cloudapi.server.entities.Motd;
import net.labormc.core.proxy.CoreProxy;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.text.MessageFormat;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCore
 * Author  : Ryixz (Dominik Auer)
 */
public class ProxyPingListener implements Listener {

    private final CoreProxy proxy;

    public ProxyPingListener(CoreProxy proxy) {
        this.proxy = proxy;
        this.proxy.getProxy().getPluginManager().registerListener(this.proxy, this);
    }

    @EventHandler
    public void on(ProxyPingEvent event) {
        final ServerPing response = event.getResponse();

        if (this.proxy.isMaintenance()) {
            String version = ChatColor.translateAlternateColorCodes('&', this.proxy.getMaintenanceMotd().getPlayerCountMessage());
            response.setVersion(new ServerPing.Protocol(version, 1));

            Motd motd = this.proxy.getMaintenanceMotd();
            this.formatMotd(event, response, motd);
            return;
        }

        Motd motd = this.proxy.getMotdList().get(new Random().nextInt(this.proxy.getMotdList().size()));
        this.formatMotd(event, response, motd);
    }

    private void formatMotd(ProxyPingEvent event, ServerPing response, Motd motd) {
        String message = MessageFormat.format("{0}\n{1}", motd.getLine1(), motd.getLine2());
        response.setDescriptionComponent(new TextComponent(ChatColor.translateAlternateColorCodes('&', message)));

        List<String> playerInfo = this.proxy.getPlayerInfo();
        ServerPing.PlayerInfo[] playerInfos = new ServerPing.PlayerInfo[playerInfo.size()];

        for (short i = 0; i != playerInfos.length; i++)
            playerInfos[i] = new ServerPing.PlayerInfo(ChatColor.translateAlternateColorCodes('&', playerInfo.get(i)), UUID.randomUUID());
        response.setPlayers(new ServerPing.Players(this.proxy.getMaxPlayers(), this.proxy.getOnlinePlayers(), playerInfos));
        event.setResponse(response);
    }
}
