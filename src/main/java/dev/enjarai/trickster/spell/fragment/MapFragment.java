package dev.enjarai.trickster.spell.fragment;

import java.util.Stack;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.executor.FoldingSpellExecutor;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public record MapFragment(HashMap<Fragment, Fragment> map) implements FoldableFragment {
    public static final StructEndec<MapFragment> ENDEC = StructEndecBuilder.of(
            Endec.map(Fragment.ENDEC, Fragment.ENDEC).xmap(HashMap::ofAll, HashMap::toJavaMap).fieldOf("entries", MapFragment::map),
            MapFragment::new);

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
            out.append(entry._1().asFormattedText()).append(": ").append(entry._2().asFormattedText());
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
            weight += kv._1().getWeight();
            weight += kv._2().getWeight();
        }

        return weight;
    }

    @Override
    public MapFragment applyEphemeral() {
        return new MapFragment(map.map((key, value) -> new Tuple2<>(key.applyEphemeral(), value.applyEphemeral())));
    }

    public HashMap<Pattern, SpellPart> getMacroMap() {
        return map.filter((key, value) -> key instanceof PatternGlyph && value instanceof SpellPart)
                .mapKeys(k -> ((PatternGlyph) k).pattern())
                .mapValues(SpellPart.class::cast);
    }

    @Override
    public FoldingSpellExecutor fold(SpellContext ctx, SpellPart executable, Fragment identity) {
        var keys = new Stack<Fragment>();
        var values = new Stack<Fragment>();

        for (var kv : map) {
            keys.addFirst(kv._1());
            values.addFirst(kv._2());
        }

        return new FoldingSpellExecutor(ctx, executable, identity, values, keys, this);
    }

    public MapFragment mergeWith(MapFragment other) {
        return new MapFragment(map.merge(other.map));
    }
}
