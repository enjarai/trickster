package dev.enjarai.trickster.spell;

import com.google.common.collect.ImmutableList;
import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import io.wispforest.endec.Endec;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public record Pattern(List<PatternEntry> entries) implements Fragment {
    public static final Endec<Pattern> ENDEC = Endec.ifAttr(EndecTomfoolery.UBER_COMPACT_ATTRIBUTE, Endec.INT.xmap(Pattern::from, Pattern::toInt))
            .orElse(PatternEntry.ENDEC.listOf().xmap(Pattern::new, Pattern::entries));
    public static final Pattern EMPTY = Pattern.of();

    private static final PatternEntry[] possibleLines = new PatternEntry[32];

    static {
        int i = 0;
        for (byte p1 = 0; p1 < 9; p1++) {
            for (byte p2 = 0; p2 < 9; p2++) {
                if (p2 > p1 && p1 + p2 != 8) {
                    possibleLines[i] = new PatternEntry(p1, p2);
                    i++;
                }
            }
        }
    }

    public static Pattern from(List<Byte> pattern) {
        var list = new ArrayList<PatternEntry>();
        Byte last = null;
        for (var current : pattern) {
            if (last != null) {
                if (last < current) {
                    list.add(new PatternEntry(last, current));
                } else {
                    list.add(new PatternEntry(current, last));
                }
            }
            last = current;
        }
        list.sort(PatternEntry::compareTo);
        return new Pattern(ImmutableList.copyOf(list));
    }

    public static Pattern from(int pattern) {
        var builder = ImmutableList.<PatternEntry>builder();
        for (int i = 0; i < 32; i++) {
            if ((pattern >> i & 0x1) == 1) {
                builder.add(possibleLines[i]);
            }
        }
        return new Pattern(builder.build());
    }

    public static Pattern of(int... pattern) {
        var result = from(Stream.of(ArrayUtils.toObject(pattern)).map(Integer::byteValue).toList());

        for (var line : result.entries) {
            boolean b = false;

            for (var line2 : possibleLines) {
                if (line2.equals(line)) {
                    b = true;
                    break;
                }
            }

            if (!b) {
                throw new IllegalArgumentException("Pattern is not valid");
            }
        }

        return result;
    }

    public boolean isEmpty() {
        return entries().isEmpty();
    }

    public boolean contains(int point) {
        byte realPoint = (byte) point;
        for (PatternEntry entry : entries) {
            if (entry.p1 == realPoint || entry.p2 == realPoint) {
                return true;
            }
        }
        return false;
    }

    public int toInt() {
        var result = 0;
        for (int i = 0; i < 32; i++) {
            if (entries.contains(possibleLines[i])) {
                result |= 1 << i;
            }
        }
        return result;
    }

    @Override
    public Fragment activateAsGlyph(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return new PatternGlyph(this);
    }

    @Override
    public FragmentType<?> type() {
        return FragmentType.PATTERN_LITERAL;
    }

    @Override
    public Text asText() {
        return Text.literal("<").append(new PatternGlyph(this).asText()).append(">");
    }

    @Override
    public int getWeight() {
        return 32;
    }

    public record PatternEntry(byte p1, byte p2) implements Comparable<PatternEntry> {
        public static final Endec<PatternEntry> ENDEC = Endec.BYTES
                .xmap(list -> new PatternEntry(list[0], list[1]), entry -> new byte[] { entry.p1, entry.p2 });

        @Override
        public int compareTo(@NotNull Pattern.PatternEntry o) {
            var p1Compare = Integer.compare(p1, o.p1);
            return p1Compare == 0 ? Integer.compare(p2, o.p2) : p1Compare;
        }
    }
}
