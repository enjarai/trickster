package dev.enjarai.trickster.spell.trick.color;

import dev.enjarai.trickster.block.SpellColoredBlockEntity;
import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.fleck.PaintableFleck;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlockInvalidBlunder;
import dev.enjarai.trickster.spell.blunder.NoSuchFleckBlunder;
import dev.enjarai.trickster.spell.fragment.*;
import dev.enjarai.trickster.spell.trick.fleck.AbstractFleckTrick;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;
import java.util.Optional;

public class PaintTrick extends AbstractFleckTrick<PaintTrick> {
    public PaintTrick() {
        super(Pattern.of(4, 6, 7, 4, 3, 0, 1, 2, 5, 8, 7), Signature.of(FragmentType.VECTOR, FragmentType.COLOR.variadicOfArg().require(), PaintTrick::run, FragmentType.VECTOR));
        overload(Signature.of(FragmentType.NUMBER, FragmentType.COLOR, FragmentType.ENTITY.variadicOfArg().unpack().optionalOfArg(), PaintTrick::paintFleck, FragmentType.NUMBER));
    }

    public VectorFragment run(SpellContext ctx, VectorFragment pos, List<ColorFragment> colors) {
        var blockPos = pos.toBlockPos();
        var world = ctx.source().getWorld();

        expectCanBuild(ctx, blockPos);

        if (world.getBlockEntity(blockPos) instanceof SpellColoredBlockEntity ent) {
            var cs = new int[colors.size()];
            for (int i = 0; i < cs.length; i++) {
                cs[i] = colors.get(i).color();
            }
            ent.setColors(cs);
            return pos;
        }
        throw new BlockInvalidBlunder(this);
    }

    public NumberFragment paintFleck(SpellContext ctx, NumberFragment id, ColorFragment color, Optional<List<EntityFragment>> targets) {
        var players = getPlayersInRangeOrTargets(ctx, targets);
        players.forEach(player -> {
            var flecks = player.getComponent(ModEntityComponents.FLECKS).getFlecks();
            if (!flecks.containsKey(id.asInt())) {
                throw new NoSuchFleckBlunder(this);
            }
            var fleck = flecks.get(id.asInt()).fleck();
            if (fleck instanceof PaintableFleck rfleck) {
                display(ctx, id, rfleck.paintFleck(color.color()), Optional.of(List.of(EntityFragment.from(player))));
            }
        });

        return id;
    }
}
