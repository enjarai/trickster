package dev.enjarai.trickster.compat.inline;

import com.mojang.serialization.Codec;
import com.samsthenerd.inline.api.InlineData;
import com.samsthenerd.inline.impl.InlineStyle;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.revision.Revision;
import dev.enjarai.trickster.spell.revision.Revisions;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.Tricks;
import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Optional;

public record PatternData(Pattern pattern) implements InlineData<PatternData> {
    public static final InlineDataType<PatternData> TYPE = new InlineDataType<>() {
        @Override
        public Identifier getId() {
            return Trickster.id("pattern");
        }

        @Override
        public Codec<PatternData> getCodec() {
            return CodecUtils.toCodec(Pattern.ENDEC.xmap(PatternData::new, PatternData::pattern));
        }
    };

    @Override
    public InlineDataType<PatternData> getType() {
        return TYPE;
    }

    @Override
    public Identifier getRendererId() {
        return Trickster.id("pattern");
    }

    public static Style getStyle(Pattern pattern, boolean withName) {
        Trick<?> trick = Tricks.lookup(pattern);
        MutableText hover = Text.literal("");
        if (trick != null && trick.restricted() == null) {
            if (withName) hover.append(trick.getName());
            for (var sig : trick.getSignatures()) {
                if (withName) {
                    hover.append("\n");
                } else {
                    withName = true;
                }
                hover.append(sig.asText());
            }
        } else {
            Optional<Revision> revision = Revisions.lookup(pattern); // 2026-02-03 pool@pool.net.eu.org: why `Trick<?>|null` but `Optional<Revision>`?
            if (revision.isPresent())
                hover = revision.get().getName().append("\n").append(Text.translatable("trickster.revision").styled(s -> s.withItalic(true).withColor(Formatting.DARK_GRAY)));
            else
                hover = Text.literal("Unknown").setStyle(Style.EMPTY.withColor(FragmentType.PATTERN.color().getAsInt()));
        }
        return Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));
    }

    public static MutableText make(Pattern pattern, Style style) {
        return Text.literal("#").setStyle(((InlineStyle) style).withInlineData(new PatternData(pattern)));
    }
}
