package operations;

import entity.Item;
import entity.Package;
import exception.ConstraintException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class PackageOperations
{
    private static Scanner input;
    // I use a static class variable to keep a count of the packages so that error messages can include package no in case of a constraint.
    public static int packageCount = 1;
    
    ItemOperations itemOperations = new ItemOperations();
    
    public String getItemNosFromPackage(Package packagee)
    {
        // Return "-" if no item fits into package
        if (packagee.getItems().size() == 0)
        {
            return "-";
        }
        
        // I retrieve the item nos for each item in the package, convert it to a string and then use the Collectors joining method to collect them into a comma separated string and
        // return it as the items to send in the package.
        return packagee.getItems().stream().map(Item::getNo).sorted().map(String::valueOf).collect(Collectors.joining(",", "", ""));
    }
    
    public Package optimizePackage(Package packagee)
    {
        /** Sort items by cost in descending order (highest to lowest) in order to add items with higher cost to package first.
         * HashSet is not an ordered data-structure so they cannot be sorted. We use the LinkedHashSet which is the ordered version of the HashSet.
         * */
        LinkedHashSet<Item> sortedItems = packagee.getItems().stream().sorted(Comparator.comparing(Item::getCost, Comparator.reverseOrder())).collect(Collectors.toCollection(LinkedHashSet::new));
        double packageWeightLimit = packagee.getMaxWeight();
        double sumOfWeightOfItems = 0;
        // Each package item is unique due to the unique item no's in the description so I use a HashSet to collect the items.
        Set<Item> valuedPackage = new HashSet<>();
        Item previousItem = new Item();
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
    
    public List<Package> getPackagesFromTestCaseFile(Path filePath) throws ConstraintException
    {
        List<Package> packages = new ArrayList<>();
        // Each item is unique so I use a HashSet to hold packaging items. All items in a Set data structure are unique.
        Set<Item> items;
        try
        {
            // Pass file to scanner and read contents of file into a Package line by line while ignoring blank lines
            input = new Scanner(filePath);
            while (input.hasNext())
            {
                items = new HashSet<>();
                double packageWeightLimit = 0;
                String testCase = input.nextLine();
                if (testCase.length() > 0) // ignore empty lines
                {
                    // Split testCase into weight and corresponding items list
                    String[] weightAndItems = testCase.split(":");
                    
                    // First array index is the weight Limit. I trim the value to remove surrounding spaces and convert weight limit do a double for manipulation
                    packageWeightLimit = Double.parseDouble(weightAndItems[0].trim());
                    // Constraint: Package weight should not be greater than 100. Throw a constraint exception in case of a violation
                    if (packageWeightLimit > 100)
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
                        Item item = itemOperations.createItem(itemsWithDescription);
                        // Filter to remove items that are singularly heavier than the package weight limit
                        if (item.getWeight() <= packageWeightLimit)
                        {
                            items.add(item);
                        }
                    }
                    packages.add(new Package(items, packageWeightLimit));
                }
            }
        }
        catch (IOException ex)
        {
            // End program if there is an error reading file with testCases.
            System.err.println("Error opening file. Terminating.");
            System.exit(1);
        }
        finally
        {
            if (input != null)
            {
                input.close();
            }
        }
        
        return packages;
    }
}
