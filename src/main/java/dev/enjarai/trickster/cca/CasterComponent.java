package dev.enjarai.trickster.cca;

import com.mojang.serialization.DataResult;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.SpellExecutor;
import dev.enjarai.trickster.spell.execution.source.PlayerSpellSource;
import dev.enjarai.trickster.spell.execution.SpellExecutionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.List;

public class CasterComponent implements ServerTickingComponent {
    private final PlayerEntity player;
    private SpellExecutionManager executionManager;

    public CasterComponent(PlayerEntity player) {
        this.player = player;
        this.executionManager = new SpellExecutionManager();

        if (!player.getWorld().isClient()) {
            this.executionManager.setSource(new PlayerSpellSource((ServerPlayerEntity) player));
        }
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

    public void queue(SpellPart spell, List<Fragment> arguments) {
        executionManager.queue(spell, arguments);
    }
}
