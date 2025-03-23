package dev.enjarai.trickster.aldayim;

import net.minecraft.client.resource.language.I18n;

public interface TextEntryDialogue extends Dialogue {
    void submit(DialogueBackend backend, Option chosenOption, String input);

    static Dialogue of(String prompt, SubmitHandler handler) {
        return new Impl(prompt, handler);
    }

    static Dialogue translatable(String prompt, SubmitHandler handler) {
        return new Impl(I18n.translate(prompt), handler);
    }

    interface SubmitHandler {
        void onSubmit(DialogueBackend backend, int chosenOption, String input);
    }

    class Impl extends Dialogue.Impl implements TextEntryDialogue {
        protected SubmitHandler submitHandler;

        public Impl(String prompt, SubmitHandler handler) {
            super(prompt);
            this.submitHandler = handler;
        }

        @Override
        public void submit(DialogueBackend backend, Option chosenOption, String input) {
            submitHandler.onSubmit(backend, responses.indexOf(chosenOption), input);
        }
    }
}
