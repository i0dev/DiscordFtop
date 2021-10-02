package com.i0dev.DiscordFTOP.commands;

import com.i0dev.DiscordFTOP.Heart;
import com.i0dev.DiscordFTOP.config.GeneralConfig;
import com.i0dev.DiscordFTOP.config.MessageConfig;
import com.i0dev.DiscordFTOP.managers.DiscordManager;
import com.i0dev.DiscordFTOP.templates.AbstractManager;
import com.i0dev.DiscordFTOP.utility.ConfigUtil;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DCmdFtop extends AbstractManager {

    public DCmdFtop(Heart heart) {
        super(heart);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        GeneralConfig cnf = heart.getConfig(GeneralConfig.class);
        if (e.getMessage().getContentRaw().equalsIgnoreCase(cnf.getBotPrefix() + "ftop"))
            e.getMessage().replyEmbeds(heart.getManager(DiscordManager.class).getFtopEmbed()).queue();
        else if (e.getMessage().getContentRaw().equalsIgnoreCase(cnf.getBotPrefix() + "ftopToggle")) {
            if (cnf.autoFTopEnabled) cnf.setAutoFTopEnabled(false);
            else cnf.setAutoFTopEnabled(true);
            ConfigUtil.save(cnf, cnf.path);
            e.getMessage().reply(heart.getConfig(MessageConfig.class).getToggleAutoFTopDiscord().replace("{status}", cnf.autoFTopEnabled ? "Enabled" : "Disabled")).queue();
        }
    }
}
