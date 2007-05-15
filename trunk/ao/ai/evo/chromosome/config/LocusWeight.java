package ao.ai.evo.chromosome.config;

import ao.util.rand.Rand;

/**
 *
 */
public class LocusWeight implements Comparable<LocusWeight>
{
    //--------------------------------------------------------------------
    public static LocusWeight UNUSABLE =
            new LocusWeight(-Double.MAX_VALUE);

    //--------------------------------------------------------------------
    public static LocusWeight randomized(double inRange)
    {
        return new LocusWeight(Rand.nextDouble( inRange ));
    }


    //--------------------------------------------------------------------
    private final double VALUE;


    //--------------------------------------------------------------------
    public LocusWeight(double value)
    {
        VALUE = value;
    }

    //--------------------------------------------------------------------
    public boolean isUnusable()
    {
        return this == UNUSABLE;
    }

    public boolean isUsable()
    {
        return this != UNUSABLE;
    }


    //--------------------------------------------------------------------
    public int compareTo(LocusWeight o)
    {
        return Double.compare(VALUE, o.VALUE);
    }

    //--------------------------------------------------------------------
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocusWeight that = (LocusWeight) o;
        return Double.compare(that.VALUE, VALUE) == 0;
    }

    @Override
    public int hashCode()
    {
        long temp = VALUE != +0.0d ? Double.doubleToLongBits(VALUE) : 0L;
        return (int) (temp ^ (temp >>> 32));
    }
}
