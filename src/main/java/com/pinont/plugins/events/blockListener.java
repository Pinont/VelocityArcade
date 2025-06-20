package com.pinont.plugins.events;

import com.pinont.lib.api.annotation.AutoRegister;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

@AutoRegister
public class blockListener implements Listener {

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.ICE) {
            event.setCancelled(true);
        }
    }

}
