package dev.enjarai.trickster.spell.trick.wristpocket;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.IncompatibleSourceBlunder;
import dev.enjarai.trickster.spell.blunder.InvalidEntityBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;

import java.util.List;

public class AttackTrick extends Trick {
    public AttackTrick() {
        super(Pattern.of(7, 4 ,1, 0, 6, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var wristpocket = ctx.source().getComponent(ModEntityComponents.WRIST_POCKET)
                .orElseThrow(() -> new IncompatibleSourceBlunder(this));

        var entity = expectInput(fragments,FragmentType.ENTITY,0).getEntity(ctx)
                .orElseThrow(() -> new InvalidEntityBlunder(this));
        var crouch = supposeInput(fragments, 1).map(Fragment::asBoolean).orElse(false);

        ctx.useMana(this, (float) Math.max(
                0.0,
                ctx.source().getPos().distance(entity.getPos().toVector3d()) - 3.0
        ));

        wristpocket.attack(entity, crouch);
        return fragments.getFirst();
    }
}
