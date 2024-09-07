package dev.enjarai.trickster.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.Map.Hamt;
import io.wispforest.endec.Endec;
import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.item.ItemStack;

import java.util.Optional;

public record MacroComponent(Hamt<Pattern, SpellPart> macros) {
    public static final Codec<MacroComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CodecUtils.toCodec(Endec.map(Pattern.ENDEC, SpellPart.ENDEC).xmap(Hamt::fromMap, Hamt::asMap)).fieldOf("macros").forGetter(MacroComponent::macros)
    ).apply(instance, MacroComponent::new));

    public static Optional<Hamt<Pattern, SpellPart>> getMap(ItemStack stack) {
        return Optional.of(stack)
                .filter(stack2 -> stack2.contains(ModComponents.MACRO_MAP))
                .map(stack2 -> stack2.get(ModComponents.MACRO_MAP))
                .map(MacroComponent::macros);
    }

    public static boolean setMap(ItemStack stack, Hamt<Pattern, SpellPart> macros) {
        stack.set(ModComponents.MACRO_MAP, new MacroComponent(macros));

        return true;
    }
}
