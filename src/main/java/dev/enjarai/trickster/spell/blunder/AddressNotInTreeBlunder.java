package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;

public class AddressNotInTreeBlunder extends TrickBlunderException {
    public final List<NumberFragment> address;

    public AddressNotInTreeBlunder(Trick<?> source, List<NumberFragment> address) {
        super(source);
        this.address = address;
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append(Text.translatable(Trickster.MOD_ID + ".blunder.address_not_in_tree", formatAddress(address)));
    }
}
