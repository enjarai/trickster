package dev.enjarai.trickster.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.*;

public class SpellExecutionManager {
    public static final Codec<SpellExecutionManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SpellQueue.CODEC.listOf().fieldOf("spells").forGetter((e) -> e.spells)
    ).apply(instance, SpellExecutionManager::new));

    private final Queue<SpellQueue> spells = new ArrayDeque<>();

    private SpellExecutionManager(List<SpellQueue> spells) {
        this.spells.addAll(spells);
    }

    public SpellExecutionManager() {

    }

    public Optional<Fragment> execute(SpellContext ctx, SpellPart spell) {
        return spell.runSafely(ctx);
    }

    public void queue(SpellContext ctx, SpellPart spell) {
        spells.add(new SpellQueue(ctx, spell));
    }

    public void tick() {
        int size = spells.size();

        for (int i = 0; i < size; i++) {
            var spell = spells.poll(); assert spell != null;

            if (!spell.run())
                spells.add(spell);
        }
    }
}
