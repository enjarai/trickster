package dev.enjarai.trickster.spell;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.execution.SerializedSpellInstruction;
import dev.enjarai.trickster.spell.execution.SpellInstructionType;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public non-sealed interface Fragment extends SpellInstruction {
    final int MAX_WEIGHT = 64000;
    final Text TRUNCATED_VALUE_TEXT = Text.literal(" [...]")
        .setStyle(Style.EMPTY
                .withColor(Formatting.RED)
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable(Trickster.MOD_ID + ".text.misc.value_truncated"))));
    @SuppressWarnings("unchecked")
    final StructEndec<Fragment> ENDEC = EndecTomfoolery.lazy(() -> (StructEndec<Fragment>) Endec.dispatchedStruct(
            FragmentType::endec,
            Fragment::type,
            Endec.<FragmentType<?>>ifAttr(EndecTomfoolery.UBER_COMPACT_ATTRIBUTE, Endec.INT.xmap(FragmentType::getFromInt, FragmentType::getIntId))
                    .orElse(MinecraftEndecs.ofRegistry(FragmentType.REGISTRY))
    ));

    FragmentType<?> type();

    Text asText();

    default Text asFormattedText() {
        var text = type().color().isPresent() ? asText().copy().withColor(type().color().getAsInt()) : asText().copy();
        var siblings = text.getSiblings();
        var size = siblings.size();
        var newSiblings = new ArrayList<>(siblings).subList(0, Math.min(size, 100));
        siblings.clear();
        siblings.addAll(newSiblings);
        return text.append(size != newSiblings.size()
                ? TRUNCATED_VALUE_TEXT
                : Text.of(""));
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
