package com.i0dev.DiscordFTOP.templates;


import com.i0dev.DiscordFTOP.Heart;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Getter
@Setter
public class AbstractManager extends ListenerAdapter{

    /*
    Credit given to EmberCM
    Discord: Ember#1404
    GitHub: https://github.com/EmberCM
     */

    public Heart heart;
    public boolean loaded = false;

    public void initialize() {

    }

    public void deinitialize() {

    }

    public AbstractManager(Heart heart) {
        this.heart = heart;
    }
}
