package com.i0dev.DiscordFTOP.config;

import com.i0dev.DiscordFTOP.Heart;
import com.i0dev.DiscordFTOP.templates.AbstractConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.dv8tion.jda.api.Permission;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class GeneralConfig extends AbstractConfiguration {

    public String botToken = "";
    public String botActivity = "ftop on play.mcrivals.com";
    public String botActivityType = "dnd";
    public String botActivityState = "watching";
    public char botPrefix = '.';
    public long fTopChannel = 793715082881662986L;
    public boolean autoFTopEnabled = true;
    public String discordPermissionRequiredToToggleFTOP = "ADMINISTRATOR";

    List<String> autoFTopClockEndingSendTimes = Arrays.asList(
            "00",
            "30"
    );

    long topFactionCount = 10;

    String embedTitle = "Factions Top Placements";
    String embedColor = "#2f3136";
    String embedIcon = "https://images-ext-2.discordapp.net/external/QsWQeCHhOtV3yg-AZPoLQ25DXNtYpsONzSromk3VTZo/https/cdn.discordapp.com/avatars/720468625999527958/bde888078fb4df451e314ea8c7a34de1.png";
    String placementFormat = "{place}. **{faction}** » ${total} `{change}`";

    public GeneralConfig(Heart heart, String path) {
        this.path = path;
        this.heart = heart;
    }

}
