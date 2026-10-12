# Thee Sheep Simulation
## Overview
comp2000 assignment essentially

## Simulation Concept
The original proposal involves:
- Sheep grazing on grass and flowers to survive
- Wolves hunting sheep as their primary food source
- Animals searching for food, moving around the environment, and reproducing
- Offspring inheriting characteristics from their parents, with the possibility of mutation
- Vegetation growing and spreading throughout the environment

## Current Implementation
The simulation currently contains:
- A graphical simulation board using Java Swing
- Sheep and wolf entities with different behaviours
- Animal attributes e.g. speed, hunger, perception, and lifespan
- Food searching, movement, and predator-prey interactions
- Animal reproduction and inherited attributes
- Grass spreading and flower generation
- Basic animal behaviour states for finding food and mates

## Project Structure
- `Main.java` - application entry point and window setup
- `Board.java` - Simulation environment, entity management, and rendering
- `Entity.java` - base entity class
- `Animal.java` - Shared animal attributes and behaviours
- `Sheep.java` - Sheep specific behaviour
- `Wolf.java` - Wolf specific behaviour
- `Plant.java` - Base plant class
- `Grass.java` - Grass growth and spreading
- `Flower.java` - Flower Entity
- `Position.java` - Position and distance calculation

## Ongoing Development
- Improving animal mating and reproduction logic
- Balancing hunger, food availability, and predator-prey populations
- Expanding the use of lambdas and streams
- Improving simulation stability and entity management
- Exploring additional behaviours
