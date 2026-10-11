import java.util.ArrayList;
import java.util.List;
public class Sheep extends Animal{

    Flower targetFlower = null;

    public Sheep(String name, float speed, float perception, int lifeSpan, Animal[] parents)
    {
        super(name, speed, perception, lifeSpan, parents);
        nutrition = 0.5;
        entityType = Board.Ent.sheep;
    }

    /*
    public Entity LookForFood()
    {
        //tempo just to allow for logic development. Replace with proper entity database at some point
        // Do flowers first, then grass
        List<Entity> allPlants = Board.entities.get(Board.Ent.flower.get());


        Entity closest = null;
        double closestDist = 99999;
        for (Entity flower : allPlants) {

            double dist = this.pos.dist(flower.pos) ;
            if(dist < this.perception && dist < closestDist)
            {
                closest = flower;
                closestDist = dist;
            }
        }
        if(closest != null)
        {
            //System.out.println("Closest flower @ " + closest.pos);
            return closest;
        }else{
            //System.out.println("No flowers in range");

        }
        allPlants = Board.entities.get(Board.Ent.grass.get());
        closestDist = 99999;
        for (Entity grass : allPlants) {

            double dist = this.pos.dist(grass.pos) ;
            if(dist < this.perception && dist < closestDist)
            {
                closest = grass;
                closestDist = dist;
            }
        }

        return closest;
        */

    @Override
    public Entity LookForFood()
    {
        // search for nearest flower in perception range
        Entity closestFlower = Board.entities.get(Board.Ent.flower.get())
            .stream()
            .filter(flower -> this.pos.dist(flower.pos) < this.perception)
            .min((flower1, flower2) ->
                Double.compare(
                    this.pos.dist(flower1.pos),
                    this.pos.dist(flower2.pos)))
            .orElse(null);

        // flowers chosen over grass
        if(closestFlower != null) {
            return closestFlower;
        }

        // if flower not available, search for nearest grass
        return Board.entities.get(Board.Ent.grass.get())
            .stream()
            .filter(grass -> this.pos.dist(grass.pos) < this.perception)
            .min((grass1, grass2) ->
                Double.compare(
                    this.pos.dist(grass1.pos),
                    this.pos.dist(grass2.pos)))
            .orElse(null);
    }

    @Override
    protected void Reproduce(Animal partAnimal)
    {
        super.Reproduce(partAnimal);
       Board.babyAnimals.add(new Sheep(null, 1, 60, 5, new Animal[]{this, partAnimal}));

    }
}
