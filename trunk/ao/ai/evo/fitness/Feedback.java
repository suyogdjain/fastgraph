package ao.ai.evo.fitness;

/**
 *
 */
public class Feedback
{
    //--------------------------------------------------------------------
    private final double value;


    //--------------------------------------------------------------------
    public Feedback(double val)
    {
        value = val;
    }


    //--------------------------------------------------------------------
    public Fitness credit(int atDistance)
    {
        return new Fitness(
                    value / Math.sqrt(atDistance + 1));
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return String.valueOf( value );
    }
}
