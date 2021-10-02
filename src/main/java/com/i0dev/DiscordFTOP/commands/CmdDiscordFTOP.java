package com.i0dev.DiscordFTOP.commands;

import com.i0dev.DiscordFTOP.Heart;
import com.i0dev.DiscordFTOP.config.GeneralConfig;
import com.i0dev.DiscordFTOP.config.MessageConfig;
import com.i0dev.DiscordFTOP.managers.MessageManager;
import com.i0dev.DiscordFTOP.templates.AbstractCommand;
import com.i0dev.DiscordFTOP.utility.ConfigUtil;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CmdDiscordFTOP extends AbstractCommand {


    MessageConfig msg;
    MessageManager msgManager;

    public CmdDiscordFTOP(Heart heart, String command) {
        super(heart, command);
    }

    @Override
    public void initialize() {
        msgManager = getHeart().getManager(MessageManager.class);
        msg = getHeart().getConfig(MessageConfig.class);
    }

    @Override
    public void deinitialize() {
        msgManager = null;
        msg = null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            msgManager.msg(sender, msg.getReloadUsage());
            msgManager.msg(sender, msg.getForceUsage());
            return;
        }
            GeneralConfig cnf = heart.getConfig(GeneralConfig.class);
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("discordftop.reload")) {
                msgManager.msg(sender, msg.getNoPermission());
                return;
            }
            getHeart().reload();
            msgManager.msg(sender, msg.getReloadedConfig());
            return;
        }

        if (args[0].equalsIgnoreCase("force")) {
            if (!sender.hasPermission("discordftop.force")) {
                msgManager.msg(sender, msg.getNoPermission());
                return;
            }
            heart.dManager().sendFTopUpdate();
            msgManager.msg(sender, msg.getForcedDiscordSend());
        }

        if (args[0].equalsIgnoreCase("toggle")) {
            if (!sender.hasPermission("discordftop.toggle")) {
                msgManager.msg(sender, msg.getNoPermission());
                return;
            }

            if (cnf.autoFTopEnabled) cnf.setAutoFTopEnabled(false);
            else cnf.setAutoFTopEnabled(true);

            ConfigUtil.save(cnf, cnf.path);
            msgManager.msg(sender, msg.getToggleAutoFTop(), new MessageManager.Pair<>("{status}", cnf.autoFTopEnabled ? "&a&lEnabled" : "&c&lDisabled"));
        }
    }

    List<String> blank = new ArrayList<>();

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) return Arrays.asList("reload", "force", "toggle");
        return blank;
    }
}
