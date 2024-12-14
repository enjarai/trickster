package dev.enjarai.trickster.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class ManaUsedCriterion extends AbstractCriterion<ManaUsedCriterion.Conditions> {
    @Override
    public Codec<ManaUsedCriterion.Conditions> getConditionsCodec() {
        return ManaUsedCriterion.Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, float amountUsed) {
        super.trigger(player, conditions -> conditions.match(amountUsed));
    }

    public record Conditions(Optional<LootContextPredicate> player, Optional<Float> min, Optional<Float> max) implements AbstractCriterion.Conditions {

        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player),
                Codec.FLOAT.optionalFieldOf("min").forGetter(Conditions::min),
                Codec.FLOAT.optionalFieldOf("max").forGetter(Conditions::max)).apply(instance, Conditions::new));

        public boolean match(float amountUsed) {
            boolean b = true;

            if (min.isPresent())
                b = amountUsed >= min.get();

            if (b && max.isPresent())
                b = amountUsed <= max.get();

            return b;
        }

        @Override
        public void validate(LootContextPredicateValidator validator) {
            AbstractCriterion.Conditions.super.validate(validator);
        }
    }
}
