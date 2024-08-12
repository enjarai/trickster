package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.spell.Fragment;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public record DimensionFragment(ServerWorld world /* TODO: is this correct? */) implements Fragment {
    @Override
    public FragmentType<?> type() {
        return null; //TODO
    }

    @Override
    public Text asText() {
        return null; //TODO
    }

    @Override
    public BooleanFragment asBoolean() {
        return null; //TODO
    }
}
