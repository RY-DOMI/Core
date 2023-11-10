package net.labormc.core.plugin.listener;

import net.labormc.cloudapi.server.game.GameStates;
import net.labormc.core.CoreAPI;
import net.labormc.core.plugin.CorePlugin;
import net.labormc.core.plugin.sign.GameSign;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCore
 * Author  : Ryixz (Dominik Auer)
 */
public class PlayerInteractListener implements Listener {

    private final CorePlugin plugin;

    public PlayerInteractListener(CorePlugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (!(Objects.requireNonNull(event.getClickedBlock()).hasMetadata("server")))
            return;
        GameSign gameSign = (GameSign) event.getClickedBlock().getMetadata("server").get(0).value();
        GameStates gameState = Objects.requireNonNull(gameSign).getGameState();

        if (gameState == GameStates.LOBBY) {
            if (gameSign.getOnlinePlayers() >= gameSign.getMaxPlayers()) {
                if (player.hasPermission("labormc.joinfull")) {
                    plugin.sendToServer(player, gameSign.getServerName());
                    return;
                }
                player.sendMessage(CoreAPI.getInstance().getPrefix() + "§cDieser Server ist voll!");
                player.sendMessage(CoreAPI.getInstance().getPrefix() + "Um volle &bServer betreten zu können, benötigst du einen §bhöheren §7Rang&8!");
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 10F, 10F);
                return;
            }
            plugin.sendToServer(player, gameSign.getServerName());
        }
    }
}
