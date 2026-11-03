import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.util.List;
public class StatScreen extends JPanel{
    public int width;
    public int height;
    public JPanel statPanel;
    private List<Stat> stats;
    public int ticOffset;

    public StatScreen(int width, int height) {
        this.width = width;
        this.height = height;
        ticOffset = 0;
        System.out.println(width + " " + height);
        init();
    }

    public void init() {
        setLayout(null);
        setBackground(Color.BLACK);
        repaint();
        setFocusable(true);
        setPreferredSize(new Dimension(width, height));
    }

    public void drawStats(List<Stat> stats, float flowerColourR, float flowerColourB) {
        this.stats = stats;
        this.flowerColourR = flowerColourR;
        this.flowerColourB = flowerColourB;
        repaint();
    }

    private float flowerColourR;
    private float flowerColourB;
    @Override 
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (stats == null) {
            return;
        }
        for (Stat stat : stats) {
            int xPos = scaleXPos(stat.tic / 100 - ticOffset);
            g.setColor(Color.white);
            int yPos = getHeight() - stat.numSheep;
            g.fillOval(xPos, yPos - 4, 4, 4);

            g.setColor(Color.gray);
            yPos = getHeight() - stat.numWolves;
            g.fillOval(xPos, yPos - 4, 4, 4);

            g.setColor(new Color(flowerColourR, 0, flowerColourB));
            yPos = getHeight() - stat.numFlowers;
            g.fillOval(xPos, yPos - 4, 4, 4);

            g.setColor(Color.green);
            yPos = getHeight() - stat.numGrass;
            g.fillOval(xPos, yPos - 4, 4, 4);
        }

        for (int i = 0; i < stats.size() - 1; i++) {
            Stat stat1 = stats.get(i);
            Stat stat2 = stats.get(i + 1);

            int xPos1 = scaleXPos(stat1.tic / 100 - ticOffset) + 2;
            int xPos2 = scaleXPos(stat2.tic / 100 - ticOffset) + 2;

            g.setColor(Color.white);
            int yPos1 = getHeight() - stat1.numSheep - 2;
            int yPos2 = getHeight() - stat2.numSheep - 2;
            g.drawLine(xPos1, yPos1, xPos2, yPos2);

            g.setColor(Color.gray);
            yPos1 = getHeight() - stat1.numWolves - 2;
            yPos2 = getHeight() - stat2.numWolves - 2;
            g.drawLine(xPos1, yPos1, xPos2, yPos2);

            g.setColor(new Color(flowerColourR, 0, flowerColourB));
            yPos1 = getHeight() - stat1.numFlowers - 2;
            yPos2 = getHeight() - stat2.numFlowers - 2;
            g.drawLine(xPos1, yPos1, xPos2, yPos2);

            g.setColor(Color.green);
            yPos1 = getHeight() - stat1.numGrass - 2;
            yPos2 = getHeight() - stat2.numGrass - 2;
            g.drawLine(xPos1, yPos1, xPos2, yPos2);

            
        }
    }

    private int scaleXPos(int xPos) {
        int ratio = getWidth() / stats.size();
        return xPos * ratio;
    }
}
