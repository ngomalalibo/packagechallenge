package operations;

import entity.Item;
import exception.ConstraintException;

public class ItemOperations
{
    
    
    public Item createItem(String itemsWithDescription) throws ConstraintException
    {
        Item item1 = new Item();
        
        // Split each item description on comma to get its no, weight and costs. Store the values in a java Item object in property types for manipulation.
        String[] item = itemsWithDescription.split(",");
        int no = Integer.parseInt(item[0].trim());
        double weight = Double.parseDouble(item[1].trim());
        // Remove euro symbol from cost
        item[2] = item[2].trim().substring(1, item[2].length());
        double cost = Double.parseDouble(item[2]);
        // Constraint: Perform constraint checks on no of items, item weight and item cost
        if (no > 15)
        {
            throw new ConstraintException("Too many items to choose from in package " + PackageOperations.packageCount + ". Items may not exceed 15.");
        }
        if (weight > 100)
        {
            throw new ConstraintException("Item weights above 100 for item " + no + " in package " + PackageOperations.packageCount + ". Maximum weight of any item should be below 100.");
        }
        if (cost > 100)
        {
            throw new ConstraintException("Item costs more than €100 for item " + no + " in package " + PackageOperations.packageCount + ". Maximum cost of any item should be below €100.");
        }
        item1.setNo(no);
        item1.setWeight(weight);
        item1.setCost(cost);
        
        return item1;
    }
}
