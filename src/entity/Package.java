package entity;

import exception.ConstraintException;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is an object of a Package consisting of Items. It contains properties and methods to manipulate a package
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
    
    public String getItemNos()
    {
        // Return "-" if no item fits into package
        if (this.getItems().size() == 0)
        {
            return "-";
        }
        
        // Return comma separated item numbers
        return this.getItems().stream().map(Item::getNo).sorted().map(String::valueOf).collect(Collectors.joining(",", "", ""));
    }
    
    public Package optimizePackage()
    {
        /** Sort items by cost in descending order (highest to lowest) in order to add items with higher cost to package first.
         * HashSet is not an ordered data-structure so they cannot be sorted. We use the LinkedHashSet which is the ordered version of the HashSet.
         * */
        LinkedHashSet<Item> sortedItems = this.getItems().stream().sorted(Comparator.comparing(Item::getCost, Comparator.reverseOrder())).collect(Collectors.toCollection(LinkedHashSet::new));
        double packageWeightLimit = this.getMaxWeight();
        double sumOfWeightOfItems = 0;
        // Each package item is unique due to the unique item no's in the description so I use a HashSet to collect the items.
        Set<Item> valuedPackage = new HashSet<>();
        Item previousItem = null;
        for (Item item : sortedItems)
        {
            // Check if this is the first item in the list.
            // Add item to package weight. Add item to package. Keep track of current item as previousItem in case the next item has same cost as this item.
            if (valuedPackage.size() == 0)
            {
                sumOfWeightOfItems += item.getWeight();
                valuedPackage.add(item);
                previousItem = item;
            }
            
            // If package weight will exceed with new item, compare it against previous item to check if they are same and add item with lower weight to package
            else if ((sumOfWeightOfItems + item.getWeight() > packageWeightLimit) && (item.getCost() == previousItem.getCost()))
            {
                if (item.getWeight() < previousItem.getWeight())
                {
                    // Items have same cost. Remove item with larger weight before adding item with lesser weight to package
                    valuedPackage.remove(previousItem);
                    sumOfWeightOfItems -= previousItem.getWeight();
                    sumOfWeightOfItems += item.getWeight();
                    valuedPackage.add(item);
                }
            }
            // Check if package weight can accommodate item based on weight limit and add if it is.
            else if (sumOfWeightOfItems + item.getWeight() <= packageWeightLimit)
            {
                sumOfWeightOfItems += item.getWeight();
                valuedPackage.add(item);
                previousItem = item;
            }
        }
        
        return new Package(valuedPackage, packageWeightLimit);
        
    }
    
    public Package(String testCase) throws ConstraintException
    {
        createPackage(testCase);
    }
    
    public Package createPackage(String testCase) throws ConstraintException
    {
        this.items = new HashSet<>();
        
        // Split testCase into weight and corresponding items list
        String[] weightAndItems = testCase.split(":");
        
        // First array index is the weight Limit. I trim the value to remove surrounding spaces and convert weight limit do a double for manipulation
        this.maxWeight = Double.parseDouble(weightAndItems[0].trim());
        // Constraint: Package weight should not be greater than 100. Throw a constraint exception in case of a violation
        if (this.maxWeight > 100)
        {
            throw new ConstraintException("Package must not weight more than 100");
        }
        weightAndItems[1] = weightAndItems[1].trim();
        // Split items on space into item string comprising no, weight and cost present
        String[] itemsWithDescriptions = weightAndItems[1].split("\\s+");
        
        // Iterate through the items and remove parenthesis enclosing each item
        for (int i = 0; i < itemsWithDescriptions.length; i++)
        {
            
            String itemsWithDescription = itemsWithDescriptions[i];
            itemsWithDescription = itemsWithDescription.trim().substring(1, itemsWithDescription.length() - 1);
            // create item objects from descriptions
            Item item = new Item(itemsWithDescription);
            // Filter to remove items that are singularly heavier than the package weight limit
            if (item.getWeight() <= this.maxWeight)
            {
                items.add(item);
            }
        }
        
        return this;
    }
}
