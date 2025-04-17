package com.simulation.genomes;

import com.simulation.Bot;
import com.simulation.Genome;
import com.simulation.World;
import com.utils.MathU;
import com.utils.RandU;

import java.awt.*;

public class Genome_CPU extends Genome {
    public static final int GENOME_SIZE = 64;

    private int adr;
    private int[] mind;

    public Genome_CPU(){
        adr = 0;
        mind = new int[GENOME_SIZE];
        family = new Color(RandU.getRandint(0, 255), RandU.getRandint(0, 255), RandU.getRandint(0, 255));
        mind_type = new Color(0x235DC9);
        repeat = 10;
        for (int i = 0; i < GENOME_SIZE; i++) mind[i] = RandU.getRandint(0, 64);
    }

    private Genome_CPU(int[] parent, Color family){
        adr = 0;
        mind = new int[GENOME_SIZE];
        this.family = new Color(family.getRGB());
        mind_type = new Color(0x235DC9);
        repeat = 10;
        System.arraycopy(parent, 0, mind, 0, GENOME_SIZE);
    }

    @Override
    public void mutate(int x, int y) {
        mind[RandU.getRandint(0, GENOME_SIZE, x, y)] = RandU.getRandint(0, 64, x, y);
        mutate_color(x, y);
    }

    @Override
    public Genome copy() {
        return new Genome_CPU(mind, family);
    }

    @Override
    public int get_action(Bot bot) {
        int command = mind[adr];
        switch (command){
            case 0:
            case 1:
                INC();
                return 0;
            case 2:
            case 3:
                INC();
                return 1;
            case 4:
            case 5:
                INC();
                return 2;
            case 6:
            case 7:
                INC();
                return 3;
            case 8:
            case 9:
                INC();
                return 4;
            case 10:
            case 11:
                INC();
                return 5;
            case 12:
            case 13:
                INC();
                return 6;
            case 14:
            case 15:
                INC();
                return 7;
            case 16:
            case 17:
                 JMP(WATCH(bot));
                 return -1;
            case 18:
                JMP(ENERGY(bot));
                return -1;
            case 19:
                JMP(ORGANIC(bot));
                return -1;
            case 20:
                JMP(SALT(bot));
                return -1;
            case 21:
                JMP(SOIL_LIGHT(bot));
                return -1;
            case 22:
                JMP(SOIL_ORGANIC(bot));
                return -1;
            case 23:
                JMP(SOIL_SALT(bot));
                return -1;
            default:
                adr = (adr + command) % GENOME_SIZE;
                return -1;
        }
    }

    private int WATCH(Bot bot){
        int tx = MathU.getTx(bot.x, bot.dir);
        int ty = MathU.getTy(bot.y, bot.dir);
        Bot other = World.bot_map[tx][ty];
        if (other == null) return 1;
        if (bot.isRelative(other)) return 2;
        return 3;
    }

    private int ENERGY(Bot bot){
        if (bot.energy >= ARG(1) * 15) return 2;
        return 3;
    }

    private int ORGANIC(Bot bot){
        if (bot.organic >= ARG(1) * 15) return 2;
        return 3;
    }

    private int SALT(Bot bot){
        if (bot.salt >= ARG(1) * 15) return 2;
        return 3;
    }

    private int SOIL_LIGHT(Bot bot){
        if (World.light_map[bot.x][bot.y] >= ARG(1) * 15) return 2;
        return 3;
    }

    private int SOIL_ORGANIC(Bot bot){
        if (World.organic_map[bot.x][bot.y] >= ARG(1) * 15) return 2;
        return 3;
    }

    private int SOIL_SALT(Bot bot){
        if (World.salt_map[bot.x][bot.y] >= ARG(1) * 15) return 2;
        return 3;
    }

    private void INC(){
        adr = (adr + 1) % GENOME_SIZE;
    }

    private void JMP(int param){
        adr = (adr + ARG(param)) % GENOME_SIZE;
    }

    private int ARG(int num){
        return mind[(adr + num) % GENOME_SIZE];
    }


}
