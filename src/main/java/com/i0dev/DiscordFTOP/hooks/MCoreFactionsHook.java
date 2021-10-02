package com.i0dev.DiscordFTOP.hooks;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class MCoreFactionsHook {

    public static boolean isWilderness(Location location) {
        return BoardColl.get().getFactionAt(PS.valueOf(location)).isNone();
    }

    public static boolean isSystemFaction(Location location) {
        if (isWilderness(location)) return false;
        return BoardColl.get().getFactionAt(PS.valueOf(location)).isSystemFaction();
    }

    public static boolean isOwn(Location location, Player player) {
        return BoardColl.get().getFactionAt(PS.valueOf(location)).getId().equals(MPlayer.get(player).getFaction().getId());
    }

}
