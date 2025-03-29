package dev.enjarai.trickster.aldayim;

import net.minecraft.text.Text;

public interface TextEntryDialogue extends Dialogue {
    void submit(DialogueBackend backend, Option chosenOption, String input);

    static Dialogue of(Text prompt, SubmitHandler handler) {
        return new Impl(prompt, handler);
    }

    static Dialogue translatable(String key, SubmitHandler handler) {
        return of(Text.translatable(key), handler);
    }

    interface SubmitHandler {
        void onSubmit(DialogueBackend backend, Option chosenOption, String input);
    }

    class Impl extends Dialogue.Impl implements TextEntryDialogue {
        protected SubmitHandler submitHandler;

        public Impl(Text prompt, SubmitHandler handler) {
            super(prompt);
            this.submitHandler = handler;
        }

        @Override
        public void submit(DialogueBackend backend, Option chosenOption, String input) {
            submitHandler.onSubmit(backend, chosenOption, input);
        }
    }
}
