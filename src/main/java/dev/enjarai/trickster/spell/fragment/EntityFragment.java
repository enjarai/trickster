package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.UUID;

public record EntityFragment(UUID uuid, Text name) implements Fragment {
    public static final StructEndec<EntityFragment> ENDEC = StructEndecBuilder.of(
            EndecTomfoolery.UUID.fieldOf("uuid", EntityFragment::uuid),
            uuid -> new EntityFragment(uuid, Text.of(uuid))
    );

    @Override
    public FragmentType<?> type() {
        return FragmentType.ENTITY;
    }

    @Override
    public Text asText() {
        return name;
    }

    public Optional<Entity> getEntity(SpellContext ctx) {
        return Optional.ofNullable(ctx.source().getWorld().getEntity(uuid));
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
