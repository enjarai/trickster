package dev.enjarai.trickster.spell.type;

import java.util.List;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.Trick;
import io.vavr.*;

public interface Signature<T extends Trick<T>, R> {
    boolean match(List<Fragment> fragments);

    R run(T trick, SpellContext ctx, List<Fragment> fragments) throws BlunderException;

    static <T extends Trick<T>, R> Signature<T, R> of(Function2<T, SpellContext, R> handler) {
        return new Signature<T, R>() {
            @Override
            public boolean match(List<Fragment> fragments) {
                return true;
            }

            @Override
            public R run(T trick, SpellContext ctx, List<Fragment> fragments) throws BlunderException {
                return handler.apply(trick, ctx);
            }
        };
    }

    static <T extends Trick<T>, R, T1> Signature<T, R> of(ArgType<T1> t1, Function3<T, SpellContext, T1, R> handler) {
        return new Signature<T, R>() {
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
            public R run(T trick, SpellContext ctx, List<Fragment> fragments) throws BlunderException {
                var args1 = t1.isolate(0, fragments);
                var v1 = t1.compose(args1);
                fragments = fragments.subList(args1.size(), fragments.size());

                return handler.apply(trick, ctx, v1);
            }
        };
    }

    static <T extends Trick<T>, R, T1, T2> Signature<T, R> of(ArgType<T1> t1, ArgType<T2> t2,
            Function4<T, SpellContext, T1, T2, R> handler) {
        return new Signature<T, R>() {
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
            public R run(T trick, SpellContext ctx, List<Fragment> fragments) throws BlunderException {
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

    static <T extends Trick<T>, R, T1, T2, T3> Signature<T, R> of(ArgType<T1> t1, ArgType<T2> t2, ArgType<T3> t3,
            Function5<T, SpellContext, T1, T2, T3, R> handler) {
        return new Signature<T, R>() {
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
            public R run(T trick, SpellContext ctx, List<Fragment> fragments) throws BlunderException {
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

    static <T extends Trick<T>, R, T1, T2, T3, T4> Signature<T, R> of(ArgType<T1> t1, ArgType<T2> t2, ArgType<T3> t3,
            ArgType<T4> t4, Function6<T, SpellContext, T1, T2, T3, T4, R> handler) {
        return new Signature<T, R>() {
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
            public R run(T trick, SpellContext ctx, List<Fragment> fragments) throws BlunderException {
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

    static <T extends Trick<T>, R, T1, T2, T3, T4, T5> Signature<T, R> of(ArgType<T1> t1, ArgType<T2> t2,
            ArgType<T3> t3, ArgType<T4> t4, ArgType<T5> t5, Function7<T, SpellContext, T1, T2, T3, T4, T5, R> handler) {
        return new Signature<T, R>() {
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
            public R run(T trick, SpellContext ctx, List<Fragment> fragments) throws BlunderException {
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

    static <T extends Trick<T>, R, T1, T2, T3, T4, T5, T6> Signature<T, R> of(ArgType<T1> t1, ArgType<T2> t2,
            ArgType<T3> t3, ArgType<T4> t4, ArgType<T5> t5, ArgType<T6> t6,
            Function8<T, SpellContext, T1, T2, T3, T4, T5, T6, R> handler) {
        return new Signature<T, R>() {
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
            public R run(T trick, SpellContext ctx, List<Fragment> fragments) throws BlunderException {
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
