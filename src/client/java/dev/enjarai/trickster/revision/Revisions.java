package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.spell.Pattern;

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
