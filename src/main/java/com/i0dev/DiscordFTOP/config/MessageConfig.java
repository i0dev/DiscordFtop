package com.i0dev.DiscordFTOP.config;

import com.i0dev.DiscordFTOP.Heart;
import com.i0dev.DiscordFTOP.templates.AbstractConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MessageConfig extends AbstractConfiguration {

    String reloadUsage = "&cUsage: &7/discordFtop reload";
    String forceUsage = "&cUsage: &7/discordFtop force";

    //DiscordFTOP Cmd
    String ForcedDiscordSend = "&7You have forced ftop to send in discord!";
    String toggleAutoFTop = "&7You have toggled automatic placements to: {status}";
    String toggleAutoFTopDiscord = "You have toggled automatic placements to: {status}";

    String reloadedConfig = "&7You have&a reloaded&7 the configuration.";
    String noPermission = "&cYou don not have permission to run that command.";

    public MessageConfig(Heart heart, String path) {
        this.path = path;
        this.heart = heart;
    }
}
