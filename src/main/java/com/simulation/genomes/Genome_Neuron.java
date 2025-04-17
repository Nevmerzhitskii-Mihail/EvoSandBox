package com.simulation.genomes;

import com.simulation.Bot;
import com.simulation.Genome;
import com.simulation.World;
import com.utils.MathU;
import com.utils.RandU;

import java.awt.*;

public class Genome_Neuron extends Genome {
    public static final int[] LAYERS_SIZES = new int[]{7, 7, 7, 8, 8, 8};

    float[][] layer01, layer12, layer23, layer34, layer45;

    public Genome_Neuron(){
        family = new Color(RandU.getRandint(0, 255), RandU.getRandint(0, 255), RandU.getRandint(0, 255));
        mind_type = new Color(0x8DDE56);
        repeat = 10;
        layer01 = new float[LAYERS_SIZES[0]][LAYERS_SIZES[1]];
        for (int i = 0; i < LAYERS_SIZES[0]; i++){
            for (int j = 0; j < LAYERS_SIZES[1]; j++){
                layer01[i][j] = RandU.getRandint(-300, 301) / 100f;
            }
        }
        layer12 = new float[LAYERS_SIZES[1]][LAYERS_SIZES[2]];
        for (int i = 0; i < LAYERS_SIZES[1]; i++){
            for (int j = 0; j < LAYERS_SIZES[2]; j++){
                layer12[i][j] = RandU.getRandint(-300, 301) / 100f;
            }
        }
        layer23 = new float[LAYERS_SIZES[2]][LAYERS_SIZES[3]];
        for (int i = 0; i < LAYERS_SIZES[2]; i++){
            for (int j = 0; j < LAYERS_SIZES[3]; j++){
                layer23[i][j] = RandU.getRandint(-300, 301) / 100f;
            }
        }
        layer34 = new float[LAYERS_SIZES[3]][LAYERS_SIZES[4]];
        for (int i = 0; i < LAYERS_SIZES[3]; i++){
            for (int j = 0; j < LAYERS_SIZES[4]; j++){
                layer34[i][j] = RandU.getRandint(-300, 301) / 100f;
            }
        }
        layer45 = new float[LAYERS_SIZES[4]][LAYERS_SIZES[5]];
        for (int i = 0; i < LAYERS_SIZES[4]; i++){
            for (int j = 0; j < LAYERS_SIZES[5]; j++){
                layer45[i][j] = RandU.getRandint(-300, 301) / 100f;
            }
        }
    }

    private Genome_Neuron(Genome_Neuron parent){
        family = new Color(parent.family.getRGB());
        mind_type = new Color(0x8DDE56);
        repeat = 10;
        layer01 = new float[LAYERS_SIZES[0]][LAYERS_SIZES[1]];
        for (int i = 0; i < LAYERS_SIZES[0]; i++) System.arraycopy(parent.layer01[i], 0, layer01[i], 0, LAYERS_SIZES[1]);
        layer12 = new float[LAYERS_SIZES[1]][LAYERS_SIZES[2]];
        for (int i = 0; i < LAYERS_SIZES[1]; i++) System.arraycopy(parent.layer12[i], 0, layer12[i], 0, LAYERS_SIZES[2]);
        layer23 = new float[LAYERS_SIZES[2]][LAYERS_SIZES[3]];
        for (int i = 0; i < LAYERS_SIZES[2]; i++) System.arraycopy(parent.layer23[i], 0, layer23[i], 0, LAYERS_SIZES[3]);
        layer34 = new float[LAYERS_SIZES[3]][LAYERS_SIZES[4]];
        for (int i = 0; i < LAYERS_SIZES[3]; i++) System.arraycopy(parent.layer34[i], 0, layer34[i], 0, LAYERS_SIZES[4]);
        layer45 = new float[LAYERS_SIZES[4]][LAYERS_SIZES[5]];
        for (int i = 0; i < LAYERS_SIZES[4]; i++) System.arraycopy(parent.layer45[i], 0, layer45[i], 0, LAYERS_SIZES[5]);
    }

    @Override
    public Genome copy() {
        return new Genome_Neuron(this);
    }

    @Override
    public void mutate(int x, int y) {
        for (int i = 0; i < 5; i++) mutate_layer(x, y, RandU.getRandint(0, 5));
        mutate_color(x, y);
    }

    private void mutate_layer(int x, int y, int l){
        switch (l){
            case 0:
                layer01[RandU.getRandint(0, LAYERS_SIZES[0], x, y)][RandU.getRandint(0, LAYERS_SIZES[1], x, y)] = RandU.getRandint(-300, 301) / 100f;
                break;
            case 1:
                layer12[RandU.getRandint(0, LAYERS_SIZES[1], x, y)][RandU.getRandint(0, LAYERS_SIZES[2], x, y)] = RandU.getRandint(-300, 301) / 100f;
                break;
            case 2:
                layer23[RandU.getRandint(0, LAYERS_SIZES[2], x, y)][RandU.getRandint(0, LAYERS_SIZES[3], x, y)] = RandU.getRandint(-300, 301) / 100f;
                break;
            case 3:
                layer34[RandU.getRandint(0, LAYERS_SIZES[3], x, y)][RandU.getRandint(0, LAYERS_SIZES[4], x, y)] = RandU.getRandint(-300, 301) / 100f;
                break;
            case 4:
                layer45[RandU.getRandint(0, LAYERS_SIZES[4], x, y)][RandU.getRandint(0, LAYERS_SIZES[5], x, y)] = RandU.getRandint(-300, 301) / 100f;
                break;
        }
    }

    @Override
    public int get_action(Bot bot) {
        float[] input = new float[]{
                relative(bot),
                bot.energy / 1000f,
                bot.organic / 1000f,
                bot.salt / 1000f,
                World.light_map[bot.x][bot.y] / 1000f,
                World.organic_map[bot.x][bot.y] / 1000f,
                World.salt_map[bot.x][bot.y] / 1000f
        };
        float[] l1 = forward(input, layer01);
        float[] l2 = forward(l1, layer12);
        float[] l3 = forward(l2, layer23);
        float[] l4 = forward(l3, layer34);
        float[] output = forward(l4, layer45);

        int action = -1;
        float value = -1f;
        for (int i = 0; i < 8; i++){
            if (output[i] >= value) {
                value = output[i];
                action = i;
            }
        }
        return action;
    }

    private float[] forward(float[] l0, float[][] weights){
        float[] l1 = new float[weights[0].length];
        for (int i = 0; i < weights[0].length; i++){
            for (int j = 0; j < weights.length; j++){
                l1[i] += l0[j] * weights[j][i];
            }
            l1[i] = Math.max(0f, Math.min(1f, l1[i]));
        }
        return l1;
    }

    private float relative(Bot bot){
        int tx = MathU.getTx(bot.x, bot.dir);
        int ty = MathU.getTy(bot.y, bot.dir);
        Bot other = World.bot_map[tx][ty];
        if (other == null) return 1f;
        if (bot.isRelative(other)) return 1f;
        return 0f;
    }
}
