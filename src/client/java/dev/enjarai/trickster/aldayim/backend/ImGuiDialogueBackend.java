package dev.enjarai.trickster.aldayim.backend;

import dev.enjarai.trickster.aldayim.Dialogue;
import dev.enjarai.trickster.aldayim.DialogueBackend;
import dev.enjarai.trickster.aldayim.TextEntryDialogue;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.type.ImString;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.random.Random;
import nl.enjarai.cicada.api.imgui.ImGuiThing;

import java.util.Iterator;
import java.util.Stack;

public class ImGuiDialogueBackend implements DialogueBackend, ImGuiThing {
    boolean active = false;
    Stack<Entry> dialogueStack = new Stack<>();
    Random random = Random.createLocal();

    @Override
    public void start(Dialogue dialogue) {
        active = true;
        var d = dialogue.open(this);
        dialogueStack.push(new Entry(d, d instanceof TextEntryDialogue ? new ImString() : null));
    }

    @Override
    public void resetStack() {
        dialogueStack.clear();
    }

    @Override
    public void render() {
        for (var entry : dialogueStack.stream().toList()) {
            var dialogue = entry.dialogue();

            float responsesWidth = 0;
            for (Iterator<Dialogue.Option> it = dialogue.responses().iterator(); it.hasNext(); ) {
                Dialogue.Option option = it.next();
                String text = option.text().getString();
                responsesWidth += ImGui.calcTextSize(text).x;
                responsesWidth += ImGui.getStyle().getFramePaddingX() * 2;

                if (it.hasNext()) {
                    responsesWidth += ImGui.getStyle().getItemSpacingX();
                }
            }
            String promptText = dialogue.getPrompt().getString();
            float promptWidth = ImGui.calcTextSize(promptText).x;

            var width = Math.max(
                    150,
                    (int) Math.max(
                            promptWidth / 2,
                            responsesWidth
                    )
            );
            var height = entry.input() != null ? 150 : 100;
            ImGui.setNextWindowSize(width + ImGui.getStyle().getWindowPaddingX() * 2, height, ImGuiCond.Appearing);

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
            ImGui.textWrapped(promptText);

            ImGui.beginDisabled(dialogueStack.peek() != entry);

//            offset = (spaceAvailable - width) * 0.5f;
//            if (offset > 0.0f) {
//                ImGui.setCursorPosX(ImGui.getCursorPosX() + offset);
//            }
            if (entry.input() != null) {
                ImGui.inputText("## input", entry.input());
            }

            offset = (spaceAvailable - responsesWidth) * 0.5f;
            if (offset > 0.0f) {
                ImGui.setCursorPosX(ImGui.getCursorPosX() + offset);
            }

            for (var option : dialogue.responses()) {
                if (ImGui.button(option.text().getString())) {
                    if (dialogue instanceof TextEntryDialogue textEntryDialogue) {
                        //noinspection DataFlowIssue
                        textEntryDialogue.submit(this, option, entry.input().get());
                    }

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

    public record Entry(Dialogue dialogue, ImString input) {

    }
}
