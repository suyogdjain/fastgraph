package ao.graph.common;

import ao.graph.user.EdgeWeight;

/**
 * very sinmple stuff
 */
public class RealEdgeWeight implements EdgeWeight<RealEdgeWeight>
{
    private final float WEIGHT;

    public RealEdgeWeight(float weight)
    {
        this.WEIGHT = weight;
    }

    public RealEdgeWeight mergeWith(RealEdgeWeight other)
    {
        return new RealEdgeWeight((WEIGHT + other.WEIGHT) / 2.0f);
    }

    public float asFloat()
    {
        return WEIGHT;
    }

    //-----------------------------------------------------------------------------------
    @Override
    public boolean equals(Object obj)
    {
        if (! (obj instanceof RealEdgeWeight)) return false;

        RealEdgeWeight that = (RealEdgeWeight) obj;

        return Float.compare(WEIGHT, that.WEIGHT) == 0;
    }

    @Override
    public int hashCode()
    {
        return (WEIGHT != +0.0f
                ? Float.floatToIntBits(WEIGHT)
                : 0);
    }

    @Override
    public String toString()
    {
        return String.valueOf(WEIGHT);
    }
}
