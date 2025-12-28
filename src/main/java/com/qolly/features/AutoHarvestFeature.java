package com.qolly.features;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import net.minecraft.util.hit.BlockHitResult;

public class AutoHarvestFeature extends Feature {
    private BlockPos targetPos;

    public AutoHarvestFeature() {
        super("Auto Harvest");
    }

    @Override
    public void onTick() {
        if (!isEnabled() || mc.player == null || mc.world == null)
            return;

        BlockPos playerPos = mc.player.getBlockPos();

        if (targetPos == null) {
            targetPos = findTarget(playerPos);
        }

        if (targetPos != null) {
            // Check if still valid
            if (!isValidTarget(targetPos)) {
                targetPos = null;
                mc.options.forwardKey.setPressed(false);
                return;
            }

            // Move towards
            double dist = Math.sqrt(mc.player.squaredDistanceTo(targetPos.toCenterPos()));

            if (dist > 3.0) { // Move range
                lookAt(targetPos);
                mc.options.forwardKey.setPressed(true);
            } else {
                mc.options.forwardKey.setPressed(false);
                lookAt(targetPos);

                // Break it
                if (mc.interactionManager != null) {
                    mc.interactionManager.attackBlock(targetPos, Direction.UP);
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
            }
        } else {
            mc.options.forwardKey.setPressed(false);
        }
    }

    private BlockPos findTarget(BlockPos start) {
        int radius = 10;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                BlockPos pos = start.add(x, 0, z); // Same height
                if (isValidTarget(pos)) {
                    return pos;
                }
            }
        }
        return null;
    }

    private boolean isValidTarget(BlockPos pos) {
        if (mc.world == null)
            return false;

        // Must be same height as player feet roughly (checked by finding loop)
        // Check block
        Block block = mc.world.getBlockState(pos).getBlock();
        boolean isCrop = block == Blocks.SUGAR_CANE || block == Blocks.CACTUS || block == Blocks.BAMBOO;

        if (!isCrop)
            return false;

        // Must be the 2nd block from bottom (so it has a plant below it)
        Block down = mc.world.getBlockState(pos.down()).getBlock();
        return down == block; // If block below is same type, it's at least 2nd block
    }

    private void lookAt(BlockPos pos) {
        if (mc.player == null)
            return;

        Vec3d targetCenter = pos.toCenterPos();
        Vec3d playerPos = mc.player.getEyePos();

        double dX = targetCenter.x - playerPos.x;
        double dY = targetCenter.y - playerPos.y;
        double dZ = targetCenter.z - playerPos.z;

        double dist = Math.sqrt(dX * dX + dZ * dZ);

        float yaw = (float) (Math.atan2(dZ, dX) * 180.0 / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(dY, dist) * 180.0 / Math.PI);

        mc.player.setYaw(yaw);
        mc.player.setPitch(pitch);
    }
}
