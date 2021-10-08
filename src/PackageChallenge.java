import entity.Package;
import exception.ConstraintException;
import operations.PackageOperations;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * This is the main solution class. It contains the main method that starts run the application. Line comments have been placed to describe the implementation
 * and calls to operations in other classes
 */

public class PackageChallenge
{
    public static void main(String[] args)
    {
        PackageOperations packageOperations = new PackageOperations();
        
        // Retrieves the path of the current working directory. The sampleInput.txt file which contains the testCases resides in same folder
        String pathToFile = Paths.get(".").toAbsolutePath().normalize().toString();
        
        // retrieve complete testCases file path
        Path filePath = Paths.get(pathToFile + "/sampleInput.txt");
        
        // A list that will hold all packages
        List<Package> packages;
        
        try
        {
            packages = packageOperations.getPackagesFromTestCaseFile(filePath);
            for (int i = PackageOperations.packageCount - 1; i < packages.size(); i++)
            {
                PackageOperations.packageCount = i + 1;
                Package aPackage = packageOperations.optimizePackage(packages.get(i));
                // retrieve Item nos from Package as string and print to console
                System.out.println(packageOperations.getItemNosFromPackage(aPackage));
            }
        }
        catch (ConstraintException ce)
        {
            // Print error message to console.
            System.out.println(ce.getMessage());
        }
    }
}
