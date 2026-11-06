
import java.awt.*;
import java.io.IOException;
import java.util.Comparator;

import javax.imageio.ImageIO;

public class Sheep extends Animal {

    public Sheep(Genes genes, Animal[] parents, int age)
    {
        super(genes, parents, age);
        nutrition = 0.5;
        entityType = Board.Ent.sheep;
        this.sprite = getSheepImage();
        sprintOnFlee = true;
    }

    @Override
    public Entity LookForFood()
    {
        // Search for the closest living flower within perception range.
        Entity closestFlower = Board.entities.get(Board.Ent.flower.get())
            .stream()
            .filter(Entity::IsAlive)
            .filter(flower ->
                this.pos.dist(flower.pos) < genes.GetPerception())
            .min(Comparator.comparingDouble(
                flower -> this.pos.dist(flower.pos)))
            .orElse(null);

        // Flowers are preferred over grass.
        if (closestFlower != null)
        {
            return closestFlower;
        }

        // If no flower is nearby, search for grass instead.
        return Board.entities.get(Board.Ent.grass.get())
            .stream()
            .filter(Entity::IsAlive)
            .filter(grass ->
                this.pos.dist(grass.pos) < genes.GetPerception())
            .min(Comparator.comparingDouble(
                grass -> this.pos.dist(grass.pos)))
            .orElse(null);
    }

    @Override
    protected void Reproduce(Animal partAnimal)
    {
        System.out.println("Making baby sheep");

        Genes babyGenes = new Genes(genes, partAnimal.GetGenes());

        Sheep newSheep = new Sheep(
            babyGenes,
            new Animal[]{this, partAnimal},
            0
        );

        Board.babyAnimals.add(newSheep);

        super.Reproduce(partAnimal);
    }

    public static Image getSheepImage() {
        try {
            return ImageIO.read(Sheep.class.getResource("/images/sheep.png"));
        } catch(IOException | IllegalArgumentException e) {
            return null;
        }
    }
}

