import entity.Item;
import exception.ConstraintException;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This is the main solution class which contains the main method to run the application. Comments have been placed to describe portions of the code
 */

public class PackageChallenge
{
    private static Scanner input;
    private static int packageCount;
    
    
    public String packageItems(String testCase)
    {
        double weightLimit;
        Item item1;
        // Each item is unique so we have a set of items to consider for packaging.
        Set<Item> items = new HashSet<>();
        
        // split testCase into weight and corresponding items list
        String[] weightAndItems = testCase.split(":");
        
        // first item is weight Limit
        weightLimit = Double.parseDouble(weightAndItems[0].trim());
        // Constraint: weight item should not be greater than 100. throw exception in case of constraint violation
        if (weightLimit > 100)
        {
            throw new ConstraintException("Package must not weight more than 100");
        }
        weightAndItems[1] = weightAndItems[1].trim();
        // split items on space into item string with no, weight and cost present
        String[] itemsNWC = weightAndItems[1].split("\\s+");
        int count = 0;
        // Iterate through the items and remove parenthesis enclosing each item
        for (String itemNWC : itemsNWC)
        {
            itemsNWC[count++] = itemNWC.trim().replaceAll("[\\(\\)]", "");
        }
        
        // split each item by comma to get its no, weight and costs. Store the values in a java Item object in property types  we will manipulate.
        for (String itemNWC : itemsNWC)
        {
            String[] item = itemNWC.split(",");
            item1 = new Item();
            int no = Integer.parseInt(item[0]);
            double weight = Double.parseDouble(item[1]);
            // Remove euro symbol from cost
            item[2] = item[2].replaceAll("\\€", "");
            double cost = Double.parseDouble(item[2]);
            // Perform constraint checks on no of items, item weight and item cost
            if (no > 15)
            {
                throw new ConstraintException("Too many items to choose from. Items may not exceed 15");
            }
            if (weight > 100)
            {
                throw new ConstraintException("Item weights above 100 for item " + no + " in " + packageCount + ". Maximum weight of any item should be below 100");
            }
            if (cost > 100)
            {
                throw new ConstraintException("Item costs more than €100 for item " + no + " in " + packageCount + ". Maximum cost of any item should be below €100");
            }
            item1.setNo(no);
            item1.setWeight(weight);
            item1.setCost(cost);
            // Only consider items that are within the weight limit
            if (item1.getWeight() <= weightLimit)
            {
                items.add(item1);
            }
        }
        
        /** Sort items by cost in descending order to add items with higher cost to package first. HashSet is not ordered so they cannot be sorted. We use the
         * LinkedHashSet which is the ordered version of the HashSet*/
        items = items.stream().sorted(Comparator.comparing(Item::getCost, Comparator.reverseOrder())).collect(Collectors.toCollection(LinkedHashSet::new));
        
        if (items.size() == 0)
        {
            return "-";
        }
        
        double sumOfWeight = 0;
        Set<Item> valuedPackage = new HashSet<>();
        Item previousItem = new Item();
        for (Item item : items)
        {
            //Check if first item in list. add item to package weight. Add item to package. Keep track of current item as previousItem in case next item has same cost
            if (valuedPackage.size() == 0)
            {
                sumOfWeight += item.getWeight();
                valuedPackage.add(item);
                previousItem = item;
            }
            
            // If package weight has exceeded, compare it against previous item to check if they are same and add item with lower weight to package
            else if ((sumOfWeight + item.getWeight() > weightLimit) && (item.getCost() == previousItem.getCost()))
            {
                if (item.getWeight() < previousItem.getWeight())
                {
                    // remove item with larger weight before adding item with lesser weight to package
                    valuedPackage.remove(previousItem);
                    sumOfWeight -= previousItem.getWeight();
                    valuedPackage.add(item);
                }
            }
            // Check if package weight can accommodate item based on weight limit and add if yes.
            else if (sumOfWeight + item.getWeight() <= weightLimit)
            {
                sumOfWeight += item.getWeight();
                valuedPackage.add(item);
                previousItem = item;
            }
        }
        
        return valuedPackage.stream().map(Item::getNo).sorted().map(String::valueOf).collect(Collectors.joining(",", "", ""));
    }
    
    public static void main(String[] args)
    {
        List<String> testCases = new ArrayList<>();
        PackageChallenge packageChallenge = new PackageChallenge();
        // String testCaseSample = "81 : (1,53.38,€45) (2,88.62,€98) (3,78.48,€3) (4,72.30,€76) (5,30.18,€9) (6,46.34,€48)";
        // String testCaseSample = "8 : (1,15.3,€34)";
        // String testCaseSample = "75 : (1,85.31,€29) (2,14.55,€74) (3,3.98,€16) (4,26.24,€55) (5,63.69,€52) (6,76.25,€75) (7,60.02,€74) (8,93.18,€35) (9,89.95,€78)";
        // String testCaseSample = "56 : (1,90.72,€13) (2,33.80,€40) (3,43.15,€10) (4,37.97,€16) (5,46.81,€36) (6,48.77,€79) (7,81.80,€45) (8,19.36,€79) (9,6.76,€64)";
        // System.out.println(packageChallenge.packageItems(testCaseSample));
        try
        {
            // Retrieves the path of the current working directory. The sampleInput.txt file resides in same folder
            String pathToFile = Paths.get(".").toAbsolutePath().normalize().toString();
            input = new Scanner(Paths.get(pathToFile + "/sampleInput.txt"));
            while (input.hasNext())
            {
                String testCase = input.nextLine();
                if (testCase.length() > 0)
                {
                    testCases.add(testCase);
                }
            }
            
            for (String testCase : testCases)
            {
                ++packageCount;
                System.out.println(packageChallenge.packageItems(testCase));
            }
        }
        catch (IOException ex)
        {
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
    }
}
