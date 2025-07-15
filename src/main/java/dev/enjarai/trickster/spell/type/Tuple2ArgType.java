package dev.enjarai.trickster.spell.type;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.trick.Trick;
import io.vavr.Tuple2;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;

public class Tuple2ArgType<T1, T2> implements ArgType<Tuple2<T1, T2>> {
    protected final ArgType<T1> arg1;
    protected final ArgType<T2> arg2;

    public Tuple2ArgType(ArgType<T1> arg1, ArgType<T2> arg2) {
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    @Override
    public int argc(List<Fragment> fragments) {
        return arg1.argc(fragments) + arg2.argc(fragments);
    }

    @Override
    public Tuple2<T1, T2> compose(Trick<?> trick, SpellContext ctx, List<Fragment> fragments) {
        return new Tuple2<>(
                arg1.compose(trick, ctx, isolate(0, fragments)),
                arg2.compose(trick, ctx, isolate(arg1.argc(fragments), fragments))
        );
    }

    @Override
    public boolean match(List<Fragment> fragments) {
        return arg1.isolateAndMatch(fragments) && arg2.match(isolate(arg1.argc(fragments), fragments));
    }

    @Override
    public ArgType<Tuple2<T1, T2>> wardOf() {
        return new Tuple2ArgType<>(arg1.wardOf(), arg2.wardOf());
    }

    @Override
    public MutableText asText() {
        return Text.literal("(").append(arg1.asText()).append(", ").append(arg2.asText()).append(")");
    }
}
