package dev.enjarai.trickster.spell.trick.ward;

import java.util.ArrayList;
import java.util.List;

import dev.enjarai.trickster.cca.ModWorldComponents;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.UnknownActionBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.fragment.WardFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.ward.SimpleCubicWard;
import dev.enjarai.trickster.spell.ward.Ward;
import dev.enjarai.trickster.spell.ward.action.ActionType;

public class CreateWardTrick extends Trick<CreateWardTrick> {
    public CreateWardTrick() {
        //TODO: choose a better pattern
        super(Pattern.of(0, 5, 4), Signature.of(FragmentType.VECTOR, FragmentType.VECTOR, FragmentType.PATTERN.variadicOfArg(), CreateWardTrick::simpleCubic, FragmentType.WARD));
    }

    public WardFragment simpleCubic(SpellContext ctx, VectorFragment pos1, VectorFragment pos2, List<PatternGlyph> patterns) {
        var actions = computeActionsFromPatterns(ctx, patterns);
        var ward = SimpleCubicWard.tryCreate(this, ctx, pos1.vector(), pos2.vector(), actions);

        return finish(ctx, ward);
    }

    public WardFragment finish(SpellContext ctx, Ward ward) {
        var manager = ModWorldComponents.WARD_MANAGER.get(ctx.source().getWorld());
        var uuid = manager.put(ward);

        return new WardFragment(uuid);
    }

    public List<ActionType<?>> computeActionsFromPatterns(SpellContext ctx, List<PatternGlyph> patterns) {
        var actions = new ArrayList<ActionType<?>>();

        for (var pattern : patterns) {
            var action = ActionType.lookup(pattern.pattern());

            if (action == null) {
                throw new UnknownActionBlunder(); //TODO: this blunder is bad
            }

            actions.add(action);
        }

        return actions;
    }
}
