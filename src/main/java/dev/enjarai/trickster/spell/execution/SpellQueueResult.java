package dev.enjarai.trickster.spell.execution;

public record SpellQueueResult(Type type, ExecutionState state) {
    public enum Type {
        NOT_QUEUED,
        QUEUED_DONE,
        QUEUED_STILL_RUNNING
    }
}