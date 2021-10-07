package entity;

/**
 * Plain java object with getter and setter methods to hold instances of items. I also override the toString method to return all property names and values pairs
 */
public class Item
{
    // we use integer for the item no and double for both weight and cost to capture floating point values.
    private int no;
    private double weight;
    private double cost;
    
    public int getNo()
    {
        return no;
    }
    
    public void setNo(int no)
    {
        this.no = no;
    }
    
    public double getWeight()
    {
        return weight;
    }
    
    public void setWeight(double weight)
    {
        this.weight = weight;
    }
    
    public double getCost()
    {
        return cost;
    }
    
    public void setCost(double cost)
    {
        this.cost = cost;
    }
    
    @Override
    public String toString()
    {
        return "No: " + this.no + " Weight: " + this.weight + " Cost: " + this.cost;
    }
}
