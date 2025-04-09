package com.utils;

import java.util.Random;

public class RandU {
    static Random rand = new Random();
    public static void setSeed(long seed){
        rand.setSeed(seed);
    }

    public static int getRandint(int min, int max){
        return (rand.nextInt() % (max - min) + max - min) % (max - min) + min;
    }
}
