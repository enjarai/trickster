package dev.enjarai.trickster.spell.type;

import java.util.List;
import java.util.Optional;

import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.item.component.FragmentComponent;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import io.vavr.collection.HashMap;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

public interface ArgType<T> {
    int argc(List<Fragment> fragments);

    T compose(Trick<?> trick, SpellContext ctx, List<Fragment> fragments);

    boolean match(List<Fragment> fragments);

    ArgType<T> wardOf();

    MutableText asText();

    default List<Fragment> isolate(int start, List<Fragment> fragments) {
        return fragments.subList(start, argc(fragments));
    }

    default ArgType<Optional<T>> optionalOf() {
        return new OptionalArgType<>(this);
    }

    static void tryWard(
            Trick<?> trickSource, SpellContext triggerCtx, EntityFragment targetFragment,
            List<Fragment> fragments
    ) {
        targetFragment.getEntity(triggerCtx).ifPresent(target -> {
            if (target instanceof ServerPlayerEntity player) {
                var triggerCaster = triggerCtx.source().getCaster();

                if (triggerCaster.map(c -> c.equals(player)).orElse(false)) {
                    return;
                }

                var sourceFragment = triggerCaster
                        .<Fragment>map(EntityFragment::from)
                        .orElse(new VectorFragment(triggerCtx.source().getPos()));
                var charmMap = FragmentComponent.getUserMergedMap(player, "charm", HashMap::empty);
                var spell = charmMap.get(trickSource.getPattern());
                var caster = ModEntityComponents.CASTER.get(player);

                spell.peek(s -> {
                    ModCriteria.TRIGGER_WARD.trigger(player);
                    caster.queueSpellAndCast(s, List.of(sourceFragment, new ListFragment(fragments)), Optional.empty());
                });
            }
        });
    }
}
