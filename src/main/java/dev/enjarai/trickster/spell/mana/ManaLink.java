package dev.enjarai.trickster.spell.mana;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.cca.ManaComponent;
import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.NotEnoughManaBlunder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Uuids;
import net.minecraft.world.World;

import java.util.Objects;

public final class ManaLink {
    public final ManaPool owner;
    public final LivingEntity source;
    public final ManaComponent manaPool;
    public final float taxRatio;
    private float availableMana;

    // TODO if you use this codec im going to murder you  - Rai
    public static final Codec<ManaLink> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ManaPool.CODEC.get().fieldOf("owner").forGetter(manaLink -> manaLink.owner),
            Uuids.CODEC.fieldOf("source_uuid").forGetter(manaLink -> manaLink.source.getUuid()),
            World.CODEC.fieldOf("source_world").forGetter(manaLink -> manaLink.source.getWorld().getRegistryKey()),
            Codec.FLOAT.fieldOf("tax_ratio").forGetter(manaLink -> manaLink.taxRatio),
            Codec.FLOAT.fieldOf("available_mana").forGetter(manaLink -> manaLink.availableMana)
    ).apply(instance, (owner, sourceUuid, sourceWorld, taxRatio, availableMana) -> new ManaLink((LivingEntity) Objects.requireNonNull(Trickster.getCurrentServer().getWorld(sourceWorld)).getEntity(sourceUuid), owner, taxRatio, availableMana)));

                                                     // TODO: FIX ME AURI
    private ManaLink(LivingEntity source, ManaPool owner, float taxRatio, float availableMana) {
        this.owner = owner;
        this.source = source;
        this.manaPool = ModEntityCumponents.MANA.get(source);
        this.taxRatio = taxRatio;
        this.availableMana = availableMana;
    }

    public ManaLink(ManaPool owner, LivingEntity source, float ownerHealth, float availableMana) {
        this.owner = owner;
        this.source = source;
        this.manaPool = ModEntityCumponents.MANA.get(source);
        this.taxRatio = ownerHealth / source.getHealth();
        this.availableMana = availableMana;
    }

    public float useMana(Trick trickSource, float amount) throws BlunderException {
        if (!owner.decrease(amount / taxRatio))
            throw new NotEnoughManaBlunder(trickSource, amount);

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
