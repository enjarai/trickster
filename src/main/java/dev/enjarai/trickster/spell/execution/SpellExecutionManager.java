package dev.enjarai.trickster.spell.execution;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.SpellPart;

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

    public void tick() {
        int size = spells.size();

        for (int i = 0; i < size; i++) {
            var spell = spells.poll(); assert spell != null;

            if (spell.run(source).isEmpty())
                spells.add(spell);
        }
    }

    public void setSource(SpellSource source) {
        this.source = source;
    }
}
