package dev.enjarai.trickster.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.enjarai.trickster.SpellTooltipData;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.entity.AmethystProjectile;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.SpellPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(Item.class)
public abstract class ItemMixin {
    @ModifyReturnValue(
            method = "hasGlint",
            at = @At("RETURN")
    )
    private boolean spellGlint(boolean original, ItemStack stack) {
        return original
                || (stack.contains(ModComponents.FRAGMENT))
                && !stack.isIn(ModItems.NO_SPELL_GLINT);
    }

    @Inject(
            method = "appendTooltip",
            at = @At("HEAD")
    )
    private void addGarble(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type, CallbackInfo ci) {
        var spellComponent = stack.get(ModComponents.FRAGMENT);
        var manaComponent = stack.get(ModComponents.MANA);

        if (spellComponent != null) {
            if (spellComponent.closed()) {
                tooltip.add(spellComponent.name()
                        .flatMap(str -> Optional.of(Text.literal(str)))
                        .orElse(Text.literal("Mortal eyes upon my carvings").setStyle(Style.EMPTY.withObfuscated(true))));
            } else if (!(spellComponent.value() instanceof SpellPart)) {
                tooltip.add(spellComponent.value().asFormattedText());
            }
        }

        if (manaComponent != null) {
            if (Trickster.merlinTooltipAppender != null) {
                Trickster.merlinTooltipAppender.appendTooltip(stack, context, tooltip, type);
            }
        }
    }

    @Inject(
            method = "use",
            at = {
                    @At(value = "INVOKE", target = "Lnet/minecraft/util/TypedActionResult;pass(Ljava/lang/Object;)Lnet/minecraft/util/TypedActionResult;"),
                    @At(value = "INVOKE", target = "Lnet/minecraft/util/TypedActionResult;fail(Ljava/lang/Object;)Lnet/minecraft/util/TypedActionResult;")
            },
            cancellable = true
    )
    private void trickster$use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        var itemStack = user.getStackInHand(hand);
        if (itemStack.isOf(Items.AMETHYST_SHARD)) {
            var projectile = AmethystProjectile.tryThrow(user,itemStack);
            if (projectile == null) {
                cir.setReturnValue(new TypedActionResult<>(ActionResult.FAIL, itemStack));
                cir.cancel();
                return;
            }
            projectile.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 0F);
            user.getItemCooldownManager().set(Items.AMETHYST_SHARD, 20);
            world.spawnEntity(projectile);
        }
    }


    @Inject(
            method = "getTooltipData",
            at = @At("HEAD"),
            cancellable = true
    )
    private void trickster$getSpellTooltipData(ItemStack stack, CallbackInfoReturnable<Optional<TooltipData>> cir) {
        var comp = stack.get(ModComponents.FRAGMENT);

        if (comp != null && !comp.closed() && comp.value() instanceof SpellPart spell && !spell.isEmpty()) {
            cir.setReturnValue(Optional.of(new SpellTooltipData(spell)));
        }
    }


}
