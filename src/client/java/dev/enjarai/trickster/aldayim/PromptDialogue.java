package dev.enjarai.trickster.aldayim;

import java.util.List;

public interface PromptDialogue extends Dialogue {
    DialogueOption getConfirmation();

    void onConfirm();

    @Override
    default List<DialogueOption> responses() {
        return List.of(getConfirmation());
    }
}
