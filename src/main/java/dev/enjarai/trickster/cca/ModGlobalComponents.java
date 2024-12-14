package dev.enjarai.trickster.cca;

import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.scoreboard.ScoreboardComponentInitializer;

import dev.enjarai.trickster.Trickster;

public class ModGlobalComponents implements ScoreboardComponentInitializer {
    public static final ComponentKey<SharedManaComponent> SHARED_MANA = ComponentRegistry.getOrCreate(Trickster.id("shared_mana"), SharedManaComponent.class);
    public static final ComponentKey<MessageHandlerComponent> MESSAGE_HANDLER = ComponentRegistry.getOrCreate(Trickster.id("message_handler"),
            MessageHandlerComponent.class);

    @Override
    public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
        registry.registerScoreboardComponent(SHARED_MANA, SharedManaComponent::new);
        registry.registerScoreboardComponent(MESSAGE_HANDLER, MessageHandlerComponent::new);
    }
}
