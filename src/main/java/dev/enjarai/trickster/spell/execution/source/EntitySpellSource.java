package dev.enjarai.trickster.spell.execution.source;

import dev.enjarai.trickster.ModAttachments;
import dev.enjarai.trickster.spell.CrowMindAttachment;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.mana.MutableManaPool;
import dev.enjarai.trickster.spell.mana.generation.HasMana;
import dev.enjarai.trickster.spell.mana.generation.ManaHandler;
import dev.enjarai.trickster.spell.mana.generation.event.EntityManaHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3d;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.ComponentKey;

import java.util.Optional;

public class EntitySpellSource implements SpellSource {

    Entity entity;

    public EntitySpellSource(Entity entity) {

        if (!(entity instanceof HasMana)) {
            throw new IllegalStateException(String.format("an entity spell source was made from an entity (%s) that does not have mana", entity));
        }

        this.entity = entity;
    }

    @Override
    public <T extends Component> Optional<T> getComponent(ComponentKey<T> key) {
        return Optional.of(entity.getComponent(key));
    }

    @Override
    public Optional<Vector3d> getFacing() {
        var x = Math.sin(Math.toRadians(entity.getYaw()));
        var y = Math.cos(Math.toRadians(entity.getPitch()));
        var z = Math.cos(Math.toRadians(entity.getYaw()));

        return Optional.of(new Vector3d(x, y, z));
    }

    @Override
    public float getHealth() {
        if (entity instanceof LivingEntity living) {
            return living.getHealth();
        }
        return -1;
    }

    @Override
    public float getMaxHealth() {
        if (entity instanceof LivingEntity living) {
            return living.getMaxHealth();
        }
        return -1;
    }

    @Override
    public MutableManaPool getManaPool() {
        //noinspection unchecked
        return ((HasMana) entity).getPool();
    }

    @Override
    public ManaHandler getManaHandler() {
        return new EntityManaHandler(entity);
    }

    @Override
    public Vector3d getPos() {
        return entity.getPos().toVector3d();
    }

    @Override
    public ServerWorld getWorld() {
        return (ServerWorld) entity.getWorld();
    }

    @Override
    public Fragment getCrowMind() {
        var crow = entity.getAttached(ModAttachments.CROW_MIND);
        if (crow == null) {
            return VoidFragment.INSTANCE;
        }
        return crow.fragment();
    }

    @Override
    public void setCrowMind(Fragment fragment) {
        entity.setAttached(ModAttachments.CROW_MIND, new CrowMindAttachment(fragment));
    }

    @Override
    public void offerOrDropItem(ItemStack stack) {
        ItemEntity itemEntity = new ItemEntity(this.getWorld(), entity.getX(), entity.getZ(), entity.getZ(), stack);
        itemEntity.setPickupDelay(40);

        var dx = entity.getRandom().nextFloat() * 0.5F; // i dunno tbh
        var dy = entity.getRandom().nextFloat() * 6.2831855F;
        itemEntity.setVelocity((-MathHelper.sin(dy) * dx), 0.20000000298023224, (MathHelper.cos(dy) * dx));

        getWorld().spawnEntity(itemEntity);
    }
}
