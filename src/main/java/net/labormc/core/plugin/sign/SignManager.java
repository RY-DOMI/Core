package net.labormc.core.plugin.sign;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.labormc.cloudapi.network.client.types.MinecraftServer;
import net.labormc.cloudapi.server.game.GameStates;
import net.labormc.cloudapi.sign.SignLayout;
import net.labormc.cloudapi.sign.SignLocation;
import net.labormc.cloudapi.sign.enums.SignLayoutStates;
import net.labormc.core.CoreAPI;
import net.labormc.core.plugin.CorePlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCore
 * Author  : Ryixz (Dominik Auer)
 */
@RequiredArgsConstructor
public class SignManager {

    private final CorePlugin plugin;

    private final Map<SignLayoutStates, SignLayout> signLayoutMap = new LinkedHashMap<>();
    private List<SignLocation> signLocationList;
    @Getter
    private List<MinecraftServer> gameServerList;

    public void load() {
        this.signLayoutMap.clear();
        this.signLocationList.clear();

        CoreAPI.getInstance().getSignLayoutList((value, throwable) ->
                value.forEach((layout) -> this.signLayoutMap.put(layout.getState(), layout)));
        CoreAPI.getInstance().getSignLocationList((value, throwable) -> this.signLocationList = value);
        CoreAPI.getInstance().getGameServers((value, throwable) -> this.gameServerList = value);
    }

    public void updateSign(String serverName, GameStates state, int onlinePlayers, int maxPlayers,
                           Location location, String[] lines) {
        Block block = location.getBlock();

        if (!location.getChunk().isLoaded())
            plugin.getServer().getScheduler().runTask(plugin, () -> location.getChunk().load());

        block.getType();
        if (block.getType() != Material.OAK_SIGN && block.getType() != Material.OAK_WALL_SIGN)
            return;
        Sign sign = (Sign) block.getState();

        if(state != GameStates.LOBBY && sign.hasMetadata("server"))
            sign.removeMetadata("server", plugin);

        for (int i = 0; i != sign.getLines().length; i++)
            sign.setLine(i, lines[i]);
        sign.update();

        GameSign gameSign = (sign.hasMetadata("server") ? (GameSign) sign.getMetadata("server").get(0).value() : new GameSign());
        if (gameSign != null) {
            gameSign.setServerName(serverName);
            gameSign.setGameState(state);
            gameSign.setOnlinePlayers(onlinePlayers);
            gameSign.setMaxPlayers(maxPlayers);
        }

        sign.setMetadata("server", new FixedMetadataValue(plugin, gameSign));
    }
}
