package dev.enjarai.trickster.spell.execution;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.mana.ManaPool;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.NaNBlunder;
import net.minecraft.text.Text;

import java.util.*;

public class SpellExecutionManager {
    public static final Codec<SpellExecutionManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SpellExecutor.CODEC.listOf().fieldOf("spells").forGetter((e) -> e.spells.stream().toList())
    ).apply(instance, SpellExecutionManager::new));

    private SpellSource source;
    private final Queue<SpellExecutor> spells = new ArrayDeque<>();

    private SpellExecutionManager(List<SpellExecutor> spells) {
        this.spells.addAll(spells);
    }

    public SpellExecutionManager() {

    }

    public void queue(SpellPart spell, List<Fragment> arguments) {
        spells.add(new SpellExecutor(spell, arguments));
    }

    public void queue(SpellPart spell, List<Fragment> arguments, ManaPool poolOverride) {
        spells.add(new SpellExecutor(spell, new ExecutionState(arguments, poolOverride)));
    }

    public void tick() {
        if (source == null)
            return;

        int size = spells.size();

        for (int i = 0; i < size; i++) {
            var spell = spells.poll(); assert spell != null;

            try {
                if (spell.run(source).isEmpty())
                    spells.add(spell);
            } catch (BlunderException e) {
                if (e instanceof NaNBlunder)
                    source.getPlayer().ifPresent(ModCriteria.NAN_NUMBER::trigger);

                source.getPlayer().ifPresent(player -> player.sendMessage(e.createMessage().append(" (").append("spell.formatStackTrace()").append(")")));
            } catch (Exception e) {
                source.getPlayer().ifPresent(player -> player.sendMessage(Text.literal("Uncaught exception in spell: " + e.getMessage())));
            }
        }
    }

    public void setSource(SpellSource source) {
        this.source = source;
    }
}
