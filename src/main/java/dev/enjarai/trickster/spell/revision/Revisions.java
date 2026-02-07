package dev.enjarai.trickster.spell.revision;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.MapFragment;
import dev.enjarai.trickster.spell.trick.Tricks;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.util.*;

public class Revisions {
    /** Do not add to this directly */
    public static final Registry<Revision> REGISTRY = FabricRegistryBuilder.<Revision>createSimple(RegistryKey.ofRegistry(Trickster.id("revisions"))).buildAndRegister();
    private static final Map<Pattern, Revision> LOOKUP = new HashMap<>();

    public static final Revision CREATE_SUBCIRCLE = register(Trickster.id("add_subcircle"), new CreateSubcircleRevision());
    public static final Revision CREATE_INNER_CIRCLE = register(Trickster.id("add_inner_circle"), new CreateInnerCircleRevision());
    public static final Revision INSERT_PARENT_CIRCLE = register(Trickster.id("to_subcircle"), new InsertParentCircleRevision());
    public static final Revision INTO_INNER_CIRCLE = register(Trickster.id("to_inner_circle"), new IntoInnerCircleRevision());
    public static final Revision REPLACE_OUTER_CIRCLE = register(Trickster.id("remove_outer"), new ReplaceOuterCircleRevision());
    public static final Revision CREATE_OUTER_SUBCIRCLE = register(Trickster.id("add_outer_subcircle"), new CreateOuterSubcircleRevision());
    public static final Revision DELETE_CIRCLE = register(Trickster.id("remove_self"), new DeleteCircleRevision());
    public static final Revision DELETE_BRANCH = register(Trickster.id("remove_self_recursive"), new DeleteBranchRevision());
    public static final Revision SWAP_SUBCIRCLE = register(Trickster.id("swap"), new SwapSubcircleRevision());
    public static final Revision SHIFT_SUBCIRCLE_BACKWARDS = register(Trickster.id("rotate_ccw"), new ShiftSubcircleBackwardsRevision());
    public static final Revision SHIFT_SUBCIRCLE_FORWARDS = register(Trickster.id("rotate_cw"), new ShiftSubcircleForwardsRevision());
    public static final Revision REPLACE_CIRCLE_WITH_OFF_HAND = register(Trickster.id("splice"), new ReplaceCircleWithOffHandRevision());
    public static final Revision REPLACE_GLYPH_WITH_OFF_HAND = register(Trickster.id("splice_inner"), new ReplaceGlyphWithOffHandRevision());
    //    public static final Revision REPLACE_CIRCLE_WITH_CROW_MIND = register(new ReplaceCircleWithCrowMindRevision());
    //    public static final Revision EXECUTE_OFF_HAND = register(new ExecuteOffHandRevision());
    public static final Revision WRITE_ADDRESS_TO_OFF_HAND = register(Trickster.id("write_path"), new WriteAddressToOffHandRevision());
    public static final Revision WRITE_BRANCH_TO_OFF_HAND = register(Trickster.id("write"), new WriteBranchToOffHandRevision());
    public static final Revision PATTERN_LITERAL = register(Trickster.id("quote_pattern"), new PatternLiteralRevision());

    public static final Revision ONE_PONY_TRICK = register(Trickster.id("two"), new ConstantRevision(Tricks.TWO.getPattern(), new NumberFragment(2)));
    public static final Revision EMPTY_LIST = register(Trickster.id("list_create"), new ConstantRevision(Tricks.LIST_CREATE.getPattern(), ListFragment.EMPTY));
    public static final Revision EMPTY_MAP = register(Trickster.id("map_create"), new ConstantRevision(Pattern.of(2, 5, 8, 6, 3, 0), MapFragment.EMPTY));

    public static Optional<Revision> lookup(Pattern pattern) {
        return Optional.ofNullable(LOOKUP.get(pattern));
    }

    public static Revision register(Identifier id, Revision revision) {
        Registry.register(REGISTRY, id, revision);
        if (LOOKUP.put(revision.pattern(), revision) instanceof Revision prev) {
            Trickster.LOGGER.warn("Revision '{}' is overwriting revision '{}' because both have the same pattern", id, REGISTRY.getId(prev));
        }

        return revision;
    }

    public static void register() {}
}
