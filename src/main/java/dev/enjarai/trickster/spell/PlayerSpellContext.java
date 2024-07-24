package dev.enjarai.trickster.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.ModAttachments;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.EntityInvalidBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.NotEnoughManaBlunder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
import net.minecraft.world.World;
import org.joml.Vector3d;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class PlayerSpellContext extends SpellContext {
    public static final MapCodec<PlayerSpellContext> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.list(Codec.list(Fragment.CODEC.get().codec())).fieldOf("part_glyph_stack").forGetter(ctx -> new ArrayList<>(ctx.partGlyphStack())),
            Codec.BOOL.fieldOf("destructive").forGetter(SpellContext::isDestructive),
            Codec.BOOL.fieldOf("has_affected_world").forGetter(SpellContext::hasAffectedWorld),
            Codec.list(Codec.INT).fieldOf("stacktrace").forGetter(ctx -> new ArrayList<>(ctx.stacktrace())),
            ManaPool.CODEC.get().fieldOf("mana_pool").forGetter(ctx -> ctx.manaPool),
            Codec.list(ManaLink.CODEC).fieldOf("mana_links").forGetter(ctx -> ctx.manaLinks),
            Uuids.CODEC.fieldOf("player_uuid").forGetter(ctx -> ctx.player.getUuid()),
            World.CODEC.fieldOf("source_world").forGetter(ctx -> ctx.player.getWorld().getRegistryKey()),
            EquipmentSlot.CODEC.fieldOf("slot").forGetter(ctx -> ctx.slot)
    ).apply(instance, (partGlyphStack, destructive, hasAffectedWorld, stacktrace, manaPool, manaLinks, playerUuid, sourceWorld, slot) -> new PlayerSpellContext(partGlyphStack, destructive, hasAffectedWorld, stacktrace, manaPool, manaLinks, (ServerPlayerEntity) Objects.requireNonNull(Trickster.getCurrentServer().getWorld(sourceWorld)).getPlayerByUuid(playerUuid), slot)));

    private final ServerPlayerEntity player;
    private final EquipmentSlot slot;

    private PlayerSpellContext(List<List<Fragment>> partGlyphStack, boolean destructive, boolean hasAffectedWorld, List<Integer> stacktrace, ManaPool manaPool, List<ManaLink> manaLinks, ServerPlayerEntity player, EquipmentSlot slot) {
        super(partGlyphStack, destructive, hasAffectedWorld, stacktrace, manaPool, manaLinks);
        this.player = player;
        this.slot = slot;
    }

    public PlayerSpellContext(ServerPlayerEntity player, EquipmentSlot slot) {
        this(ModEntityCumponents.MANA.get(player), player, slot);
    }

    public PlayerSpellContext(ManaPool manaPool, ServerPlayerEntity player, EquipmentSlot slot) {
        super(manaPool);
        this.player = player;
        this.slot = slot;
    }

    @Override
    public void useMana(Trick source, float amount) throws BlunderException {
        try {
            super.useMana(source, amount);
        } catch (NotEnoughManaBlunder blunder) {
            ModCriteria.MANA_OVERFLUX.trigger(player);
            throw blunder;
        }

        ModCriteria.MANA_USED.trigger(player, amount);
    }

    @Override
    public Optional<ServerPlayerEntity> getPlayer() {
        return Optional.of(player);
    }

    @Override
    public Optional<Entity> getCaster() {
        return Optional.of(player);
    }

    @Override
    public Optional<ItemStack> getOtherHandSpellStack() {
        if (slot == EquipmentSlot.MAINHAND) {
            return Optional.ofNullable(player.getOffHandStack()).filter(this::isSpellStack);
        } else if (slot == EquipmentSlot.OFFHAND) {
            return Optional.ofNullable(player.getMainHandStack()).filter(this::isSpellStack);
        }

        return Optional
                .ofNullable(player.getMainHandStack())
                .filter(this::isSpellStack)
                .or(() -> Optional.ofNullable(player.getOffHandStack())
                        .filter(this::isSpellStack));
    }

    protected boolean isSpellStack(ItemStack stack) {
        return stack.contains(ModComponents.SPELL) ||
                (stack.contains(DataComponentTypes.CONTAINER) && stack.contains(ModComponents.SELECTED_SLOT));
    }

    @Override
    public Vector3d getPos() {
        return new Vector3d(player.getX(), player.getY(), player.getZ());
    }

    @Override
    public ServerWorld getWorld() {
        return player.getServerWorld();
    }

    @Override
    public Fragment getCrowMind() {
        var crow = player.getAttached(ModAttachments.CROW_MIND);
        if (crow == null) {
            return VoidFragment.INSTANCE;
        }
        return crow.fragment();
    }

    @Override
    public void setCrowMind(Fragment fragment) {
        player.setAttached(ModAttachments.CROW_MIND, new CrowMind(fragment));
    }

    @Override
    public SpellContextType<?> type() {
        return SpellContextType.PLAYER;
    }

    @Override
    public SpellContext delayed(List<Fragment> arguments) {
        var ctx = new PlayerSpellContext(player, slot);
        ctx.manaLinks.addAll(manaLinks);
        ctx.pushPartGlyph(arguments);
        return ctx;
    }

    @Override
    public void addManaLink(Trick source, LivingEntity target, float limit) {
        addManaLink(source, new ManaLink(manaPool, target, player.getHealth(), limit));
    }
}
