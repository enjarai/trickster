package dev.enjarai.trickster.item.component;

import java.util.List;
import java.util.function.Consumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.executor.DefaultSpellExecutor;
import dev.enjarai.trickster.spell.execution.executor.ErroredSpellExecutor;
import dev.enjarai.trickster.spell.execution.executor.SpellExecutor;
import net.minecraft.component.ComponentMap;
import net.minecraft.text.Text;

public record SpellCoreComponent(SpellExecutor executor) {
    public static final Codec<SpellCoreComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    EndecTomfoolery.toCodec(SpellExecutor.ENDEC).fieldOf("executor").forGetter(SpellCoreComponent::executor)
            ).apply(instance, SpellCoreComponent::new)
    );

    public SpellCoreComponent(SpellPart spell) {
        this(new DefaultSpellExecutor(spell, List.of()));
    }

    public SpellCoreComponent fail(Text error) {
        return new SpellCoreComponent(new ErroredSpellExecutor(executor.spell(), error));
    }

    public static void refresh(ComponentMap map, Consumer<SpellCoreComponent> updateCallback) {
        if (map.contains(ModComponents.FRAGMENT)) {
            var fragment = map.get(ModComponents.FRAGMENT).value();

            if (fragment instanceof SpellPart spell) {
                if (
                    !map.contains(ModComponents.SPELL_CORE)
                            || map.get(ModComponents.SPELL_CORE) instanceof SpellCoreComponent comp
                                    && (!spell.equals(comp.executor().spell())
                                            || comp.executor() instanceof ErroredSpellExecutor)
                ) {
                    updateCallback.accept(new SpellCoreComponent(spell));
                }
            }
        }
    }
}
