package dev.enjarai.trickster.spell.trick.blunder;

import dev.enjarai.trickster.spell.fragment.FragmentType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;

public abstract class BlunderException extends RuntimeException {
    public abstract MutableText createMessage();

    protected Text formatInt(int number) {
        return Text.literal("" + number).withColor(FragmentType.NUMBER.color().getAsInt());
    }

    protected Text formatFloat(float number) {
        return Text.literal(String.format("%f", number)).withColor(FragmentType.NUMBER.color().getAsInt());
    }

    protected Text formatAddress(List<Integer> address) {
        var out = Text.literal("[");
        var first = true;
        for (int integer : address) {
            if (!first) out.append(Text.literal(", "));
            out.append(formatInt(integer));
            first = false;
        }
        out.append("]");
        return out;
    }
}
