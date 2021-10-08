package entity;

import java.util.Set;

/**
 * This class is an object of a Package consisting of Items. It has 2 properties. Its items and its maximum weight
 */
public class Package
{
    private Set<Item> items;
    private double maxWeight;
    
    public Package(Set<Item> items, double maxWeight)
    {
        this.items = items;
        this.maxWeight = maxWeight;
    }
    
    public double getMaxWeight()
    {
        return this.maxWeight;
    }
    
    public Set<Item> getItems()
    {
        return this.items;
    }
}
