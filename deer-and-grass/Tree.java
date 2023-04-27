import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * Tree Organism for OOP team project. Overrides methods found in the 
 * Organism abstract class. Trees will reproduce every few turns of the 
 * simulation. There is also a chance a Tree will catch fire any turn.
 *
 * @author Adrian Lezcano
 * @version 1.0
 */
public class Tree extends Organism
{
    // Characteristics shared by all Trees.
    
    // How often a Tree will produce offspring.
    private static final int REPRODUCTION_RATE = 5;
    // Probabilty that Tree survives wildfire.
    private static final double PROBABILTY_OF_SURVIVING = 0.60;
    // Shared randomizer, assuming it will be used.
    private static final Random rand = Randomizer.getRandom();
    // The maximum number of possible new Trees after reproduction.
    private static final int MAX_SEEDS = 2;
    // Keeps track of the turns passed since new Trees were grown.
    private static int turnsSinceLastReproduction = 0;
    
    /**
     * Constructor for Trees. Calls constructer from superclass Organism. 
     * May later have parameter to have random value for turns since 
     * reproduction. 
     * 
     * @param field The field currently occupied.
     * @param location The location of this tree within the field.
     */
    public Tree(Field field, Location location)
    {
        super(field, location);
    }

    /**
     * Simulation of a turn on the field. Tree will get one step closer to 
     * reproducing if not yet ready, otherwise will reproduce. If a wildfire 
     * is near, the Tree might burn.
     * 
     * @param newTrees A list of new Trees from reproduction.
     */
    public void act(List<Organism> newTrees)
    {
        turnsSinceLastReproduction++;
        
        // Check for wildfire in adjacent locations.
        Field field = getField();
        List<Location> adj = field.adjacentLocations(getLocation());
        for (Iterator<Location> it = adj.iterator(); it.hasNext; ) {
            Location location = it.next();
            Object obj = field.getObjectAt(location);
            // If fire is found, determine if tree survives.
            if (obj instanceof Wildfire){
                if(willTreeBurn()){
                    setDead();    
                }
            }
        }
        
        // Reproduction phase
        if (isAlive() && canBreed()){
            reproduce(newTrees);
            turnsSinceLastReproduction = 0;
        }
        
    }
    
    /**
     * Create new instances of trees in free adjacent locatons.
     * 
     * @param newTrees A list of recently born trees.
     */
    private void reproduce(List<Organism> newTrees)
    {
        // Get list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int saplings = breed();
        for (int i = 0; i < saplings && free.size() > 0; i++) {
            Location loc = free.remove(0);
            Tree seed = new Tree(field, loc);
            newTrees.add(seed);
        }
    }
    
    /**
     * Trees reproduce each time the turns since last reproduction matches the
     * reproduction rate.
     * 
     * @return true if Tree can reproduce, false otherwise
     */
    private boolean canBreed()
    {
        return turnsSinceLastReproduction >= REPRODUCTION_RATE;     
    }
    
    /**
     * Generate the number of new trees to be grown into free adjacent 
     * locations.
     * 
     * @return The number of new saplings.
     */
    private int breed()
    {
        int saplings = 0;
        if (canBreed()){
            saplings = rand.nextInt(MAX_SEEDS) + 1;
        }
        return saplings;
    }
    
    /**
     * Boolean to determine if a Tree burns to wildfire. Probabilty of surving 
     * is compared against generated random double value.
     * 
     * @return true if Tree burns, false otherwise.
     */
    private boolean willTreeBurn()
    {
        return rand.nextDouble() <= PROBABILTY_OF_SURVIVING;      
    }
}
