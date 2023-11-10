package net.labormc.core.proxy.listener;

import net.labormc.core.proxy.CoreProxy;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCore
 * Author  : Ryixz (Dominik Auer)
 */
public class LoginListener implements Listener {

    private final CoreProxy proxy;

    public LoginListener(CoreProxy proxy) {
        this.proxy = proxy;
        this.proxy.getProxy().getPluginManager().registerListener(this.proxy, this);
    }

    @EventHandler
    public void on(LoginEvent event) {
        event.registerIntent(this.proxy);
        final PendingConnection connection = event.getConnection();

        if (this.proxy.isMaintenance()) {
            if (!(this.proxy.getWhiteList().contains(connection.getUniqueId().toString()))) {
                event.setCancelReason(TextComponent.fromLegacyText(this.proxy.getMaintenanceKickMessage()));
                event.setCancelled(true);
            }
        }
        event.completeIntent(this.proxy);
    }
}
