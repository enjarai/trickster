package dev.enjarai.trickster.aldayim;

import net.minecraft.client.resource.language.I18n;
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

    String getPrompt();

    List<Option> responses();

    static Dialogue of(String prompt) {
        return new Impl(prompt);
    }

    static Dialogue translatable(String prompt, Object... args) {
        return new Impl(I18n.translate(prompt, args));
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
        protected UUID id = UUID.randomUUID();
        protected OpenHandler openHandler = (backend, newDialogue) -> newDialogue;
        protected NextHandler nextHandler = (backend, oldDialogue, option) -> option == null ? null : option.resultDialogue().get();
        protected String prompt;
        protected List<Option> responses = List.of();
        protected boolean resetsStack = false;

        public Impl(String prompt) {
            this.prompt = prompt;
        }

        //TODO: @enjarai make this pick a random position for the dialogue, ideally
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
        public String getPrompt() {
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

    record Option(String text, Supplier<Dialogue> resultDialogue) {
        public static Option of(String text, Supplier<Dialogue> resultDialogue) {
            return new Option(text, resultDialogue);
        }

        public static Option of(String text, Dialogue resultDialogue) {
            return of(text, () -> resultDialogue);
        }

        public static Option translatable(String text, Supplier<Dialogue> resultDialogue) {
            return new Option(I18n.translate(text), resultDialogue);
        }

        public static Option translatable(String text, Dialogue resultDialogue) {
            return of(I18n.translate(text), () -> resultDialogue);
        }
    }
}
