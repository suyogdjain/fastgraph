package ao.ai.evo.fitness;

import ao.util.stats.Stats;

/**
 *
 */
public class Fitness implements Comparable<Fitness>
{
    //--------------------------------------------------------------------
    private final double cumulative;
    private final int    confidence;


    //--------------------------------------------------------------------
    public Fitness()
    {
        this(0, 0);
    }
    public Fitness(double value)
    {
        this(adjust(value), 1);
    }

    private Fitness(double val, int count)
    {
        cumulative = val;
        confidence = count;
    }


    //--------------------------------------------------------------------
    public int confidence()
    {
        return confidence;
    }


    //--------------------------------------------------------------------
    public Fitness cumulate(Fitness delta)
    {
        return new Fitness(cumulative + delta.cumulative,
                           confidence + delta.confidence);
    }

    public Fitness reduce()
    {
        return new Fitness((cumulative == 0)
                            ? -1
                            : (cumulative > 0)
                               ? cumulative / 2
                               : cumulative * 2,
                            confidence + 1);
    }


    //--------------------------------------------------------------------
    private double value()
    {
        return (confidence == 0)
                ? 0
                : Stats.accountForStatisticalError(
                            cumulative, confidence);
    }

    private static double adjust(double val)
    {
        return Math.signum(val) * Math.cbrt( Math.abs(val) + 1 );
    }


    //--------------------------------------------------------------------
    public int compareTo(Fitness o)
    {
        return Double.compare(value(), o.value());
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return String.valueOf( value() );
    }
}

