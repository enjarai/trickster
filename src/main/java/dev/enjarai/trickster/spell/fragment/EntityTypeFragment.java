package dev.enjarai.trickster.spell.fragment;

import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.spell.Fragment;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

public record EntityTypeFragment(EntityType<?> entityType) implements Fragment {
    public static final MapCodec<EntityTypeFragment> CODEC = Registries.ENTITY_TYPE.getCodec()
            .fieldOf("entity_type").xmap(EntityTypeFragment::new, EntityTypeFragment::entityType);

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
