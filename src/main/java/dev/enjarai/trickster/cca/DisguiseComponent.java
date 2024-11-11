package dev.enjarai.trickster.cca;

import dev.enjarai.trickster.pond.LimbAnimatorDuck;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;

import java.util.function.BiConsumer;

public class DisguiseComponent implements AutoSyncedComponent, CommonTickingComponent {
    public static BiConsumer<World, Entity> entityAdder;

    private final LivingEntity source;
    @Nullable
    private Entity entity = null;

    public DisguiseComponent(LivingEntity source) {
        this.source = source;
        if (source instanceof PlayerEntity) {
            entity = new CatEntity(EntityType.CAT, source.getWorld());
//            source.calculateDimensions();
            if (source.getWorld().isClient) {
                entityAdder.accept(source.getWorld(), entity);
            }
        }
    }

    // TODO:
    // death sound
    // disable interactions if not biped
    // make leashing work >:3
    // pass through use actions (milking and shearing)
    // show proper hands

    public @Nullable Entity getEntityForRendering() {
        if (entity != null) {
            entity.setYaw(source.getYaw());
            entity.prevYaw = source.prevYaw;
            entity.setPitch(source.getPitch());
            entity.prevPitch = source.prevPitch;

            entity.setPos(source.getX(), source.getY(), source.getZ());
            entity.prevX = source.prevX;
            entity.prevY = source.prevY;
            entity.prevZ = source.prevZ;

//            entity.age = player.age;

            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.setBodyYaw(source.bodyYaw);
                livingEntity.prevBodyYaw = source.prevBodyYaw;
                livingEntity.setHeadYaw(source.headYaw);
                livingEntity.prevHeadYaw = source.prevHeadYaw;

                livingEntity.hurtTime = source.hurtTime;

                livingEntity.handSwinging = source.handSwinging;
                livingEntity.handSwingTicks = source.handSwingTicks;
                livingEntity.handSwingProgress = source.handSwingProgress;
                livingEntity.lastHandSwingProgress = source.lastHandSwingProgress;

                ((LimbAnimatorDuck) livingEntity.limbAnimator).trickster$copyFrom(source.limbAnimator);
            }
        }
        return entity;
    }

    public @Nullable Entity getEntity() {
        return entity;
    }

    public void setEntity(@Nullable Entity entity) {
        this.entity = entity;
        if (source.getWorld().isClient && entity != null) {
            entityAdder.accept(source.getWorld(), entity);
        }
        ModEntityComponents.DISGUISE.sync(source);
        source.calculateDimensions();
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if (tag.contains("entity")) {
            setEntity(EntityType.getEntityFromNbt(tag.getCompound("entity"), source.getWorld()).orElse(null));
        } else {
            setEntity(null);
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if (entity != null) {
            var compound = new NbtCompound();
            entity.saveSelfNbt(compound);
            tag.put("entity", compound);
        }
    }

    @Override
    public void tick() {
        if (entity != null) {
            if (entity instanceof MobEntity mob) {
                mob.setAiDisabled(true);
            }

            if (entity instanceof LivingEntity living) {
                living.preferredHand = source.preferredHand;

                for (Hand hand : Hand.values()) {
                    living.setStackInHand(hand, source.getStackInHand(hand));
                }
            }

//            entity.setPos(source.getX(), source.getY(), source.getZ());
//            entity.tick();
        }
    }
}
