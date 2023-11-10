package net.labormc.core.proxy;

import lombok.Getter;
import net.labormc.cloud.impl.CloudAPIImpl;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.document.AbstractDocument;
import net.labormc.cloudapi.message.Message;
import net.labormc.cloudapi.network.client.types.ProxyServer;
import net.labormc.cloudapi.network.protocol.packets.api.PacketPlayInAPIRequest;
import net.labormc.cloudapi.network.protocol.packets.api.PacketPlayOutAPIResponse;
import net.labormc.cloudapi.network.protocol.packets.channel.PacketPlayInClientConnect;
import net.labormc.cloudapi.network.protocol.packets.channel.PacketPlayOutConnectionSuccess;
import net.labormc.cloudapi.network.protocol.packets.cloudplayer.PacketPlayInCloudPlayerConnected;
import net.labormc.cloudapi.network.protocol.packets.cloudplayer.PacketPlayInCloudPlayerDisconnected;
import net.labormc.cloudapi.network.protocol.packets.cloudplayer.PacketPlayInCloudPlayerUpdate;
import net.labormc.cloudapi.network.protocol.packets.maintenance.PacketPlayOutUpdateMaintenance;
import net.labormc.cloudapi.network.protocol.packets.server.*;
import net.labormc.cloudapi.network.protocol.packets.tablist.PacketPlayOutUpdateTablist;
import net.labormc.cloudapi.server.ServerConfig;
import net.labormc.cloudapi.server.entities.Motd;
import net.labormc.cloudapi.server.entities.Tablist;
import net.labormc.cloudapi.server.template.Template;
import net.labormc.core.CoreAPI;
import net.labormc.core.proxy.command.CloudCommand;
import net.labormc.core.proxy.listener.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCore
 * Author  : Ryixz (Dominik Auer)
 */
@Getter
public class CoreProxy extends Plugin {

    private String maintenanceKickMessage;

    private int maxPlayers, onlinePlayers = 0;

    private boolean maintenance = true;
    private Motd maintenanceMotd;

    private ProxyServer proxyServer;
    private Tablist tablist;

    private List<String> whiteList, playerInfo;
    private List<Motd> motdList = new LinkedList<>();

    private ScheduledTask onlinePlayersTask;

    @Override
    public void onLoad() {
        this.initCloud();
        new CoreAPI();

        final File configFile = new File("configs/config.json");
        final AbstractDocument document = CloudAPI.getInstance().document().load(configFile);
        CoreAPI.getInstance().setServerConfig(document.get("cloud", ServerConfig.class));
        this.proxyServer = document.get("server", ProxyServer.class);
    }

    @Override
    public void onEnable() {
        this.registerCommands();
        this.registerListeners();
        this.loadSchedulers();
    }

    @Override
    public void onDisable() {

    }

    private void registerCommands() {
        new CloudCommand(this);
    }

    private void registerListeners() {
        new LoginListener(this);
        new PlayerDisconnectListener(this);
        new PostLoginListener(this);
        new ProxyPingListener(this);
        new ServerConnectedListener(this);
        new ServerSwitchListener(this);
    }

    private void initCloud() {
        CloudAPI.setInstance(new CloudAPIImpl());

        CloudAPI.getInstance().getPacketRegistry()
                .registerPacket(new PacketPlayInClientConnect())
                .registerPacket(new PacketPlayOutConnectionSuccess())
                .registerPacket(new PacketPlayOutRegisterServer())
                .registerPacket(new PacketPlayOutUnregisterServer())
                .registerPacket(new PacketPlayInUpdateServerInfo())
                .registerPacket(new PacketPlayInRequestServer())
                .registerPacket(new PacketPlayInDeleteServer())
                .registerPacket(new PacketPlayInAPIRequest())
                .registerPacket(new PacketPlayOutAPIResponse())
                .registerPacket(new PacketPlayOutUpdateMaintenance())
                .registerPacket(new PacketPlayInCloudPlayerConnected())
                .registerPacket(new PacketPlayInCloudPlayerDisconnected())
                .registerPacket(new PacketPlayInCloudPlayerUpdate())
                .registerPacket(new PacketPlayOutUpdateTablist())
                .registerPacket(new PacketPlayOutReloadProxy())
                .registerPacket(new PacketPlayInStartServer());

        CloudAPI.getInstance().getPacketRegistry()
                .registerListener(new PacketPlayOutUpdateTablistListener(this))
                .registerListener(new PacketPlayOutRegisterServerListener(this))
                .registerListener(new PacketPlayOutUnregisterServerListener(this))
                .registerListener(new PacketPlayOutReloadProxyListener(this));

    }

    public void loadConfig() {
        CoreAPI.getInstance().getMaintenanceKickMessage((value, throwable)
                -> this.maintenanceKickMessage = ChatColor.translateAlternateColorCodes('&', value));
        CoreAPI.getInstance().getTablist((value, throwable) -> this.tablist = value);
        CoreAPI.getInstance().getWhiteList((value, throwable) -> this.whiteList = value);
        CoreAPI.getInstance().isMaintenance((value, throwable) -> this.maintenance = value);
        CoreAPI.getInstance().getMaxPlayers((value, throwable) -> this.maxPlayers = value);
        CoreAPI.getInstance().getPlayerInfo((value, throwable) -> this.playerInfo = value);
        CoreAPI.getInstance().getMessages((value, throwable) ->
                value.forEach(message -> CloudAPI.getInstance().getMessageRegistry().registerMessage(message)));

        this.motdList.clear();
        CoreAPI.getInstance().getMotdList((value, throwable) -> value.forEach((motd) -> {
            if (motd.getName() != null && !motd.getName().trim().isEmpty() && motd.getName().equalsIgnoreCase("maintenance"))
                this.maintenanceMotd = motd;
            else
                this.motdList.add(motd);
        }));

        CoreAPI.getInstance().getTemplates((value, throwable) ->
                value.forEach((template) -> CloudAPI.getInstance().getTemplateRegistry().registerTemplate(template)));
    }

    private void loadSchedulers() {
        if (this.onlinePlayersTask != null)
            this.onlinePlayersTask.cancel();

        this.onlinePlayersTask = this.getProxy().getScheduler().schedule(this, () ->
                CoreAPI.getInstance().getPlayerCount((value, throwable) -> {
            if (throwable != null)
                return;
            if (this.onlinePlayers != value) {
                this.onlinePlayers = value;

                getProxy().getPlayers().forEach((player) -> setTabList(player, new LinkedHashMap<>()));
            }
        }), 0, 1500, TimeUnit.MILLISECONDS);
    }

    public void setTabList(ProxiedPlayer player, Map<String, Object> values) {
        values.put("name", player.getName());
        values.put("server", player.getServer() == null ? "NONE" : player.getServer().getInfo().getName());
        values.put("onlinePlayers", onlinePlayers);
        values.put("maxPlayers", maxPlayers);

        final String header = replaceTab(this.tablist.getHeader(), values);
        final String footer = replaceTab(this.tablist.getFooter(), values);
        player.setTabHeader(TextComponent.fromLegacyText(header), TextComponent.fromLegacyText(footer));
    }

    private String replaceTab(String tab, Map<String, Object> values) {
        String newTab = tab;
        for (Map.Entry<String, Object> valueMap : values.entrySet()) {
            String key = valueMap.getKey();
            Object value = valueMap.getValue();

            newTab = newTab.replaceAll("%" + key + "%", String.valueOf(value));
        }
        return ChatColor.translateAlternateColorCodes('&', newTab);
    }

    public BaseComponent[] getMessage(String message) {
        return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', CoreAPI.getInstance().getPrefix()) + message);
    }

    public void updateInfo() {
        CoreAPI.getInstance().sendPacketSync(new PacketPlayInUpdateServerInfo(ProxyServer.class,
                this.proxyServer));
    }

    public void updatePlayerCount(boolean disconnect) {
        final int size = this.getProxy().getOnlineCount();

        this.proxyServer.setOnlineCount((disconnect ? (size - 1) : size));
        final File file = new File("configs/config.json");
        final AbstractDocument document = CloudAPI.getInstance().document().load(file);

        document.append("server", this.proxyServer);
        document.save(file);
        this.updateInfo();
    }
}
