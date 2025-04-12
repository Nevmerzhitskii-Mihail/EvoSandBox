package com;

import com.simulation.World;
import com.utils.RandU;
import com.visual.Drawer;

public class Main {
    public static final int SCREEN_WIDTH = 1600, SCREEN_HEIGHT = 900, TILE = 2;
    public static int WIDTH, HEIGHT;

    public static int drawing_mode;
    public static int background_mode;

    public static boolean is_started = false;
    public static void regenerate(long seed){
        World.stop();
        RandU.setSeed(seed);
        World.init(WIDTH, HEIGHT);
        is_started = false;
        World.start();
    }

    public static void main(String[] args){
        WIDTH = SCREEN_WIDTH / TILE;
        HEIGHT = SCREEN_HEIGHT / TILE;
        regenerate(System.currentTimeMillis());
        Drawer.init(SCREEN_WIDTH, SCREEN_HEIGHT, TILE);
        while (true){
            Drawer.redraw();
        }
    }
}
