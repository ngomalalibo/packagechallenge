package exception;

/**
 * A subclass of RuntimeException that is used to catch constraint violations during runtime and return a descriptive message to the user.
 */
public class ConstraintException extends RuntimeException
{
    public ConstraintException()
    {
        super();
    }
    
    public ConstraintException(String message)
    {
        super(message);
    }
}
