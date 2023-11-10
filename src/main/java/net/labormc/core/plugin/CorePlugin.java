package net.labormc.core.plugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import net.labormc.cloud.impl.CloudAPIImpl;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.document.AbstractDocument;
import net.labormc.cloudapi.network.client.types.MinecraftServer;
import net.labormc.cloudapi.network.protocol.packets.api.PacketPlayInAPIRequest;
import net.labormc.cloudapi.network.protocol.packets.api.PacketPlayOutAPIResponse;
import net.labormc.cloudapi.network.protocol.packets.channel.PacketPlayInClientConnect;
import net.labormc.cloudapi.network.protocol.packets.channel.PacketPlayOutConnectionSuccess;
import net.labormc.cloudapi.network.protocol.packets.server.PacketPlayInDeleteServer;
import net.labormc.cloudapi.network.protocol.packets.server.PacketPlayInStartServer;
import net.labormc.cloudapi.network.protocol.packets.server.PacketPlayInUpdateServerInfo;
import net.labormc.cloudapi.network.protocol.packets.server.PacketPlayOutUpdateSign;
import net.labormc.cloudapi.server.ServerConfig;
import net.labormc.core.CoreAPI;
import net.labormc.core.network.CoreNetworkImpl;
import net.labormc.core.plugin.listener.*;
import net.labormc.core.plugin.sign.SignManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCore
 * Author  : Ryixz (Dominik Auer)
 */
@Getter
public class CorePlugin extends JavaPlugin {

    private MinecraftServer minecraftServer;
    private SignManager signManager;

    @Override
    public void onLoad() {
        this.initCloud();
        new CoreAPI();

        final File configFile = new File("configs/config.json");
        final AbstractDocument document = CloudAPI.getInstance().document().load(configFile);
        CoreAPI.getInstance().setServerConfig(document.get("cloud", ServerConfig.class));
        this.minecraftServer = document.get("server", MinecraftServer.class);

        this.initNetwork();

        CoreAPI.getInstance().getTemplates((value, throwable) ->
                value.forEach(template -> CloudAPI.getInstance().getTemplateRegistry().registerTemplate(template)));

        this.signManager = new SignManager(this);
        this.signManager.load();
    }

    @Override
    public void onEnable() {
        this.registerCommands();
        this.registerListener();
        this.loadSchedulers();
    }

    @Override
    public void onDisable() {

    }

    private void registerCommands() {

    }

    private void registerListener() {
        new PlayerInteractListener(this);
        new PlayerJoinListener(this);
        new PlayerQuitListener(this);
    }

    private void initCloud() {
        CloudAPI.setInstance(new CloudAPIImpl());

        CloudAPI.getInstance().getPacketRegistry()
                .registerPacket(new PacketPlayInClientConnect())
                .registerPacket(new PacketPlayOutConnectionSuccess())
                .registerPacket(new PacketPlayInUpdateServerInfo())
                .registerPacket(new PacketPlayInAPIRequest())
                .registerPacket(new PacketPlayOutUpdateSign())
                .registerPacket(new PacketPlayOutAPIResponse())
                .registerPacket(new PacketPlayInDeleteServer())
                .registerPacket(new PacketPlayInStartServer());

        CloudAPI.getInstance().getPacketRegistry()
                .registerListener(new PacketPlayInDeleteServerListener(this))
                .registerListener(new PacketPlayInStartServerListener(this))
                .registerListener(new PacketPlayInUpdateServerListener(this))
                .registerListener(new PacketPlayOutUpdateSignListener(this));
    }

    private void initNetwork() {
        final ServerConfig serverConfig = CoreAPI.getInstance().getServerConfig();
        new Thread(new CoreNetworkImpl(serverConfig.getHostName(), serverConfig.getPort(), this.minecraftServer,
                MinecraftServer.class, () ->
                CoreAPI.getInstance().sendPacketAsync(new PacketPlayInStartServer(MinecraftServer.class,
                        this.minecraftServer))), "network-Thread").start();
    }

    private void loadSchedulers() {
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, () ->
                CoreAPI.getInstance().getGameServers((result, throwable) -> result.forEach((object) -> {
            if (this.signManager.getGameServerList().stream()
                    .noneMatch((s) -> s.getName().equalsIgnoreCase(object.getName())))
                this.signManager.getGameServerList().add(object);
        })), 30, 30);
    }

    public void updateInfo() {
        CoreAPI.getInstance().sendPacketSync(new PacketPlayInUpdateServerInfo(MinecraftServer.class, this.minecraftServer));
    }

    public void updatePlayerCount(boolean disconnected) {
        final int size = this.getServer().getOnlinePlayers().size();

        this.getMinecraftServer().setOnlineCount((disconnected ? (size - 1) : size));
        final File file = new File("configs/config.json");
        final AbstractDocument document = CloudAPI.getInstance().document().load(file);

        document.append("server", this.minecraftServer);
        document.save(file);
        this.updateInfo();
    }

    public void sendToServer(Player player, String target) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("Connect");
        output.writeUTF(target);
        player.sendPluginMessage(this, "BungeeCord", output.toByteArray());
    }
}
