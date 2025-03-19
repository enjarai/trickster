package dev.enjarai.trickster.aldayim;

import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public interface Dialogue {
    @Nullable
    Dialogue open(DialogueBackend backend);

    @Nullable
    Dialogue next(DialogueBackend backend, @Nullable Option option);

    String getId();

    Text getPrompt();

    List<Option> responses();

    static Dialogue of(Text prompt) {
        return new Impl(prompt);
    }

    static Dialogue translatable(String key) {
        return new Impl(Text.translatable(key));
    }

    Dialogue responses(Option... options);

    Dialogue onOpen(OpenHandler openHandler);

    Dialogue onNext(NextHandler nextHandler);

    void resetsStack();

    interface OpenHandler {
        @Nullable
        Dialogue onOpen(DialogueBackend backend, Dialogue newDialogue);
    }

    interface NextHandler {
        @Nullable
        Dialogue onNext(DialogueBackend backend, Dialogue oldDialogue, @Nullable Option option);
    }

    class Impl implements Dialogue {
        private UUID id = UUID.randomUUID();
        private OpenHandler openHandler = (backend, newDialogue) -> newDialogue;
        private NextHandler nextHandler = (backend, oldDialogue, option) -> option == null ? null : option.resultDialogue().get();
        private Text prompt;
        private Text title = Text.empty();
        private List<Option> responses = List.of();
        private boolean resetsStack = false;

        public Impl(Text prompt) {
            this.prompt = prompt;
        }

        @Override
        public @Nullable Dialogue open(DialogueBackend backend) {
            return openHandler.onOpen(backend, this);
        }

        @Override
        public @Nullable Dialogue next(DialogueBackend backend, @Nullable Option option) {
            if (resetsStack) {
                backend.resetStack();
            }
            return nextHandler.onNext(backend, this, option);
        }

        @Override
        public String getId() {
            return id.toString();
        }

        @Override
        public Text getPrompt() {
            return prompt;
        }

        @Override
        public List<Option> responses() {
            return responses;
        }

        @Override
        public Dialogue responses(Option... options) {
            this.responses = List.of(options);
            return this;
        }

        @Override
        public Dialogue onOpen(OpenHandler openHandler) {
            this.openHandler = openHandler;
            return this;
        }

        @Override
        public Dialogue onNext(NextHandler nextHandler) {
            this.nextHandler = nextHandler;
            return this;
        }

        public void resetsStack() {
            resetsStack = true;
        }
    }

    record Option(Text text, Supplier<Dialogue> resultDialogue) {
        public static Option of(Text text, Supplier<Dialogue> resultDialogue) {
            return new Option(text, resultDialogue);
        }

        public static Option of(Text text, Dialogue resultDialogue) {
            return of(text, () -> resultDialogue);
        }

        public static Option translatable(String key, Supplier<Dialogue> resultDialogue) {
            return of(Text.translatable(key), resultDialogue);
        }

        public static Option translatable(String key, Dialogue resultDialogue) {
            return of(Text.translatable(key), resultDialogue);
        }
    }
}
