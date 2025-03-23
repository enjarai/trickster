package dev.enjarai.trickster.aldayim.backend;

import dev.enjarai.trickster.aldayim.Dialogue;
import dev.enjarai.trickster.aldayim.DialogueBackend;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.random.Random;
import nl.enjarai.cicada.api.imgui.ImGuiThing;

import java.util.Iterator;
import java.util.Stack;

public class ImGuiDialogueBackend implements DialogueBackend, ImGuiThing {
    boolean active = false;
    Stack<Dialogue> dialogueStack = new Stack<>();
    Random random = Random.createLocal();

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
            float responsesWidth = 0;
            for (Iterator<Dialogue.Option> it = dialogue.responses().iterator(); it.hasNext(); ) {
                var option = it.next();
                responsesWidth += ImGui.calcTextSize(option.text()).x;
                responsesWidth += ImGui.getStyle().getFramePaddingX() * 2.0f;

                if (it.hasNext()) {
                    responsesWidth += ImGui.getStyle().getItemSpacingX();
                }
            }
            float promptWidth = ImGui.calcTextSize(dialogue.getPrompt()).x;

            var width = Math.max(
                    150,
                    (int) Math.max(
                            promptWidth / 2,
                            responsesWidth
                    )
            );
            var height = 100;
            ImGui.setNextWindowSize(width, height, ImGuiCond.Appearing);

            var window = MinecraftClient.getInstance().getWindow();
            ImGui.setNextWindowPos(
                    random.nextBetween(0, window.getWidth() - width),
                    random.nextBetween(0, window.getHeight() - height),
                    ImGuiCond.Appearing
            );

            ImGui.begin(dialogue.getId());

            float spaceAvailable = ImGui.getContentRegionAvailX();

            float offset = (spaceAvailable - promptWidth) * 0.5f;
            if (offset > 0.0f) {
                ImGui.setCursorPosX(ImGui.getCursorPosX() + offset);
            }
            ImGui.textWrapped(dialogue.getPrompt());

            ImGui.beginDisabled(dialogueStack.peek() != dialogue);

            offset = (spaceAvailable - responsesWidth) * 0.5f;
            if (offset > 0.0f) {
                ImGui.setCursorPosX(ImGui.getCursorPosX() + offset);
            }

            for (var option : dialogue.responses()) {
                if (ImGui.button(option.text())) {
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
                ImGui.sameLine();
            }

            ImGui.endDisabled();

            ImGui.end();
        }
    }
}
