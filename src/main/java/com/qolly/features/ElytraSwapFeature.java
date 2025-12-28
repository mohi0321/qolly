package com.qolly.features;

import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class ElytraSwapFeature extends Feature {
    public ElytraSwapFeature() {
        super("Elytra Swap");
    }

    @Override
    public void onKeyPressed() {
        if (mc.player == null)
            return;

        // Check chest slot (index 38 in full inventory, or 6 in equipment)
        // Inventory mapping: 0-8 hotbar, 9-35 maintain, 36 boots, 37 leggings, 38
        // chestplate, 39 helmet.
        // Wait, slot IDs for generic container are different from player inventory
        // container.
        // For PlayerScreenHandler:
        // 0-4 Crafting
        // 5 Helmet, 6 Chestplate, 7 Leggings, 8 Boots
        // 9-35 Inventory
        // 36-44 Hotbar

        // Use client interaction manager to swap.

        int chestSlotId = 6;
        ItemStack chestItem = mc.player.getInventory().getArmorStack(2); // 0 feet, 1 legs, 2 chest, 3 head in
                                                                         // getArmorStack

        // Find swap target
        boolean hasElytraOnCheck = chestItem.isOf(Items.ELYTRA);

        int targetSlot = -1;
        for (int i = 9; i < 45; i++) { // Include hotbar
            ItemStack stack = mc.player.playerScreenHandler.getSlot(i).getStack();
            if (hasElytraOnCheck) {
                // Looking for chestplate
                // Simple logic: IS it a chestplate?
                // Checking if it fits in chest slot is better, but simple check is okay.
                if (!stack.isEmpty() && !stack.isOf(Items.ELYTRA)
                        && EquipmentSlot.CHEST == net.minecraft.entity.LivingEntity.getPreferredEquipmentSlot(stack)) {
                    targetSlot = i;
                    break;
                }
            } else {
                // Looking for elytra
                if (stack.isOf(Items.ELYTRA)) {
                    targetSlot = i;
                    break;
                }
            }
        }

        if (targetSlot != -1) {
            // Swap action: Pick up item from target slot, put in chest slot (swapping)

            // pickup from target
            // click chest slot
            // put back

            // Actually, we can just click the target slot with SHIFT if the chest slot is
            // empty?
            // Or pick up target slot and click chest slot.

            mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, targetSlot, 0, SlotActionType.PICKUP,
                    mc.player);
            mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, chestSlotId, 0, SlotActionType.PICKUP,
                    mc.player);
            mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, targetSlot, 0, SlotActionType.PICKUP,
                    mc.player);
        }
    }
}
