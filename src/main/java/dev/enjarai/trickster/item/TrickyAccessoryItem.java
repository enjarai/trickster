package dev.enjarai.trickster.item;

import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.PatternGlyph;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import io.wispforest.accessories.api.AccessoryItem;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.Optional;

public class TrickyAccessoryItem extends AccessoryItem {
    public TrickyAccessoryItem(Settings settings) {
        super(settings);
    }

    public static void tryWard(SpellContext triggerCtx, ServerPlayerEntity player, Trick source, List<Fragment> inputs) throws BlunderException {
        var sourceFragment = triggerCtx.source().getCaster()
                .<Fragment>map(EntityFragment::from)
                .orElse(new VectorFragment(triggerCtx.source().getPos()));

        var charmStack = SlotReference.of(player, "charm", 0).getStack();
        if (charmStack == null || charmStack.isEmpty()) {
            return;
        }

        var spellComponent = charmStack.get(ModComponents.SPELL);
        if (spellComponent == null) {
            return;
        }

        var spell = spellComponent.spell();
        var caster = ModEntityCumponents.CASTER.get(player);

        caster.queueSpellAndCast(spell, List.of(new SpellPart(new PatternGlyph(source.getPattern())), sourceFragment, new ListFragment(inputs)), Optional.empty());
    }
}
