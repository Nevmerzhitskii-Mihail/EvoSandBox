package com.simulation;

import com.Main;
import com.utils.MathU;
import com.utils.RandU;
import javafx.util.Pair;

public class Bot {
    public static final int PROTEINS_COUNT = 50;
    public static final int SYNTESIS_LOOPS = 3;

    public int x, y;
    public int dir;
    public int energy;
    public int organic;
    public int salt;
    public int age;
    public int[] efficiency = new int[]{333, 333, 333};
    public int[][] proteins = new int[SYNTESIS_LOOPS][PROTEINS_COUNT];

    public Genome genome;
    public int redC = 128, greenC = 128, blueC = 128;

    public Bot(int x, int y, int dir, int energy, int organic, int salt, Genome parent){
        this.x = x; this.y = y; this.dir = dir;
        this.energy = energy; this.organic = organic; this.salt = salt;
        genome = new Genome(parent);
        age = 0;
    }

    public void step(){
        collectOrganic();
        collectSalt();
        for (int i = 0; i < SYNTESIS_LOOPS; i++) proteinsSynthesis(i + 1);
        doAction();
        if (energy <= 0 || age >= 1000) {killBot(); return;}
        energy--;
        age++;
    }

    public void collectOrganic(){
        if (organic + World.organic_map[x][y] <= 1000){
            organic = organic + World.organic_map[x][y];
            World.organic_map[x][y] = 0;
        }
        else {
            World.organic_map[x][y] = World.organic_map[x][y] + organic - 1000;
            organic = 1000;
        }
    }

    public void collectSalt(){
        if (salt + World.salt_map[x][y] <= 1000){
            salt = salt + World.salt_map[x][y];
            World.salt_map[x][y] = 0;
        }
        else {
            World.salt_map[x][y] = World.salt_map[x][y] + salt - 1000;
            salt = 1000;
        }
    }

    public void proteinsSynthesis(int loop){
        System.arraycopy(proteins[(loop - 1) % SYNTESIS_LOOPS], 0, proteins[loop % SYNTESIS_LOOPS], 0, PROTEINS_COUNT);
        for (int i = 0; i < PROTEINS_COUNT; i++) proteins[loop % SYNTESIS_LOOPS][i] = MathU.clamp(proteins[loop % SYNTESIS_LOOPS][i] - 50, 0, 1000);
        for (int i = 0; i < genome.size(); i++){
            boolean cond1 = getOuterCond(genome.get(i, 0), genome.get(i, 1));
            boolean cond2 = getInnerCond(genome.get(i, 2), genome.get(i, 3), loop);
            boolean cond3 = getInnerCond(genome.get(i, 4), genome.get(i, 5), loop);
            if (cond1 && cond2 && cond3) proteins[loop % SYNTESIS_LOOPS][genome.get(i, 6) % PROTEINS_COUNT] = MathU.clamp(proteins[loop % SYNTESIS_LOOPS][genome.get(i, 6) % PROTEINS_COUNT] + genome.get(i, 7), 0, 1000);
        }
    }

    public boolean getOuterCond(int value, int param){
        switch (value){
            case 0:
                int tx = MathU.getTx(x, (dir + param) % 8), ty = MathU.getTy(y, (dir + param) % 8);
                return World.bot_map[tx][ty] == null;
            case 1:
                tx = MathU.getTx(x, (dir + param) % 8); ty = MathU.getTy(y, (dir + param) % 8);
                return World.bot_map[tx][ty] != null;
            case 2:
                tx = MathU.getTx(x, (dir + param) % 8); ty = MathU.getTy(y, (dir + param) % 8);
                if (World.bot_map[tx][ty] == null) return false;
                return isRelative(World.bot_map[tx][ty]);
            case 3:
                tx = MathU.getTx(x, (dir + param) % 8); ty = MathU.getTy(y, (dir + param) % 8);
                if (World.bot_map[tx][ty] == null) return true;
                return !isRelative(World.bot_map[tx][ty]);
            case 4:
                return energy >= param;
            case 5:
                return energy <= param;
            case 6:
                return organic >= param;
            case 7:
                return organic <= param;
            case 8:
                return salt >= param;
            case 9:
                return salt <= param;
            case 10:
                tx = MathU.getTx(x, (dir + param) % 8); ty = MathU.getTy(y, (dir + param) % 8);
                if (World.bot_map[tx][ty] == null) return true;
                return World.bot_map[tx][ty].energy <= energy;
            case 11:
                tx = MathU.getTx(x, (dir + param) % 8); ty = MathU.getTy(y, (dir + param) % 8);
                if (World.bot_map[tx][ty] == null) return false;
                return World.bot_map[tx][ty].energy >= energy;
            case 12:
                tx = MathU.getTx(x, (dir + param) % 8); ty = MathU.getTy(y, (dir + param) % 8);
                if (World.bot_map[tx][ty] == null) return true;
                return World.bot_map[tx][ty].organic <= organic;
            case 13:
                tx = MathU.getTx(x, (dir + param) % 8); ty = MathU.getTy(y, (dir + param) % 8);
                if (World.bot_map[tx][ty] == null) return false;
                return World.bot_map[tx][ty].organic >= organic;
            case 14:
                tx = MathU.getTx(x, (dir + param) % 8); ty = MathU.getTy(y, (dir + param) % 8);
                if (World.bot_map[tx][ty] == null) return true;
                return World.bot_map[tx][ty].salt <= salt;
            case 15:
                tx = MathU.getTx(x, (dir + param) % 8); ty = MathU.getTy(y, (dir + param) % 8);
                if (World.bot_map[tx][ty] == null) return false;
                return World.bot_map[tx][ty].salt >= salt;
            case 16:
                return World.light_map[x][y] >= param;
            case 17:
                return World.light_map[x][y] <= param;
            case 18:
                return World.organic_map[x][y] >= param;
            case 19:
                return World.organic_map[x][y] <= param;
            case 20:
                return World.salt_map[x][y] >= param;
            case 21:
                return World.salt_map[x][y] <= param;
            case 22:
                return ((dir - param) % 8 + 8) % 8 == 0;
            case 23:
                return ((dir - param) % 8 + 8) % 8 != 0;
            default:
                return true;
        }
    }

    public boolean getInnerCond(int cond, int param, int loop){
        if (cond < PROTEINS_COUNT) return proteins[(loop - 1) % SYNTESIS_LOOPS][cond] >= param;
        if (cond < 2 * PROTEINS_COUNT) return proteins[(loop - 1) % SYNTESIS_LOOPS][cond - PROTEINS_COUNT] <= param;
        return param <= 500;
    }

    public void doAction(){
        int max_v = -1, max_i = -1;
        for (int i = 0; i < 8; i++){
            if (proteins[0][i] > max_v){
                max_v = proteins[0][i];
                max_i = i;
            }
        }
        if (max_v < 500) return;
        proteins[0][max_i] -= 500;
        switch (max_i){
            case 0:
                dir = (dir + 1) % 8;
                break;
            case 1:
                dir = (dir + 7) % 8;
            case 2:
                moveBot();
                break;
            case 3:
                doubleBot();
                break;
            case 4:
                attackBot();
                break;
            case 5:
                photosynthesisBot();
                break;
            case 6:
                chemosynthesisBot();
                break;
            case 7:
                produceEnergyBot();
                break;
        }
    }

    public void killBot(){
        World.organic_map[x][y] = MathU.clamp(World.organic_map[x][y] + organic + 100, 0, 1000);
        World.salt_map[x][y] = MathU.clamp(World.salt_map[x][y] + salt, 0, 1000);
        World.bot_map[x][y] = null;
    }

    public void moveBot(){
        energy -= 5;
        int tx = MathU.getTx(x, dir);
        int ty = MathU.getTy(y, dir);
        if (World.bot_map[tx][ty] != null) return;
        World.bot_map[tx][ty] = this;
        World.bot_map[x][y] = null;
        x = tx; y = ty;
    }

    public void doubleBot(){
        if (energy < 150 && organic <= 100) return;
        int doub_dir = findEmptyDir();
        if (doub_dir == 8) return;
        energy -= 150; organic -= 100;
        int tx = MathU.getTx(x, doub_dir);
        int ty = MathU.getTy(y, doub_dir);
        Bot new_bot = new Bot(tx, ty, doub_dir, energy / 2, organic / 2, salt / 2, genome);
        World.bot_map[tx][ty] = new_bot;
        if (RandU.getRandint(0, 1000, tx, ty) <= World.mutation_probably) new_bot.genome.mutate(tx, ty);
        energy -= energy / 2;
        organic -= organic / 2;
        salt -= salt / 2;
    }

    public void attackBot(){
        energy -= 10;
        int tx = MathU.getTx(x, dir);
        int ty = MathU.getTy(y, dir);
        if (World.bot_map[tx][ty] == null) return;
        int new_e = MathU.clamp(energy + World.bot_map[tx][ty].energy, 0, 1000);
        int new_v = MathU.clamp(organic + World.bot_map[tx][ty].organic, 0, 1000);
        salt = MathU.clamp(salt + World.bot_map[tx][ty].salt, 0, 1000);
        World.bot_map[tx][ty] = null;
        addRed((new_v - organic + new_e - energy) / 2);
        organic = new_v;
        energy = new_e;
    }

    public void photosynthesisBot(){
        energy--;
        int new_v = MathU.clamp(organic + World.light_map[x][y] / 3, 0, 1000);
        addGreen(new_v - organic);
        organic = new_v;
    }

    public void chemosynthesisBot(){
        energy--;
        int new_v = MathU.clamp(organic + Math.min(salt, 50), 0, 1000);
        salt = Math.max(0, salt - 50);
        addBlue(new_v - organic);
        organic = new_v;
    }

    public void produceEnergyBot(){
        energy = MathU.clamp(energy + organic, 0, 1000);
        organic = 0;
    }

    public int findEmptyDir(){
        for (int i = 0; i < 8; i++){
            int tx = MathU.getTx(x, (dir + i) % 8);
            int ty = MathU.getTy(y, (dir + i) % 8);
            if (World.bot_map[tx][ty] == null) return (dir + i) % 8;
        }
        return 8;
    }

    public boolean isRelative(Bot other){
        int dr = other.genome.family.getRed() - genome.family.getRed();
        int dg = other.genome.family.getGreen() - genome.family.getGreen();
        int db = other.genome.family.getBlue() - genome.family.getBlue();
        return Math.abs(dr) + Math.abs(dg) + Math.abs(db) < 900;
    }

    private void addRed(int d){
        d = MathU.cast(d, 0, 1000, 0, 255);
        redC = MathU.clamp(redC + d, 0, 255);
        greenC = MathU.clamp(greenC - d, 0, 255);
        blueC = MathU.clamp(blueC - d, 0, 255);
    }

    private void addGreen(int d){
        d = MathU.cast(d, 0, 1000, 0, 255);
        redC = MathU.clamp(redC - d, 0, 255);
        greenC = MathU.clamp(greenC + d, 0, 255);
        blueC = MathU.clamp(blueC - d, 0, 255);
    }

    private void addBlue(int d){
        d = MathU.cast(d, 0, 1000, 0, 255);
        redC = MathU.clamp(redC - d, 0, 255);
        greenC = MathU.clamp(greenC - d, 0, 255);
        blueC = MathU.clamp(blueC + d, 0, 255);
    }
}
