package dev.enjarai.trickster.aldayim;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public interface DialogueBackend {
    ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setDaemon(true).build());

    void start(Dialogue dialogue);

    default void startOffThread(Dialogue dialogue) {
        EXECUTOR.submit(() -> start(dialogue));
    }
}
