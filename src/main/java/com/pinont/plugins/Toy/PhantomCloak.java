package com.pinont.plugins.Toy;

import com.pinont.lib.api.items.CustomItem;
import com.pinont.lib.api.items.ItemCreator;
import com.pinont.lib.api.items.ItemInteraction;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;

public class PhantomCloak extends CustomItem {
    @Override
    public ItemInteraction getInteraction() {
        return new ItemInteraction() {
            @Override
            public String getName() {
                return "phantomCloak";
            }

            @Override
            public Set<Action> getAction() {
                return Set.of(Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK);
            }

            @Override
            public void execute(Player player) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 5, 1, false, true, false));
            }
        };
    }

    @Override
    public ItemCreator register() {
        return new ItemCreator(Material.PHANTOM_MEMBRANE)
                .setName("Phantom Cloak")
                .addLore("<bold><yellow>Left Click</yellow> <white>to turn invisible for 3 seconds");
    }
}
