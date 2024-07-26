package dev.enjarai.trickster.spell.mana;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.cca.ManaComponent;
import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.NotEnoughManaBlunder;
import dev.enjarai.trickster.spell.trick.blunder.UnknownEntityBlunder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;

import java.util.UUID;
import java.util.function.BiFunction;

public final class ManaLink {
    public final BiFunction<Trick, ServerWorld, LivingEntity> source;
    public final BiFunction<Trick, ServerWorld, ManaComponent> manaPool;
    public final UUID sourceUuid;
    public final float taxRatio;
    private float availableMana;

    public static final Codec<ManaLink> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Uuids.CODEC.fieldOf("target_uuid").forGetter(manaLink -> manaLink.sourceUuid),
            Codec.FLOAT.fieldOf("tax_ratio").forGetter(manaLink -> manaLink.taxRatio),
            Codec.FLOAT.fieldOf("available_mana").forGetter(manaLink -> manaLink.availableMana)
    ).apply(instance, ManaLink::new));

                                // TODO: FIX ME AURI
    private ManaLink(UUID targetUuid, float taxRatio, float availableMana) {
        this.source = (trickSource, world) -> {
            var entity = world.getEntity(targetUuid);

            if (entity instanceof LivingEntity living)
                return living;

            throw new UnknownEntityBlunder(trickSource);
        };
        this.manaPool = (trickSource, world) -> ModEntityCumponents.MANA.get(source.apply(trickSource, world));
        this.sourceUuid = targetUuid;
        this.taxRatio = taxRatio;
        this.availableMana = availableMana;
    }

    public ManaLink(LivingEntity source, float ownerHealth, float availableMana) {
        this.source = (trickSource, world) -> source;
        this.manaPool = (trickSource, world) -> ModEntityCumponents.MANA.get(source);
        this.sourceUuid = source.getUuid();
        this.taxRatio = ownerHealth / source.getHealth();
        this.availableMana = availableMana;
    }

    public float useMana(Trick trickSource, ServerWorld world, ManaPool owner, float amount) throws BlunderException {
        if (!owner.decrease(amount / taxRatio))
            throw new NotEnoughManaBlunder(trickSource, amount);

        var manaPool = this.manaPool.apply(trickSource, world);
        float oldMana = manaPool.get();
        float result = availableMana;

        if (amount > availableMana) {

            if (!manaPool.decrease(availableMana))
                availableMana -= oldMana;
            else
                availableMana = 0;
        } else {
            if (!manaPool.decrease(amount))
                availableMana -= oldMana;
            else
                availableMana -= amount;
        }

        return result - availableMana;
    }

    public float getAvailable() {
        return availableMana;
    }
}
