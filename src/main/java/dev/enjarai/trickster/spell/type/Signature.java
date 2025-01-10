package dev.enjarai.trickster.spell.type;

import java.util.List;

import dev.enjarai.trickster.spell.EvaluationResult;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.Trick;
import io.vavr.*;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public interface Signature<T extends Trick<T>> {
    boolean match(List<Fragment> fragments);

    EvaluationResult run(T trick, SpellContext ctx, List<Fragment> fragments) throws BlunderException;

    MutableText asText();

    static <T extends Trick<T>> Signature<T> of(Function2<T, SpellContext, EvaluationResult> handler) {
        return new Signature<T>() {
            @Override
            public boolean match(List<Fragment> fragments) {
                return true;
            }

            @Override
            public EvaluationResult run(T trick, SpellContext ctx, List<Fragment> fragments) throws BlunderException {
                return handler.apply(trick, ctx);
            }

            @Override
            public MutableText asText() {
                return Text.literal("None").styled(s -> s.withItalic(true).withColor(0x555555));
            }
        };
    }

    static <T extends Trick<T>, T1> Signature<T> of(ArgType<T1> t1, Function3<T, SpellContext, T1, EvaluationResult> handler) {
        return new Signature<T>() {
            @Override
            public boolean match(List<Fragment> fragments) {
                if (!t1.isolateAndMatch(fragments)) {
                    return false;
                }
                fragments = fragments.subList(t1.argc(fragments), fragments.size());

                return true;
            }

            @Override
            public EvaluationResult run(T trick, SpellContext ctx, List<Fragment> fragments) throws BlunderException {
                var args1 = t1.isolate(0, fragments);
                var v1 = t1.compose(trick, ctx, args1);
                fragments = fragments.subList(args1.size(), fragments.size());

                return handler.apply(trick, ctx, v1);
            }

            @Override
            public MutableText asText() {
                var text = Text.empty();

                text = text.append(t1.asText());

                return text;
            }
        };
    }

    static <T extends Trick<T>, T1, T2> Signature<T> of(ArgType<T1> t1, ArgType<T2> t2, Function4<T, SpellContext, T1, T2, EvaluationResult> handler) {
        return new Signature<T>() {
            @Override
            public boolean match(List<Fragment> fragments) {
                if (!t1.isolateAndMatch(fragments)) {
                    return false;
                }
                fragments = fragments.subList(t1.argc(fragments), fragments.size());

                if (!t2.isolateAndMatch(fragments)) {
                    return false;
                }
                fragments = fragments.subList(t2.argc(fragments), fragments.size());

                return true;
            }

            @Override
            public EvaluationResult run(T trick, SpellContext ctx, List<Fragment> fragments) throws BlunderException {
                var args1 = t1.isolate(0, fragments);
                var v1 = t1.compose(trick, ctx, args1);
                fragments = fragments.subList(args1.size(), fragments.size());

                var args2 = t2.isolate(0, fragments);
                var v2 = t2.compose(trick, ctx, args2);
                fragments = fragments.subList(args2.size(), fragments.size());

                return handler.apply(trick, ctx, v1, v2);
            }

            @Override
            public MutableText asText() {
                var text = Text.empty();

                text = text.append(t1.asText());
                text = text.append(", ");
                text = text.append(t2.asText());

                return text;
            }
        };
    }

    static <T extends Trick<T>, T1, T2, T3> Signature<T> of(ArgType<T1> t1, ArgType<T2> t2, ArgType<T3> t3, Function5<T, SpellContext, T1, T2, T3, EvaluationResult> handler) {
        return new Signature<T>() {
            @Override
            public boolean match(List<Fragment> fragments) {
                if (!t1.isolateAndMatch(fragments)) {
                    return false;
                }
                fragments = fragments.subList(t1.argc(fragments), fragments.size());

                if (!t2.isolateAndMatch(fragments)) {
                    return false;
                }
                fragments = fragments.subList(t2.argc(fragments), fragments.size());

                if (!t3.isolateAndMatch(fragments)) {
                    return false;
                }
                fragments = fragments.subList(t3.argc(fragments), fragments.size());

                return true;
            }

            @Override
            public EvaluationResult run(T trick, SpellContext ctx, List<Fragment> fragments) throws BlunderException {
                var args1 = t1.isolate(0, fragments);
                var v1 = t1.compose(trick, ctx, args1);
                fragments = fragments.subList(args1.size(), fragments.size());

                var args2 = t2.isolate(0, fragments);
                var v2 = t2.compose(trick, ctx, args2);
                fragments = fragments.subList(args2.size(), fragments.size());

                var args3 = t3.isolate(0, fragments);
                var v3 = t3.compose(trick, ctx, args3);
                fragments = fragments.subList(args3.size(), fragments.size());

                return handler.apply(trick, ctx, v1, v2, v3);
            }

            @Override
            public MutableText asText() {
                var text = Text.empty();

                text = text.append(t1.asText());
                text = text.append(", ");
                text = text.append(t2.asText());
                text = text.append(", ");
                text = text.append(t3.asText());

                return text;
            }
        };
    }

    static <T extends Trick<T>, T1, T2, T3, T4> Signature<T> of(ArgType<T1> t1, ArgType<T2> t2, ArgType<T3> t3, ArgType<T4> t4, Function6<T, SpellContext, T1, T2, T3, T4, EvaluationResult> handler) {
        return new Signature<T>() {
            @Override
            public boolean match(List<Fragment> fragments) {
                if (!t1.isolateAndMatch(fragments)) {
                    return false;
                }
                fragments = fragments.subList(t1.argc(fragments), fragments.size());

                if (!t2.isolateAndMatch(fragments)) {
                    return false;
                }
                fragments = fragments.subList(t2.argc(fragments), fragments.size());

                if (!t3.isolateAndMatch(fragments)) {
                    return false;
                }
                fragments = fragments.subList(t3.argc(fragments), fragments.size());

                if (!t4.isolateAndMatch(fragments)) {
                    return false;
                }
                fragments = fragments.subList(t4.argc(fragments), fragments.size());

                return true;
            }

            @Override
            public EvaluationResult run(T trick, SpellContext ctx, List<Fragment> fragments) throws BlunderException {
                var args1 = t1.isolate(0, fragments);
                var v1 = t1.compose(trick, ctx, args1);
                fragments = fragments.subList(args1.size(), fragments.size());

                var args2 = t2.isolate(0, fragments);
                var v2 = t2.compose(trick, ctx, args2);
                fragments = fragments.subList(args2.size(), fragments.size());

                var args3 = t3.isolate(0, fragments);
                var v3 = t3.compose(trick, ctx, args3);
                fragments = fragments.subList(args3.size(), fragments.size());

                var args4 = t4.isolate(0, fragments);
                var v4 = t4.compose(trick, ctx, args4);
                fragments = fragments.subList(args4.size(), fragments.size());

                return handler.apply(trick, ctx, v1, v2, v3, v4);
            }

            @Override
            public MutableText asText() {
                var text = Text.empty();

                text = text.append(t1.asText());
                text = text.append(", ");
                text = text.append(t2.asText());
                text = text.append(", ");
                text = text.append(t3.asText());
                text = text.append(", ");
                text = text.append(t4.asText());

                return text;
            }
        };
    }

    static <T extends Trick<T>, T1, T2, T3, T4, T5> Signature<T> of(ArgType<T1> t1, ArgType<T2> t2, ArgType<T3> t3, ArgType<T4> t4, ArgType<T5> t5, Function7<T, SpellContext, T1, T2, T3, T4, T5, EvaluationResult> handler) {
        return new Signature<T>() {
            @Override
            public boolean match(List<Fragment> fragments) {
                if (!t1.isolateAndMatch(fragments)) {
                    return false;
                }
                fragments = fragments.subList(t1.argc(fragments), fragments.size());

                if (!t2.isolateAndMatch(fragments)) {
                    return false;
                }
                fragments = fragments.subList(t2.argc(fragments), fragments.size());

                if (!t3.isolateAndMatch(fragments)) {
                    return false;
                }
                fragments = fragments.subList(t3.argc(fragments), fragments.size());

                if (!t4.isolateAndMatch(fragments)) {
                    return false;
                }
                fragments = fragments.subList(t4.argc(fragments), fragments.size());

                if (!t5.isolateAndMatch(fragments)) {
                    return false;
                }
                fragments = fragments.subList(t5.argc(fragments), fragments.size());

                return true;
            }

            @Override
            public EvaluationResult run(T trick, SpellContext ctx, List<Fragment> fragments) throws BlunderException {
                var args1 = t1.isolate(0, fragments);
                var v1 = t1.compose(trick, ctx, args1);
                fragments = fragments.subList(args1.size(), fragments.size());

                var args2 = t2.isolate(0, fragments);
                var v2 = t2.compose(trick, ctx, args2);
                fragments = fragments.subList(args2.size(), fragments.size());

                var args3 = t3.isolate(0, fragments);
                var v3 = t3.compose(trick, ctx, args3);
                fragments = fragments.subList(args3.size(), fragments.size());

                var args4 = t4.isolate(0, fragments);
                var v4 = t4.compose(trick, ctx, args4);
                fragments = fragments.subList(args4.size(), fragments.size());

                var args5 = t5.isolate(0, fragments);
                var v5 = t5.compose(trick, ctx, args5);
                fragments = fragments.subList(args5.size(), fragments.size());

                return handler.apply(trick, ctx, v1, v2, v3, v4, v5);
            }

            @Override
            public MutableText asText() {
                var text = Text.empty();

                text = text.append(t1.asText());
                text = text.append(", ");
                text = text.append(t2.asText());
                text = text.append(", ");
                text = text.append(t3.asText());
                text = text.append(", ");
                text = text.append(t4.asText());
                text = text.append(", ");
                text = text.append(t5.asText());

                return text;
            }
        };
    }

    static <T extends Trick<T>, T1, T2, T3, T4, T5, T6> Signature<T> of(ArgType<T1> t1, ArgType<T2> t2, ArgType<T3> t3, ArgType<T4> t4, ArgType<T5> t5, ArgType<T6> t6, Function8<T, SpellContext, T1, T2, T3, T4, T5, T6, EvaluationResult> handler) {
        return new Signature<T>() {
            @Override
            public boolean match(List<Fragment> fragments) {
                if (!t1.isolateAndMatch(fragments)) {
                    return false;
                }
                fragments = fragments.subList(t1.argc(fragments), fragments.size());

                if (!t2.isolateAndMatch(fragments)) {
                    return false;
                }
                fragments = fragments.subList(t2.argc(fragments), fragments.size());

                if (!t3.isolateAndMatch(fragments)) {
                    return false;
                }
                fragments = fragments.subList(t3.argc(fragments), fragments.size());

                if (!t4.isolateAndMatch(fragments)) {
                    return false;
                }
                fragments = fragments.subList(t4.argc(fragments), fragments.size());

                if (!t5.isolateAndMatch(fragments)) {
                    return false;
                }
                fragments = fragments.subList(t5.argc(fragments), fragments.size());

                if (!t6.isolateAndMatch(fragments)) {
                    return false;
                }
                fragments = fragments.subList(t6.argc(fragments), fragments.size());

                return true;
            }

            @Override
            public EvaluationResult run(T trick, SpellContext ctx, List<Fragment> fragments) throws BlunderException {
                var args1 = t1.isolate(0, fragments);
                var v1 = t1.compose(trick, ctx, args1);
                fragments = fragments.subList(args1.size(), fragments.size());

                var args2 = t2.isolate(0, fragments);
                var v2 = t2.compose(trick, ctx, args2);
                fragments = fragments.subList(args2.size(), fragments.size());

                var args3 = t3.isolate(0, fragments);
                var v3 = t3.compose(trick, ctx, args3);
                fragments = fragments.subList(args3.size(), fragments.size());

                var args4 = t4.isolate(0, fragments);
                var v4 = t4.compose(trick, ctx, args4);
                fragments = fragments.subList(args4.size(), fragments.size());

                var args5 = t5.isolate(0, fragments);
                var v5 = t5.compose(trick, ctx, args5);
                fragments = fragments.subList(args5.size(), fragments.size());

                var args6 = t6.isolate(0, fragments);
                var v6 = t6.compose(trick, ctx, args6);
                fragments = fragments.subList(args6.size(), fragments.size());

                return handler.apply(trick, ctx, v1, v2, v3, v4, v5, v6);
            }

            @Override
            public MutableText asText() {
                var text = Text.empty();

                text = text.append(t1.asText());
                text = text.append(", ");
                text = text.append(t2.asText());
                text = text.append(", ");
                text = text.append(t3.asText());
                text = text.append(", ");
                text = text.append(t4.asText());
                text = text.append(", ");
                text = text.append(t5.asText());
                text = text.append(", ");
                text = text.append(t6.asText());

                return text;
            }
        };
    }
}
