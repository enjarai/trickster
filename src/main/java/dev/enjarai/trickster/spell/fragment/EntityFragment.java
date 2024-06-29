package dev.enjarai.trickster.spell.fragment;

import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Uuids;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public record EntityFragment(UUID uuid, Text name) implements Fragment {
    public static final MapCodec<EntityFragment> CODEC = Uuids.CODEC
            .xmap(uuid -> new EntityFragment(uuid, Text.of(uuid)), EntityFragment::uuid).fieldOf("uuid");

    @Override
    public FragmentType<?> type() {
        return FragmentType.ENTITY;
    }

    @Override
    public Text asText() {
        return name;
    }

    public Optional<Entity> getEntity(SpellContext ctx) {
        return Optional.ofNullable(ctx.getWorld().getEntity(uuid));
    }

    @Override
    public BooleanFragment asBoolean() {
        return BooleanFragment.TRUE;
    }

    @Override
    public boolean isEphemeral() {
        return true;
    }

    public static EntityFragment from(Entity entity) {
        return new EntityFragment(entity.getUuid(), entity.getName());
    }
}
