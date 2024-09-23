package dev.enjarai.trickster.spell.trick.fleck;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.fleck.Fleck;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public abstract class AbstractFleckTrick extends Trick {

    public AbstractFleckTrick(Pattern pattern) {
        super(pattern);
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) {

        var playerList = supposeType(fragments.getLast(), FragmentType.LIST) // take either a list of entities
            .map(listFragment -> listFragment.fragments().stream()
                .map(entry -> expectType(entry, FragmentType.ENTITY)))
            .or(() -> supposeType(fragments.getLast(), FragmentType.ENTITY) // or a single entity
                .map(Stream::of))
            .map(stream -> stream                                           // and throw if an entity doesn't exit
                .map(frag -> frag.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this))).toList());

        var id = expectType(fragments.removeFirst(), FragmentType.NUMBER).asInt();
        var pos = ctx.source().getPos();

        ArrayList<PlayerEntity> entities = new ArrayList<>();
        ctx.source().getWorld().collectEntitiesByType(
                EntityType.PLAYER, new Box(
                        pos.x() - 64, pos.y() - 64, pos.z() - 64,
                        pos.x() + 64, pos.y() + 64, pos.z() + 64
                ),
                e -> e.getPos().squaredDistanceTo(pos.x(), pos.y(), pos.z()) <= 64*64, entities
         ); //find all the players within a 64 block sphere

        if (playerList.isPresent()) {
            fragments.removeLast();
        }

        entities.stream()
            .filter(serverPlayer -> playerList.map(list -> list.contains(serverPlayer)).orElse(true)) //if a list of players was specified, filter against it
            .forEach(serverPlayer -> serverPlayer.getComponent(ModEntityComponents.FLECKS).addFleck(id, makeFleck(ctx, fragments)));

        return fragments.getFirst();
    }

    protected abstract Fleck makeFleck(SpellContext ctx, List<Fragment> fragments) throws BlunderException;
}
