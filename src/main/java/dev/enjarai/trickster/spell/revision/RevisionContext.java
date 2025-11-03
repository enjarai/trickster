package dev.enjarai.trickster.spell.revision;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.SpellView;
import io.vavr.collection.HashMap;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface RevisionContext {
    void updateSpell(SpellPart sp);

    void delegateToServer(Revision revision, SpellView view, Consumer<SpellPart> responseHandler);

    HashMap<Pattern, SpellPart> getMacros();

    /**
     * Only works on the server side.
     */
    @Nullable
    ItemStack getOtherHandStack();

    RevisionContext DUMMY = new RevisionContext() {
        @Override
        public void updateSpell(SpellPart sp) {}

        @Override
        public HashMap<Pattern, SpellPart> getMacros() {
            return HashMap.empty();
        }

        @Override
        public ItemStack getOtherHandStack() {
            return null;
        }

        @Override
        public void delegateToServer(Revision revision, SpellView view, Consumer<SpellPart> responseHandler) {}
    };
}
