package com.utils;

import com.Main;
import com.Settings;
import com.simulation.World;

import java.util.Random;

public class RandU {
    static Random rand = new Random();
    static Random[][] rand_grid;
    static long seed = 0;
    public static void setSeed(long seed){
        rand.setSeed(seed);
        RandU.seed = seed;
        rand_grid = new Random[Settings.WIDTH][Settings.HEIGHT];
        for (long id = 0; id < (long) Settings.WIDTH * Settings.HEIGHT; id++){
            rand_grid[MathU.getXFromId(id)][MathU.getYFromID(id)] = new Random(seed + id + 1);
        }
    }

    public static int getRandint(int min, int max){
        return (rand.nextInt() % (max - min) + max - min) % (max - min) + min;
    }

    public static int getRandint(int min, int max, int x, int y){
        try {
            return (rand_grid[x][y].nextInt() % (max - min) + max - min) % (max - min) + min;
        }
        catch (ArrayIndexOutOfBoundsException e){
            System.out.println(x + " " + y);
            return 0;
        }
    }
}
