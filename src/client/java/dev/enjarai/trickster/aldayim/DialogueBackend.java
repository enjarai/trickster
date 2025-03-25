package dev.enjarai.trickster.aldayim;

public interface DialogueBackend {
    void start(Dialogue dialogue);

    void resetStack();

    boolean isActive();
}
