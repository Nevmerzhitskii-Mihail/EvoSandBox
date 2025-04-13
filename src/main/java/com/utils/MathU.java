package com.utils;

import com.Settings;

import java.awt.*;

public class MathU {
    public static int getXFromId(long id){
        return (int)((id % ((long) Settings.WIDTH * Settings.HEIGHT)) / Settings.HEIGHT);
    }

    public static int getYFromID(long id){
        return (int)((id % ((long) Settings.WIDTH * Settings.HEIGHT)) % Settings.HEIGHT);
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

    public static Color avr(Color[] colors){
        int r = 0, g = 0, b = 0;
        for (int i = 0; i < colors.length; i++){
            r += colors[i].getRed();
            g += colors[i].getGreen();
            b += colors[i].getBlue();
        }
        r /= colors.length;
        g /= colors.length;
        b /= colors.length;
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
        return ((x + getDx(dir)) % Settings.WIDTH + Settings.WIDTH) % Settings.WIDTH;
    }

    public static int getTy(int y, int dir){
        return clamp(y + getDy(dir), 0, Settings.HEIGHT - 1);
    }
}
