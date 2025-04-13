package com;

public class Settings {
    // параметры отрисовки
    public static final int SCREEN_WIDTH = 1600;
    public static final int SCREEN_HEIGHT = 900;
    public static final int TILE = 2;

    // системные параметры
    public static final int WIDTH = SCREEN_WIDTH / TILE, HEIGHT = SCREEN_HEIGHT / TILE;
    public static final int THREADS_COUNT = 10;

    // параметры симуляции
    public static final int PROTEINS_COUNT = 50;
    public static final int MORPHOGENES_COUNT = 12;
    public static final int SYNTESIS_LOOPS = 2;
}
