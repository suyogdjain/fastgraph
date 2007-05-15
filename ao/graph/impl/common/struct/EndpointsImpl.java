package ao.graph.impl.common.struct;

import ao.graph.struct.Endpoints;
import ao.graph.struct.NodeDataPair;
import ao.graph.user.EdgeWeight;
import ao.graph.user.NodeData;

/**
 * Used like an immutable struct for returning Graph.merge().
 */
public class EndpointsImpl
        <D extends NodeData<D>, W extends EdgeWeight<W>>
        implements Endpoints<D, W>
{
    //--------------------------------------------------------------------
    public static <ND extends NodeData<ND>, EW extends EdgeWeight<EW>>
            Endpoints<ND, EW> newInstance(ND nodeA, ND nodeB, EW edge)
    {
        return new EndpointsImpl<ND, EW>(nodeA, nodeB, edge);
    }


    //--------------------------------------------------------------------
    private final D DATA_A;
    private final D DATA_B;
    private final W WEIGHT;


    //--------------------------------------------------------------------
    private EndpointsImpl(D dataA, D dataB, W weight)
    {
        DATA_A = dataA;
        DATA_B = dataB;
        WEIGHT = weight;
    }


    //--------------------------------------------------------------------
    // accessors

    public D nodeDataA()
    {
        return DATA_A;
    }

    public D nodeDataB()
    {
        return DATA_B;
    }

    public W weight()
    {
        return WEIGHT;
    }

    public NodeDataPair<D> nodes() {
        return new NodeDataPairImpl<D>(DATA_A, DATA_B);
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return DATA_A + " |" + WEIGHT + "| " + DATA_B;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || !(o instanceof Endpoints)) return false;

        final Endpoints that = (Endpoints) o;

        return WEIGHT.equals(that.weight()) &&
                (DATA_A.equals(that.nodeDataA()) ||
                        DATA_A.equals(that.nodeDataB())) &&
                (DATA_B.equals(that.nodeDataA()) ||
                        DATA_B.equals(that.nodeDataB()));
    }
    
    @Override
    public int hashCode()
    {
        int edgeHash    = WEIGHT.hashCode();
        int nodeHashSum = DATA_A.hashCode() + DATA_B.hashCode();

        return edgeHash + 29 * nodeHashSum;
    }
}
