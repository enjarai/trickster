package dev.enjarai.trickster.cca;

import com.mojang.serialization.DataResult;
import dev.enjarai.trickster.spell.PlayerSpellContext;
import dev.enjarai.trickster.spell.SpellCaster;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellExecutionManager;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public class CasterComponent implements SpellCaster, ServerTickingComponent {
    private final PlayerEntity player;
    private SpellExecutionManager executionManager;

    public CasterComponent(PlayerEntity player) {
        this.player = player;
        this.executionManager = new SpellExecutionManager();
    }

    @Override
    public SpellContext getDefaultCtx() {
        return new PlayerSpellContext((ServerPlayerEntity) player, player.getActiveHand() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
    }

    @Override
    public SpellExecutionManager getExecutionManager() {
        return executionManager;
    }

    @Override
    public void serverTick() {
        executionManager.tick();
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        DataResult<SpellExecutionManager> result = SpellExecutionManager.CODEC.parse(NbtOps.INSTANCE, tag.get("manager"));

        if (result.hasResultOrPartial())
            executionManager = result.result().orElseThrow();
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        DataResult<NbtElement> result = SpellExecutionManager.CODEC.encodeStart(NbtOps.INSTANCE, executionManager);
        tag.put("manager", result.result().orElseThrow());
    }
}
