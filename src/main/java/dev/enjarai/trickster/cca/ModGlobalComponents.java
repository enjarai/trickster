package dev.enjarai.trickster.cca;

import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.scoreboard.ScoreboardComponentInitializer;

import dev.enjarai.trickster.Trickster;


public class ModGlobalComponents implements ScoreboardComponentInitializer {
    public static final ComponentKey<SharedManaComponent> SHARED_MANA =
            ComponentRegistry.getOrCreate(Trickster.id("shared_mana"), SharedManaComponent.class);

    @Override
    public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
        registry.registerScoreboardComponent(SHARED_MANA, SharedManaComponent::new);
    }
}
