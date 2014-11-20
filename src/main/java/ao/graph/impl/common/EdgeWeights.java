package ao.graph.impl.common;

import ao.graph.user.EdgeWeight;

/**
 * Utility methods
 */
public class EdgeWeights
{
    //--------------------------------------------------------------------
    private EdgeWeights() {}


    //--------------------------------------------------------------------
    public static <W extends EdgeWeight<W>>
            W merge(W weightA, W weightB)
    {
        return (weightA == null
                ? weightB
                : (weightB == null
                   ? weightA
                   : weightA.mergeWith( weightB )));
    }

    public static <W extends EdgeWeight<W>>
            W merge(W weightA, W weightB, W nullWeight)
    {
        return (weightA == null
                ? merge(weightB, nullWeight)
                : (weightB == null
                   ? merge(weightA, nullWeight)
                   : weightA.mergeWith( weightB )));
    }
}
