package dev.enjarai.trickster.spell.trick.entity;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.cca.CurseComponent;
import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.InvalidEntityBlunder;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public class CatCurseTrick extends Trick<CatCurseTrick> {
    public CatCurseTrick() {
        super(
                Pattern.of(7, 4, 0, 3, 6, 7, 8, 5, 2, 4),
                Signature.of(FragmentType.ENTITY, CatCurseTrick::curse)
        );
    }

    public Fragment curse(SpellContext ctx, EntityFragment target) throws BlunderException {
        var entity = target.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this));

        if (!(entity instanceof ServerPlayerEntity)) {
            throw new InvalidEntityBlunder(this);
        }

        var component = ModEntityComponents.CURSE.get(entity);

        component.setCurrentCurse(component.getCurrentCurse() == CurseComponent.Curse.MEOW_MRRP
                ? CurseComponent.Curse.NONE
                : CurseComponent.Curse.MEOW_MRRP);

        return VoidFragment.INSTANCE;
    }

    @Override
    public @Nullable Set<UUID> restricted() {
        return Trickster.THE_MAKERS_OF_KIBTY;
    }
}
