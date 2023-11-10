package net.labormc.core.plugin.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCore
 * Author  : Ryixz (Dominik Auer)
 */
@RequiredArgsConstructor
@Getter
public class CloudServerDeleteEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final String server;
    private final String templateName;

    @Override
    public HandlerList getHandlers() {
        return CloudServerDeleteEvent.HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
