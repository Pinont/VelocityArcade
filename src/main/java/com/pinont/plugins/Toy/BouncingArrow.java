package com.pinont.plugins.Toy;

import com.pinont.lib.api.annotation.AutoRegister;
import com.pinont.lib.api.items.CustomItem;
import com.pinont.lib.api.items.ItemCreator;
import com.pinont.lib.api.items.ItemInteraction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

import static com.pinont.lib.api.utils.Common.plugin;

// From ChasingClub Space ball project, modified for a bouncing slime bow.
// ref: https://github.com/ChasingClub/Core/blob/main/Core-Spaceball/src/main/java/cc/Machanic/BouncingArrow.java

@AutoRegister
public class BouncingArrow implements CustomItem, Listener {
    @Override
    public ItemInteraction getInteraction() {
        return null;
    }

    @Override
    public ItemCreator register() {
        return new ItemCreator(Material.ARROW).addEnchant(Enchantment.MULTISHOT, 3, true)
                .setName("<green>Bouncing Arrow")
                .addLore("<yellow>This arrow can bounce off blocks when shoot.");
    }

    public static final double MIN_MAGNITUDE_THRESHOLD = 0.6;
    public static final int MAX_BOUNCE_COUNT = 10;

    @EventHandler
    public void onEntityShootBowEvent(EntityShootBowEvent event) {

        LivingEntity entity = event.getEntity();
        Entity projectile = event.getProjectile();

        if (entity instanceof Player && projectile instanceof Arrow arrow) {
            if (!arrow.getItemStack().getItemMeta().hasEnchant(Enchantment.MULTISHOT)) return;
            final int bouncingCount = Math.max(1, Math.min(arrow.getItemStack().getEnchantmentLevel(Enchantment.MULTISHOT), MAX_BOUNCE_COUNT));
            projectile.setMetadata("bouncing", new FixedMetadataValue(plugin, bouncingCount));
            ItemStack bow = event.getBow();
            if (bow == null) return;
            if (bow.getItemMeta().hasEnchant(Enchantment.INFINITY)) {
                projectile.setMetadata("infinity", new FixedMetadataValue(plugin, true));
                arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
            }
        }

    }

    @EventHandler(priority= EventPriority.HIGH, ignoreCancelled=true)
    public void onProjectileHitEvent(ProjectileHitEvent event) {

        Projectile entity = event.getEntity();

        ProjectileSource shooter = event.getEntity().getShooter();

        if (event.getHitEntity() != null) return;

        if (shooter instanceof Player
                && entity instanceof Arrow arrow
                && entity.hasMetadata("bouncing")) {

            Vector arrowVector = entity.getVelocity();

            final double magnitude = Math.sqrt(
                    Math.pow(arrowVector.getX(), 2) +
                            Math.pow(arrowVector.getY(), 2) +
                            Math.pow(arrowVector.getZ(), 2));

            if (magnitude < MIN_MAGNITUDE_THRESHOLD) {
                return;
            }

            BlockFace blockFace = getBlockFace(event, entity, arrowVector);

            if (blockFace != null) {

                // Convert blockFace SELF to UP:
                if (blockFace == BlockFace.SELF) {
                    blockFace = BlockFace.UP;
                }

                Vector hitPlain = new Vector(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());

                double dotProduct = arrowVector.dot(hitPlain);
                Vector u = hitPlain.multiply(dotProduct).multiply(2.0);

                float speed = (float) magnitude;
                speed *= 0.6F;

                Arrow newArrow = entity.getWorld().spawnArrow(entity.getLocation(), arrowVector.subtract(u), speed, 12.0F);

                List<MetadataValue> metaDataValues = entity.getMetadata("bouncing");
                if (!metaDataValues.isEmpty()) {
                    int prevBouncingRate = metaDataValues.getFirst().asInt();
                    if (prevBouncingRate > 1) {
                        newArrow.setMetadata("bouncing", new FixedMetadataValue(plugin, prevBouncingRate - 1));
                    }
                }

                if (entity.hasMetadata("infinity")) {
                    newArrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
                    newArrow.setMetadata("infinity", new FixedMetadataValue(plugin, true));
                }

                newArrow.setShooter(shooter);
                newArrow.setFireTicks(entity.getFireTicks());
                newArrow.setItemStack(arrow.getItemStack().clone());

                entity.remove();

            }

        }
    }

    private static @Nullable BlockFace getBlockFace(ProjectileHitEvent event, Projectile entity, Vector arrowVector) {
        Location hitLoc = entity.getLocation();

        BlockIterator b = new BlockIterator(hitLoc.getWorld(),
                hitLoc.toVector(), arrowVector, 0, 3);

        Block blockBefore = event.getEntity().getLocation().getBlock();
        Block nextBlock = b.next();

        while (b.hasNext() && nextBlock.getType() == Material.AIR)
        {
            blockBefore = nextBlock;
            nextBlock = b.next();
        }

        return nextBlock.getFace(blockBefore);
    }
}
