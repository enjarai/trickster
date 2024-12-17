package dev.enjarai.trickster.entity;

import dev.enjarai.trickster.item.component.FragmentComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.TickData;
import dev.enjarai.trickster.spell.execution.executor.DefaultSpellExecutor;
import dev.enjarai.trickster.spell.execution.executor.SpellExecutor;
import dev.enjarai.trickster.spell.execution.source.EntitySpellSource;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.mana.SimpleManaPool;
import dev.enjarai.trickster.spell.mana.generation.HasMana;
import io.wispforest.endec.impl.KeyedEndec;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

import java.util.List;

import static dev.enjarai.trickster.entity.SpellRunningState.*;

//the repos code quality when i commit this:📉
public class AmethystProjectile extends PersistentProjectileEntity implements HasMana, SpellDisplayingEntity {
    public static final KeyedEndec<SpellPart> SPELL_PART_KEYED_ENDEC = SpellPart.ENDEC.keyed("spell", new SpellPart(VoidFragment.INSTANCE));
    public static final KeyedEndec<SimpleManaPool> SIMPLE_MANA_POOL_KEYED_ENDEC = SimpleManaPool.ENDEC.keyed("pool", new SimpleManaPool(0));
    public static final KeyedEndec<SpellExecutor> SPELL_EXECUTOR_KEYED_ENDEC = SpellExecutor.ENDEC.nullableOf().keyed("spellExecutor", new DefaultSpellExecutor(new SpellPart(VoidFragment.INSTANCE), List.of()));
    public static final KeyedEndec<State> SPELL_DISPLAY_STATE_KEYED_ENDEC = ENDEC.keyed("displayState",() -> Idle.instance);

    private SimpleManaPool pool;
    private SpellExecutor spellExecutor;
    private SpellPart spell;

    private static final TrackedData<State> STATE_TRACKED_DATA = DataTracker.registerData(AmethystProjectile.class, SPELL_DISPLAY_STATE);

    public static float dimensions = 0.75F;
    private static float lastChimeIntensity = 1.0f;
    private static float lastChimeAge = 1.0f;

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.put(SPELL_PART_KEYED_ENDEC, spell);
        nbt.put(SIMPLE_MANA_POOL_KEYED_ENDEC, pool);
        nbt.put(SPELL_EXECUTOR_KEYED_ENDEC, spellExecutor);
        nbt.put(SPELL_DISPLAY_STATE_KEYED_ENDEC, getRunningState());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        spell = nbt.get(SPELL_PART_KEYED_ENDEC);
        pool = nbt.get(SIMPLE_MANA_POOL_KEYED_ENDEC);
        spellExecutor = nbt.get(SPELL_EXECUTOR_KEYED_ENDEC);
        setRunningState(nbt.get(SPELL_DISPLAY_STATE_KEYED_ENDEC));
    }

    public AmethystProjectile(EntityType<? extends PersistentProjectileEntity> entityEntityType, World world) {
        super(entityEntityType, world);
    }

    private AmethystProjectile(LivingEntity owner, ItemStack stack) {
        super(ModEntities.AMETHYST_SHARD, owner, owner.getWorld(), stack, null);
    }

    private AmethystProjectile(LivingEntity owner, ItemStack stack, SpellPart spell) {
        super(ModEntities.AMETHYST_SHARD, owner, owner.getWorld(), stack.copyWithCount(1), null);
        this.pool = new SimpleManaPool(128, 128);
        this.spell = spell;
        this.spellExecutor = null;
        this.setRunningState(Idle.instance);
        pickupType = PickupPermission.DISALLOWED;
    }

    public static AmethystProjectile tryThrow(LivingEntity owner, ItemStack itemStack) {
        FragmentComponent fragmentComponent = itemStack.get(ModComponents.FRAGMENT);
        if (fragmentComponent == null) return null;

        Fragment fragment = fragmentComponent.value();
        if (fragment instanceof SpellPart spell) return new AmethystProjectile(owner, itemStack, spell);

        return null;
    }

    @Override
    protected void age() {
        if (getRunningState().isRunning()) {
            return;
        }
        super.age();
    }

    @Override
    public void tick() {
        super.tick();

        if (!getRunningState().isRunning()) {
           return;
        }
        if (!getWorld().isClient) {
            var source = new EntitySpellSource(this);
            this.setRunningState(
                    SpellExecutor.runReportState(
                            spellExecutor,
                            spell,
                            source,
                            new TickData().setExecutionLimitRatioAbsolute(1 / 4.0f)
                    ));
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        if (hitResult.getType() != HitResult.Type.MISS) {
            lastChimeIntensity *= (float) Math.pow(0.997, (this.age - lastChimeAge));
            lastChimeIntensity = Math.min(1.0F, lastChimeIntensity + 0.07F);
            float f = 0.5F + AmethystProjectile.lastChimeIntensity * getWorld().random.nextFloat() * 1.2F;
            float g = 0.1F + lastChimeIntensity * 1.2F;
            this.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, g, f);
            lastChimeAge = this.age;
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        var entity = entityHitResult.getEntity();

        if (getRunningState().isIdle() && !getWorld().isClient) {
            this.setRunningState(new Running(spell));
            spellExecutor = new DefaultSpellExecutor(spell, List.of(VectorFragment.of(entityHitResult.getPos()), EntityFragment.from(entity)));
        }

        super.onEntityHit(entityHitResult);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        if (getRunningState().isIdle() && !getWorld().isClient) {
            setRunningState(new Running(spell));
            spellExecutor = new DefaultSpellExecutor(spell, List.of(VectorFragment.of(blockHitResult.getPos()), VoidFragment.INSTANCE));
        }

        super.onBlockHit(blockHitResult);
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        var stack = Items.AMETHYST_SHARD.getDefaultStack();
        stack.setCount(1);
        return stack;
    }

    @Override
    public void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(STATE_TRACKED_DATA, Idle.instance);
    }

    @Override
    public State getRunningState() {
        return dataTracker.get(STATE_TRACKED_DATA);
    }

    public void setRunningState(State state) {
        dataTracker.set(STATE_TRACKED_DATA, state);
    }

    @Override
    public SimpleManaPool getPool() {
        return pool;
    }

}

