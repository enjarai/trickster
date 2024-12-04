package dev.enjarai.trickster.spell.trick.type;

import java.util.List;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.Trick;
import io.vavr.Function3;
import io.vavr.Function4;
import io.vavr.Function5;
import io.vavr.Function6;
import io.vavr.Function7;
import io.vavr.Function8;

public interface TrickSignature<T extends Trick<T>> {
    boolean match(List<Fragment> fragments);
    Fragment run(T trick, SpellContext ctx, List<Fragment> fragments) throws BlunderException;

    static <T extends Trick<T>, T1> TrickSignature<T> of(ArgType<T1> t1, Function3<T, SpellContext, T1, Fragment> handler) {
        return new TrickSignature<T>() {
            @Override
            public boolean match(List<Fragment> fragments) {
                var args1 = t1.isolate(0, fragments);
                fragments = fragments.subList(args1.size(), fragments.size());

                if (!t1.match(args1)) {
                    return false;
                }

                return true;
            }

            @Override
            public Fragment run(T trick, SpellContext ctx, List<Fragment> fragments) throws BlunderException {
                var args1 = t1.isolate(0, fragments);
                var v1 = t1.compose(args1);
                fragments = fragments.subList(args1.size(), fragments.size());

                return handler.apply(trick, ctx, v1);
            }
        };
    }

    static <T extends Trick<T>, T1, T2> TrickSignature<T> of(ArgType<T1> t1, ArgType<T2> t2, Function4<T, SpellContext, T1, T2, Fragment> handler) {
        return new TrickSignature<T>() {
            @Override
            public boolean match(List<Fragment> fragments) {
                var args1 = t1.isolate(0, fragments);
                fragments = fragments.subList(args1.size(), fragments.size());

                if (!t1.match(args1)) {
                    return false;
                }

                var args2 = t2.isolate(0, fragments);
                fragments = fragments.subList(args2.size(), fragments.size());

                if (!t2.match(args2)) {
                    return false;
                }

                return true;
            }

            @Override
            public Fragment run(T trick, SpellContext ctx, List<Fragment> fragments) throws BlunderException {
                var args1 = t1.isolate(0, fragments);
                var v1 = t1.compose(args1);
                fragments = fragments.subList(args1.size(), fragments.size());

                var args2 = t2.isolate(0, fragments);
                var v2 = t2.compose(args2);
                fragments = fragments.subList(args2.size(), fragments.size());

                return handler.apply(trick, ctx, v1, v2);
            }
        };
    }

    static <T extends Trick<T>, T1, T2, T3> TrickSignature<T> of(ArgType<T1> t1, ArgType<T2> t2, ArgType<T3> t3, Function5<T, SpellContext, T1, T2, T3, Fragment> handler) {
        return new TrickSignature<T>() {
            @Override
            public boolean match(List<Fragment> fragments) {
                var args1 = t1.isolate(0, fragments);
                fragments = fragments.subList(args1.size(), fragments.size());

                if (!t1.match(args1)) {
                    return false;
                }

                var args2 = t2.isolate(0, fragments);
                fragments = fragments.subList(args2.size(), fragments.size());

                if (!t2.match(args2)) {
                    return false;
                }

                var args3 = t3.isolate(0, fragments);
                fragments = fragments.subList(args3.size(), fragments.size());

                if (!t3.match(args3)) {
                    return false;
                }

                return true;
            }

            @Override
            public Fragment run(T trick, SpellContext ctx, List<Fragment> fragments) throws BlunderException {
                var args1 = t1.isolate(0, fragments);
                var v1 = t1.compose(args1);
                fragments = fragments.subList(args1.size(), fragments.size());

                var args2 = t2.isolate(0, fragments);
                var v2 = t2.compose(args2);
                fragments = fragments.subList(args2.size(), fragments.size());

                var args3 = t3.isolate(0, fragments);
                var v3 = t3.compose(args3);
                fragments = fragments.subList(args3.size(), fragments.size());

                return handler.apply(trick, ctx, v1, v2, v3);
            }
        };
    }

    static <T extends Trick<T>, T1, T2, T3, T4> TrickSignature<T> of(ArgType<T1> t1, ArgType<T2> t2, ArgType<T3> t3, ArgType<T4> t4, Function6<T, SpellContext, T1, T2, T3, T4, Fragment> handler) {
        return new TrickSignature<T>() {
            @Override
            public boolean match(List<Fragment> fragments) {
                var args1 = t1.isolate(0, fragments);
                fragments = fragments.subList(args1.size(), fragments.size());

                if (!t1.match(args1)) {
                    return false;
                }

                var args2 = t2.isolate(0, fragments);
                fragments = fragments.subList(args2.size(), fragments.size());

                if (!t2.match(args2)) {
                    return false;
                }

                var args3 = t3.isolate(0, fragments);
                fragments = fragments.subList(args3.size(), fragments.size());

                if (!t3.match(args3)) {
                    return false;
                }

                var args4 = t4.isolate(0, fragments);
                fragments = fragments.subList(args4.size(), fragments.size());

                if (!t4.match(args4)) {
                    return false;
                }

                return true;
            }

            @Override
            public Fragment run(T trick, SpellContext ctx, List<Fragment> fragments) throws BlunderException {
                var args1 = t1.isolate(0, fragments);
                var v1 = t1.compose(args1);
                fragments = fragments.subList(args1.size(), fragments.size());

                var args2 = t2.isolate(0, fragments);
                var v2 = t2.compose(args2);
                fragments = fragments.subList(args2.size(), fragments.size());

                var args3 = t3.isolate(0, fragments);
                var v3 = t3.compose(args3);
                fragments = fragments.subList(args3.size(), fragments.size());

                var args4 = t4.isolate(0, fragments);
                var v4 = t4.compose(args4);
                fragments = fragments.subList(args4.size(), fragments.size());

                return handler.apply(trick, ctx, v1, v2, v3, v4);
            }
        };
    }

    static <T extends Trick<T>, T1, T2, T3, T4, T5> TrickSignature<T> of(ArgType<T1> t1, ArgType<T2> t2, ArgType<T3> t3, ArgType<T4> t4, ArgType<T5> t5, Function7<T, SpellContext, T1, T2, T3, T4, T5, Fragment> handler) {
        return new TrickSignature<T>() {
            @Override
            public boolean match(List<Fragment> fragments) {
                var args1 = t1.isolate(0, fragments);
                fragments = fragments.subList(args1.size(), fragments.size());

                if (!t1.match(args1)) {
                    return false;
                }

                var args2 = t2.isolate(0, fragments);
                fragments = fragments.subList(args2.size(), fragments.size());

                if (!t2.match(args2)) {
                    return false;
                }

                var args3 = t3.isolate(0, fragments);
                fragments = fragments.subList(args3.size(), fragments.size());

                if (!t3.match(args3)) {
                    return false;
                }

                var args4 = t4.isolate(0, fragments);
                fragments = fragments.subList(args4.size(), fragments.size());

                if (!t4.match(args4)) {
                    return false;
                }

                var args5 = t5.isolate(0, fragments);
                fragments = fragments.subList(args5.size(), fragments.size());

                if (!t5.match(args5)) {
                    return false;
                }

                return true;
            }

            @Override
            public Fragment run(T trick, SpellContext ctx, List<Fragment> fragments) throws BlunderException {
                var args1 = t1.isolate(0, fragments);
                var v1 = t1.compose(args1);
                fragments = fragments.subList(args1.size(), fragments.size());

                var args2 = t2.isolate(0, fragments);
                var v2 = t2.compose(args2);
                fragments = fragments.subList(args2.size(), fragments.size());

                var args3 = t3.isolate(0, fragments);
                var v3 = t3.compose(args3);
                fragments = fragments.subList(args3.size(), fragments.size());

                var args4 = t4.isolate(0, fragments);
                var v4 = t4.compose(args4);
                fragments = fragments.subList(args4.size(), fragments.size());

                var args5 = t5.isolate(0, fragments);
                var v5 = t5.compose(args5);
                fragments = fragments.subList(args5.size(), fragments.size());

                return handler.apply(trick, ctx, v1, v2, v3, v4, v5);
            }
        };
    }

    static <T extends Trick<T>, T1, T2, T3, T4, T5, T6> TrickSignature<T> of(ArgType<T1> t1, ArgType<T2> t2, ArgType<T3> t3, ArgType<T4> t4, ArgType<T5> t5, ArgType<T6> t6, Function8<T, SpellContext, T1, T2, T3, T4, T5, T6, Fragment> handler) {
        return new TrickSignature<T>() {
            @Override
            public boolean match(List<Fragment> fragments) {
                var args1 = t1.isolate(0, fragments);
                fragments = fragments.subList(args1.size(), fragments.size());

                if (!t1.match(args1)) {
                    return false;
                }

                var args2 = t2.isolate(0, fragments);
                fragments = fragments.subList(args2.size(), fragments.size());

                if (!t2.match(args2)) {
                    return false;
                }

                var args3 = t3.isolate(0, fragments);
                fragments = fragments.subList(args3.size(), fragments.size());

                if (!t3.match(args3)) {
                    return false;
                }

                var args4 = t4.isolate(0, fragments);
                fragments = fragments.subList(args4.size(), fragments.size());

                if (!t4.match(args4)) {
                    return false;
                }

                var args5 = t5.isolate(0, fragments);
                fragments = fragments.subList(args5.size(), fragments.size());

                if (!t5.match(args5)) {
                    return false;
                }

                var args6 = t6.isolate(0, fragments);
                fragments = fragments.subList(args6.size(), fragments.size());

                if (!t6.match(args6)) {
                    return false;
                }

                return true;
            }

            @Override
            public Fragment run(T trick, SpellContext ctx, List<Fragment> fragments) throws BlunderException {
                var args1 = t1.isolate(0, fragments);
                var v1 = t1.compose(args1);
                fragments = fragments.subList(args1.size(), fragments.size());

                var args2 = t2.isolate(0, fragments);
                var v2 = t2.compose(args2);
                fragments = fragments.subList(args2.size(), fragments.size());

                var args3 = t3.isolate(0, fragments);
                var v3 = t3.compose(args3);
                fragments = fragments.subList(args3.size(), fragments.size());

                var args4 = t4.isolate(0, fragments);
                var v4 = t4.compose(args4);
                fragments = fragments.subList(args4.size(), fragments.size());

                var args5 = t5.isolate(0, fragments);
                var v5 = t5.compose(args5);
                fragments = fragments.subList(args5.size(), fragments.size());

                var args6 = t6.isolate(0, fragments);
                var v6 = t6.compose(args6);
                fragments = fragments.subList(args6.size(), fragments.size());

                return handler.apply(trick, ctx, v1, v2, v3, v4, v5, v6);
            }
        };
    }
}
