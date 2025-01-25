package dev.enjarai.trickster.aldayim;

import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Dialogue {
    @Nullable Dialogue open();

    @Nullable Dialogue close(@Nullable DialogueOption option);

    Text getTitle();

    Text getPrompt();

    List<DialogueOption> responses();

    static Dialogue of(Text prompt) {
        return new Impl(prompt);
    }

    Dialogue title(Text title);

    Dialogue responses(DialogueOption... options);

    Dialogue onOpen(OpenHandler openHandler);

    Dialogue onClose(CloseHandler closeHandler);

    interface OpenHandler {
        @Nullable Dialogue onOpen(Dialogue newDialogue);
    }

    interface CloseHandler {
        @Nullable Dialogue onClose(Dialogue oldDialogue, @Nullable DialogueOption option);
    }

    class Impl implements Dialogue {
        private OpenHandler openHandler = newDialogue -> newDialogue;
        private CloseHandler closeHandler = (oldDialogue, option) -> option == null ? null : option.resultDialogue();
        private Text prompt;
        private Text title = Text.empty();
        private List<DialogueOption> responses = List.of();

        public Impl(Text prompt) {
            this.prompt = prompt;
        }

        @Override
        public @Nullable Dialogue open() {
            return openHandler.onOpen(this);
        }

        @Override
        public @Nullable Dialogue close(@Nullable DialogueOption option) {
            return closeHandler.onClose(this, option);
        }

        @Override
        public Text getTitle() {
            return title;
        }

        @Override
        public Text getPrompt() {
            return prompt;
        }

        @Override
        public List<DialogueOption> responses() {
            return responses;
        }

        @Override
        public Dialogue title(Text title) {
            this.title = title;
            return this;
        }

        @Override
        public Dialogue responses(DialogueOption... options) {
            this.responses = List.of(options);
            return this;
        }

        @Override
        public Dialogue onOpen(OpenHandler openHandler) {
            this.openHandler = openHandler;
            return this;
        }

        @Override
        public Dialogue onClose(CloseHandler closeHandler) {
            this.closeHandler = closeHandler;
            return this;
        }
    }
}
