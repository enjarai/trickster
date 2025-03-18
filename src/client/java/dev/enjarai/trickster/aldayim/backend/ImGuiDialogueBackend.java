package dev.enjarai.trickster.aldayim.backend;

import dev.enjarai.trickster.aldayim.Dialogue;
import dev.enjarai.trickster.aldayim.DialogueBackend;
import imgui.ImGui;
import nl.enjarai.cicada.api.imgui.ImGuiThing;

import java.util.Stack;

public class ImGuiDialogueBackend implements DialogueBackend, ImGuiThing {
    boolean active = false;
    Stack<Dialogue> dialogueStack = new Stack<>();

    @Override
    public void start(Dialogue dialogue) {
        active = true;
        dialogueStack.push(dialogue.open(this));
    }

    @Override
    public void resetStack() {
        dialogueStack.clear();
    }

    @Override
    public void render() {
        for (var dialogue : dialogueStack.stream().toList()) {

            ImGui.begin(dialogue.getTitle().getString());
            ImGui.text(dialogue.getPrompt().getString());

            ImGui.beginDisabled(dialogueStack.peek() != dialogue);

            for (var option : dialogue.responses()) {
                if (ImGui.button(option.text().getString())) {
                    var next = dialogue.next(this, option);
                    if (next == null) {
                        resetStack();
                    } else {
                        next = next.open(this);
                        if (next == null) {
                            resetStack();
                        } else {
                            start(next);
                        }
                    }
                }
            }

            ImGui.endDisabled();

            ImGui.end();
        }
    }
}
