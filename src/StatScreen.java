import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.util.List;
public class StatScreen extends JPanel{
    public int width;
    public int height;
    public JPanel statPanel;
    private List<Stat> stats;

    public StatScreen(int width, int height) {
        this.width = width;
        this.height = height;
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

    public void drawStats(List<Stat> stats) {
        this.stats = stats;
        repaint();
    }

    float flowerKeyColourR = (float)Math.random() * 0.75f + 0.25f;
    float flowerKeyColourB = (float)Math.random() * 0.75f + 0.25f;
    @Override 
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Stat stat : stats) {
            System.out.println(stat);
            int xPos = scaleXPos(stat.tic / 100);
            g.setColor(Color.white);
            int yPos = getHeight() - stat.numSheep;
            g.fillOval(xPos, yPos, 5, 5);

            g.setColor(Color.gray);
            yPos = getHeight() - stat.numWolves;
            g.fillOval(xPos, yPos, 5, 5);


            if (Board.tic % 100 == 0) {
                flowerKeyColourR = (float)Math.random() * 0.75f + 0.25f;
                flowerKeyColourB = (float)Math.random() * 0.75f + 0.25f;
            }
            g.setColor(new Color(flowerKeyColourR, 0, flowerKeyColourB));
            yPos = getHeight() - stat.numFlowers;
            g.fillOval(xPos, yPos, 5, 5);

            g.setColor(Color.green);
            yPos = getHeight() - stat.numGrass;
            g.fillOval(xPos, yPos, 5, 5);
        }
    }

    private int scaleXPos(int xPos) {
        int ratio = getWidth() / stats.size();
        return xPos * ratio;
    }
}
