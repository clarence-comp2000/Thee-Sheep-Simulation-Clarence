import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;


public abstract class Animal extends Entity{

    protected enum AnimalState
    {
        lookingForFood,
        chasingFood,
        lookingForMate,
        chasingMate;
    }

    private static String[] nameList = {"Mary", "Franky", "Franklin", "Georgie", "Susan", "Laura", "Daisy", "Rose","Cody","Cuddles", "Bill", "Shaun", "Katie", "Brutus", "Scar", "Fido", "Jet", };

    public String name = "Background Character";

    protected double hunger = 0.5; //Range from 1.0 (Creature full), (0.0) Creature dies)

    public double GetHunger()
    {
        return hunger;
    }

    public double speed = 1.0; //Tiles moved

    public double perception = 2.0; //Range of which creature can see

    public int age = 0; // Increase by 1 every generation

    public int lifeSpan = 5;//Every generation after this, flip coin to see if survives

    public Color colour = Color.white;

    protected static double hungerToReproduce = 0.75; //Will look for a mate if hunger above this level;

    private ArrayList<Animal> children = new ArrayList<Animal>();
    private Animal[] parents = new Animal[2];

    private Entity targetEntity = null;

    public Position randPos;

    protected AnimalState state = AnimalState.lookingForFood;

    public AnimalState GetState(){
        return state;
    }





    public Animal()
    {

    }

    public Animal(String name, double speed, double perception, int lifeSpan, Animal[] parents)
    {
        if(name != null)
        {
            this.name = name;
        }else{
            this.name = nameList[(int)(Math.random() * nameList.length)];
        }

        this.speed = speed;
        this.perception = perception;
        this.lifeSpan = lifeSpan;
        this.parents = parents;
        this.pos = new Position(0, 0);
        this.hunger = 0.6;
        if(parents[0] != null)
        {
            this.pos.setX(parents[0].pos.getX());
            this.pos.setY(parents[0].pos.getY());
        }
        this.randPos = Position.genRand(Board.bWidth, Board.bHeight, 0, 100, 40);
    }

    //Moves animal towards destination, returns true if it is at the destination already with buffer.
    public boolean Move(Position destination, int buffer)
    {
        Position direction = pos.dir(destination);
        if(Math.abs(direction.getX()) <= buffer && Math.abs(direction.getY()) <= buffer){return true;}
        if(Math.abs(direction.getX()) > Math.abs(direction.getY()))
        {
            pos.setX((int)(pos.getX() + speed * Math.signum(direction.getX())));
        }else{
            pos.setY((int)(pos.getY() + speed * Math.signum(direction.getY())));
        }
        return false;
    }

    int wait = 0;
    public void AnimalBehaviour()
    {
        if(!IsAlive()) return;
        hunger -= 0.0001;
        if(hunger < 0)
        {
            System.out.println(name + " Starved");
            hunger = 0;
            KillEntity();
            return;
        }

        switch(state){
            case AnimalState.lookingForFood:
            {
                LookingForFood();
                break;
            }
            case AnimalState.chasingFood:
            {
                ChasingFood();
                break;
            }case AnimalState.lookingForMate:
            {
                LookingForMate();
                break;
            }case AnimalState.chasingMate:
            {
                ChasingMate();
                break;
            }

        }
    }

    protected void LookingForFood()
    {
        if(targetEntity == null || !targetEntity.IsAlive())
        {
            this.targetEntity = LookForFood();
            if(this.targetEntity != null)
            {
                state = AnimalState.chasingFood;
            }else
            {
                RoamRandomly();

            }

        }else{
            targetEntity = null;
            RoamRandomly();
        }

    }

    protected void ChasingFood()
    {
        //NEED TO FIX THIS AS, WHILST TECHINICALLY IT SHOULD NOT CRASH, SHEEP WILL HUNT INVISIBLE FLOWERS
        if(targetEntity == null || !targetEntity.IsAlive())
        {
           targetEntity = null;
            state = AnimalState.lookingForFood;
            return;
        }
        if(Move(targetEntity.pos, 10))
        {
            EatFood(targetEntity);
            if(hunger >= hungerToReproduce)
            {
                state = AnimalState.lookingForMate;
                //state = AnimalState.lookingForFood;
            }else{
                state = AnimalState.lookingForFood;
            }
        }
    }

    public void AttractMate(Entity ent)
    {
        targetEntity = ent;
        state = AnimalState.chasingMate;
    }

    /*
        protected void LookingForMate()
    {
        Entity closest = null;
        double closestDist = 99999;
        for (Entity ent: Board.entities.get(entityType.get()) )
        {
            if(ent == null || ent == this)
            {
                continue;
            }else {
                Animal a = (Animal)ent;
                if(a != null && a.GetState() == AnimalState.lookingForMate)
                {
                    //other creature is not ready to mate
                    a.AttractMate(this);

                }else{
                    continue;
                }
            }
            double dist = this.pos.dist(ent.pos) ;
            if(dist < this.perception*100 && dist < closestDist)
            {
                closest = ent;
                closestDist = dist;
            }
        }




        if(closest != null)
        {
            targetEntity = closest;
            state = AnimalState.chasingMate;
        }else if(hunger < hungerToReproduce)
        {
            System.out.println(name + " is now to hungry for love :(");
            state = AnimalState.lookingForFood;
        }else
        {
            RoamRandomly();
        }

    }
        */

    protected void LookingForMate()
    {
        if(hunger < hungerToReproduce)
        {
            targetEntity = null;
            state = AnimalState.lookingForFood;
            return;
        }

        Animal closest = null;
        double closestDist = Double.MAX_VALUE;

        for(Entity ent : Board.entities.get(entityType.get()))
        {
            if(!(ent instanceof Animal) || ent == this || !ent.IsAlive())
            {
                continue;
            }
            Animal potentialMate = (Animal) ent;

            if(potentialMate.GetState() != AnimalState.lookingForMate
                || potentialMate.GetHunger() < hungerToReproduce)
            {
                continue;
            }

            double dist = pos.dist(potentialMate.pos);

            if(dist < perception && dist < closestDist)
            {
                closest = potentialMate;
                closestDist = dist;
            }
        }

        if(closest != null)
        {
            targetEntity = closest;
            state = AnimalState.chasingMate;

            closest.AttractMate(this);
        }
        else
        {
            RoamRandomly();
        }
    }

    protected void RoamRandomly()
    {
        boolean moved = Move(randPos, 10);
            if (moved && (wait == 0 || hunger < 0.25))
            {
                wait = 100;
                randPos = Position.genRand(Board.bWidth, Board.bHeight, 0, 100, 40);
            }
            else if (moved) {
                wait--;
            }
    }

    /*
        protected void ChasingMate()
    {
        if(hunger < hungerToReproduce)
        {
            state = AnimalState.lookingForFood;
            targetEntity = null;
            return;
        }
        if(targetEntity == null || !targetEntity.IsAlive())
        {
            targetEntity = null;
            state = AnimalState.lookingForMate;
        }
        try{
            if(Move(targetEntity.pos, 10))
            {
                System.out.println("reproducing");
                Reproduce((Animal)targetEntity);
                System.out.println("baby Created");
                state = AnimalState.lookingForFood;

            }
        } catch(NullPointerException npe) {
            System.out.println(this + "Target Entity not found");
        }
    }
    */

    protected void ChasingMate()
    {
        if(hunger < hungerToReproduce)
        {
            state = AnimalState.lookingForFood;
            targetEntity = null;
            return;
        }

        if (targetEntity == null || !targetEntity.IsAlive())
        {
            targetEntity = null;
            state = AnimalState.lookingForMate;
            return;
        }

        Animal mate = (Animal) targetEntity;

        if(mate.GetHunger() < hungerToReproduce)
        {
            targetEntity = null;
            state = AnimalState.lookingForFood;
            return;
        }

        if(Move(mate.pos, 10))
        {
            Reproduce(mate);
            state = AnimalState.lookingForFood;
            mate.state = AnimalState.lookingForFood;
        }
    }

    protected void Reproduce(Animal anim)
    {
        targetEntity = null;

        hunger = hunger - 0.4f;
        anim.hunger = anim.hunger - 0.4f;
        anim.targetEntity = null;

        //temp


    }
    public Entity LookForFood()
    {
        //System.out.println("This is the base look for food function, should not be called");
        return null;
    }


    public void EatFood(Entity entity)
    {

        hunger += entity.nutrition;

        if(hunger > 1.0)
        {
            hunger = 1.0;
        }
        System.out.println("Creature "+ name + " ate entity worth "+ entity.nutrition + " nutrition. Total hunger now: " + hunger);
        entity.KillEntity();
        targetEntity = null;

    }



    public void Reproduce()
    {

    }

    @Override
    public void drawEntity(Graphics g) {
        g.setColor(colour);
        g.fillRect(pos.getX(), pos.getY(), 40,40);




        g.setColor(Color.RED);
        g.drawString(name.toString(), pos.getX() + 5, pos.getY() + 5);
        g.setColor(Color.green);
        double rounded = Math.round(hunger * 100.0) / 100.0;

        g.drawString(Double.toString(rounded), pos.getX() + 5, pos.getY() + 20);

        g.setColor(Color.BLUE);
        g.drawString(state.toString(), pos.getX() + 5, pos.getY() + 35);
    }
}
