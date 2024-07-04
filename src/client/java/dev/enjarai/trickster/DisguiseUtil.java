package dev.enjarai.trickster;

import com.mojang.authlib.GameProfile;
import dev.enjarai.trickster.cca.DisguiseCumponent;
import dev.enjarai.trickster.cca.ModChunkCumponents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.EmptyChunk;
import org.jetbrains.annotations.Nullable;

public class DisguiseUtil {
    @Nullable
    public static GameProfile getGameProfile(DisguiseCumponent component) {
        if (component.getUuid() == null) {
            return null;
        }
        var result = MinecraftClient.getInstance().getSessionService()
                .fetchProfile(component.getUuid(), true);
        if (result == null) {
            return null;
        }
        return result.profile();
    }

    public static boolean inShadowBlock(ClientWorld world, BlockPos blockPos) {
        var chunk = world.getChunk(blockPos);

        if (chunk instanceof EmptyChunk)
            return false;

        var shadowBlocks = ModChunkCumponents.SHADOW_DISGUISE_MAP.get(chunk);
        var funnyState = shadowBlocks.getFunnyState(blockPos);

        return funnyState != null;
    }
}
