package dev.enjarai.trickster.spell.fragment.slot;

import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.text.Text;
import org.joml.Vector3dc;

import java.util.Optional;

public record ContainerFragment(StorageSource source, VariantType<?> variantType) implements StorageFragment {
    public static final StructEndec<ContainerFragment> ENDEC = StructEndecBuilder.of(
            StorageSource.ENDEC.fieldOf("source", ContainerFragment::source),
            VariantType.ENDEC.fieldOf("variant_type", ContainerFragment::variantType),
            ContainerFragment::new
    );

    @Override
    public FragmentType<?> type() {
        return FragmentType.CONTAINER;
    }

    @Override
    public Text asText() {
        return source.describe();
    }

    @Override
    public int getWeight() {
        return 32 + source.getWeight();
    }

    public Storage<?> getStorage(Trick<?> trick, SpellContext ctx) {
        return source.getStorage(trick, ctx, variantType);
    }

    public <T> Storage<T> getStorage(Trick<?> trick, SpellContext ctx, VariantType<T> expectedVariantType) {
        assertVariantType(trick, expectedVariantType);
        return source.getStorage(trick, ctx, expectedVariantType);
    }

    @Override
    public Optional<Vector3dc> getSourcePos(Trick<?> trick, SpellContext ctx) {
        if (source() == StorageSource.Caster.INSTANCE) {
            return Optional.empty();
        } else {
            return Optional.of(source().getPosition(trick, ctx));
        }
    }

    @Override
    public Vector3dc getSourceOrCasterPos(Trick<?> trick, SpellContext ctx) {
        return source().getPosition(trick, ctx);
    }
}
