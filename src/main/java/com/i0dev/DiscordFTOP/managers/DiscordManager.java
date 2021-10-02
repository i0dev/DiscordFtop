package com.i0dev.DiscordFTOP.managers;

import com.i0dev.DiscordFTOP.Heart;
import com.i0dev.DiscordFTOP.commands.DCmdFtop;
import com.i0dev.DiscordFTOP.config.GeneralConfig;
import com.i0dev.DiscordFTOP.templates.AbstractManager;
import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.awt.*;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DiscordManager extends AbstractManager {
    public DiscordManager(Heart heart) {
        super(heart);
    }

    public void sendFTopUpdate() {
        GeneralConfig cnf = heart.getConfig(GeneralConfig.class);
        TextChannel channel = getJda().getTextChannelById(cnf.fTopChannel);
        if (channel == null) {
            Bukkit.getLogger().severe("Failed to find the factions top channel. Please make sure it is properly entered in config, then run /discordFTOP reload");
            return;
        }

        channel.sendMessageEmbeds(getFtopEmbed()).complete().crosspost();

    }


    public MessageEmbed getFtopEmbed() {
        GeneralConfig cnf = heart.getConfig(GeneralConfig.class);

        String desc = "Error getting Factions Top Placements.";
        if (heart.isUsingMCoreFactions()) {
            com.massivecraft.massivecore.collections.MassiveMapDef<String, com.massivecraft.factions.entity.FactionValue> data;
            if (com.massivecraft.factions.task.TaskFactionTopCalculate.get().isRunning()) {
                data = (new com.massivecraft.massivecore.collections.MassiveMapDef<>(com.massivecraft.factions.entity.FactionTopData.get().getBackupFactionValues()));
            } else {
                data = (new com.massivecraft.massivecore.collections.MassiveMapDef<>(com.massivecraft.factions.entity.FactionTopData.get().getFactionValues()));
            }
            int iterator = 1;
            StringBuilder content = new StringBuilder();
            for (String factionID : data.keySet()) {
                if (iterator >= cnf.getTopFactionCount() + 1) break;
                com.massivecraft.factions.entity.Faction faction = com.massivecraft.factions.entity.Faction.get(factionID);
                if (faction == null) continue;
                if (data.get(factionID).getTotalSpawnerValue() <= 0) continue;
                long value = data.get(factionID).getTotalSpawnerValue();
                Long oldTotal = com.massivecraft.factions.entity.FactionTopData.get().getNearestTotal(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24L), faction);
                com.massivecraft.factions.entity.FactionValue factionValue = data.get(faction.getId());
                String dailyPercentageChange = "[0.0%]";
                double dailyValueChange;
                if (oldTotal != null) {
                    dailyValueChange = factionValue.getTotalSpawnerValue() - oldTotal;
                    double percent = Math.round(dailyValueChange / ((oldTotal == 0.0) ? 1.0 : oldTotal) * 100.0 * 10.0) / 10.0;
                    double clampedPercent = Math.max(-9999.0, Math.min(percent, 9999.0));
                    String StringclampedPercent = String.valueOf(clampedPercent).replace(".0", "");
                    if (dailyValueChange > 0) {
                        StringclampedPercent = "+" + StringclampedPercent;
                    }
                    dailyPercentageChange = com.massivecraft.massivecore.util.Txt.parse("[" + ((clampedPercent < percent) ? ">" : ((clampedPercent < percent) ? "<" : "")) + StringclampedPercent + "%]");
                }
                String dailyPercentage = dailyPercentageChange;
                content.append(cnf.getPlacementFormat()
                        .replace("{faction}", faction.getName())
                        .replace("{total}", NumberFormat.getIntegerInstance().format(value))
                        .replace("{change}", dailyPercentage)
                        .replace("{place}", iterator + "")
                );
                content.append("\n");
                iterator++;
            }
            if (iterator == 1) desc = "No F-Top factions to display.";
            else desc = content.toString();
        }


        EmbedBuilder builder = new EmbedBuilder()
                .setAuthor(cnf.getEmbedTitle(), null, cnf.getEmbedIcon())
                .setColor(Color.decode(cnf.getEmbedColor()))
                .setDescription(desc);

        return builder.build();
    }

    @Override
    public void deinitialize() {
        if (task != null) task.cancel();
        task = null;
        if (jda != null) {
            jda.removeEventListener(new DCmdFtop(heart));
            jda.shutdownNow();
        }
        jda = null;
    }

    @SneakyThrows
    @Override
    public void initialize() {
        if (createJDA() == null) return;
        GeneralConfig cnf = heart.getConfig(GeneralConfig.class);
        String activity = cnf.getBotActivity();
        switch (cnf.getBotActivityState().toLowerCase()) {
            case "watching":
                getJda().getPresence().setActivity(Activity.watching(activity));
                break;
            case "listening":
                getJda().getPresence().setActivity(Activity.listening(activity));
                break;
            case "playing":
                getJda().getPresence().setActivity(Activity.playing(activity));
                break;
        }
        jda.addEventListener(new DCmdFtop(heart));
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(getHeart(), taskSendFTop, 45L * 20L, 45L * 20L);
    }

    public BukkitTask task;
    static long lastSentFTopTime = 0;

    public Runnable taskSendFTop = () -> {
        GeneralConfig cnf = heart.getConfig(GeneralConfig.class);
        if (!cnf.isAutoFTopEnabled()) return;
        List<String> minutes = cnf.getAutoFTopClockEndingSendTimes();
        ZonedDateTime time = ZonedDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.of("America/New_York"));
        if (minutes.contains(time.getMinute() + "") && System.currentTimeMillis() > lastSentFTopTime + (120 * 1000)) {
            sendFTopUpdate();
            lastSentFTopTime = System.currentTimeMillis();
        }
    };

    @Getter
    public static JDA jda = null;

    @SneakyThrows
    public JDA createJDA() {
        try {
            jda = JDABuilder.create(heart.getConfig(GeneralConfig.class).botToken, GatewayIntent.GUILD_MESSAGES)
                    .setStatus(OnlineStatus.fromKey(heart.getConfig(GeneralConfig.class).botActivityType))
                    .setContextEnabled(true)
                    .build()
                    .awaitReady();

        } catch (Exception ignored) {
            Bukkit.getLogger().severe("Could not load DiscordFTOP. Please make sure the token is correct in config, then force reload the plugin.");
            heart.onDisable();
        }
        return jda;
    }

}
