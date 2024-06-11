package dev.enjarai.trickster.spell.fragment;

import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.spell.Fragment;
import net.minecraft.text.Text;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record EntityFragment(UUID uuid, Text name) implements Fragment {
    public static final MapCodec<EntityFragment> CODEC = Uuids.CODEC
            .xmap(uuid -> new EntityFragment(uuid, Text.of(uuid)), EntityFragment::uuid).fieldOf("uuid");

    @Override
    public FragmentType<?> type() {
        return FragmentType.ENTITY;
    }

    @Override
    public Text asText() {
        return name;
    }

    @Override
    public BooleanFragment asBoolean() {
        return BooleanFragment.TRUE;
    }
}
