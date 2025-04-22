package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;

public abstract class BlunderException extends RuntimeException {
    public abstract MutableText createMessage();

    protected Text formatInt(int number) {
        return Text.literal("" + number).withColor(FragmentType.NUMBER.color().getAsInt());
    }

    protected Text formatFloat(float number) {
        return Text.literal(String.format("%.2f", number)).withColor(FragmentType.NUMBER.color().getAsInt());
    }

    protected Text formatAddress(List<NumberFragment> address) {
        var out = Text.literal("[");
        var first = true;

        for (var index : address) {
            if (!first) out.append(Text.literal(", "));
            out.append(formatInt(index.asInt()));
            first = false;
        }

        out.append("]");
        return out;
    }
}
