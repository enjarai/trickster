package dev.enjarai.trickster.mixin;

import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.world.SpellCircleEvent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {
	@Shadow protected ServerWorld world;

	@Shadow @Final protected ServerPlayerEntity player;

	@Inject(
			method = "tryBreakBlock",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;",
					ordinal = 0
			),
			cancellable = true
	)
	private void fireBlockBreakEvent(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (SpellCircleEvent.BREAK_BLOCK.fireAllNearby(world, pos, List.of(
				VectorFragment.of(pos) // TODO held item
		))) {
			var particlePos = pos.toCenterPos();
			world.spawnParticles(
					ModParticles.PROTECTED_BLOCK, particlePos.x, particlePos.y, particlePos.z,
					1, 0, 0, 0, 0
			);
			cir.setReturnValue(false);
		}
	}

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

	@Inject(
			method = "interactBlock",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/item/ItemUsageContext;<init>(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)V"
			),
			cancellable = true
	)
	private void fireBlockPlaceEvent(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
		var pos = hitResult.getBlockPos().offset(hitResult.getSide());
		if (stack.getItem() instanceof BlockItem && SpellCircleEvent.PLACE_BLOCK.fireAllNearby((ServerWorld) world, pos, List.of(
				VectorFragment.of(pos) // TODO held item
		))) {
			var particlePos = pos.toCenterPos();
			((ServerWorld) world).spawnParticles(
					ModParticles.PROTECTED_BLOCK, particlePos.x, particlePos.y, particlePos.z,
					1, 0, 0, 0, 0
			);
			player.currentScreenHandler.syncState();
			cir.setReturnValue(ActionResult.FAIL);
		}
	}
}