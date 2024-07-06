package dev.enjarai.trickster.cca;

import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.WardModifiedSelfBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.WardReturnBlunder;
import io.wispforest.endec.impl.KeyedEndec;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.Component;

import java.util.List;

public class WardComponent implements Component {
    private static final KeyedEndec<SpellPart> SPELL = SpellPart.ENDEC.keyed("handler", () -> null);

    private final PlayerEntity player;
    @Nullable
    private SpellPart spell;

    public WardComponent(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if (spell != null)
            tag.put(SPELL, spell);
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        spell = tag.get(SPELL);
    }

    public void register(SpellPart spell) {
        this.spell = spell;
    }

    public List<Fragment> run(SpellContext triggerCtx, Trick source, List<Fragment> inputs) throws BlunderException {
        if (spell == null)
            return inputs;

        var manaPool = ModEntityCumponents.MANA.get(player);
        var finalResult = inputs;
        boolean isModified = false;
        boolean applyBacklashIfModified = true;

        try {
            var ctx = new PlayerSpellContext((ServerPlayerEntity)this.player, EquipmentSlot.MAINHAND);
            ctx.pushPartGlyph(List.of(new PatternGlyph(source.getPattern()), new ListFragment(inputs)));

            var result = spell.run(ctx);
            ctx.popPartGlyph();

            if (result.type() == FragmentType.LIST) {
                var newInputs = ((ListFragment)result).fragments();
                int index = 0;

                if (newInputs.size() != inputs.size())
                    throw new WardReturnBlunder();

                for (var input : inputs) {
                    var inputType = input.type();
                    var newInput = newInputs.get(index);

                    if (!newInput.equals(input)) {
                        isModified = true;
                    }

                    if (newInput.type() != inputType)
                        throw new WardReturnBlunder();

                    if (inputType == FragmentType.ENTITY) {
                        var entity = (EntityFragment)input;
                        var newEntity = (EntityFragment)newInput;

                        if (entity.uuid() == player.getUuid() && newEntity.uuid() != entity.uuid())
                            throw new WardModifiedSelfBlunder();
                    }

                    index++;
                }

                finalResult = newInputs;
            } else throw new WardReturnBlunder();
        } catch (BlunderException e) {
            isModified = true;
            applyBacklashIfModified = false;
            player.sendMessage(Text.literal("Ward failure: ").append(e.createMessage()));
        }

        if (isModified) {
            manaPool.decrease(14);

            if (applyBacklashIfModified)
                triggerCtx.useMana(source, 9);
        }

        return finalResult;
    }
}
