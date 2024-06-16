package dev.enjarai.trickster.mixin;

import dev.enjarai.trickster.block.SpellCircleBlockEntity;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.world.SpellCircleEvent;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {
	@Shadow protected ServerWorld world;

	@Shadow @Final protected ServerPlayerEntity player;

	@Shadow protected abstract void onBlockBreakingAction(BlockPos pos, boolean success, int sequence, String reason);

	@Inject(
			method = "processBlockBreakingAction",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/server/network/ServerPlayerInteractionManager;tickCounter:I",
					ordinal = 0
			),
			cancellable = true
	)
	private void init(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight, int sequence, CallbackInfo info) {
		if (SpellCircleBlockEntity.fireAllNearby(world, pos, SpellCircleEvent.BREAK_BLOCK, List.of(
				VectorFragment.of(pos) // TODO held item
		))) {
			player.networkHandler.sendPacket(new BlockUpdateS2CPacket(pos, this.world.getBlockState(pos)));
			onBlockBreakingAction(pos, false, sequence, "[Trickster] cancelled by spell circle");
			info.cancel();
		}
	}
}