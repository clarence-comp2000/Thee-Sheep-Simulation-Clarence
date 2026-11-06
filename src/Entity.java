import java.awt.*;
public abstract class Entity {
    public Position pos;
    protected Board.Ent entityType;
    protected Image sprite;
    protected float spriteScale = 1;

    public Board.Ent GetType()
    {
        return entityType;
    }

    public double nutrition = 0.3; //How much this entity fills a creatures hunger when eaten
    private boolean alive = true;

    public boolean IsAlive()
    {
        return alive;
    }





    public void KillEntity()
    {
        //Death logic
        alive = false;
    }

    public void drawEntity(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(pos.getX(), pos.getY(), 40,40);
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " " + pos;
    }
}
