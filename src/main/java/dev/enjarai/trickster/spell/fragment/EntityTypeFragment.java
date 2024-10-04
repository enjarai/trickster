package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.spell.Fragment;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

public record EntityTypeFragment(EntityType<?> entityType) implements Fragment {
    public static final StructEndec<EntityTypeFragment> ENDEC = StructEndecBuilder.of(
            MinecraftEndecs.ofRegistry(Registries.ENTITY_TYPE).fieldOf("entity_type", EntityTypeFragment::entityType),
            EntityTypeFragment::new
    );

    @Override
    public FragmentType<?> type() {
        return FragmentType.ENTITY_TYPE;
    }

    @Override
    public Text asText() {
        return entityType.getName();
    }

    @Override
    public BooleanFragment asBoolean() {
        return BooleanFragment.TRUE;
    }
}
