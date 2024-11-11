package dev.enjarai.trickster.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.enjarai.trickster.SpellTooltipData;
import dev.enjarai.trickster.cca.SharedManaComponent;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.net.SubscribeToPoolPacket;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.mana.SharedManaPool;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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

        if (spellComponent != null) {
            if (spellComponent.closed()) {
                tooltip.add(spellComponent.name()
                        .flatMap(str -> Optional.of(Text.literal(str)))
                        .orElse(Text.literal("Mortal eyes upon my carvings").setStyle(Style.EMPTY.withObfuscated(true))));
            } else if (!(spellComponent.value() instanceof SpellPart)) {
                tooltip.add(spellComponent.value().asFormattedText());
            }
        }

        var manaComponent = stack.get(ModComponents.MANA);

        if (manaComponent != null) {
            var pool = manaComponent.pool();
            
            if (type.isAdvanced()) {
                tooltip.add(Text.literal("Stored: ")
                        .append(pool.get() + "kG")
                        .append(" / ")
                        .append(pool.getMax() + "kG")
                        .styled(s -> s.withColor(0xaaaabb)));

                if (pool instanceof SharedManaPool shared) {
                    // if ever run on the server, will fail -- consider putting a try-catch if it causes an issue with a mod?
                    if (SharedManaComponent.getInstance().get(shared.uuid()).isEmpty()) {
                        ModNetworking.CHANNEL.clientHandle().send(new SubscribeToPoolPacket(shared.uuid()));
                    }

                    tooltip.add(Text
                            .literal(shared.uuid().toString())
                            .setStyle(Style.EMPTY.withFormatting(Formatting.LIGHT_PURPLE)));
                }
            } 
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
