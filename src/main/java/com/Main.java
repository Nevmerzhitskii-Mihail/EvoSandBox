package com;

import com.simulation.World;
import com.utils.RandU;
import com.visual.Drawer;

public class Main {
    public static final int SCREEN_WIDTH = 1600, SCREEN_HEIGHT = 900, TILE = 2;
    public static int WIDTH, HEIGHT;

    public static int drawing_mode;
    public static int background_mode;
    public static void main(String[] args){
        WIDTH = SCREEN_WIDTH / TILE;
        HEIGHT = SCREEN_HEIGHT / TILE;
        long seed = System.currentTimeMillis();
//        long seed = 1744289340867l;
        World.width = WIDTH; World.height = HEIGHT;
        System.out.println("Seed: " + seed);
        RandU.setSeed(seed);
        Drawer.init(SCREEN_WIDTH, SCREEN_HEIGHT, TILE);
        World.init(WIDTH, HEIGHT);
        World.start();
        while (true){
            Drawer.redraw();
        }
    }
}
