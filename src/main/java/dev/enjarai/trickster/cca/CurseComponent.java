package dev.enjarai.trickster.cca;

import dev.enjarai.trickster.pond.LimbAnimatorDuck;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.KeyedEndec;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;

import java.util.Set;

public class CurseComponent implements AutoSyncedComponent, CommonTickingComponent {
    private static final KeyedEndec<Curse> curseKey = Curse.ENDEC.keyed("curse", Curse.NONE);

    private final PlayerEntity player;
    private Curse currentCurse = Curse.NONE;

    @Nullable
    private CatEntity entity = null;
    private int timeTillSit = 0;

    public CurseComponent(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        //        currentCurse = tag.get(curseKey);
        //        updateEntity();
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        //        tag.put(curseKey, currentCurse);
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        currentCurse = buf.read(Curse.ENDEC);
        updateEntity();
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.write(Curse.ENDEC, currentCurse);
    }

    public void setCurrentCurse(Curse newCurse) {
        currentCurse = newCurse;
        updateEntity();
        ModEntityComponents.CURSE.sync(player);
    }

    public Curse getCurrentCurse() {
        return currentCurse;
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    private void updateEntity() {
        entity = switch (currentCurse) {
            case MEOW_MRRP -> {
                var cat = new CatEntity(EntityType.CAT, player.getWorld());
                cat.setAiDisabled(true);
                cat.setInvulnerable(true);
                yield cat;
            }
            default -> null;
        };
        player.calculateDimensions();
    }

    @Override
    public void tick() {
        if (entity != null) {
            if (!entity.getPos().equals(player.getPos())) {
                timeTillSit = 80;
            } else if (timeTillSit > 0) {
                timeTillSit--;
            }
            entity.setPosition(player.getPos());
            if (entity.getRandom().nextInt(1000) < entity.ambientSoundChance++) {
                entity.ambientSoundChance = -entity.getMinAmbientSoundDelay();
                entity.playAmbientSound();
            }
        }
    }

    public @Nullable CatEntity getEntityForRendering() {
        if (entity != null) {
            entity.setYaw(player.getYaw());
            entity.prevYaw = player.prevYaw;
            entity.setPitch(player.getPitch());
            entity.prevPitch = player.prevPitch;

            entity.setPos(player.getX(), player.getY(), player.getZ());
            entity.prevX = player.prevX;
            entity.prevY = player.prevY;
            entity.prevZ = player.prevZ;

            entity.setBodyYaw(player.bodyYaw);
            entity.prevBodyYaw = player.prevBodyYaw;
            entity.setHeadYaw(player.headYaw);
            entity.prevHeadYaw = player.prevHeadYaw;

            entity.hurtTime = player.hurtTime;

            entity.handSwinging = player.handSwinging;
            entity.handSwingTicks = player.handSwingTicks;
            entity.handSwingProgress = player.handSwingProgress;
            entity.lastHandSwingProgress = player.lastHandSwingProgress;

            ((LimbAnimatorDuck) entity.limbAnimator).trickster$copyFrom(player.limbAnimator);

            if (timeTillSit <= 0 && player.getPose() == EntityPose.CROUCHING) {
                entity.setInSittingPose(true);
                entity.setPose(EntityPose.STANDING);
            } else {
                entity.setInSittingPose(false);
                entity.setPose(player.getPose());
            }
        }
        return entity;
    }

    public @Nullable CatEntity getEntity() {
        return entity;
    }

    public enum Curse implements StringIdentifiable {
        NONE,
        MEOW_MRRP;

        public static final Endec<Curse> ENDEC = Endec.forEnum(Curse.class);

        @Override
        public String asString() {
            return switch (this) {
                case NONE -> "NONE";
                case MEOW_MRRP -> "MEOW_MRRP";
            };
        }

        // @afamiliarquiet thank you so so much for making this <3
        public static String meowify(String chatText) {
            if (chatText.isEmpty() || chatText.charAt(0) == '\\') {
                return chatText;
            }

            StringBuilder meowMrrp = new StringBuilder();
            char[] theMeow = { 'm', 'e', 'o', 'w' };
            Set<Character> allowable = Set.of('!', '?', '~', '.', ',', ' ');
            for (int i = 0, mewi = 0; i < chatText.length(); i++, mewi++) {
                char current = chatText.charAt(i);
                if (allowable.contains(current)) {
                    meowMrrp.append(current);
                    mewi = theMeow.length - 1; // effectively 0 after ++
                } else {
                    meowMrrp.append(theMeow[mewi % theMeow.length]);
                }
            }
            return meowMrrp.toString();
        }
    }
}
