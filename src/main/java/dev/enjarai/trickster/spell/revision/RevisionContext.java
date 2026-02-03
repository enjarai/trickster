package dev.enjarai.trickster.spell.revision;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.SpellView;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Consumer;

public interface RevisionContext {
    void updateSpell(SpellPart sp);

    void delegateToServer(Revision revision, SpellView view, Consumer<SpellPart> responseHandler);

    void delegateToServer(Pattern revision, SpellView view, Consumer<SpellPart> responseHandler);

    Set<Pattern> getMacros();

    /**
     * Only works on the server side.
     */
    @Nullable
    ItemStack getOtherHandStack();

    RevisionContext DUMMY = new RevisionContext() {
        @Override
        public void updateSpell(SpellPart sp) {}

        @Override
        public Set<Pattern> getMacros() {
            return Set.of();
        }

        @Override
        public ItemStack getOtherHandStack() {
            return null;
        }

        @Override
        public void delegateToServer(Revision revision, SpellView view, Consumer<SpellPart> responseHandler) {}

        @Override
        public void delegateToServer(Pattern revision, SpellView view, Consumer<SpellPart> responseHandler) {}
    };
}
