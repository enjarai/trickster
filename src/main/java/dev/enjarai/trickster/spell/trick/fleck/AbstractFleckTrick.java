package dev.enjarai.trickster.spell.trick.fleck;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.fleck.Fleck;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

public abstract class AbstractFleckTrick<T extends AbstractFleckTrick<T>> extends Trick<T> {
    public AbstractFleckTrick(Pattern pattern) {
        super(pattern);
    }

    public AbstractFleckTrick(Pattern pattern, List<Signature<T>> handlers) {
        super(pattern, handlers);
    }

    public AbstractFleckTrick(Pattern pattern, Signature<T> primary) {
        super(pattern, primary);
    }

    protected Fragment display(SpellContext ctx, NumberFragment id, Fleck fleck, Optional<List<EntityFragment>> targets) {
        var players = getPlayersInRangeOrTargets(ctx, targets);

        players.forEach(player -> player.getComponent(ModEntityComponents.FLECKS).addFleck(id.asInt(), fleck));
        return id;
    }

    protected Stream<PlayerEntity> getPlayersInRangeOrTargets(SpellContext ctx, Optional<List<EntityFragment>> targets) {
        return targets
                .map(List::stream)
                .map(
                        stream -> stream
                                .map(
                                        frag -> frag.getEntity(ctx)
                                                .orElseThrow(() -> new UnknownEntityBlunder(this))
                                )
                                .flatMap(e -> e instanceof PlayerEntity player ? Stream.of(player) : Stream.of())
                )
                .orElseGet(() -> {
                    var pos = ctx.source().getPos();
                    var entities = new ArrayList<PlayerEntity>();
                    ctx.source().getWorld().collectEntitiesByType(
                            EntityType.PLAYER, new Box(
                                    pos.x() - 64, pos.y() - 64, pos.z() - 64,
                                    pos.x() + 64, pos.y() + 64, pos.z() + 64
                            ),
                            e -> e.getPos().squaredDistanceTo(pos.x(), pos.y(), pos.z()) <= 64 * 64, entities
                    ); //find all the players within a 64 block sphere
                    return entities.stream();
                });
    }
}
