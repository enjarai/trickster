package dev.enjarai.trickster.aldayim;

import java.util.List;

public interface PromptDialogue extends Dialogue {
    Dialogue.Option getConfirmation();

    void onConfirm();

    @Override
    default List<Dialogue.Option> responses() {
        return List.of(getConfirmation());
    }
}
