package dev.enjarai.trickster.aldayim.backend;

import dev.enjarai.trickster.aldayim.Dialogue;
import dev.enjarai.trickster.aldayim.DialogueBackend;
import dev.enjarai.trickster.aldayim.DialogueOption;
import org.jetbrains.annotations.Nullable;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PopupBackend implements DialogueBackend {
    protected final Consumer<Exception> failureHandler;
    protected final List<JFrame> frames = new ArrayList<>();

    public PopupBackend(Consumer<Exception> failureHandler) {
        this.failureHandler = failureHandler;
    }

    @Override
    public void start(Dialogue dialogue) {
        openNext(dialogue, true);

        try {
            synchronized (this) {
                this.wait();
            }
        } catch (InterruptedException e) {
            failureHandler.accept(e);
        }
    }

    protected void openNext(Dialogue dialogue, boolean first) {
        var next = dialogue.open();
        if (next != null) {
            try {
                createPopup(dialogue);
            } catch (Exception e) {
                if (first) {
                    failureHandler.accept(e);
                }
                perish();
            }
        } else {
            perish();
        }
    }

    protected void selectOption(Dialogue dialogue, @Nullable DialogueOption option) {
        var next = dialogue.close(option);
        if (next != null) {
            openNext(dialogue, false);
        } else {
            perish();
        }
    }

    protected void perish() {
        for (var frame : frames) {
            frame.dispose();
        }

        synchronized (this) {
            this.notifyAll();
        }
    }

    protected void createPopup(Dialogue dialogue) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException, InterruptedException {
        System.setProperty("awt.useSystemAAFontSettings", "lcd");
        System.setProperty("swing.aatext", "true");

        // Force GTK if available
//        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
//            if (!"GTK+".equals(laf.getName())) continue;
//            UIManager.setLookAndFeel(laf.getClassName());
//        }

        // ------
        // Window
        // ------

        JFrame window = new JFrame(dialogue.getTitle().getString());
        frames.add(window);

        window.setVisible(false);

        window.setMinimumSize(new Dimension(200, 100));
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                selectOption(dialogue, null);
            }
        });
        window.setLocationByPlatform(true);

        // -----
        // Title
        // -----

        JPanel titlePanel = new JPanel(new BorderLayout());

        JTextArea description = new JTextArea(2, 20);
        description.setText(dialogue.getPrompt().getString());
        description.setWrapStyleWord(true);
        description.setLineWrap(true);
        description.setOpaque(false);
        description.setEditable(false);
        description.setFocusable(false);
        description.setBackground(UIManager.getColor("Label.background"));
        description.setFont(UIManager.getFont("Label.font"));
        description.setBorder(new EmptyBorder(15, 15, 15, 15));
        titlePanel.add(description, BorderLayout.CENTER);


        window.getContentPane().add(titlePanel, BorderLayout.WEST);

        // -------
        // Buttons
        // -------

        JPanel buttons = new JPanel(new GridLayout(0, 1, 0, 5));
        buttons.setBorder(new EmptyBorder(15, 15, 15, 15));
        buttons.setMinimumSize(new Dimension(200, 0));

        for (var response : dialogue.responses()) {
            JButton globalDirButton = new JButton(response.text().getString());
            globalDirButton.addActionListener(e -> {
                selectOption(dialogue, response);
            });

            buttons.add(globalDirButton);
        }

        window.getContentPane().add(buttons, BorderLayout.CENTER);

        // ---------------
        // Window creation
        // ---------------

        window.pack();
        window.setVisible(true);
        window.requestFocus();
    }
}
