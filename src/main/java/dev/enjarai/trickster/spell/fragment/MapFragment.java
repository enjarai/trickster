package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.spell.Fragment;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.text.MutableText;
import dev.enjarai.trickster.util.Hamt;
import net.minecraft.text.Text;

public record MapFragment(Hamt<? extends Fragment, ? extends Fragment> map) implements Fragment {
    public static final StructEndec<MapFragment> ENDEC = StructEndecBuilder.of(
            Endec.map(Fragment.ENDEC, Fragment.ENDEC).xmap(Hamt::fromMap, Hamt::asMap)
                    .fieldOf("macros", MapFragment::downcast),
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
        return map.size() > 0;
    }

	@Override
	public int getWeight() {
        int weight = 0;

        for (var kv : map) {
            weight += kv.getKey().getWeight();
            weight += kv.getValue().getWeight();
        }

        return weight;
	}

    @SuppressWarnings("unchecked")
	public Hamt<Fragment, Fragment> downcast() {
        return (Hamt<Fragment, Fragment>) map;
    }
}
