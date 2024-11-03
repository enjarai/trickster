package dev.enjarai.trickster.spell.fragment;

import java.util.HashMap;
import java.util.Optional;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;
import dev.enjarai.trickster.spell.SpellPart;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.text.MutableText;
import dev.enjarai.trickster.util.Hamt;
import net.minecraft.text.Text;

public record MapFragment(Hamt<Fragment, Fragment> map) implements Fragment {
    public static final StructEndec<MapFragment> ENDEC = StructEndecBuilder.of(
            Endec.map(Fragment.ENDEC, Fragment.ENDEC).xmap(Hamt::fromMap, Hamt::asMap).fieldOf("entries", MapFragment::map),
            MapFragment::new
    );

    @Override
    public FragmentType<?> type() {
        return FragmentType.MAP;
    }

    @Override
    public Text asText() {
        MutableText out = Text.empty();
        out.append("{");
        var iterator = map.iterator();

        iterator.forEachRemaining(entry -> {
            out.append(entry.getKey().asFormattedText()).append(": ").append(entry.getValue().asFormattedText());
            if (iterator.hasNext())
                out.append(", ");
        });

        out.append("}");

        return out;
    }

    @Override
    public boolean asBoolean() {
        return !map.isEmpty();
    }

	@Override
	public int getWeight() {
        int weight = 16;

        for (var kv : map) {
            weight += kv.getKey().getWeight();
            weight += kv.getValue().getWeight();
        }

        return weight;
	}

    @Override
    public MapFragment applyEphemeral() {
        return new MapFragment(map.stream()
                .reduce(Hamt.<Fragment, Fragment>empty(),
                    (last, current) -> Hamt.<Fragment, Fragment>empty().assoc(current.getKey().applyEphemeral(), current.getValue().applyEphemeral()),
                    Hamt::assocAll));
    }

    public Optional<Hamt<Pattern, SpellPart>> getMacroMap() {
        var macros = new HashMap<Pattern, SpellPart>();

        for (var entry : map) {
            if (entry.getKey() instanceof PatternGlyph pattern && entry.getValue() instanceof SpellPart spell) {
                macros.put(pattern.pattern(), spell);
            } else {
                return Optional.empty();
            }
        }

        return Optional.of(Hamt.fromMap(macros));
    }
}
