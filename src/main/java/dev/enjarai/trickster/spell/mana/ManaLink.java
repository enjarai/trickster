package dev.enjarai.trickster.spell.mana;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.cca.ManaComponent;
import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.NotEnoughManaBlunder;
import dev.enjarai.trickster.spell.trick.blunder.UnknownEntityBlunder;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;
import java.util.function.BiFunction;

public final class ManaLink {
    public final BiFunction<Trick, ServerWorld, LivingEntity> source;
    public final BiFunction<Trick, ServerWorld, ManaComponent> manaPool;
    public final UUID sourceUuid;
    private float availableMana;

    public static final StructEndec<ManaLink> ENDEC = StructEndecBuilder.of(
            EndecTomfoolery.UUID.fieldOf("target_uuid", manaLink -> manaLink.sourceUuid),
            Endec.FLOAT.fieldOf("available_mana", manaLink -> manaLink.availableMana),
            ManaLink::new
    );

    private ManaLink(UUID targetUuid, float availableMana) {
        this.source = (trickSource, world) -> {
            var entity = world.getEntity(targetUuid);

            if (entity instanceof LivingEntity living && EntityFragment.isValidEntity(entity)) {
                return living;
            }

            throw new UnknownEntityBlunder(trickSource);
        };
        this.manaPool = (trickSource, world) -> ModEntityCumponents.MANA.get(source.apply(trickSource, world));
        this.sourceUuid = targetUuid;
        this.availableMana = availableMana;
    }

    public ManaLink(LivingEntity source, float availableMana) {
        this(source.getUuid(), availableMana);
    }

    public float getTaxRatio(Trick trickSource, SpellSource source) {
        return source.getHealth() / this.source.apply(trickSource, source.getWorld()).getHealth();
    }

    public float useMana(Trick trickSource, SpellSource source, ManaPool owner, float amount) throws BlunderException {
        if (!owner.decrease(amount / getTaxRatio(trickSource, source)))
            throw new NotEnoughManaBlunder(trickSource, amount);

        var manaPool = this.manaPool.apply(trickSource, source.getWorld());
        float result = availableMana;

        if (amount > availableMana) {
            manaPool.decrease(availableMana);
            availableMana = 0;
        } else {
            if (!manaPool.decrease(amount))
                availableMana = 0;
            else
                availableMana -= amount;
        }

        return result - availableMana;
    }

    public float getAvailable() {
        return availableMana;
    }
}
