
import java.awt.Color;

public class Grass extends Plant{
    public Grass()
    {
        super("Grass", Color.green);
        this.nutrition = 0.3f;
    }

    public Plant spreadGrass() {
        int x = (pos.getX() - 20) / 40;
        int y = (pos.getY() - 120) / 40;
        int randX = (int)(Math.random() * 3) - 1;
        int randY = (int)(Math.random() * 3) - 1;
        int newX = 0;
        int newY = 0;

        newX = (x + randX + Board.plantGrid[0].length) % Board.plantGrid[0].length;
        newY = (y + randY + Board.plantGrid.length) % Board.plantGrid.length;
        
        if (Board.plantGrid[newY][newX] != null) {
            return null;
        }
        int rand = (int)(Math.random() * 5);
        if (rand == 1) {
            Flower newF = new Flower();
            newF.pos = new Position(newX * 40 + 20, newY * 40 + 120);
            Board.plantGrid[newY][newX] = newF;
            return newF;
        }
        Grass newG = new Grass();
        newG.pos = new Position(newX * 40 + 20, newY * 40 + 120);
        Board.plantGrid[newY][newX] = newG;
        return newG;
    }
}
