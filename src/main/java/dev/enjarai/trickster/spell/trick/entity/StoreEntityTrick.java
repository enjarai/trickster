package dev.enjarai.trickster.spell.trick.entity;

import dev.enjarai.trickster.entity.ModEntities;
import dev.enjarai.trickster.item.component.EntityStorageComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.List;
import java.util.Optional;

public class StoreEntityTrick extends Trick {
    public StoreEntityTrick() {
        super(Pattern.of(5, 2, 4, 3, 6, 7, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var target = expectInput(fragments, FragmentType.ENTITY, 0).getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this));

        if (target instanceof PlayerEntity)
            throw new EntityInvalidBlunder(this);

        if (target.getType().isIn(ModEntities.IRREPRESSIBLE))
            throw new EntityCannotBeStoredBlunder(this, target);

        expectCanInteract(ctx, target);
        expectCanAttack(ctx, target);

        var player = ctx.source().getPlayer().orElseThrow(() -> new NoPlayerBlunder(this));
        var offhand = player.getOffHandStack();
        var entityStorage = offhand.get(ModComponents.ENTITY_STORAGE);

        if (entityStorage == null)
            throw new ItemInvalidBlunder(this);

        if (entityStorage.nbt().isEmpty()) {
            var dist = player.getPos().distanceTo(target.getPos());
            ctx.useMana(this, (float) (60 + Math.pow(dist, (dist / 5))));

            var compound = new NbtCompound();
            target.saveSelfNbt(compound);
            offhand.set(ModComponents.ENTITY_STORAGE, new EntityStorageComponent(Optional.of(compound)));
            target.remove(Entity.RemovalReason.CHANGED_DIMENSION);
        } else throw new EntityAlreadyStoredBlunder(this);

        return VoidFragment.INSTANCE;
    }
}
