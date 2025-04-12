package com.simulation;

import com.simulation.multiprocessing.Solver;
import com.utils.MathU;
import com.utils.RandU;

public class World{

    public static final int THREADS_COUNT = 7;

    public static Solver main_solver;
    public static Solver[] computation_solvers = new Solver[THREADS_COUNT];

    public static int width, height;
    public static Bot[][] bot_map;
    public static int[][] salt_map;
    public static int[][] organic_map;
    public static int[][] light_map;

    public static int current_step = 0;

    public static int mutation_probably = 250;
    public static int organic_viscosity = 10;

    public static void init(int width, int height){
        World.width = width;
        World.height = height;
        bot_map = new Bot[width][height];
        salt_map = new int[width][height];
        organic_map = new int[width][height];
        light_map = new int[width][height];

        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                organic_map[x][y] = 0;
                salt_map[x][y] = 0;
            }
        }

        for (int i = 0; i < 4000; i++){
            int x = RandU.getRandint(0, width);
            int y = RandU.getRandint(0, height);
            bot_map[x][y] = new Bot(x, y, RandU.getRandint(0, 8), 300, 300, 0, new Genome(30));
        }
    }

    public static void start(){
        main_solver = new Solver(() -> {
            World.update_maps();
            for (int i = 0; i < THREADS_COUNT; i++) computation_solvers[i].trigger();
        }, false);
        for (int i = 0; i < THREADS_COUNT; i++) {
            int finalI = i;
            computation_solvers[i] = new Solver(() -> World.step(finalI), true);
            computation_solvers[i].start();
        }
        main_solver.start();
    }

    public static void update_maps(){
        update_light();
        //update_organic();
        update_salt();
        current_step++;
    }

    public static void step(int thread){
        long min_id = (long) width * height / THREADS_COUNT * thread;
        long max_id = thread == THREADS_COUNT - 1 ? (long) width * height : (long) width * height / THREADS_COUNT * (thread + 1);
        for (long id = min_id; id < max_id; id++){
            long new_id = (id + current_step * ((long) width * height - 2)) % ((long) width * height);
            int x = MathU.getXFromId(new_id), y = MathU.getYFromID(new_id);
            if (bot_map[x][y] != null) bot_map[x][y].step();
        }
    }

    public static void update_light(){
        for (int x = 0; x < width; x++) light_map[x][0] = 1000;
        for (int y = 1; y < height; y++){
            for (int x = 0; x < width; x++){
                int c = 0;
                for (int d = 0; d < 3; d++){
                    int tx = MathU.getTx(x, d);
                    int ty = MathU.getTy(y, d);
                    int v = light_map[tx][ty];
                    if (bot_map[tx][ty] != null) v = v * 20 / 21;
                    c += v;
                }
                light_map[x][y] = c / 3;
            }
        }
    }

    public static void update_organic(){
        int[][] tmp = new int[width][height];
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                int c = 0, val = 0;
                for (int dx = -1; dx < 2; dx++) for (int dy = -1; dy < 2; dy++){
                    if (y + dy < 0 || y + dy >= height || (dx == 0 && dy == 0)) continue;
                    int tx = (x + dx + width) % width, ty = y + dy;
                    val += organic_map[tx][ty];
                    c++;
                }
                tmp[x][y] = (int)((organic_map[x][y] + ((float) organic_viscosity / 1000f) * val) / (1 + c * ((float)organic_viscosity / 1000f)));
            }
        }
        for (int x = 0; x < width; x++) System.arraycopy(tmp[x], 0, organic_map[x], 0, height);
    }

    public static void update_salt(){
        for (int x = 0; x < width; x++) salt_map[x][height - 1] = 1000;
        for (int y = height - 2; y >= 0; y--){
            for (int x = 0; x < width; x++){
                int c = 0;
                for (int d = 4; d < 7; d++){
                    int tx = MathU.getTx(x, d);
                    int ty = MathU.getTy(y, d);
                    int v = salt_map[tx][ty] * 100 / 101;
                    if (bot_map[tx][ty] != null) v = v * 100 / 101;
                    c += v;
                }
                salt_map[x][y] = Math.max(0, c / 3);
            }
        }
    }
}
