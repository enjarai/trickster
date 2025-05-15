package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.world.LightType;

import java.util.Optional;

public class GetLightLevelTrick extends Trick<GetLightLevelTrick> {
    public GetLightLevelTrick() {
        super(Pattern.of(8, 4, 0, 1, 2, 0, 6, 8, 2), Signature.of(FragmentType.VECTOR, FragmentType.BOOLEAN.optionalOf(), GetLightLevelTrick::get));
    }

    public Fragment get(SpellContext ctx, VectorFragment pos, Optional<BooleanFragment> sky) throws BlunderException {
        var blockPos = pos.toBlockPos();
        var world = ctx.source().getWorld();

        expectLoaded(ctx, blockPos);

        if (sky.isPresent()) {
            if (sky.get().asBoolean()) {
                return new NumberFragment(world.getLightLevel(LightType.SKY, blockPos));
            } else {
                return new NumberFragment(world.getLightLevel(LightType.BLOCK, blockPos));
            }
        } else {
            return new NumberFragment(world.getLightLevel(blockPos));
        }
    }
}
