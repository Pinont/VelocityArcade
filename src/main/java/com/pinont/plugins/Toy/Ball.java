package com.pinont.plugins.Toy;

import com.pinont.lib.api.annotation.AutoRegister;
import com.pinont.lib.api.items.CustomItem;
import com.pinont.lib.api.items.ItemCreator;
import com.pinont.lib.api.items.ItemInteraction;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemFlag;

import java.util.Random;
import java.util.Set;

@AutoRegister
public class Ball implements CustomItem, Listener {
    @Override
    public ItemCreator register() {
        Random random = new Random();
        Material[] colors = {Material.RED_WOOL, Material.LIME_WOOL, Material.BLUE_WOOL, Material.YELLOW_WOOL, Material.WHITE_WOOL};
        String[] name = {"Red Ball", "Green Ball", "Blue Ball", "Yellow Ball", "White Ball"};
        int randomIndex = random.nextInt(colors.length);
        return new ItemCreator(colors[randomIndex]).addEnchant(Enchantment.PROTECTION, 1, true).setName(name[randomIndex]).addItemFlag(ItemFlag.HIDE_ENCHANTS);
    }

    @Override
    public ItemInteraction getInteraction() {
        return new ItemInteraction() {
            @Override
            public int removeItemAmountOnExecute() {
                return 1;
            }

            @Override
            public String getName() {
                return "BALL";
            }

            @Override
            public Set<Action> getAction() {
                return Set.of(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK, Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK);
            }

            @Override
            public void execute(Player player) {
                Snowball snowball = player.launchProjectile(Snowball.class);
                snowball.setVelocity(player.getLocation().getDirection().multiply(2));
                snowball.setShooter(player);
            }
        };
    }

    @EventHandler
    public void ballHit(ProjectileHitEvent event) {
        if (event.getEntity().getShooter() instanceof Player && event.getHitEntity() instanceof Player player && event.getEntity() instanceof Snowball) {
            player.setVelocity(event.getEntity().getLocation().getDirection().multiply(2));
        }
    }

    @Override
    public String getName() {
        return "ball";
    }
}
