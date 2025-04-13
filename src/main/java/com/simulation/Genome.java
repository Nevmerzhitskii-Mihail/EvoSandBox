package com.simulation;

import com.utils.MathU;
import com.utils.RandU;

import java.awt.*;
import java.util.ArrayList;

public class Genome {

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


    public ArrayList<ArrayList<Integer>> genome = new ArrayList<>(); // Список генов
    public Color family; // "Натуральный" цвет бота (используется при определении родственников)

    // Создание случайного генома
    public Genome(int size) {
        for (int i = 0; i < size; i++) {
            ArrayList<Integer> gene = new ArrayList<>();
            int gene_size = RandU.getRandint(1, 8) * 2 + 2;
            for (int j = 0; j < gene_size; j++) gene.add(RandU.getRandint(0, 1000));
            genome.add(gene);
        }
        family = new Color(RandU.getRandint(30, 225), RandU.getRandint(30, 225), RandU.getRandint(30, 225));
    }

    // Создание генома-копии
    public Genome(Genome parent) {
        for (int i = 0; i < parent.genome.size(); i++) {
            genome.add(new ArrayList<>(parent.genome.get(i)));
        }
        family = new Color(parent.family.getRGB());
    }

    // Мутация генома
    public void mutate(int x, int y) {
        int r = RandU.getRandint(0, 100, x, y);
        if (r <= 10){
            ArrayList<Integer> tmp = new ArrayList<>(genome.get(RandU.getRandint(0, genome.size(), x, y)));
            genome.add(tmp);
        }
        else if (r <= 20){
            genome.remove(RandU.getRandint(0, genome.size(), x, y));
        }
        else if (r <= 30){
            int gene_num = RandU.getRandint(0, genome.size(), x, y);
            if (genome.get(gene_num).size() < 32) {
                genome.get(gene_num).add(RandU.getRandint(0, 1000, x, y));
                genome.get(gene_num).add(RandU.getRandint(0, 1000, x, y));
            }
        }
        else if (r <= 40){
            int gene_num = RandU.getRandint(0, genome.size(), x, y);
            try {
                if (genome.get(gene_num).size() > 4) {
                    int codon_num = RandU.getRandint(1, genome.get(gene_num).size() / 2, x, y);
                    int a = genome.get(gene_num).remove(codon_num * 2);
                    a = genome.get(gene_num).remove(codon_num * 2);
                }
            }
            catch (IndexOutOfBoundsException e){
                System.out.println(genome.get(gene_num).size());
            }
        }
        else {
            for (int i = 0; i < 5; i++) {
                int gene_num = RandU.getRandint(0, genome.size(), x, y);
                genome.get(gene_num).set(RandU.getRandint(0, genome.get(gene_num).size()), RandU.getRandint(0, 1000, x, y));
            }
        }

        // Каждая компонента цвета изменяется максимум на 10 и ограничивается 0 и 255
        family = new Color(MathU.clamp(family.getRed() + RandU.getRandint(-10, 11, x, y), 0, 255),
                MathU.clamp(family.getGreen() + RandU.getRandint(-10, 11, x, y), 0, 255),
                MathU.clamp(family.getBlue() + RandU.getRandint(-10, 11, x, y), 0, 255));
    }

    // Получение значения данного числа в геноме
    public int get(int a, int b) {
        return genome.get(a).get(b);
    }

    // Получение размера генома
    public int size() {
        return genome.size();
    }
}
