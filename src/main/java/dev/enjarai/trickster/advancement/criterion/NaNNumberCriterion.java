package dev.enjarai.trickster.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class NaNNumberCriterion extends AbstractCriterion<NaNNumberCriterion.Conditions> {
    @Override
    public Codec<NaNNumberCriterion.Conditions> getConditionsCodec() {
        return NaNNumberCriterion.Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player) {
        super.trigger(player, conditions -> true);
    }

    public record Conditions(Optional<LootContextPredicate> player) implements AbstractCriterion.Conditions {
        public static final Codec<NaNNumberCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(NaNNumberCriterion.Conditions::player))
                .apply(instance, NaNNumberCriterion.Conditions::new));

        @Override
        public void validate(LootContextPredicateValidator validator) {
            AbstractCriterion.Conditions.super.validate(validator);
        }
    }
}
