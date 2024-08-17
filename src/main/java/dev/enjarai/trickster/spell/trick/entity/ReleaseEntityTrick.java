package dev.enjarai.trickster.spell.trick.entity;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.item.component.EntityStorageComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.EntityInvalidBlunder;
import dev.enjarai.trickster.spell.trick.blunder.NoPlayerBlunder;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Optional;

public class ReleaseEntityTrick extends Trick {
    public ReleaseEntityTrick() {
        super(Pattern.of(4, 3, 6, 7, 4, 8, 2));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos = expectInput(fragments, FragmentType.VECTOR, 0).vector();
        var player = ctx.source().getPlayer().orElseThrow(() -> new NoPlayerBlunder(this));
        var offhand = player.getOffHandStack();
        var entityStorage = offhand.get(ModComponents.ENTITY_STORAGE);

        if (entityStorage == null)
            return VoidFragment.INSTANCE;

        if (entityStorage.nbt().isPresent()) {
            var dist = player.getPos().distanceTo(new Vec3d(pos.x(), pos.y(), pos.z()));
            ctx.useMana(this, (float) (60 + Math.pow(dist, (dist / 5))));
            offhand.set(ModComponents.ENTITY_STORAGE, new EntityStorageComponent(Optional.empty()));

            var entity = EntityType.getEntityFromNbt(entityStorage.nbt().get(), ctx.source().getWorld());

            if (entity.isPresent()) {
                entity.get().setPos(pos.x(), pos.y(), pos.z());
                if (!ctx.source().getWorld().spawnEntity(entity.get())) {
                    throw new EntityInvalidBlunder(this);
                }
                return EntityFragment.from(entity.get());
            } else {
                Trickster.LOGGER.warn("Failed to read entity from offhand due to invalid NBT, entity storage component has been cleared");
                return VoidFragment.INSTANCE;
            }
        } else return VoidFragment.INSTANCE;
    }
}
