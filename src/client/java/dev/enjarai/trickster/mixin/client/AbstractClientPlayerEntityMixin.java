package dev.enjarai.trickster.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import dev.enjarai.trickster.DisguiseUtil;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.quack.DisguisePlayerQuack;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntityMixin implements DisguisePlayerQuack {
    @Unique
    @Nullable
    private PlayerListEntry disguisePlayerListEntry;

    protected AbstractClientPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    public PlayerListEntry trickster$getApplicableEntry() {
        var disguise = getComponent(ModEntityCumponents.DISGUISE);
        if (disguise.getUuid() != null) {
            if (disguisePlayerListEntry == null || !disguisePlayerListEntry.getProfile().getId().equals(disguise.getUuid())) {
                // Update the entry if its out of date
                var profile = DisguiseUtil.getGameProfile(disguise);
                if (profile == null) {
                    profile = new GameProfile(disguise.getUuid(), "");
                }

                disguisePlayerListEntry = new PlayerListEntry(profile, false);
            }
        } else if (disguisePlayerListEntry != null) {
            // Reset the entry if we're not disguised
            disguisePlayerListEntry = null;
        }
        return disguisePlayerListEntry;
    }

    @ModifyExpressionValue(
            method = "getFovMultiplier",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Float;isInfinite(F)Z"
            )
    )
    private boolean fixFovWhenFrozen(boolean original) {
        return original || getAttributes().hasModifierForAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED, Trickster.NEGATE_ATTRIBUTE.id());
    }

    @Inject(
            method = "getSkinTextures",
            at = @At("HEAD"),
            cancellable = true
    )
    private void disguisePlayerSkin(CallbackInfoReturnable<SkinTextures> cir) {
        var entry = trickster$getApplicableEntry();
        if (entry != null) {
            cir.setReturnValue(entry.getSkinTextures());
        }
    }

    @Override
    protected void disguiseDisplayName(CallbackInfoReturnable<Text> cir) {
        var entry = trickster$getApplicableEntry();
        if (entry != null) {
            cir.setReturnValue(Text.literal(entry.getProfile().getName()));
        }
    }
}
