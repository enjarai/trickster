package dev.enjarai.trickster.item.component;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.executor.DefaultSpellExecutor;
import dev.enjarai.trickster.spell.execution.executor.ErroredSpellExecutor;
import dev.enjarai.trickster.spell.execution.executor.SpellExecutor;
import net.minecraft.text.Text;

public record SpellCoreComponent(SpellExecutor executor) {
    public static final Codec<SpellCoreComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EndecTomfoolery.toCodec(SpellExecutor.ENDEC).fieldOf("executor").forGetter(SpellCoreComponent::executor)
    ).apply(instance, SpellCoreComponent::new));

    public SpellCoreComponent(SpellPart spell) {
        this(new DefaultSpellExecutor(spell, List.of()));
    }

    public SpellCoreComponent fail(Text error) {
        return new SpellCoreComponent(new ErroredSpellExecutor(executor.spell(), error));
    }
}
