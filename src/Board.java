import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.*;

public class Board extends JPanel implements ActionListener {
    public static int bWidth; // Board width
    public static int bHeight; // Board height
    public static int tic; // Simulation tic
    public static int GetTic()
    {
        return tic;
    }
    private Timer timer;
    public static List<List<Entity>> entities;
    public static List<Animal> babyAnimals;
    public static Plant[][] plantGrid;

    private List<Stat> stats;

    public static boolean debug;

    public StatScreen statScreen;
    public JFrame statFrame;

    private Image sheepSprite;
    private Image wolfSprite;
    private Image grassSprite;
    private Image flowerSprite;

    public Board(int bWidth, int bHeight) {
        Board.bWidth = bWidth;
        Board.bHeight = bHeight;

        init();
    }

    //Initialise the board
    private void init() {
        addKeyListener(new KeyInput());
        setLayout(null);
        Color darkGreen = new Color(0, 210, 0);
        setBackground(darkGreen);
        setFocusable(true);
        setPreferredSize(new Dimension(bWidth + 100, bHeight + 100));

        statScreen = new StatScreen(bWidth + 100, bHeight + 100);
        statFrame = new JFrame();
        statFrame.add(statScreen);
        statFrame.setTitle("Statistics");
        statFrame.setResizable(false);
        statFrame.pack();
        statFrame.setLocationRelativeTo(null);

        statFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                showStats = false;
                statFrame.setVisible(false);
            }
        });

        showStats = false;
        statFrame.setVisible(false);
        stats = new ArrayList<>();

        sheepSprite = Sheep.getSheepImage();
        wolfSprite = Wolf.getWolfImage();
        grassSprite = Grass.getGrassImage();
        flowerSprite = Flower.getFlowerImage(new Color(flowerKeyColourR, 0, flowerKeyColourB));

        resetSimulation();

    }

    private void resetSimulation() {

        babyAnimals = new ArrayList<>();
        entities = new ArrayList<>();
        entities.add(new ArrayList<>());
        entities.add(new ArrayList<>());
        entities.add(new ArrayList<>());
        entities.add(new ArrayList<>());
        spawnEntities();
        entities.stream()
                  .forEach(list -> list.stream()
                                      .map(ent -> ent.pos = Position.genRand(bWidth, bHeight, 20, 120, 40))
                                      .collect(Collectors.toList())
                          );

        plantGrid = new Plant[12][16];
        for(Entity e : entities.get(Ent.flower.get())) {
            plantGrid[(e.pos.getY() - 100) / 40][e.pos.getX() / 40] = (Flower)e;
        }

        for(Entity e : entities.get(Ent.grass.get())) {
            plantGrid[(e.pos.getY() - 100) / 40][e.pos.getX() / 40] = (Grass)e;
        }

        tic = 1;

        stats.clear();
        statScreen.ticOffset = 0;
        addNewStat();

        timePassed = 0;
        nextSpawn = 300;
        timer = new Timer(10, this);
        timer.start();
    }

    /*
        Entity start amounts:

        Sheep: 5-8
        Wolves: 2-3
        Grass: 8-12
        Flowers: 0-2
    */
    private void spawnEntities() {
        //Spawn sheep
        int startAmount = (int)(Math.random() * 5 + 7);

        for (int i = 0; i < startAmount; i++) {

            Genes genes = new Genes(
                20 * (int)(Math.random()  + 8),
                (int)(Math.random()) + 1,

                2500,
                3000,
                2,
                20,
                1,
                2,
                20,
                100


            );
            /*
            float speed = (int)(Math.random()) + 1;
            float perception = 10 * ((int)(Math.random() * 4) + 6);
            */
            Sheep newSheep = new Sheep(genes, new Animal[]{null, null}, (int) (Math.random()*  genes.GetLifespan()* 0.75));
            entities.get(Ent.sheep.get()).add(newSheep);
        }

        //Spawn wolves
        startAmount = (int)(Math.random() * 3 + 2);
        for (int i = 0; i < startAmount; i++) {
            Genes genes = new Genes(
//
//                 15 * ((int)(Math.random()) + 6),
//                 (int)(Math.random() * 1) + 1,
//                 (int)(Math.random() * 1000) + 750,
//                 (int)(Math.random() * 1500) + 750,
//                 (int)(Math.random() * 15) + 5,
//                 (int)(Math.random() * 10) + 10,
//                 1,
//                 1.2,
//                 20,
//                 1000

                90,
                2,
                (int)(Math.random() * 1000) + 2500, // lifespan
                (int)(Math.random() * 1500) + 750,
                (int)(Math.random() * 15) + 5,
                (int)(Math.random() * 10) + 10,
                1,
                2.0,
                20,
                1000

            );


            Wolf newWolf = new Wolf(genes, new Animal[]{null, null}, (int) (Math.random()*  genes.GetLifespan() * 0.20)); // changed from 0.75
            entities.get(Ent.wolf.get()).add(newWolf);
        }

        //Spawn Grass
        startAmount = (int)(Math.random() * 6 + 12);
        for (int i = 0; i < startAmount; i++) {
            entities.get(Ent.grass.get()).add(new Grass());
        }

        //Spawn Flower(s)
        startAmount = (int)(Math.random() * 2);
        for (int i = 0; i < startAmount; i++) {
            entities.get(Ent.flower.get()).add(new Flower());
        }
    }
    public boolean showStats;
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.darkGray);
        g.fillRect(bWidth, 0, getWidth(), getHeight());
        g.setColor(Color.gray);
        g.fillRect(0, 0, getWidth(), 100);
        drawGrid(g);
        drawEntities(g);
        drawCounts(g);
        drawKey(g);
        if (showStats) {
            drawStats(g);
        }
    }

    /*
    private void drawEntities(Graphics g) {
        entities.stream()
                .forEach(list -> {
                    list.stream()
                        .peek(ent -> ent.drawEntity(g))
                        .filter(ent -> ent instanceof Animal)
                        .map(ent -> (Animal)ent)
                        .forEach(animal -> animal.AnimalBehaviour());
                });
    }
    */

    // this pretty much prevents additional repaints from affecting the simulation (for rendering only)
    private void drawEntities(Graphics g) {
        entities.stream()
            .flatMap(List::stream)
            .filter(Entity::IsAlive)
            .forEach(entity -> entity.drawEntity(g));

    }

    private void drawGrid(Graphics g) {
        Color darkerGreen = new Color(0, 170, 0);
        g.setColor(darkerGreen);
        for (int i = 40; i <= bWidth; i += 40) {
            g.drawLine(i, 100, i, bHeight + 100);
        }
        for (int i = 100; i < bHeight + 100; i += 40) {
            g.drawLine(0, i, bWidth, i);
        }
    }

    // updates living animal once per simulation tick (streams used to process the nested entity collectionz)
    private void updateAnimals() {
        entities.stream()
            .flatMap(List::stream)
            .filter(entity -> entity instanceof Animal)
            .map(entity -> (Animal) entity)
            .filter(Animal::IsAlive)
            .forEach(Animal::AnimalBehaviour);
    }

    Font medium = new Font("Helvetica", Font.PLAIN, 16);
    private long timePassed;
    private long lastTime = System.nanoTime();
    private void drawCounts(Graphics g) {
        g.setColor(Color.white);
        g.setFont(medium);
        FontMetrics metrics = g.getFontMetrics();
        int yPos =  ((100 - metrics.getHeight()) / 2) + metrics.getAscent() / 4;

        int nextX = bWidth / 16;
        g.drawString("Sheep: " + entities.get(Ent.sheep.get()).size(), nextX, yPos);

        long seconds = timePassed / 1000;
        String timeString = seconds / 60 + ":";
        timeString += (seconds % 60 > 10) ? seconds % 60 : "0" + seconds % 60;
        timeString += ".";
        for (int i = 0; i < 2 - (int)Math.log10(timePassed % 1000); i++) {
            timeString += "0";
        }
        timeString += timePassed % 1000;
        g.drawString("Elapsed Time: " + timeString, nextX, yPos * 5 / 3);

        nextX += bWidth / 4;
        g.drawString("Wolves: " + entities.get(Ent.wolf.get()).size(), nextX, yPos);

        nextX += bWidth / 4;
        g.drawString("Flowers: " + entities.get(Ent.flower.get()).size(), nextX, yPos);
        g.drawString("Tic: " + tic, nextX, yPos * 5 / 3);

        nextX += bWidth / 4;
        g.drawString("Grass: " + entities.get(Ent.grass.get()).size(), nextX, yPos);


    }

    float flowerKeyColourR = (float)Math.random() * 0.75f + 0.25f;
    float flowerKeyColourB = (float)Math.random() * 0.75f + 0.25f;
    private void drawKey(Graphics g) {
        FontMetrics metrics = g.getFontMetrics();
        int height = getHeight() - bHeight;
        int width = getWidth() - bWidth;
        int yPos =  height + metrics.getHeight() + metrics.getAscent() / 4;
        int xPos = getWidth() - (width + metrics.stringWidth("Key")) / 2;
        g.drawString("Key", xPos, yPos);

        yPos += metrics.getHeight() * 2;
        xPos = getWidth() - (width + metrics.stringWidth("Sheep")) / 2;
        g.drawString("Sheep", xPos, yPos);
        // g.fillRect(bWidth + (width - 40) / 2, yPos + metrics.getHeight(), 40, 40);
        g.drawImage(sheepSprite, bWidth + (width - 60) / 2, yPos + metrics.getHeight(), 60, 60, null);

        yPos += height;
        xPos = getWidth() - (width + metrics.stringWidth("Wolf")) / 2;
        g.drawString("Wolf", xPos, yPos);
        // g.setColor(Color.gray);
        // g.fillRect(bWidth + (width - 40) / 2, yPos + metrics.getHeight(), 40, 40);
        g.drawImage(wolfSprite, bWidth + (width - 60) / 2, yPos + metrics.getHeight(), 60, 60, null);

        g.setColor(Color.white);
        yPos += height;
        xPos = getWidth() - (width + metrics.stringWidth("Flower")) / 2;
        g.drawString("Flower", xPos, yPos);
        if (tic % 500 == 0) {
            flowerKeyColourR = (float)Math.random() * 0.75f + 0.25f;
            flowerKeyColourB = (float)Math.random() * 0.75f + 0.25f;
            Color flowerKeyColour = new Color(flowerKeyColourR, 0, flowerKeyColourB);
            flowerSprite = Flower.getFlowerImage(flowerKeyColour);
        }
        // g.setColor(new Color(flowerKeyColourR, 0, flowerKeyColourB));
        // g.fillOval(bWidth + (width - 40) / 2, yPos + metrics.getHeight(), 40, 40);
        g.drawImage(flowerSprite, bWidth + (width - 60) / 2, yPos + metrics.getHeight(), 60, 60, null);


        g.setColor(Color.white);
        yPos += height;
        xPos = getWidth() - (width + metrics.stringWidth("Grass")) / 2;
        g.drawString("Grass", xPos, yPos);
        // g.setColor(Color.green);
        // g.fillOval(bWidth + (width - 40) / 2, yPos + metrics.getHeight(), 40, 40);
        g.drawImage(grassSprite, bWidth + (width - 60) / 2, yPos + metrics.getHeight(), 60, 60, null);
    }

    private void drawStats(Graphics g) {
        statScreen.drawStats(stats, flowerKeyColourR, flowerKeyColourB);
    }

    private int nextSpawn = 50;
    @Override
    // After the timer finishes do this
    public void actionPerformed(ActionEvent e) {
        if (tic >= nextSpawn) {
            spreadGrass();
            if (entities.get(Ent.grass.get()).size() <= 2) {
                Grass newGrass = new Grass();
                do {
                    newGrass.pos = Position.genRand(bWidth, bHeight, 20, 120, 40);
                } while (plantGrid[(newGrass.pos.getY() - 100) / 40][newGrass.pos.getX() / 40] != null);
                entities.get(Ent.grass.get()).add(newGrass);
                plantGrid[(newGrass.pos.getY() - 100) / 40][newGrass.pos.getX() / 40] = newGrass;
            }
        }


        debugHover();

        updateAnimals();

        CreateChildren();
        CleanUp();

        tic++;

        // records population changes after the entities are updated
        if(tic % 100 == 0) {
            addNewStat();
        }

        long time = System.nanoTime();
        long delta = (time - lastTime) / 1000000;
        timePassed += delta;
        lastTime = time;

        repaint();
    }

    private void addNewStat() {
        int numSheep = entities.get(Ent.sheep.get()).size();
        int numWolves = entities.get(Ent.wolf.get()).size();
        int numGrass = entities.get(Ent.grass.get()).size();
        int numFlowers = entities.get(Ent.flower.get()).size();
        stats.add(new Stat(numSheep, numWolves, numGrass, numFlowers, tic));
        if (stats.size() > 246) {
            stats.remove(0);
            statScreen.ticOffset++;
        }
    }


    private void debugHover() {
    Point mousePoint = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(mousePoint, this);
        Position mousePos = new Position(mousePoint.x, mousePoint.y);
        //System.out.println(mousePos);
        entities.stream().forEach(list -> list.stream()
                                                .filter(ent -> ent instanceof Animal)
                                                .forEach(ent -> {
                                                    if (ent.pos.dist(mousePos) < 40) {
                                                        ((Animal)ent).debug = true;
                                                    }
                                                    else {
                                                        ((Animal)ent).debug = false;
                                                    }
                                                }));
    }
    // private void printPlantGrid() {
    //     for (Plant[] arr : plantGrid) {
    //         for (Plant p : arr) {
    //             if (p == null) {
    //                 System.out.print("- ");
    //             }
    //             else if (p instanceof Grass) {
    //                 System.out.print("g ");
    //             }
    //             else if (p instanceof Flower) {
    //                 System.out.print("f ");
    //             }
    //         }
    //         System.out.println();
    //     }
    // }

    private void spreadGrass() {
        Collections.shuffle(entities.get(Ent.grass.get()));
        List<Entity> newGrass = new ArrayList<>();
        List<Entity> newFlower = new ArrayList<>();
        int maxSpawn = Math.max(entities.get(Ent.sheep.get()).size(), 3);
        newGrass = entities.get(Ent.grass.get()).stream()
                                                .map(en -> ((Grass)en).spreadGrass())
                                                .filter(en -> en != null)
                                                .limit(maxSpawn)
                                                .collect(Collectors.toList());

        newFlower = newGrass.stream()
                            .filter(en -> en instanceof Flower)
                            .collect(Collectors.toList());

        newGrass = newGrass.stream()
                            .filter(en -> en instanceof Grass)
                            .collect(Collectors.toList());

        entities.get(Ent.grass.get()).addAll(newGrass);
        entities.get(Ent.flower.get()).addAll(newFlower);
        nextSpawn = tic + (int)(Math.random() * 200 + 75);
    }

    private void CreateChildren()
    {

        babyAnimals.stream()
                    .forEach(baby -> entities.get(baby.GetType().get()).add(baby));

        babyAnimals.clear();
    }

    private void CleanUp()
    {

        for(List<Entity> subList : entities)
        {
            for(int i = subList.size() -1; i >= 0; i--)
            {
                if(!subList.get(i).IsAlive())
                {
                    if (subList.get(i) instanceof Plant) {
                        plantGrid[(subList.get(i).pos.getY() - 100) / 40][subList.get(i).pos.getX() / 40] = null;
                    }
                    subList.remove(i);
                }
            }
        }
    }

    public enum Ent {
        sheep(2), wolf(3), flower(1), grass(0);

        private final int index;

        private Ent(int index) {
            this.index = index;
        }

        public int get() {
            return index;
        }
    }

    private class KeyInput extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            if (keyCode == KeyEvent.VK_R) {
                entities.clear();
                timer.stop();
                resetSimulation();
            }

            if(keyCode == KeyEvent.VK_Q)
            {
                debug = !debug;
            }

            if (keyCode == KeyEvent.VK_S) {
                showStats = !showStats;
                statFrame.setVisible(showStats);
            }
        }
    }
}
