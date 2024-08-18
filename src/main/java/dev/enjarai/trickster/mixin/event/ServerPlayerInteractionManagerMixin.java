package dev.enjarai.trickster.mixin.event;

import dev.enjarai.trickster.spell.ItemTriggerProvider;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin implements ItemTriggerProvider {
	@Shadow protected ServerWorld world;

	@Shadow @Final protected ServerPlayerEntity player;

	@Inject(
			method = "finishMining",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/network/ServerPlayerInteractionManager;onBlockBreakingAction(Lnet/minecraft/util/math/BlockPos;ZILjava/lang/String;)V",
					ordinal = 1
			)
	)
	private void whyTheFuckDoesntMojangDoThis(BlockPos pos, int sequence, String reason, CallbackInfo ci) {
		// wait why does this not work...
		var blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null) {
			var packet = blockEntity.toUpdatePacket();
			if (packet != null) {
				player.networkHandler.sendPacket(packet);
			}
		}
	}

	@Inject(method = "finishMining", at = @At("RETURN"))
	private void triggerItemSpell(BlockPos pos, int sequence, String reason, CallbackInfo ci) {
		trickster$triggerMainHand(player, VectorFragment.of(pos));
	}
}