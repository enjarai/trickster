package dev.enjarai.trickster.spell;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.spell.execution.SerializedSpellInstruction;
import dev.enjarai.trickster.spell.execution.SpellInstructionType;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import io.wispforest.endec.Endec;
import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public non-sealed interface Fragment extends SpellInstruction {
    Supplier<MapCodec<Fragment>> CODEC = Suppliers.memoize(() -> FragmentType.REGISTRY.getCodec().dispatchMap(Fragment::type, FragmentType::codec));
    Supplier<Endec<Fragment>> ENDEC = Suppliers.memoize(() -> CodecUtils.toEndec(CODEC.get().codec()));

    FragmentType<?> type();

    Text asText();

    default Text asFormattedText() {
        if (type().color().isPresent()) {
            return asText().copy().withColor(type().color().getAsInt());
        }
        return asText();
    }

    BooleanFragment asBoolean();

    default Fragment activateAsGlyph(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return this;
    }

    @Override
    default SerializedSpellInstruction asSerialized() {
        return new SerializedSpellInstruction(SpellInstructionType.FRAGMENT, this);
    }

    default boolean isEphemeral() {
        return false;
    }

    default Optional<BiFunction<SpellContext, List<Fragment>, Fragment>> getActivator() {
        return Optional.of(this::activateAsGlyph);
    }
}
