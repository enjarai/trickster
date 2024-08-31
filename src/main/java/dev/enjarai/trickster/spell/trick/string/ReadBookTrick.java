package dev.enjarai.trickster.spell.trick.string;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.StringFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.ItemInvalidBlunder;
import dev.enjarai.trickster.spell.trick.blunder.NoPlayerBlunder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ReadBookTrick extends Trick {
    public ReadBookTrick() {
        super(Pattern.of(2, 4, 6, 3, 1, 2, 5, 7, 6));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var stack = supposeInput(fragments, FragmentType.SLOT, 0)
                .flatMap(slot -> Optional.of(slot.reference(this, ctx)))
                .orElseGet(() -> ctx.source().getPlayer().orElseThrow(() -> new NoPlayerBlunder(this)).getOffHandStack());
        Stream<Fragment> stream;

        if (stack.get(DataComponentTypes.WRITABLE_BOOK_CONTENT) instanceof WritableBookContentComponent comp) {
            stream = comp.stream(false).map(s -> new StringFragment(Text.literal(s)));
        } else if (stack.get(DataComponentTypes.WRITTEN_BOOK_CONTENT) instanceof WrittenBookContentComponent comp) {
            stream = comp.getPages(false).stream().map(StringFragment::new);
        } else {
            throw new ItemInvalidBlunder(this);
        }

        return new ListFragment(stream.toList());
    }
}
