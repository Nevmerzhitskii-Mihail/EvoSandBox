package com.visual;

import com.Main;
import com.Settings;
import com.simulation.Bot;
import com.simulation.World;
import com.utils.MathU;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Drawer {
    public static JFrame mainWindow;

    public static JPanel canvas = new JPanel(){
        @Override
        public void paint(Graphics g) {
            ((Graphics2D) g).drawImage(canvas_image, null, 0, 0);
        }
    };
    public static BufferedImage canvas_image;

    public static JPanel info = new JPanel();
    public static VarLabel step = new VarLabel("Step");

    public static JToolBar settings = new JToolBar(SwingConstants.VERTICAL);
    public static JButton generate = new JButton("Generate");
    public static JTextField seed_field = new JTextField();
    public static JButton stop = new JButton("Start");
    public static JComboBox drawing = new JComboBox(new String[]{"Family", "Predators", "Energy", "Organic", "Salt", "Age"});
    public static JComboBox background = new JComboBox(new String[]{"Light", "Organic", "Salt", "Morphogenes"});

    static Color[] morphogenes_colors = new Color[]{
            new Color(0xFF0000),
            new Color(0x38FF0E),
            new Color(0x0045FF),
            new Color(0xFFB100),
            new Color(0x00FF9B),
            new Color(0x00FFFF),
            new Color(0x8400FF),
            new Color(0xE600FF),
            new Color(0xFF0087),
            new Color(0xFF8080),
            new Color(0xCFFF8B),
            new Color(0x9EB8FF)
    };

    public static void init(){

        canvas_image = new BufferedImage(Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);

        mainWindow = new JFrame();

        mainWindow.add(canvas, BorderLayout.CENTER);

        info.add(step);
        mainWindow.add(info, BorderLayout.SOUTH);

        settings.add(generate);
        generate.addActionListener(e -> {
            long seed;
            String str = seed_field.getText();
            if (str.equals("")) seed = System.currentTimeMillis();
            else seed = Long.parseLong(str);
            Main.regenerate(seed);
            seed_field.setText(String.valueOf(seed));
        });

        settings.add(seed_field);

        settings.add(stop);
        stop.addActionListener(e -> {
            Main.is_started = !Main.is_started;
            generate.setEnabled(!Main.is_started);
            seed_field.setEnabled(!Main.is_started);
        });

        settings.add(new JLabel("Drawing Mode"));
        settings.add(drawing);
        drawing.addActionListener(e -> {
            Main.drawing_mode = drawing.getSelectedIndex();
        });
        settings.add(new JLabel("Background Mode"));
        settings.add(background);
        background.addActionListener(e -> {
            Main.background_mode = background.getSelectedIndex();
        });

        for (int i = 0; i < 50; i++) settings.add(new JLabel(" "));
        settings.setEnabled(false);
        mainWindow.add(settings, BorderLayout.EAST);

        mainWindow.pack();
        mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainWindow.setResizable(false);
        mainWindow.setSize(Settings.SCREEN_WIDTH + 200, Settings.SCREEN_HEIGHT + 100);
        mainWindow.setVisible(true);
    }

    public static void redraw(){
        BufferedImage buffer = new BufferedImage(Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = buffer.createGraphics();
        drawBackground(g);
        for (int x = 0; x < Settings.WIDTH; x++) {
            for (int y = 0; y < Settings.HEIGHT; y++) {
                Bot b = World.bot_map[x][y];
                if (b != null) drawBot(g, x, y, b);
            }
        }
        canvas_image.createGraphics().drawImage(buffer, null, 0, 0);
        step.update(String.valueOf(World.current_step));

        canvas.repaint();
    }

    public static void drawBackground(Graphics2D g){
        for (int x = 0; x < Settings.WIDTH; x++)
            for (int y = 0; y < Settings.HEIGHT; y++){
                switch (Main.background_mode){
                    case 0:
                        g.setColor(MathU.lerp(World.light_map[x][y], new Color(0x0F2841), new Color(0xF88E3F)));
                        break;
                    case 1:
                        g.setColor(MathU.lerp(World.organic_map[x][y], new Color(0x5D8073), new Color(0xB620A9)));
                        break;
                    case 2:
                        g.setColor(MathU.lerp(World.salt_map[x][y], new Color(0x50503D), new Color(0x2E97BD)));
                        break;
                    case 3:
                        Color[] colors = new Color[Settings.MORPHOGENES_COUNT];
                        for (int i = 0; i < Settings.MORPHOGENES_COUNT; i++){
                            colors[i] = MathU.lerp(World.morphogenes_map[x][y][i] * 31, Color.BLACK, morphogenes_colors[i]);
                        }
                        g.setColor(MathU.avr(colors));
                        break;
                }
                g.fillRect(x * Settings.TILE, y * Settings.TILE, Settings.TILE, Settings.TILE);
            }
    }

    public static void drawBot(Graphics2D g, int x, int y, Bot bot){
        if (Main.background_mode == 3) return;
        switch (Main.drawing_mode){
            case 0:
                g.setColor(bot.genome.family);
                break;
            case 1:
                g.setColor(new Color(bot.redC, bot.greenC, bot.blueC));
                break;
            case 2:
                g.setColor(MathU.lerp(bot.energy, new Color(0x540606), new Color(0xDCB122)));
                break;
            case 3:
                g.setColor(MathU.lerp(bot.organic, new Color(0x6E5656), new Color(0xA92CCB)));
                break;
            case 4:
                g.setColor(MathU.lerp(bot.salt, new Color(0x1C5415), new Color(0x2DD8E1)));
                break;
            case 5:
                g.setColor(MathU.lerp(bot.age, new Color(0x24250F), new Color(0x7337D5)));
        }
        g.fillRect(x * Settings.TILE, y * Settings.TILE, Settings.TILE, Settings.TILE);
    }
}
