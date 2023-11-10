package net.labormc.core.proxy.command;

import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.network.client.types.MinecraftServer;
import net.labormc.cloudapi.network.protocol.packets.server.PacketPlayInDeleteServer;
import net.labormc.cloudapi.network.protocol.packets.server.PacketPlayInRequestServer;
import net.labormc.core.CoreAPI;
import net.labormc.core.proxy.CoreProxy;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.text.MessageFormat;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCore
 * Author  : Ryixz (Dominik Auer)
 */
public class CloudCommand extends Command {

    private final CoreProxy core;

    public CloudCommand(CoreProxy core) {
        super("cloud", "labormc.command.cloud");
        this.core = core;
        this.core.getProxy().getPluginManager().registerCommand(this.core, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer))
            return;
        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (!player.hasPermission(this.getPermission())) {
            player.sendMessage(this.core.getMessage("&c"));
            return;
        }

        if (args.length == 0) {
            this.getUsageList(player);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "server":
                if (args.length == 2) {
                    if (args[1].equalsIgnoreCase("list")) {
                        player.sendMessage(this.core.getMessage("&7Server&8-&7List&8:"));

                        CloudAPI.getInstance().getTemplateRegistry().getAll().forEach(template -> {
                            final int size = CloudAPI.getInstance().getServerRegistry().getAll(template).size();
                            player.sendMessage(this.core.getMessage(" &8- &b" + template.getName() + " &8(&b&l" + size + "&8)"));
                        });
                        return;
                    }
                    this.getUsageList(player);
                    return;
                }

                if (args.length == 3) {
                    if (args[1].equalsIgnoreCase("delete")) {
                        String name = args[2];

                        if (this.core.getProxy().getServers().containsKey(name)) {
                            final ServerInfo info = this.core.getProxy().getServers().get(name);
                            player.sendMessage(this.core.getMessage("Der §bServer §8(§b§l" + info.getName() + "&8) &7wird &cgestoppt&8."));
                            MinecraftServer server = CloudAPI.getInstance().getServerRegistry().getServer(name, MinecraftServer.class);
                            CoreAPI.getInstance().sendPacketSync(new PacketPlayInDeleteServer(name, server.getTemplateName()));
                            return;
                        }
                        player.sendMessage(this.core.getMessage("&cDer angegebene Server existiert nicht!"));
                        return;
                    }
                    if (args[1].equalsIgnoreCase("info")) {
                        String name = args[2];

                        if (!this.core.getProxy().getServers().containsKey(name)) {
                            player.sendMessage(this.core.getMessage("&cDer angegebene Server existiert nicht!"));
                            return;
                        }
                        /*
                        CoreAPI.getInstance().getServer(name, (server, throwable) -> {
                            if (server == null || throwable != null) {
                                player.sendMessage(plugin.getMessage("&cDer angegebene Server existiert nicht!"));
                                return;
                            }

                            player.sendMessage(plugin.getMessage("&eInformationen &7über den &eServer &8(&a&l" + name + "&8)"));
                            final MinecraftServer minecraftServer = (MinecraftServer) server;

                            player.sendMessage(plugin.getMessage("&8 - &7UUID&8: &e" + server.getUniqueId().toString()));
                            player.sendMessage(plugin.getMessage("&8 - &7Spieler&8: &e" + server.getOnlineCount() + " &8/ &e" +
                                    server.getTemplate().getMaxPlayers()));
                            player.sendMessage(plugin.getMessage("&8 - &7State&8: &e" + minecraftServer.getGameState().name()));
                        });*/
                        return;
                    }

                    this.getUsageList(player);
                    return;
                }

                if (args.length == 4) {
                    if (args[1].equalsIgnoreCase("request")) {
                        final String templateName = args[2];
                        final int amount = Integer.parseInt(args[3]);

                        if (amount > 0) {
                            player.sendMessage(this.core.getMessage(MessageFormat.format(
                                    "&8(&b&l{0}&8) &7{1} von dem &bTemplate &8(&b&l{2}&8) &7angefragt.",
                                    amount, (amount == 1 ? "wurde" : "wurden"), templateName.toUpperCase())));
                            CoreAPI.getInstance().sendPacketSync(new PacketPlayInRequestServer(templateName, amount));
                            return;
                        }
                        player.sendMessage(this.core.getMessage("&cDu musst einen Wert über 0 angeben!"));
                        return;
                    }
                    this.getUsageList(player);
                    return;
                }
                this.getUsageList(player);
                break;
            case "maintenance":
                if(args.length == 2) {
                    String state = args[1];

                    if(state.equalsIgnoreCase("on") || state.equalsIgnoreCase("off")) {
                        /*
                        CoreAPI.getInstance().updateMaintenance(state.equalsIgnoreCase("on"));
                        this.core.setMaintenance(state.equalsIgnoreCase("on"));
                        if(state.equalsIgnoreCase("on"))
                            player.sendMessage(plugin.getMessage("&bWartungsarbeiten &7wurden &aaktiviert&7!"));
                        else
                            player.sendMessage(plugin.getMessage("&bWartungsarbeiten &7wurden &cdeaktiviert&7!"));*/
                        return;
                    }
                    this.getUsageList(player);
                    return;
                }
                if(args.length == 3) {
                    String templateName = args[1];
                    String state = args[2];

                    if(state.equalsIgnoreCase("on") || state.equalsIgnoreCase("off")) {
                        /*
                        CloudAPI.getInstance().updateMaintenance(state.equalsIgnoreCase("on"), templateName);
                        if(state.equalsIgnoreCase("on"))
                            player.sendMessage(plugin.getMessage("&bWartungsarbeiten &7wurden für das &bTemplate &8(&b&l" + templateName + "&8) &aaktiviert&7!"));
                        else
                            player.sendMessage(plugin.getMessage("&bWartungsarbeiten &7wurden für das &bTemplate &8(&b&l" + templateName + "&8) &cdeaktiviert&7!"));
                        return;*/
                    }
                    this.getUsageList(player);
                }
                break;
            case "whitelist":
                break;
            default:
                this.getUsageList(player);
                break;
        }
    }

    private void getUsageList(ProxiedPlayer player) {
        player.sendMessage(this.core.getMessage("&8/&7cloud server request &8<&bTemplatename&8> <&bAnzahl&8>"));
        player.sendMessage(this.core.getMessage("&8/&7cloud server delete &8<&bServername&8>"));
        player.sendMessage(this.core.getMessage("&8/&7cloud server info &8<&bServername&8>"));

        player.sendMessage(this.core.getMessage("&8/&7cloud maintenance &8<&bon &8/ &boff&8>"));
        player.sendMessage(this.core.getMessage("&8/&7cloud maintenance &8<&bTemplatename&8> <&bon &8/ &boff&8>"));

        player.sendMessage(this.core.getMessage("&8/&7cloud whitelist add &8<&bSpielername&8>"));
        player.sendMessage(this.core.getMessage("&8/&7cloud whitelist remove &8<&bSpielername&8"));
    }
}
