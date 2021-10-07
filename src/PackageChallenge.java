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
    // tetCaseCount is used to keep a count of the number of testCases. This is useful for troubleshooting and resolving constraint violations related to packages.
    // The program can continue executing even after a constraint violation is encountered on a particular testCase.
    private static int testCaseCount;
    // used to keep program running in case of a constraint violation exception
    private static boolean completed = false;
    
    
    public String packageItems(String testCase)
    {
        double packageWeightLimit;
        Item item1;
        // Each item is unique so I use a HashSet to hold packaging items. All items in a Set data structure are unique.
        Set<Item> items = new HashSet<>();
        
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
        int count = 0;
        // Iterate through the items and remove parenthesis enclosing each item
        for (String itemWithDescription : itemsWithDescriptions)
        {
            itemsWithDescriptions[count++] = itemWithDescription.trim().replaceAll("[\\(\\)]", "");
        }
        
        // Split each item description on comma to get its no, weight and costs. Store the values in a java Item object in property types for manipulation.
        for (String itemWithDescription : itemsWithDescriptions)
        {
            String[] item = itemWithDescription.split(",");
            item1 = new Item();
            int no = Integer.parseInt(item[0].trim());
            double weight = Double.parseDouble(item[1].trim());
            // Remove euro symbol from cost
            item[2] = item[2].trim().replaceAll("\\€", "");
            double cost = Double.parseDouble(item[2]);
            // Constraint: Perform constraint checks on no of items, item weight and item cost
            if (no > 15)
            {
                throw new ConstraintException("Too many items to choose from in package " + testCaseCount + ". Items may not exceed 15.");
            }
            if (weight > 100)
            {
                throw new ConstraintException("Item weights above 100 for item " + no + " in package " + testCaseCount + ". Maximum weight of any item should be below 100.");
            }
            if (cost > 100)
            {
                throw new ConstraintException("Item costs more than €100 for item " + no + " in package " + testCaseCount + ". Maximum cost of any item should be below €100.");
            }
            item1.setNo(no);
            item1.setWeight(weight);
            item1.setCost(cost);
            // Filter to remove items that are singularly heavier than the package weight limit
            if (item1.getWeight() <= packageWeightLimit)
            {
                items.add(item1);
            }
        }
        
        /** Sort items by cost in descending order (highest to lowest) in order to add items with higher cost to package first.
         * HashSet is not an ordered data-structure so they cannot be sorted. We use the LinkedHashSet which is the ordered version of the HashSet.
         * */
        items = items.stream().sorted(Comparator.comparing(Item::getCost, Comparator.reverseOrder())).collect(Collectors.toCollection(LinkedHashSet::new));
        
        // Return "-" if no item fits into package
        if (items.size() == 0)
        {
            return "-";
        }
        
        double sumOfWeightOfItems = 0;
        // Each package item is unique due to the unique item no's in the description so I use a HashSet to collect the items.
        Set<Item> valuedPackage = new HashSet<>();
        Item previousItem = new Item();
        for (Item item : items)
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
        // I retrieve the item nos for each item in the package, convert it to a string and then use the Collectors joining method to collect them into a comma separated string and
        // return it as the items to send in the package.
        return valuedPackage.stream().map(Item::getNo).sorted().map(String::valueOf).collect(Collectors.joining(",", "", ""));
    }
    
    public static void main(String[] args)
    {
        List<String> testCases = new ArrayList<>();
        PackageChallenge packageChallenge = new PackageChallenge();
        try
        {
            // Retrieves the path of the current working directory. The sampleInput.txt file which contains the testCases resides in same folder
            String pathToFile = Paths.get(".").toAbsolutePath().normalize().toString();
            // Pass file to scanner and read contents of file into a List line by line while ignoring blank lines
            input = new Scanner(Paths.get(pathToFile + "/sampleInput.txt"));
            while (input.hasNext())
            {
                String testCase = input.nextLine();
                if (testCase.length() > 0) // ignore empty lines
                {
                    testCases.add(testCase);
                }
            }
        }
        catch (IOException ex)
        {
            // End program if there is an error reading file with testCases.
            System.err.println("Error opening file. Terminating.");
            System.exit(1);
        }
        
        // Ensures that subsequent testCases are packaged in case of a constraint violation.
        while (!completed)
        {
            try
            {
                // iterate through testCases and put them into packages
                for (int i = testCaseCount; i < testCases.size(); i++)
                {
                    // Track testCases to continue with next testCase in case of a constraint violation while packaging an testCase
                    testCaseCount = i;
                    System.out.println(packageChallenge.packageItems(testCases.get(i)));
                    
                    // if all testCases have been packaged exit while loop by setting completed to true
                    completed = testCaseCount == testCases.size() - 1;
                }
            }
            catch (ConstraintException ce)
            {
                // In case of constraint violation display error message and continue from next testCase (++testCaseCount) until all testCases are packaged.
                System.out.println(ce.getMessage());
                ++testCaseCount;
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
}
