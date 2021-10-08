import operations.PackageOperations;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * This is the main solution class. It contains the main method that starts run the application. Line comments have been placed to describe the implementation
 * and calls to operations in other classes
 */

public class PackageChallenge
{
    public static void main(String[] args)
    {
        try
        {
            // Retrieves path to current working directory
            String pathToFolder = Paths.get(".").toAbsolutePath().normalize().toString();
            // Setup packaging operation by providing file path
            PackageOperations packageOperations = new PackageOperations(pathToFolder + "/sampleInput.txt");
            // Process Packages
            packageOperations.processPackagesFromTestCaseFile();
        }
        catch (IOException ex)
        {
            System.err.println("Error opening file. Terminating.");
            System.exit(1);
        }
    }
}
