package dev.enjarai.trickster.mixin.client.sodium;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.enjarai.trickster.cca.ModChunkComponents;
import dev.enjarai.trickster.cca.ShadowDisguiseMapComponent;
import dev.enjarai.trickster.pond.WorldlyRenderContextDuck;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildContext;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildOutput;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderTask;
import net.caffeinemc.mods.sodium.client.util.task.CancellationToken;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.caffeinemc.mods.sodium.client.world.cloned.ChunkRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.world.World;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkBuilderMeshingTask.class)
public abstract class ChunkBuilderMeshingTaskMixin extends ChunkBuilderTask<ChunkBuildOutput> {
    @Shadow @Final private ChunkRenderContext renderContext;

    public ChunkBuilderMeshingTaskMixin(RenderSection render, int time, Vector3dc absoluteCameraPos) {
        super(render, time, absoluteCameraPos);
    }

    @Inject(
            method = "execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            at = @At("HEAD"),
            remap = false
    )
    private void preGetComponent(ChunkBuildContext buildContext, CancellationToken cancellationToken,
                                 CallbackInfoReturnable<ChunkBuildOutput> cir,
                                 @Share("shadow_map") LocalRef<ShadowDisguiseMapComponent> shadowMap) {
        //noinspection resource
        var world = ((WorldlyRenderContextDuck) renderContext).trickster$getWorld();
        shadowMap.set(ModChunkComponents.SHADOW_DISGUISE_MAP.get(world.getChunk(render.getChunkX(), render.getChunkZ())));
    }

    @WrapOperation(
            method = "execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/caffeinemc/mods/sodium/client/world/LevelSlice;getBlockState(III)Lnet/minecraft/block/BlockState;"
            ),
            remap = false
    )
    private BlockState modifyBlockState(LevelSlice instance, int blockX, int blockY, int blockZ, Operation<BlockState> original, @Share("shadow_map") LocalRef<ShadowDisguiseMapComponent> shadowMap) {
        var customState = shadowMap.get().getFunnyState(blockX, blockY, blockZ);
        return customState != null ? customState : original.call(instance, blockX, blockY, blockZ);
    }
}
