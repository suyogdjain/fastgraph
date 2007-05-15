package ao.graph.impl.common.struct;

import ao.graph.user.NodeData;
import ao.graph.struct.NodeDataPair;

/**
 * Used like an immutable struct for returning Graph.antiEdge().
 */
public class NodeDataPairImpl<D extends NodeData<D>>
        implements NodeDataPair<D>
{
    //--------------------------------------------------------------------
    public static <ND extends NodeData<ND>>
            NodeDataPair<ND> newInstance(ND nodeA, ND nodeB)
    {
        return new NodeDataPairImpl<ND>( nodeA, nodeB );
    }


    //--------------------------------------------------------------------
    private final D DATA_A;
    private final D DATA_B;


    //--------------------------------------------------------------------
    public NodeDataPairImpl(D dataA, D dataB)
    {
        this.DATA_A = dataA;
        this.DATA_B = dataB;
    }


    //--------------------------------------------------------------------
    public D dataA()
    {
        return DATA_A;
    }

    public D dataB()
    {
        return DATA_B;
    }

    //--------------------------------------------------------------------
    public String toString()
    {
        return "(" + DATA_A + ", " + DATA_B + ")";
    }

    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || !(o instanceof NodeDataPair)) return false;

        final NodeDataPair that = (NodeDataPair) o;

        return (DATA_A.equals( that.dataA() ) ||
                       DATA_A.equals( that.dataB() )) &&
                (DATA_B.equals( that.dataA() ) ||
                        DATA_B.equals( that.dataB() ));
    }

    public int hashCode()
    {
        return 17 * (DATA_A.hashCode() + DATA_B.hashCode());
    }
}
