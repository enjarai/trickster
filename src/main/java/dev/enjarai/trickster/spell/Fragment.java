package dev.enjarai.trickster.spell;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.execution.SerializedSpellInstruction;
import dev.enjarai.trickster.spell.execution.SpellInstructionType;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public non-sealed interface Fragment extends SpellInstruction {
    int MAX_WEIGHT = 6400;
    @SuppressWarnings("unchecked")
    StructEndec<Fragment> ENDEC = EndecTomfoolery.lazy(() -> (StructEndec<Fragment>) Endec.dispatchedStruct(
            FragmentType::endec,
            Fragment::type,
            Endec.<FragmentType<?>>ifAttr(EndecTomfoolery.UBER_COMPACT_ATTRIBUTE, Endec.INT.xmap(FragmentType::getFromInt, FragmentType::getIntId))
                    .orElse(MinecraftEndecs.ofRegistry(FragmentType.REGISTRY))
    ));

    FragmentType<?> type();

    Text asText();

    default Text asFormattedText() {
        if (type().color().isPresent()) {
            return asText().copy().withColor(type().color().getAsInt());
        }
        return asText();
    }

    boolean asBoolean();

    default Fragment activateAsGlyph(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return this;
    }

    @Override
    default SerializedSpellInstruction asSerialized() {
        return new SerializedSpellInstruction(SpellInstructionType.FRAGMENT, this);
    }

    /**
     * Potentially recursively remove ephemeral values from this fragment.
     * May return <pre>this</pre> or any other new fragment.
     * Potentially results in cloning the entire fragment if required.
     */
    default Fragment applyEphemeral() {
        return this;
    }

    /**
     * The weight of this fragment in terms of memory footprint.
     * If possible, should be *roughly* equivalent to the amount of bytes in the fields of this fragment.
     */
    int getWeight();

    default Optional<BiFunction<SpellContext, List<Fragment>, Fragment>> getActivator() {
        return Optional.of(this::activateAsGlyph);
    }
}
