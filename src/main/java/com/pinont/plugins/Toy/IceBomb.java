package com.pinont.plugins.Toy;

import com.pinont.lib.api.annotation.AutoRegister;
import com.pinont.lib.api.entity.EntityCreator;
import com.pinont.lib.api.items.CustomItem;
import com.pinont.lib.api.items.ItemCreator;
import com.pinont.lib.api.items.ItemInteraction;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

import static com.pinont.lib.api.utils.Common.plugin;

@AutoRegister
public class IceBomb implements CustomItem, Listener {
    @Override
    public ItemInteraction getInteraction() {
        return new ItemInteraction() {
            @Override
            public int removeItemAmountOnExecute() {
                return 1;
            }

            @Override
            public String getName() {
                return "ICE BOMB";
            }

            @Override
            public Set<Action> getAction() {
                return Set.of(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK);
            }

            @Override
            public void execute(Player player) {
                if (player.getGameMode() != GameMode.CREATIVE) player.setCooldown(getItem(), 160); // 8 seconds cooldown
                Entity entity = new EntityCreator(EntityType.FALLING_BLOCK).setVelocity(player.getLocation().getDirection().multiply(2)).spawn(player.getEyeLocation());
                FallingBlock fallingBlock = (FallingBlock) entity;
                fallingBlock.setBlockData(Material.POWDER_SNOW.createBlockData());
                fallingBlock.setHurtEntities(true);
                fallingBlock.setDropItem(false);
                fallingBlock.setGravity(true);
                fallingBlock.setVelocity(player.getLocation().getDirection().multiply(2));
            }
        };
    }

    @Override
    public ItemCreator register() {
        return new ItemCreator(Material.SNOWBALL).setModelData(1).setName("Ice Bomb")
                .addEnchant(Enchantment.VANISHING_CURSE,1,true)
                .addItemFlag(ItemFlag.HIDE_ENCHANTS);
    }

    @EventHandler
    public void BlockLand(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof FallingBlock fallingBlock) {
            fallingBlock.setBlockData(Material.AIR.createBlockData());
            fallingBlock.getNearbyEntities(2,0,2).forEach(entity -> {
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    for (int i = 0; i < 27; i++) {
                        int x = i / 9 - 1;
                        int y = (i % 9) / 3;
                        int z = i % 3 - 1;
                        if (fallingBlock.getLocation().add(x,y,z).getBlock().getType() != Material.AIR) continue;
                        fallingBlock.getWorld().spawnParticle(Particle.FIREWORK, fallingBlock.getLocation().add(x, y, z), 1, 0, 0, 0, 0.1);
                        player.getWorld().setBlockData(fallingBlock.getLocation().add(x, y, z), Material.POWDER_SNOW.createBlockData());
                        player.setVelocity(player.getVelocity().multiply(0));
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                // change the block back to air after 1 second
                                Location loc = fallingBlock.getLocation().add(x, y, z);
                                if (loc.getBlock().getType() == Material.POWDER_SNOW) {
                                    loc.getBlock().setType(Material.AIR);
                                }
                            }
                        }.runTaskLater(plugin, 80L); // freeze duration of 4 seconds (80 ticks)
                        player.setFreezeTicks(player.getMaxFreezeTicks() + 1);
                    }
                    player.setFreezeTicks(player.getMaxFreezeTicks() + 160);
                    player.playSound(fallingBlock.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
                    player.teleport(fallingBlock.getLocation());
                }
            });
        }
    }
}
