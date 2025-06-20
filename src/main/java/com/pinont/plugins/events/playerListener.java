package com.pinont.plugins.events;

import com.pinont.lib.api.annotation.AutoRegister;
import com.pinont.lib.api.utils.Common;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import static com.pinont.lib.api.utils.Common.plugin;
import static org.bukkit.Bukkit.getServer;

@AutoRegister
public class playerListener implements Listener {

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
        firework.setItem(new ItemStack(Material.DIAMOND, 64));
        event.setJoinMessage(null);
        player.removeMetadata("dash_cooldown", plugin);
        for (Player onlinePlayer : getServer().getOnlinePlayers()) {
            onlinePlayer.sendMessage(new Common().colorize("<gradient:#5e4fa2:#f79459> " + player.getName() + "</gradient> has joined the game!"));
        }
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.isSprinting() && player.isSneaking() && !player.isOnGround() && !player.isFlying() && !player.isFrozen() && !player.isGliding() && !player.isSwimming()) {
            if (player.hasMetadata("dash_cooldown") && System.currentTimeMillis() - player.getMetadata("dash_cooldown").getFirst().asLong() > 750) {
                player.removeMetadata("dash_cooldown", plugin);
            } else if (player.hasMetadata("dash_cooldown")) {
                return;
            }
            player.playSound(player.getLocation(), Sound.BLOCK_BAMBOO_STEP, 1.0f, 1.0f);
            player.setVelocity(player.getLocation().getDirection().multiply(2));
            player.setSneaking(false);
            player.setMetadata("dash_cooldown", new FixedMetadataValue(plugin, System.currentTimeMillis())); // 5 seconds cooldown
        }
    }

    @EventHandler
    public void playerDead(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.setShowDeathMessages(false);
        for (Player onlinePlayer : getServer().getOnlinePlayers()) {
            onlinePlayer.sendMessage(new Common().colorize("<gradient:#f79459:#5e4fa2> " + player.getName() + "</gradient> has died!"));
        }
    }

}
