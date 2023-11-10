package net.labormc.core.plugin.sign;

import lombok.*;
import net.labormc.cloudapi.server.game.GameStates;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCore
 * Author  : Ryixz (Dominik Auer)
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GameSign {

    private String serverName;

    private GameStates gameState;
    
    private int onlinePlayers;
    private int maxPlayers;

}
