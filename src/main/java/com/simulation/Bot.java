package com.simulation;

import com.Main;
import com.Settings;
import com.utils.MathU;
import com.utils.RandU;
import javafx.util.Pair;

public class Bot {
    public int x, y;
    public int dir;
    public int energy;
    public int organic;
    public int salt;
    public int age;
    public int[] efficiency = new int[]{333, 333, 333};
    public int[][] proteins = new int[Settings.SYNTESIS_LOOPS][Settings.PROTEINS_COUNT];

    public Genome genome;
    public int redC = 128, greenC = 128, blueC = 128;

    public Bot(int x, int y, int dir, int energy, int organic, int salt, Genome parent, int[] proteins_start){
        this.x = x; this.y = y; this.dir = dir;
        this.energy = energy; this.organic = organic; this.salt = salt;
        genome = new Genome(parent);
        age = 0;
        System.arraycopy(proteins_start, 0, proteins[0], 0, Settings.PROTEINS_COUNT);
    }

    public void step(){
        collectOrganic();
        collectSalt();
        for (int i = 0; i < Settings.SYNTESIS_LOOPS; i++) proteinsSynthesis(i + 1);
        int max_c = -1, max_i = -1;
        for (int i = 0; i < 7; i++){
            if (proteins[0][i] > max_c){
                max_c = proteins[0][i];
                max_i = i;
            }
        }
        if (max_c > 500){
            proteins[0][max_i] -= 500;
            doAction(max_i, 1);
        }
        produceEnergyBot();
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
        System.arraycopy(proteins[(loop - 1) % Settings.SYNTESIS_LOOPS], 0, proteins[loop % Settings.SYNTESIS_LOOPS], 0, Settings.PROTEINS_COUNT);
        for (int i = 0; i < Settings.PROTEINS_COUNT; i++){
            proteins[loop % Settings.SYNTESIS_LOOPS][i] = MathU.clamp(proteins[loop % Settings.SYNTESIS_LOOPS][i] - 50, 0, 1000);
        }
        for (int i = 0; i < genome.genome.size(); i++){
            boolean res = true;
            int com = genome.get(i, 0), intensity = genome.get(i, 1);
            for (int j = 2; j < genome.genome.get(i).size(); j += 2) {
                res = res & getCond(genome.get(i, j), genome.get(i, j + 1), loop);
            }
            if (res) {
                if (com < Settings.MORPHOGENES_COUNT) World.putMorphogene(x, y, com, intensity / 31);
                else if (com < Settings.MORPHOGENES_COUNT + Settings.PROTEINS_COUNT)
                    proteins[loop % Settings.SYNTESIS_LOOPS][com - Settings.MORPHOGENES_COUNT] = MathU.clamp(proteins[loop % Settings.SYNTESIS_LOOPS][com - Settings.MORPHOGENES_COUNT] + intensity, 0, 1000);
            }
        }
    }

    public boolean getCond(int cond, int param, int loop){
        switch (cond){
            case 0:
                return organic >= param;
            case 1:
                return organic <= param;
            case 2:
                return salt >= param;
            case 3:
                return salt <= param;
            case 4:
                return countEmptyCells() >= param / 112;
            case 5:
                return countEmptyCells() <= param / 112;
            case 6:
                int tx = MathU.getTx(x, dir);
                int ty = MathU.getTy(y, dir);
                Bot other = World.bot_map[tx][ty];
                if (other == null) return false;
                return isRelative(other);
            case 7:
                tx = MathU.getTx(x, dir);
                ty = MathU.getTy(y, dir);
                other = World.bot_map[tx][ty];
                if (other == null) return false;
                return !isRelative(other);
            case 8:
                tx = MathU.getTx(x, dir);
                ty = MathU.getTy(y, dir);
                return World.light_map[tx][ty] <= param;
            case 9:
                tx = MathU.getTx(x, dir);
                ty = MathU.getTy(y, dir);
                return World.light_map[tx][ty] >= param;
            case 10:
                tx = MathU.getTx(x, dir);
                ty = MathU.getTy(y, dir);
                return World.organic_map[tx][ty] <= param;
            case 11:
                tx = MathU.getTx(x, dir);
                ty = MathU.getTy(y, dir);
                return World.organic_map[tx][ty] >= param;
            case 12:
                tx = MathU.getTx(x, dir);
                ty = MathU.getTy(y, dir);
                return World.salt_map[tx][ty] <= param;
            case 13:
                tx = MathU.getTx(x, dir);
                ty = MathU.getTy(y, dir);
                return World.salt_map[tx][ty] >= param;
            default:
                if (cond < 14 + Settings.MORPHOGENES_COUNT){
                    return World.morphogenes_map[x][y][cond - 14] <= param / 32;
                }
                if (cond < 14 + 2 * Settings.MORPHOGENES_COUNT){
                    return World.morphogenes_map[x][y][cond - 14 - Settings.MORPHOGENES_COUNT] >= param / 32;
                }
                if (cond < 14 + 2 * Settings.MORPHOGENES_COUNT + Settings.PROTEINS_COUNT) {
                    return proteins[(loop - 1) % Settings.SYNTESIS_LOOPS][cond - 14 - 2 * Settings.MORPHOGENES_COUNT] <= param;
                }
                if (cond < 14 + 2 * Settings.MORPHOGENES_COUNT + 2 * Settings.PROTEINS_COUNT) {
                    return proteins[(loop - 1) % Settings.SYNTESIS_LOOPS][cond - 14 - 2 * Settings.MORPHOGENES_COUNT - Settings.PROTEINS_COUNT] >= param;
                }
                return true;
        }
    }

    public void doAction(int action, int param){
        switch (action){
            case 0:
                dir = (dir + param) % 8;
                break;
            case 1:
                moveBot();
                break;
            case 2:
                attackBot();
                break;
            case 3:
                doubleBot();
                break;
            case 4:
                photosynthesisBot();
                break;
            case 5:
                chemosynthesisBot();
                break;
            case 6:
                distributeResources();
                break;
        }
    }

    public void killBot(){
        World.organic_map[x][y] = MathU.clamp(World.organic_map[x][y] + organic + 100, 0, 1000);
        World.salt_map[x][y] = MathU.clamp(World.salt_map[x][y] + salt, 0, 1000);
        World.bot_map[x][y] = null;
    }

    public void moveBot(){
        energy -= 10;
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
        energy -= 150; organic -= 100;
        if (doub_dir == 8) return;
        int tx = MathU.getTx(x, doub_dir);
        int ty = MathU.getTy(y, doub_dir);
        Bot new_bot = new Bot(tx, ty, doub_dir, energy / 2, organic / 2, salt / 2, genome, proteins[0]);
        World.bot_map[tx][ty] = new_bot;
        if (RandU.getRandint(0, 1001, tx, ty) <= World.mutation_probably) new_bot.genome.mutate(tx, ty);
        energy -= energy / 2;
        organic -= organic / 2;
        salt -= salt / 2;
    }

    public void attackBot(){
        energy -= 5;
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

    public void distributeResources(){
        int tx = MathU.getTx(x, dir);
        int ty = MathU.getTy(y, dir);
        Bot other = World.bot_map[tx][ty];
        if (other == null) return;
        int new_e = MathU.clamp((energy + other.energy) / 2, 0, 1000);
        int new_o = MathU.clamp((organic + other.organic) / 2, 0, 1000);
        int new_s = MathU.clamp((salt + other.salt) / 2, 0, 1000);
        energy = new_e; other.energy = new_e;
        organic = new_o; other.organic = new_o;
        salt = new_s; other.salt = new_s;
    }

    public int findEmptyDir(){
        for (int i = 0; i < 8; i++){
            int tx = MathU.getTx(x, (i * 3 + 5) % 8);
            int ty = MathU.getTy(y, (i * 3 + 5) % 8);
            if (World.bot_map[tx][ty] == null) return (i * 3 + 5) % 8;
        }
        return 8;
    }

    public int countEmptyCells(){
        int count = 0;
        for (int i = 0; i < 8; i++){
            int tx = MathU.getTx(x, i % 8);
            int ty = MathU.getTy(y, i % 8);
            if (World.bot_map[tx][ty] == null) count++;
        }
        return count;
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
