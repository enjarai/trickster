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

    public static final ComponentKey<CasterComponent> CASTER =
            ComponentRegistry.getOrCreate(Trickster.id("caster"), CasterComponent.class);

    public static final ComponentKey<BarsComponent> BARS =
            ComponentRegistry.getOrCreate(Trickster.id("bars"), BarsComponent.class);

    public static final ComponentKey<DisguiseCumponent> DISGUISE =
            ComponentRegistry.getOrCreate(Trickster.id("disguise"), DisguiseCumponent.class);

    public static final ComponentKey<IsEditingScrollComponent> IS_EDITING_SCROLL =
            ComponentRegistry.getOrCreate(Trickster.id("is_editing_scroll"), IsEditingScrollComponent.class);

    public static final ComponentKey<GraceComponent> GRACE =
            ComponentRegistry.getOrCreate(Trickster.id("grace"), GraceComponent.class);
    public static final ComponentKey<FlecksComponent> FLECKS =
            ComponentRegistry.getOrCreate(Trickster.id("flecks"), FlecksComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(LivingEntity.class, MANA, ManaComponent::new);
        registry.registerForPlayers(CASTER, CasterComponent::new, RespawnCopyStrategy.LOSSLESS_ONLY);
        registry.registerForPlayers(BARS, BarsComponent::new, RespawnCopyStrategy.LOSSLESS_ONLY);
        registry.registerForPlayers(FLECKS, FlecksComponent::new, RespawnCopyStrategy.NEVER_COPY);
        registry.registerForPlayers(DISGUISE, DisguiseCumponent::new, RespawnCopyStrategy.LOSSLESS_ONLY);
        registry.registerForPlayers(IS_EDITING_SCROLL, IsEditingScrollComponent::new, RespawnCopyStrategy.NEVER_COPY);
        registry.registerFor(LivingEntity.class, GRACE, GraceComponent::new);
    }
}
