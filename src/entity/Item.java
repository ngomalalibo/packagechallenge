package entity;

import exception.ConstraintException;
import operations.PackageOperations;

/**
 * Plain java object with getter and setter methods to hold instances of items. I also override the toString method to return all property names and values pairs
 */
public class Item
{
    // we use integer for the item no and double for both weight and cost to capture floating point values.
    private int no;
    private double weight;
    private double cost;
    
    
    public Item(String description) throws ConstraintException
    {
        createItem(description);
    }
    
    public int getNo()
    {
        return no;
    }
    
    public void setNo(int no)
    {
        this.no = no;
    }
    
    public double getWeight()
    {
        return weight;
    }
    
    public void setWeight(double weight)
    {
        this.weight = weight;
    }
    
    public double getCost()
    {
        return cost;
    }
    
    public void setCost(double cost)
    {
        this.cost = cost;
    }
    
    @Override
    public String toString()
    {
        return "No: " + this.no + " Weight: " + this.weight + " Cost: " + this.cost;
    }
    
    private Item createItem(String itemsWithDescription) throws ConstraintException
    {
        // Split each item description on comma to get its no, weight and costs. Store the values in a java Item object in property types for manipulation.
        String[] item = itemsWithDescription.split(",");
        int no = Integer.parseInt(item[0].trim());
        double weight = Double.parseDouble(item[1].trim());
        // Remove euro symbol from cost
        item[2] = item[2].trim().substring(1, item[2].length());
        double cost = Double.parseDouble(item[2]);
        // Constraint: Perform constraint checks on the number of items per package, item weight and item cost
        if (no > 15)
        {
            throw new ConstraintException("Too many items to choose from in package " + PackageOperations.getPackageCount() + ". Items may not exceed 15.");
        }
        if (weight > 100)
        {
            throw new ConstraintException("Item weights above 100 for item " + no + " in package " + PackageOperations.getPackageCount() + ". Maximum weight of any item should be below 100.");
        }
        if (cost > 100)
        {
            throw new ConstraintException("Item costs more than €100 for item " + no + " in package " + PackageOperations.getPackageCount() + ". Maximum cost of any item should be below €100.");
        }
        this.setNo(no);
        this.setWeight(weight);
        this.setCost(cost);
        
        return this;
    }
}
