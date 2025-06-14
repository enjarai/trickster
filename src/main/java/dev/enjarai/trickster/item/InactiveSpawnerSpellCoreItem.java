package dev.enjarai.trickster.item;

import dev.enjarai.trickster.block.ModBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;

public class InactiveSpawnerSpellCoreItem extends SpellCoreItem {
    @Override
    public int getExecutionLimit(ServerWorld world, Vec3d pos, int originalExecutionLimit) {
        return 0;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        var world = context.getWorld();
        var pos = context.getBlockPos();
        var player = context.getPlayer();
        if (world.getBlockState(pos).isOf(Blocks.SPAWNER) && player != null) {
            world.setBlockState(pos, ModBlocks.INERT_SPAWNER.getDefaultState());
            player.setStackInHand(context.getHand(), context.getStack().withItem(ModItems.SPAWNER_SPELL_CORE));
            return ActionResult.SUCCESS;
        }

        return super.useOnBlock(context);
    }
}
