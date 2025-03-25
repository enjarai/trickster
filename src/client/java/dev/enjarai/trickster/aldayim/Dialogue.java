package dev.enjarai.trickster.aldayim;

import org.jetbrains.annotations.Nullable;

import net.minecraft.text.Text;

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

    static Dialogue of(String prompt) {
        return of(Text.literal(prompt));
    }

    static Dialogue translatable(String key, Object... args) {
        return of(Text.translatable(key, args));
    }

    static Dialogue closer() {
        return of(Text.empty()).onOpen((backend, newDialogue) -> null);
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
        Dialogue onNext(DialogueBackend backend, Dialogue oldDialogue, Option option);
    }

    class Impl implements Dialogue {
        protected UUID id = UUID.randomUUID();
        protected OpenHandler openHandler = (backend, newDialogue) -> newDialogue;
        protected NextHandler nextHandler = (backend, oldDialogue, option) -> option == null ? null : option.resultDialogue().get();
        protected Text prompt;
        protected List<Option> responses = List.of();
        protected boolean resetsStack = false;

        public Impl(Text prompt) {
            this.prompt = prompt;
        }

        @Override
        public @Nullable Dialogue open(DialogueBackend backend) {
            return openHandler.onOpen(backend, this);
        }

        @Override
        public @Nullable Dialogue next(DialogueBackend backend, Option option) {
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

        public static Option of(String text, Supplier<Dialogue> resultDialogue) {
            return of(Text.literal(text), resultDialogue);
        }

        public static Option of(String text, Dialogue resultDialogue) {
            return of(text, () -> resultDialogue);
        }

        public static Option translatable(String key, Supplier<Dialogue> resultDialogue) {
            return new Option(Text.translatable(key), resultDialogue);
        }

        public static Option translatable(String key, Dialogue resultDialogue) {
            return translatable(key, () -> resultDialogue);
        }
    }
}
