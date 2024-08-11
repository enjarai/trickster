package dev.enjarai.trickster.spell.mana;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.cca.ManaComponent;
import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.entity.ModEntities;
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
    public final float taxRatio;
    private float availableMana;

    public static final StructEndec<ManaLink> ENDEC = StructEndecBuilder.of(
            EndecTomfoolery.UUID.fieldOf("target_uuid", manaLink -> manaLink.sourceUuid),
            Endec.FLOAT.fieldOf("tax_ratio", manaLink -> manaLink.taxRatio),
            Endec.FLOAT.fieldOf("available_mana", manaLink -> manaLink.availableMana),
            ManaLink::new
    );

    private ManaLink(UUID targetUuid, float taxRatio, float availableMana) {
        this.source = (trickSource, world) -> {
            var entity = world.getEntity(targetUuid);

            if (entity instanceof LivingEntity living && EntityFragment.isValidEntity(entity)) {
                return living;
            }

            throw new UnknownEntityBlunder(trickSource);
        };
        this.manaPool = (trickSource, world) -> ModEntityCumponents.MANA.get(source.apply(trickSource, world));
        this.sourceUuid = targetUuid;
        this.taxRatio = taxRatio;
        this.availableMana = availableMana;
    }

    /**
     * A link to a living entity's mana pool.
     * @param source the living entity being linked. Its health is used as the denominator of the tax ratio.
     * @param ownerHealth the numerator of the tax ratio.
     * @param availableMana the total amount of mana this link may drain.
     */
    public ManaLink(LivingEntity source, float ownerHealth, float availableMana) {
        this(source.getUuid(), ownerHealth / source.getHealth(), source.getType().isIn(ModEntities.MANA_DEVOID) ? 0 : availableMana);
    }

    /**
     * Drains the source's mana, applying tax to the owner of the link.
     * @param trickSource the trick this call originates from.
     * @param owner the mana pool that this link belongs to.
     * @param amount the amount of mana to drain from the linked source.
     * @return the amount of mana drained from the linked source.
     * @throws NotEnoughManaBlunder if the owner does not have sufficient mana to drain the linked source.
     */
    public float useMana(Trick trickSource, ServerWorld world, ManaPool owner, float amount) throws NotEnoughManaBlunder {
        var taxAmount = amount / taxRatio;

        if (!owner.decrease(taxAmount))
            throw new NotEnoughManaBlunder(trickSource, taxAmount);

        var manaPool = this.manaPool.apply(trickSource, world);
        float result = availableMana;

        if (amount > availableMana) {
            manaPool.decrease(availableMana);
            availableMana = 0;
        } else {
            if (manaPool.decrease(amount))
                availableMana -= amount;
            else availableMana = 0;
        }

        return result - availableMana;
    }

    public float getAvailable() {
        return availableMana;
    }
}
