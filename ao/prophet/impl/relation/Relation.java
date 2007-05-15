package ao.prophet.impl.relation;

import ao.graph.user.EdgeWeight;
import ao.prophet.impl.Stats;

/**
 * How similar or disimilar items are.
 *
 * Note: objects of this class are immutable.
 */
public class Relation implements EdgeWeight<Relation>
{
    //--------------------------------------------------------------------
//    public static
//            <E extends EdgeWeight>
//            EdgeWeightDomain<E>
//                newDomain(int degree, Class<? extends EdgeWeight> edgeClass)
//    {
//        return new SimpleAbsDomain<E>(degree, 1.0f);
//    }


    //--------------------------------------------------------------------
    public static final Relation POSITIVE = new Relation( 1, 0, 0 );
    public static final Relation NEGATIVE = new Relation( 0, 1, 0 );
    public static final Relation MIXED    = new Relation( 0, 0, 1 );
    public static final Relation NEUTRAL  = new Relation( 0, 0, 0 );

    //--------------------------------------------------------------------
    private static final float POSITIVE_WEIGHT =  1.0f;
    private static final float NEGATIVE_WEIGHT =  0.5f;
    private static final float    MIXED_WEIGHT = -1.0f;


    //--------------------------------------------------------------------
    private final int    positiveCount;
    private final int    negativeCount;
    private final int    mixedCount;
    private final float weight;


    //--------------------------------------------------------------------
    private Relation(int positiveCount, int negativeCount, int mixedCount)
    {
        assert 0 <= positiveCount;
        assert 0 <= negativeCount;
        assert 0 <= mixedCount;

        this.positiveCount = positiveCount;
        this.negativeCount = negativeCount;
        this.mixedCount    = mixedCount;
        this.weight        = computeWeight();
    }


    //--------------------------------------------------------------------
    public Relation additiveMerge(Relation other)
    {
        return new Relation(
                positiveCount + other.positiveCount,
                negativeCount + other.negativeCount,
                mixedCount    + other.mixedCount);
    }


    //--------------------------------------------------------------------
    // For our purposes any information is better than no information.
    // Some non-zero theshold could be used to return new unprecedented
    //  clusters instead of ones known to have only a very week relation.
    public boolean isLighterThanUnrelated()
    {
        return false;
    }


    //--------------------------------------------------------------------
    public Relation mergeWith(Relation other)
    {
        return new Relation(
                mergeComponents(positiveCount, other.positiveCount),
                mergeComponents(negativeCount, other.negativeCount),
                mergeComponents(mixedCount,    other.mixedCount   ));
    }

    // average rounding up
    private int mergeComponents(int countA, int countB)
    {
        return (countA + countB + 1) >> 1;
    }


    //--------------------------------------------------------------------
    // In the item asFloat distribution used to determin how much of the
    //  weight to pass up a ply.
    // Should be in (-1, 1).
    public float itemWeightScaleFactor()
    {
        return weight;
    }


    //--------------------------------------------------------------------
    // can be anything, this implemintation returns [0, 1)
    public float asFloat()
    {
        return weight;
    }

    private float computeWeight()
    {
        float sum = positiveCount * POSITIVE_WEIGHT +
                    negativeCount * NEGATIVE_WEIGHT +
                    mixedCount    * MIXED_WEIGHT;

        int count = positiveCount + negativeCount + mixedCount;

        if (sum == 0 || count == 0) return 0;

        float score = sum / maximumScore(count);
        return Stats.accountForStatisticalError(score, count);
    }

    private float maximumScore(int count)
    {
        return count * POSITIVE_WEIGHT;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        //return "[" + positiveCount + ", " +
        //             negativeCount + ", " +
        //             mixedCount    + "]";
        return "[" + asFloat() + "]";
    }

    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof Relation)) return false;

        Relation relation = (Relation) obj;

        return Double.compare(relation.weight, weight) == 0;
    }

    public int hashCode()
    {
        long temp = (weight != +0.0d)
                    ? Double.doubleToLongBits(weight) : 0L;
        return (int) (temp ^ (temp >>> 32));
    }
}
