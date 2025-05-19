package dev.enjarai.trickster.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.item.Item;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class CreateKnotCriterion extends AbstractCriterion<CreateKnotCriterion.Conditions> {
    @Override
    public Codec<CreateKnotCriterion.Conditions> getConditionsCodec() {
        return CreateKnotCriterion.Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, Item item) {
        super.trigger(player, conditions -> conditions.item.equals(item));
    }

    public record Conditions(Optional<LootContextPredicate> player, Item item) implements AbstractCriterion.Conditions {
        public static final Codec<CreateKnotCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(CreateKnotCriterion.Conditions::player),
                Registries.ITEM.getCodec().fieldOf("item").forGetter(CreateKnotCriterion.Conditions::item)
        ).apply(instance, CreateKnotCriterion.Conditions::new)
        );

        @Override
        public void validate(LootContextPredicateValidator validator) {
            AbstractCriterion.Conditions.super.validate(validator);
        }
    }
}
