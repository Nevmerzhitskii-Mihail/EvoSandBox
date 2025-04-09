package com.simulation;

import com.utils.MathU;
import com.utils.RandU;

import java.awt.*;
import java.util.ArrayList;

public class Genome {
    public static final int GENE_SIZE = 8;

    // Структура генома
    // Геном состоит из списка генов и может меняться в размерах
    // Ген - это массив из 7 чисел - оснований
    // [C1, P1, C2, P2, C3, P3, Pr]
    // C1, C2, C3 - условия
    // P1, P2, P3 - параметры условий
    // Pr - номер синтезируемого белка

    // Первое условие - внешние (т.е. проверка перед собой, проверка энергии, проверка соли и т.д.)
    // Второе и третье условия - внутренние (т.е. проверка наличия или отсутсвия определённого белка)
    // Условие может быть пустым, т.е. его значение всегда "истина"
    // Синтез белка происходит только если все три условия выполнены


    public ArrayList<Integer[]> genome = new ArrayList<>(); // Список генов
    public Color family; // "Натуральный" цвет бота (используется при определении родственников)

    // Создание случайного генома
    public Genome(int size) {
        for (int i = 0; i < size; i++) {
            Integer[] tmp = new Integer[GENE_SIZE];
            for (int j = 0; j < GENE_SIZE; j++) tmp[j] = RandU.getRandint(0, 1000);
            genome.add(tmp);
        }
        family = new Color(RandU.getRandint(30, 225), RandU.getRandint(30, 225), RandU.getRandint(30, 225));
    }

    // Создание генома-копии
    public Genome(Genome parent) {
        for (int i = 0; i < parent.genome.size(); i++) {
            Integer[] tmp = new Integer[GENE_SIZE];
            System.arraycopy(parent.genome.get(i), 0, tmp, 0, GENE_SIZE);
            genome.add(tmp);
        }
        family = new Color(parent.family.getRGB());
    }

    // Мутация генома
    public void mutate() {
        genome.get(RandU.getRandint(0, genome.size() - 1))[RandU.getRandint(0, GENE_SIZE - 1)] = RandU.getRandint(0, 1000);

        // Каждая компонента цвета изменяется максимум на 10 и ограничивается 0 и 255
        family = new Color(MathU.clamp(family.getRed() + RandU.getRandint(-3, 3), 0, 255),
                MathU.clamp(family.getRed() + RandU.getRandint(-3, 3), 0, 255),
                MathU.clamp(family.getRed() + RandU.getRandint(-3, 3), 0, 255));
    }

    // Получение значения данного числа в геноме
    public int get(int a, int b) {
        return genome.get(a)[b];
    }

    // Получение размера генома
    public int size() {
        return genome.size();
    }
}
