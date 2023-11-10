package net.labormc.core;

import com.google.common.util.concurrent.MoreExecutors;
import lombok.Getter;
import lombok.Setter;
import net.labormc.cloud.impl.CloudAPIImpl;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.cloudplayer.CloudPlayer;
import net.labormc.cloudapi.message.Message;
import net.labormc.cloudapi.network.client.types.MinecraftServer;
import net.labormc.cloudapi.network.connection.Connection;
import net.labormc.cloudapi.network.protocol.IPacket;
import net.labormc.cloudapi.network.protocol.packets.api.APIRequestTypes;
import net.labormc.cloudapi.network.protocol.packets.api.PacketPlayInAPIRequest;
import net.labormc.cloudapi.network.protocol.packets.cloudplayer.PacketPlayInCloudPlayerUpdate;
import net.labormc.cloudapi.server.CloudServer;
import net.labormc.cloudapi.server.ServerConfig;
import net.labormc.cloudapi.server.entities.Motd;
import net.labormc.cloudapi.server.entities.Tablist;
import net.labormc.cloudapi.server.template.Template;
import net.labormc.cloudapi.sign.SignLayout;
import net.labormc.cloudapi.sign.SignLocation;
import net.labormc.core.network.listener.PacketPlayOutAPIResponseListener;
import net.labormc.core.network.listener.PacketPlayOutConnectionSuccessListener;
import net.labormc.core.result.APIResultCallback;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCore
 * Author  : Ryixz (Dominik Auer)
 */
@Getter
public class CoreAPI {

    @Getter
    private static CoreAPI instance;

    @Setter
    private String prefix;

    @Setter
    private UUID uniqueId;

    @Setter
    private ServerConfig serverConfig;

    private final Map<UUID, Object> map = new LinkedHashMap<>();

    private final ExecutorService pool = Executors.newFixedThreadPool(10);

    public CoreAPI() {
        CloudAPI.setInstance(new CloudAPIImpl());
        instance = this;
        this.prefix = "§8[§9§lLaborMC§8] &7";

        CloudAPI.getInstance().getPacketRegistry()
                .registerListener(new PacketPlayOutAPIResponseListener())
                .registerListener(new PacketPlayOutConnectionSuccessListener());
    }

    public void sendPacketSync(IPacket packet) {
        Connection connection = CloudAPI.getInstance().getConnectionRegistry().getConnection("CLOUD");
        if (connection == null)
            return;
        connection.getChannel().writeAndFlush(packet);
    }

    public void sendPacketAsync(IPacket packet) {
        MoreExecutors.directExecutor().execute(() -> CloudAPI.getInstance().getConnectionRegistry().getConnection("CLOUD")
                .getChannel().writeAndFlush(packet));
    }

    private <T> void get(APIRequestTypes type, APIResultCallback<T> callback) {
        this.get(type, new String[] {}, callback);
    }

    public <T> void get(APIRequestTypes type, String[] args, APIResultCallback<T> callback) {
        pool.execute(() -> {
            try {
                final UUID uniqueId = UUID.randomUUID();
                Throwable throwable = null;
                try {
                    System.out.println(uniqueId + " " + type + " " + Arrays.toString(args));

                    this.sendPacketSync(new PacketPlayInAPIRequest(uniqueId, type, args));
                    while (!this.map.containsKey(uniqueId)) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(CloudAPI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } catch (Exception ex) {
                    throwable = ex;
                    ex.printStackTrace();
                }
                callback.onResult((T) this.map.get(uniqueId), throwable);
            } catch (Exception e) {
                System.out.println(e);
            }
        });
    }

    public void getWhiteList(APIResultCallback<List<String>> callback) {
        this.get(APIRequestTypes.WHITELIST, callback);
    }

    public void getTablist(APIResultCallback<Tablist> callback) {
        this.get(APIRequestTypes.WHITELIST, callback);
    }

    public void getCloudPlayer(UUID uuid, APIResultCallback<CloudPlayer> callback) {
        this.get(APIRequestTypes.CLOUDPLAYER, new String[] { "UUID" }, callback);
    }

    public void getCloudPlayer(String name, APIResultCallback<CloudPlayer> callback) {
        this.get(APIRequestTypes.CLOUDPLAYER, new String[] { "NAME" }, callback);
    }

    public void updateCloudPlayer(CloudPlayer cloudPlayer) {
        this.sendPacketAsync(new PacketPlayInCloudPlayerUpdate(cloudPlayer));
    }

    public void getPlayerCount(APIResultCallback<Integer> callback) {
        this.get(APIRequestTypes.PLAYERCOUNT, callback);
    }

    public void getMaxPlayers(APIResultCallback<Integer> callback) {
        this.get(APIRequestTypes.MAXPLAYERS, callback);
    }

    public void getMotdList(APIResultCallback<List<Motd>> callback) {
        this.get(APIRequestTypes.MAXPLAYERS, callback);
    }

    public void getSignLocationList(APIResultCallback<List<SignLocation>> callback) {
        this.get(APIRequestTypes.SIGNLOCATION_LIST, callback);
    }

    public void getSignLayoutList(APIResultCallback<List<SignLayout>> callback) {
        this.get(APIRequestTypes.SIGNLAYOUT_LIST, callback);
    }

    public void getPlayerInfo(APIResultCallback<List<String>> callback) {
        this.get(APIRequestTypes.PLAYERINFO_LIST, callback);
    }

    public void updateMaintenance(boolean enable) {
        this.sendPacketAsync(new PacketPlayInAPIRequest(UUID.randomUUID(), APIRequestTypes.MAINTENANCE,
                new String[] { (enable ? "ENABLE" : "DISABLE") }));
    }

    public void updateMaintenance(boolean enable, String template) {
        this.sendPacketAsync(new PacketPlayInAPIRequest(UUID.randomUUID(), APIRequestTypes.MAINTENANCE,
                new String[] { (enable ? "ENABLE" : "DISABLE"), template }));
    }

    public void isMaintenance(APIResultCallback<Boolean> callback) {
        this.get(APIRequestTypes.MAINTENANCE, new String[] { "CURRENTSTATE" }, callback);
    }

    public void isMaintenance(String template, APIResultCallback<Boolean> callback) {
        this.get(APIRequestTypes.MAINTENANCE, new String[]{"CURRENTSTATE", template}, callback);
    }

    public void getMaintenanceKickMessage(APIResultCallback<String> callback) {
        this.get(APIRequestTypes.MAINTENANCE_KICK_MESSAGE, new String[]{}, callback);
    }

    public void updateServerInfo(MinecraftServer server) {
        this.sendPacketAsync(new PacketPlayInAPIRequest(UUID.randomUUID(), APIRequestTypes.SERVER_INFO,
                new String[] { "UPDATE", CloudAPI.getInstance().getGson().toJson(server) }));
    }

    public <T extends CloudServer> void getServer(String name, APIResultCallback<T> callback) {
        this.get(APIRequestTypes.SERVER_INFO, new String[] { "GET", name }, callback);
    }

    public <T extends CloudServer> void getServers(String templateName, APIResultCallback<T> callback) {
        this.get(APIRequestTypes.SERVER_INFO_BY_TEMPLATE, new String[] { templateName }, callback);
    }

    public void getGameServers(String gameName, APIResultCallback<MinecraftServer> callback) {
        this.get(APIRequestTypes.SERVER_INFO_BY_GAME, new String[] { gameName }, callback);
    }

    public void getGameServers(APIResultCallback<List<MinecraftServer>> callback) {
        this.get(APIRequestTypes.GAME_SERVER_INFO, new String[] {}, callback);
    }

    public void getTemplates(APIResultCallback<List<Template>> callback) {
        this.get(APIRequestTypes.TEMPLATE_LIST, new String[] {}, callback);
    }

    public void addMessage(Message message) {
        this.sendPacketAsync(new PacketPlayInAPIRequest(UUID.randomUUID(), APIRequestTypes.MESSAGE_LIST,
                new String[] { "ADD", CloudAPI.getInstance().getGson().toJson(message) }));
    }

    public void getMessages(APIResultCallback<List<Message>> callback) {
        this.get(APIRequestTypes.MESSAGE_LIST, new String[] {}, callback);
    }
}
