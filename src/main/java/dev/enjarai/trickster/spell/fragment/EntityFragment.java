package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.util.Optional;
import java.util.UUID;

public record EntityFragment(UUID uuid, Text name) implements Fragment {
    public static final StructEndec<EntityFragment> ENDEC = StructEndecBuilder.of(
            EndecTomfoolery.UUID.fieldOf("uuid", EntityFragment::uuid),
            CodecUtils.toEndec(TextCodecs.STRINGIFIED_CODEC).fieldOf("name", EntityFragment::name),
            EntityFragment::new
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
        return Optional
                .ofNullable(ctx.source().getWorld().getEntity(uuid))
                .filter(EntityFragment::isValidEntity);
    }

    @Override
    public Fragment applyEphemeral() {
        return new ZalgoFragment();
    }

    @Override
    public int getWeight() {
        return 32;
    }

    public static EntityFragment from(Entity entity) {
        if (entity instanceof PlayerEntity) {
            return new EntityFragment(entity.getUuid(), entity.getName());
        }

        var name = entity.hasCustomName() ? entity.getCustomName() : Text.translatable("trickster.unnamed_entity", entity.getName());
        return new EntityFragment(entity.getUuid(), name);
    }

    @SuppressWarnings("resource")
    public static boolean isValidEntity(Entity entity) {
        if (entity.getWorld() instanceof ServerWorld serverWorld) {
            return serverWorld.getChunkManager().chunkLoadingManager.getTicketManager()
                    .shouldTickEntities(entity.getChunkPos().toLong());
        }
        return false;
    }
}
