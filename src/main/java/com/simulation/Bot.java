package com.simulation;

import com.utils.MathU;
import com.utils.RandU;

public class Bot {
    public int x, y;
    public int dir;
    public int energy;
    public int organic;
    public int salt;
    public int age;

    public Genome genome;
    public int redC = 128, greenC = 128, blueC = 128;

    public Bot(int x, int y, int dir, int energy, int organic, int salt, Genome parent){
        this.x = x; this.y = y; this.dir = dir;
        this.energy = energy; this.organic = organic; this.salt = salt;
        genome = parent.copy();
        age = 0;
    }

    public void step(){
        collectOrganic();
        collectSalt();
        for (int i = 0; i < genome.repeat; i++){
            int action = genome.get_action(this);
            if (doAction(action)) break;
        }
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

    public boolean doAction(int action){
        switch (action){
            case 0:
                dir = (dir + 1) % 8;
                return false;
            case 1:
                moveBot();
                return true;
            case 2:
                doubleBot();
                return true;
            case 3:
                attackBot();
                return true;
            case 4:
                photosynthesisBot();
                return true;
            case 5:
                chemosynthesisBot();
                return true;
            case 6:
                distributeResources();
                return true;
            case 7:
                produceEnergyBot();
        }
        return false;
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
        Bot new_bot = new Bot(tx, ty, doub_dir, energy / 2, organic / 2, salt / 2, genome);
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
        return Math.abs(dr) + Math.abs(dg) + Math.abs(db) < 30;
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
