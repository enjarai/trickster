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

public class CrackKnotCriterion extends AbstractCriterion<CrackKnotCriterion.Conditions> {
    @Override
    public Codec<CrackKnotCriterion.Conditions> getConditionsCodec() {
        return CrackKnotCriterion.Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, Item item) {
        super.trigger(player, conditions -> conditions.item.map(i -> i.equals(item)).orElse(true));
    }

    public record Conditions(Optional<LootContextPredicate> player, Optional<Item> item) implements AbstractCriterion.Conditions {
        public static final Codec<CrackKnotCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(CrackKnotCriterion.Conditions::player),
                Registries.ITEM.getCodec().optionalFieldOf("item").forGetter(CrackKnotCriterion.Conditions::item)
        ).apply(instance, CrackKnotCriterion.Conditions::new)
        );

        @Override
        public void validate(LootContextPredicateValidator validator) {
            AbstractCriterion.Conditions.super.validate(validator);
        }
    }
}
