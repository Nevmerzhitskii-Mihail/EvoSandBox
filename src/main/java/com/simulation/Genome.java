package com.simulation;

import com.utils.MathU;
import com.utils.RandU;

import java.awt.*;
import java.util.ArrayList;

public abstract class Genome {
    public Color family; // "Натуральный" цвет бота (используется при определении родственников)
    public Color mind_type; // Индикатор типа генома

    // Копирование генома
    public abstract Genome copy();

    // Мутация генома
    public abstract void mutate(int x, int y);

    // Просчёт действия
    public abstract int get_action(Bot bot);

    // Изменение цвета
    protected void mutate_color(int x, int y){
        family = new Color(MathU.clamp(family.getRed() + RandU.getRandint(-10, 11, x, y), 0, 255),
                MathU.clamp(family.getGreen() + RandU.getRandint(-10, 11, x, y), 0, 255),
                MathU.clamp(family.getBlue() + RandU.getRandint(-10, 11, x, y), 0, 255));
    }
}
