package dev.enjarai.trickster.cca;

import java.util.Optional;
import java.util.Set;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import dev.enjarai.trickster.EndecTomfoolery;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.KeyedEndec;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.server.world.ServerWorld;

public class DisplacementComponent implements ServerTickingComponent, ClientTickingComponent {
    private static final KeyedEndec<Optional<Vector3d>> OFFSET_ENDEC = EndecTomfoolery.<Double, Vector3d>vectorEndec(Endec.DOUBLE, Vector3d::new, Vector3dc::x, Vector3dc::y, Vector3dc::z)
            .optionalOf()
            .keyed("offset", Optional.empty());
    public static final int CHARGE_TICKS = 40;

    private final Entity entity;
    private Optional<Vector3d> offset = Optional.empty();

    public DisplacementComponent(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void readFromNbt(NbtCompound tag, WrapperLookup registryLookup) {
        offset = tag.get(OFFSET_ENDEC);
    }

    @Override
    public void writeToNbt(NbtCompound tag, WrapperLookup registryLookup) {
        tag.put(OFFSET_ENDEC, offset);
    }

    @Override
    public void serverTick() {
        offset.ifPresent(xyz -> {
            if (!ModEntityComponents.GRACE.get(entity).isInGrace("displacement")) {
                entity.getWorld().playSoundFromEntity(null, entity, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);

                entity.teleport((ServerWorld) entity.getWorld(), entity.getX() + xyz.x, entity.getY() + xyz.y, entity.getZ() + xyz.z, Set.of(), entity.getHeadYaw(), entity.getPitch());
                offset = Optional.empty();

                entity.getWorld().playSoundFromEntity(null, entity, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }
        });
    }

    @Override
    public void clientTick() {
        if (ModEntityComponents.GRACE.get(entity).isInGrace("displacement")) {
            for (int i = 0; i < 2; i++) {
                entity.getWorld().addParticle(
                        ParticleTypes.PORTAL,
                        entity.getParticleX(0.5),
                        entity.getRandomBodyY() - 0.25,
                        entity.getParticleZ(0.5),
                        (entity.getRandom().nextDouble() - 0.5) * 2.0,
                        -entity.getRandom().nextDouble(),
                        (entity.getRandom().nextDouble() - 0.5) * 2.0
                );
            }
        }
    }

    public void modify(Vector3dc vector) {
        if (offset.isPresent()) {
            offset.get().add(vector);
        } else {
            offset = Optional.of(new Vector3d(vector));
        }

        ModEntityComponents.GRACE.get(entity).triggerGrace("displacement", CHARGE_TICKS);
        entity.getWorld().playSoundFromEntity(null, entity, SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategory.PLAYERS, 1.0F, 2.0F);
    }

    public void clear() {
        offset = Optional.empty();
        ModEntityComponents.GRACE.get(entity).cancelGrace("displacement");
    }
}
