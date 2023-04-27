import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a deer.
 * Deer age, move, eat grass, and die.
 * 
 * 
 * @author Patrick Musich
 * @version 2023.04.20
 * 
 * Based on fox model by:
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
*/
public class Deer extends Organism
{
    // Characteristics shared by all deer(class variables).
    
    //the maximum number of health points a deer can have.
    private static final int MAX_HEALTH = 8;
    
    // The age at which a deer can start to breed.
    private static final int BREEDING_AGE = 15;
    
    // The age to which a deer can live.
    private static final int MAX_AGE = 150;
    
    // The likelihood of a deer breeding.
    private static final double BREEDING_PROBABILITY = 0.08;
    
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    
    // The food value of a single grass. In effect, this is the
    // number of steps a deer can go before it has to eat again.
    //private static final int GRASS_FOOD_VALUE = 9;
    
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The deer's age.
    private int age;
    
    // The deer's food level, which is increased by eating grass.
    //private int foodLevel;
    
    // The deer's health points.
    private int health;

    /**
     * Create a deer. A deer can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the deer will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Deer(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            health = rand.nextInt(MAX_HEALTH);
        }
        else {
            age = 0;
            health = MAX_HEALTH;
        }
    }
    
    /**
     * This is what the deer does most of the time: it searches for 
     * grass to eat. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newFoxes A list to return newly born foxes.
     */
    public void act(List<Organism> newDeer)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newDeer);            
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
                
            }
            // See if it was possible to move.
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Increase the age. This could result in the deer's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this deer more hungry. This could result in the deer's death.
     */
    private void incrementHunger()
    {
        health--;
        if(health <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for vegetation (tree or grass) adjacent to the current location.
     * Only the first live grass is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object plant = field.getObjectAt(where);
            if(plant instanceof Tree) {
                Tree tree = (Tree) plant;
                if(tree.isAlive()) { 
                    tree.setDead();
                    health = ++health;
                    return where;
                }
            }
            
            else if(plant instanceof Grass){
                Grass grass = (Grass) plant;
                if (grass.isAlive()) {
                    grass.setDead();
                    health = ++health;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this deer is to give birth at this step.
     * Deer must be the appropriate age and "healthy".
     * New births will be made into free adjacent locations.
     * @param newDeer A list to return newly born deer.
     */
    private void giveBirth(List<Organism> newDeer)
    {
        // New deer are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Deer young = new Deer(false, field, loc);
            newDeer.add(young);
        }
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A deer can breed if it has reached the breeding age
     * and is in good health.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE && health >= 5;
    }
    
    
    }

