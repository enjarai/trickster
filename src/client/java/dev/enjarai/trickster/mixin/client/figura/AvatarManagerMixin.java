package dev.enjarai.trickster.mixin.client.figura;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.enjarai.trickster.cca.ModEntityComponents;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Pseudo
@Mixin(targets = "org/figuramc/figura/avatar/AvatarManager")
public class AvatarManagerMixin {
    @Dynamic
    @Inject(
            method = "getAvatarForPlayer",
            at = @At("HEAD")
    )
    private static void changeAvatarWhenPolymorphed(UUID uuid, CallbackInfoReturnable<Object> cir,
                                                    @Local(argsOnly = true, index = 0) LocalRef<UUID> uuidRef) {
        var world = MinecraftClient.getInstance().world;
        if (world != null) {
            var player = world.getPlayerByUuid(uuidRef.get());
            if (player != null) {
//                var disguise = ModEntityComponents.DISGUISE.get(player).getUuid();
//                if (disguise != null) {
//                    uuidRef.set(disguise);
//                } TODO
            }
        }
    }
}
