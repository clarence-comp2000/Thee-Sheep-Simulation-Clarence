
import java.awt.Color;
import java.awt.Graphics;



public abstract class Plant extends Entity {
    
    public String name;

    public Color pColour; 
    
    public Plant(){
        this.name = "Plant";
        this.pColour = Color.orange;
    }

    public Plant(String name, Color colour){
        this.name = name;
        this.pColour = colour;

    }

    @Override
    public void drawEntity(Graphics g) {
        g.setColor(pColour);
        int size = 40;
        if (this.sprite == null) {
            g.fillOval(pos.getX() - 20, pos.getY() - 20, size, size);
        }
        else {
            int spriteSize = (int)(size * spriteScale);
            g.drawImage(sprite, pos.getX()-(spriteSize / 2), pos.getY()-(spriteSize / 2), spriteSize, spriteSize, null);
        }
    }

}
