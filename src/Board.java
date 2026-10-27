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
    private static int tic; // Simulation tic
    public static int GetTic()
    {
        return tic;
    }
    private Timer timer;
    public static List<List<Entity>> entities;
    public static List<Animal> babyAnimals;
    public static Plant[][] plantGrid;

    public static boolean debug;
    public Board(int bWidth, int bHeight) {
        Board.bWidth = bWidth;
        Board.bHeight = bHeight;
        
        init();
    }

    //Initialise the board
    private void init() {
        addKeyListener(new KeyInput());
        setLayout(null);
        setBackground(Color.BLACK);
        setFocusable(true);
        setPreferredSize(new Dimension(bWidth + 100, bHeight + 100));

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
                
                15 * ((int)(Math.random()) + 6),
                (int)(Math.random() * 1) + 1,
                1500,
                1750,
                10,
                20,
                1
                ,1.2,
                20,
                1000
                

            );

            float speed = (int)(Math.random() * 2) + 1;
            float perception = 10 * ((int)(Math.random() * 2) + 10);
            Wolf newWolf = new Wolf(genes, new Animal[]{null, null}, (int) (Math.random()*  genes.GetLifespan() * 0.75));
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

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.darkGray);
        g.fillRect(bWidth, 0, getWidth(), getHeight());
        g.setColor(Color.gray);
        g.fillRect(0, 0, getWidth(), 100);
        drawEntities(g);
        drawGrid(g);
        drawCounts(g);
        drawKey(g);
    }

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

    private void drawGrid(Graphics g) {
        g.setColor(Color.white);
        for (int i = 40; i <= bWidth; i += 40) {
            g.drawLine(i, 100, i, bHeight + 100);
        }
        for (int i = 100; i < bHeight + 100; i += 40) {
            g.drawLine(0, i, bWidth, i);
        }
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
        g.fillRect(bWidth + (width - 40) / 2, yPos + metrics.getHeight(), 40, 40);

        yPos += height;
        xPos = getWidth() - (width + metrics.stringWidth("Wolf")) / 2;
        g.drawString("Wolf", xPos, yPos);
        g.setColor(Color.gray);
        g.fillRect(bWidth + (width - 40) / 2, yPos + metrics.getHeight(), 40, 40);

        g.setColor(Color.white);
        yPos += height;
        xPos = getWidth() - (width + metrics.stringWidth("Flower")) / 2;
        g.drawString("Flower", xPos, yPos);
        if (tic % 100 == 0) {
            flowerKeyColourR = (float)Math.random() * 0.75f + 0.25f;
            flowerKeyColourB = (float)Math.random() * 0.75f + 0.25f;
        }
        g.setColor(new Color(flowerKeyColourR, 0, flowerKeyColourB));
        g.fillOval(bWidth + (width - 40) / 2, yPos + metrics.getHeight(), 40, 40);
        

        g.setColor(Color.white);
        yPos += height;
        xPos = getWidth() - (width + metrics.stringWidth("Grass")) / 2;
        g.drawString("Grass", xPos, yPos);
        g.setColor(Color.green);
        g.fillOval(bWidth + (width - 40) / 2, yPos + metrics.getHeight(), 40, 40);
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
        repaint();
        tic++;
        long time = System.nanoTime();
        long delta = (time - lastTime) / 1000000;
        timePassed += delta;
        lastTime = time;

        CreateChildren();
        CleanUp(); 
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
        }
    }
}
