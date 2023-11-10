package net.labormc.core.plugin.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.labormc.cloudapi.network.client.types.MinecraftServer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCore
 * Author  : Ryixz (Dominik Auer)
 */
@RequiredArgsConstructor
@Getter
public class CloudServerUpdateEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final MinecraftServer server;

    @Override
    public HandlerList getHandlers() {
        return CloudServerUpdateEvent.HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
