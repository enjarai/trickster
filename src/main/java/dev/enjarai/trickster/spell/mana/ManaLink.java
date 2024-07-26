package dev.enjarai.trickster.spell.mana;

import com.google.common.base.Function;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.cca.ManaComponent;
import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.Tricks;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.NotEnoughManaBlunder;
import dev.enjarai.trickster.spell.trick.blunder.UnknownEntityBlunder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Uuids;
import net.minecraft.world.World;

import java.util.UUID;

public final class ManaLink {
    public final Function<Trick, LivingEntity> source;
    public final Function<Trick, ManaComponent> manaPool;
    public final float taxRatio;
    private float availableMana;

    // TODO if you use this codec im going to murder you  - Rai
    public static final Codec<ManaLink> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Uuids.CODEC.fieldOf("target_uuid").forGetter(manaLink -> manaLink.source.apply(Tricks.GET_MANA).getUuid()),
            World.CODEC.fieldOf("target_world").forGetter(manaLink -> manaLink.source.apply(Tricks.GET_MANA).getWorld().getRegistryKey()),
            Codec.FLOAT.fieldOf("tax_ratio").forGetter(manaLink -> manaLink.taxRatio),
            Codec.FLOAT.fieldOf("available_mana").forGetter(manaLink -> manaLink.availableMana)
    ).apply(instance, ManaLink::new));

                                                              // TODO: FIX ME AURI
    private ManaLink(UUID targetUuid, RegistryKey<World> worldKey, float taxRatio, float availableMana) {
        this.source = (trickSource) -> {
            var world = Trickster.getCurrentServer().getWorld(worldKey);

            if (world == null)
                throw new UnknownEntityBlunder(trickSource);

            var entity = world.getEntity(targetUuid);

            if (entity instanceof LivingEntity living)
                return living;

            throw new UnknownEntityBlunder(trickSource);
        };
        this.manaPool = (trickSource) -> ModEntityCumponents.MANA.get(source.apply(trickSource));
        this.taxRatio = taxRatio;
        this.availableMana = availableMana;
    }

    public ManaLink(LivingEntity source, float ownerHealth, float availableMana) {
        this.source = (trickSource) -> source;
        this.manaPool = (trickSource) -> ModEntityCumponents.MANA.get(source);
        this.taxRatio = ownerHealth / source.getHealth();
        this.availableMana = availableMana;
    }

    public float useMana(Trick trickSource, ManaPool owner, float amount) throws BlunderException {
        if (!owner.decrease(amount / taxRatio))
            throw new NotEnoughManaBlunder(trickSource, amount);

        var manaPool = this.manaPool.apply(trickSource);
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
