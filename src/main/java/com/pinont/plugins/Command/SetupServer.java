package com.pinont.plugins.Command;

import com.pinont.lib.api.annotation.AutoRegister;
import com.pinont.lib.api.command.SimpleCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.*;

@AutoRegister
public class SetupServer implements SimpleCommand {
    @Override
    public String getName() {
        return "setupserver:setup:ss";
    }

    @Override
    public String description() {
        return "Setup the server with default configurations and plugins.";
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] strings) {
        for (World world : Bukkit.getWorlds()) {
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            world.setGameRule(GameRule.DO_TILE_DROPS, false);
            world.setGameRule(GameRule.DO_ENTITY_DROPS, false);
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, true);
            world.setGameRule(GameRule.REDUCED_DEBUG_INFO, true);
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            world.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, true);
            world.setGameRule(GameRule.KEEP_INVENTORY, true);
            world.setDifficulty(Difficulty.NORMAL);
        }
        commandSourceStack.getSender().sendMessage(ChatColor.GOLD + "Server Setup Successful");
    }
}
