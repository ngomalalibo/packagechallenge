package operations;

import entity.Package;
import exception.ConstraintException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class PackageOperations
{
    private static Scanner input;
    
    // I use a static class variable to keep a count of the packages so that error messages can include package no in case of a constraint.
    private static int packageCount = 1;
    
    private boolean isCompleted = false;
    
    private Path path;
    
    /** Read testCase file */
    public PackageOperations(String filePath)
    {
        this.path = Paths.get(filePath);
    }
    
    public void processPackagesFromTestCaseFile() throws IOException
    {
        // Pass file to scanner and read contents of file into a Package line by line while ignoring blank lines
        input = new Scanner(this.path);
        
        while (!isCompleted)
        {
            while (input.hasNext())
            {
                String testCase = input.nextLine();
                Package aPackage;
                //System.out.println(testCase);
                
                // if line is empty skip to next line.
                if (testCase.length() == 0)
                {
                    continue;
                }
                try
                {
                    // use constructor to create package from testCase
                    aPackage = new Package(testCase);
                    
                    // optimize package based on cost and weight as per requirements
                    aPackage = aPackage.optimizePackage();
                    
                    // retrieve Item nos from Package as string and print to console
                    System.out.println(aPackage.getItemNos());
                }
                catch (ConstraintException ce)
                {
                    // In case of constraint violation display error message and continue from next testCase (++testCaseCount) until all testCases are packaged.
                    System.out.println(ce.getMessage());
                }
                packageCount++;
            }
            // if all testCases have been packaged exit while loop by setting completed to true
            isCompleted = true;
        }
    }
    
    public static int getPackageCount()
    {
        return packageCount;
    }
}
