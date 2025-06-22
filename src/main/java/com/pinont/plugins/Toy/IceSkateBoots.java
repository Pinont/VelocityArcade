package com.pinont.plugins.Toy;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.pinont.lib.api.annotation.AutoRegister;
import com.pinont.lib.api.enums.AttributeType;
import com.pinont.lib.api.enums.PlayerInventorySlotType;
import com.pinont.lib.api.items.CustomItem;
import com.pinont.lib.api.items.ItemCreator;
import com.pinont.lib.api.items.ItemInteraction;
import com.pinont.lib.api.utils.Common;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;

@AutoRegister
public class IceSkateBoots extends CustomItem implements Listener {
    @Override
    public ItemInteraction getInteraction() {
        return null;
    }

    @Override
    public ItemCreator register() {
        return new ItemCreator(Material.DIAMOND_BOOTS).addAttribute(AttributeType.MOVEMENT_SPEED, 2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET).setName("Ice Skate Boots");
    }

    @EventHandler
    public void playerJump(PlayerJumpEvent event) {
        Player player = event.getPlayer();
        if (Common.getItemInSlot(PlayerInventorySlotType.ARMOR_FEET, player).getType() != Material.AIR) {
            player.setVelocity(player.getLocation().getDirection().multiply(0.5)); // Boost the player forward when they jump while wearing ice skate boots
        }
    }
}
