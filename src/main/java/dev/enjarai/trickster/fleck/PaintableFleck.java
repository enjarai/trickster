package dev.enjarai.trickster.fleck;

public interface PaintableFleck extends Fleck {
    PaintableFleck paintFleck(int color);

    int getColor();
}
