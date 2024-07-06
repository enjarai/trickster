package dev.enjarai.trickster.cca;

import dev.enjarai.trickster.Trickster;
import net.minecraft.entity.LivingEntity;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

public class ModEntityCumponents implements EntityComponentInitializer {
    public static final ComponentKey<ManaComponent> MANA =
            ComponentRegistry.getOrCreate(Trickster.id("mana"), ManaComponent.class);

    public static final ComponentKey<WardComponent> WARD =
            ComponentRegistry.getOrCreate(Trickster.id("ward"), WardComponent.class);

    public static final ComponentKey<DisguiseCumponent> DISGUISE =
            ComponentRegistry.getOrCreate(Trickster.id("disguise"), DisguiseCumponent.class);

    public static final ComponentKey<IsEditingScrollComponent> IS_EDITING_SCROLL =
            ComponentRegistry.getOrCreate(Trickster.id("is_editing_scroll"), IsEditingScrollComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(LivingEntity.class, MANA, ManaComponent::new);
        registry.registerForPlayers(WARD, WardComponent::new, RespawnCopyStrategy.CHARACTER);
        registry.registerForPlayers(DISGUISE, DisguiseCumponent::new, RespawnCopyStrategy.LOSSLESS_ONLY);
        registry.registerForPlayers(IS_EDITING_SCROLL, IsEditingScrollComponent::new, RespawnCopyStrategy.NEVER_COPY);
    }
}
