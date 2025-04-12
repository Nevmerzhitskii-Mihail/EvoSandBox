package com.utils;

import com.Main;
import com.simulation.World;

import java.awt.*;

public class MathU {
    public static int getXFromId(long id){
        return (int)((id % ((long) Main.WIDTH * Main.HEIGHT)) / Main.HEIGHT);
    }

    public static int getYFromID(long id){
        return (int)((id % ((long) Main.WIDTH * Main.HEIGHT)) % Main.HEIGHT);
    }

    public static int clamp(int value, int min, int max){
        return Math.max(min, Math.min(max, value));
    }

    public static int cast(int value, int fromMin, int fromMax, int toMin, int toMax){
        return clamp((int)((toMax - toMin) * (((float) value - fromMin) / (fromMax - fromMin)) + toMin), toMin, toMax);
    }

    public static Color lerp(int value, Color start, Color end){
        int r = cast(value, 0, 1000, start.getRed(), end.getRed());
        int g = cast(value, 0, 1000, start.getGreen(), end.getGreen());
        int b = cast(value, 0, 1000, start.getBlue(), end.getBlue());
        return new Color(r, g, b);
    }

    public static int getDx(int dir){
        if (dir == 0 || dir == 6 || dir == 7) return -1;
        if (dir == 1 || dir == 5) return 0;
        return 1;
    }

    public static int getDy(int dir){
        if (dir == 0 || dir == 1 || dir == 2) return -1;
        if (dir == 3 || dir == 7) return 0;
        return 1;
    }

    public static int getTx(int x, int dir){
        return ((x + getDx(dir)) % Main.WIDTH + Main.WIDTH) % Main.WIDTH;
    }

    public static int getTy(int y, int dir){
        return clamp(y + getDy(dir), 0, Main.HEIGHT - 1);
    }
}
