import entity.Item;
import exception.ConstraintException;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This is the main solution class. It contains the main method that starts run the application. Line comments have been placed to describe the implementation
 */

public class PackageChallenge
{
    private static Scanner input;
    // orderCount is used to keep a count of the number of orders. This is useful for troubleshooting and resolving constraint violations related to packages.
    // The program can continue executing even after a constraint violation is encountered on a particular order.
    private static int orderCount;
    
    
    public String packageItems(String order)
    {
        double packageWeightLimit;
        Item item1;
        // Each item is unique so I use a HashSet to hold packaging items. All items in a Set data structure are unique.
        Set<Item> items = new HashSet<>();
        
        // Split shipping order into weight and corresponding items list
        String[] weightAndItems = order.split(":");
        
        // First array index is the weight Limit. I trim the value to remove surrounding spaces and convert weight limit do a double for manipulation
        packageWeightLimit = Double.parseDouble(weightAndItems[0].trim());
        // Constraint: Package weight should not be greater than 100. Throw a constraint exception in case of a violation
        if (packageWeightLimit > 100)
        {
            throw new ConstraintException("Package must not weight more than 100");
        }
        weightAndItems[1] = weightAndItems[1].trim();
        // Split items on space into item string comprising no, weight and cost present
        String[] itemsNWC = weightAndItems[1].split("\\s+");
        int count = 0;
        // Iterate through the items and remove parenthesis enclosing each item
        for (String itemNWC : itemsNWC)
        {
            itemsNWC[count++] = itemNWC.trim().replaceAll("[\\(\\)]", "");
        }
        
        // Split each item on comma to get its no, weight and costs. Store the values in a java Item object in property types for manipulation.
        for (String itemNWC : itemsNWC)
        {
            String[] item = itemNWC.split(",");
            item1 = new Item();
            int no = Integer.parseInt(item[0].trim());
            double weight = Double.parseDouble(item[1].trim());
            // Remove euro symbol from cost
            item[2] = item[2].trim().replaceAll("\\€", "");
            double cost = Double.parseDouble(item[2]);
            // Constraint: Perform constraint checks on no of items, item weight and item cost
            if (no > 15)
            {
                throw new ConstraintException("Too many items to choose from in package " + orderCount + ". Items may not exceed 15.");
            }
            if (weight > 100)
            {
                throw new ConstraintException("Item weights above 100 for item " + no + " in package " + orderCount + ". Maximum weight of any item should be below 100.");
            }
            if (cost > 100)
            {
                throw new ConstraintException("Item costs more than €100 for item " + no + " in package " + orderCount + ". Maximum cost of any item should be below €100.");
            }
            item1.setNo(no);
            item1.setWeight(weight);
            item1.setCost(cost);
            // Filter to remove items that are outside the package weight limit
            if (item1.getWeight() <= packageWeightLimit)
            {
                items.add(item1);
            }
        }
        
        /** Sort items by cost in descending order (highest to lowest) in order to add items with higher cost to package first. HashSet is not an ordered data-structure so they cannot be sorted. We use the
         * LinkedHashSet which is the ordered version of the HashSet*/
        items = items.stream().sorted(Comparator.comparing(Item::getCost, Comparator.reverseOrder())).collect(Collectors.toCollection(LinkedHashSet::new));
        
        // Return "-" if no items fit into package
        if (items.size() == 0)
        {
            return "-";
        }
        
        double sumOfItemWeight = 0;
        // Each package item is unique due to unique item nos so I use a HashSet to collect the items.
        Set<Item> valuedPackage = new HashSet<>();
        Item previousItem = new Item();
        for (Item item : items)
        {
            // Check if this is the first item in the list.
            // Add item to package weight. Add item to package. Keep track of current item as previousItem in case the next item has same cost as this item.
            if (valuedPackage.size() == 0)
            {
                sumOfItemWeight += item.getWeight();
                valuedPackage.add(item);
                previousItem = item;
            }
            
            // If package weight has exceeded, compare it against previous item to check if they are same and add item with lower weight to package
            else if ((sumOfItemWeight + item.getWeight() > packageWeightLimit) && (item.getCost() == previousItem.getCost()))
            {
                if (item.getWeight() < previousItem.getWeight())
                {
                    // Item shave same cost. Remove item with larger weight before adding item with lesser weight to package
                    valuedPackage.remove(previousItem);
                    sumOfItemWeight -= previousItem.getWeight();
                    valuedPackage.add(item);
                }
            }
            // Check if package weight can accommodate item based on weight limit and add if it is.
            else if (sumOfItemWeight + item.getWeight() <= packageWeightLimit)
            {
                sumOfItemWeight += item.getWeight();
                valuedPackage.add(item);
                previousItem = item;
            }
        }
        // I retrieve the item nos for items in the package and use the Collectors joining method to collect them into a comma separated string and return it as the answer to the challenge.
        return valuedPackage.stream().map(Item::getNo).sorted().map(String::valueOf).collect(Collectors.joining(",", "", ""));
    }
    
    public static void main(String[] args)
    {
        List<String> orders = new ArrayList<>();
        PackageChallenge packageChallenge = new PackageChallenge();
        try
        {
            // Retrieves the path of the current working directory. The sampleInput.txt file which contains the orders resides in same folder
            String pathToFile = Paths.get(".").toAbsolutePath().normalize().toString();
            // Pass file to scanner and read contents of file into a List line by line while ignoring blank lines
            input = new Scanner(Paths.get(pathToFile + "/sampleInput.txt"));
            while (input.hasNext())
            {
                String order = input.nextLine();
                if (order.length() > 0) // ignore empty lines
                {
                    orders.add(order);
                }
            }
        }
        catch (IOException ex)
        {
            // End program if there is an error reading file with orders.
            System.err.println("Error opening file. Terminating.");
            System.exit(1);
        }
        
        // Ensures that subsequent orders are packaged in case of a constraint violation.
        while (!completed)
        {
            try
            {
                // iterate through orders and put them into packages
                for (int i = orderCount; i < orders.size(); i++)
                {
                    // Track orders to continue with next order in case of a constraint violation while packaging an order
                    orderCount = i;
                    System.out.println(packageChallenge.packageItems(orders.get(i)));
                    
                    // if all orders have been packaged exit while loop by setting completed to true
                    completed = orderCount == orders.size() - 1;
                }
            }
            catch (ConstraintException ce)
            {
                // In case of constraint violation display error message and continue from next order until all orders are packaged.
                System.out.println(ce.getMessage());
                ++orderCount;
                // return to start of while loop
                continue;
            }
            finally
            {
                if (input != null)
                {
                    input.close();
                }
            }
        }
    }
    
    private static boolean completed = false;
}
