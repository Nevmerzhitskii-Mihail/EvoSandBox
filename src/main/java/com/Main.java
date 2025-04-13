package com;

import com.simulation.World;
import com.utils.RandU;
import com.visual.Drawer;

public class Main {

    public static int drawing_mode;
    public static int background_mode;

    public static boolean is_started = false;
    public static void regenerate(long seed){
        World.stop();
        RandU.setSeed(seed);
        World.init();
        is_started = false;
        World.start();
    }

    public static void main(String[] args){
        regenerate(System.currentTimeMillis());
        Drawer.init();
        while (true){
            Drawer.redraw();
        }
    }
}
