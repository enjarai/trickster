package dev.enjarai.trickster.spell.fragment;

import java.util.Optional;
import java.util.UUID;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.cca.ModWorldComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.ward.Ward;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.text.Text;

public record WardFragment(UUID uuid) implements Fragment {
    public static final StructEndec<WardFragment> ENDEC = StructEndecBuilder.of(
            EndecTomfoolery.UUID.fieldOf("uuid", WardFragment::uuid),
            WardFragment::new
    );

    @Override
    public FragmentType<?> type() {
        return FragmentType.WARD;
    }

    @Override
    public Text asText() {
        return Text.literal("Meow meow");
    }

    @Override
    public int getWeight() {
        return 32;
    }

    public Optional<Ward> getWard(SpellContext ctx) {
        var manager = ModWorldComponents.WARD_MANAGER.get(ctx.source().getWorld());
        return manager.get(uuid);
    }
}
