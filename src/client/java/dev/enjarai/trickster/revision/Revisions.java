package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.MapFragment;
import dev.enjarai.trickster.spell.trick.Tricks;

import java.util.*;

public class Revisions {
    private static final Map<Pattern, Revision> REGISTRY = new HashMap<>();

    public static final Revision CREATE_SUBCIRCLE = register(new CreateSubcircleRevision());
    public static final Revision CREATE_INNER_CIRCLE = register(new CreateInnerCircleRevision());
    public static final Revision INSERT_PARENT_CIRCLE = register(new InsertParentCircleRevision());
    public static final Revision INTO_INNER_CIRCLE = register(new IntoInnerCircleRevision());
    public static final Revision REPLACE_OUTER_CIRCLE = register(new ReplaceOuterCircleRevision());
    public static final Revision CREATE_OUTER_SUBCIRCLE = register(new CreateOuterSubcircleRevision());
    public static final Revision DELETE_CIRCLE = register(new DeleteCircleRevision());
    public static final Revision DELETE_BRANCH = register(new DeleteBranchRevision());
    public static final Revision SWAP_SUBCIRCLE = register(new SwapSubcircleRevision());
    public static final Revision SHIFT_SUBCIRCLE_BACKWARDS = register(new ShiftSubcircleBackwardsRevision());
    public static final Revision SHIFT_SUBCIRCLE_FORWARDS = register(new ShiftSubcircleForwardsRevision());
    public static final Revision REPLACE_CIRCLE_WITH_OFF_HAND = register(new ReplaceCircleWithOffHandRevision());
    public static final Revision REPLACE_GLYPH_WITH_OFF_HAND = register(new ReplaceGlyphWithOffHandRevision());
    public static final Revision REPLACE_CIRCLE_WITH_CROW_MIND = register(new ReplaceCircleWithCrowMindRevision());
    public static final Revision EXECUTE_OFF_HAND = register(new ExecuteOffHandRevision());
    public static final Revision WRITE_ADDRESS_TO_OFF_HAND = register(new WriteAddressToOffHandRevision());
    public static final Revision WRITE_BRANCH_TO_OFF_HAND = register(new WriteBranchToOffHandRevision());
    public static final Revision PATTERN_LITERAL = register(new PatternLiteralRevision());

    public static final Revision ONE_PONY_TRICK = register(new ConstantRevision(Tricks.TWO.getPattern(), new NumberFragment(2)));
    public static final Revision EMPTY_LIST = register(new ConstantRevision(Tricks.LIST_CREATE.getPattern(), new ListFragment(List.of())));
    public static final Revision EMPTY_MAP = register(new ConstantRevision(Pattern.of(2, 5, 8, 6, 3, 0), new MapFragment(io.vavr.collection.HashMap.empty())));
    public static final Revision PI_CONSTANT = register(new ConstantRevision(Pattern.of(6, 0, 2, 8, 5), new NumberFragment(Math.PI)));

    public static Optional<Revision> lookup(Pattern pattern) {
        return Optional.ofNullable(REGISTRY.get(pattern));
    }

    public static Revision register(Revision revision) {
        if (REGISTRY.put(revision.pattern(), revision) != null) {
            //TODO: add override warning
        }

        return revision;
    }
}
