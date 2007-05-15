package ao.graph.impl.common.struct;

import ao.graph.user.NodeData;
import ao.graph.user.EdgeWeight;
import ao.graph.struct.DataAndWeight;

/**
 * Used like an immutable struct for returning
 *  Graph.endpointsOfHeaviestEdge().
 */
public class DataAndWeightImpl
        <D extends NodeData<D>, W extends EdgeWeight<W>>
        implements DataAndWeight<D, W>
{
    //--------------------------------------------------------------------
    public static <ND extends NodeData<ND>, EW extends EdgeWeight<EW>>
            DataAndWeight<ND, EW> newInstance( ND node, EW edge )
    {
        return new DataAndWeightImpl<ND, EW>( node, edge );
    }


    //--------------------------------------------------------------------
    private final D DATA;
    private final W WEIGHT;

    public DataAndWeightImpl(D node, W edge)
    {
        this.DATA = node;
        this.WEIGHT = edge;
    }

    public D data()
    {
        return DATA;
    }

    public W weight()
    {
        return WEIGHT;
    }

    //--------------------------------------------------------------------
    public String toString()
    {
        return "data: " + DATA + ", weight: " + WEIGHT;
    }

    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || !(o instanceof DataAndWeight)) return false;

        DataAndWeight that = (DataAndWeight) o;

        return (WEIGHT == that.weight() ||
                       WEIGHT.equals(that.weight())) &&
                (DATA == that.data() ||
                        DATA.equals(that.data()));
    }

    public int hashCode()
    {
        int nodeHash = (DATA != null ? DATA.hashCode() : 0);
        int edgeHash = (WEIGHT != null ? WEIGHT.hashCode() : 0);

        return nodeHash + (31 * edgeHash);
    }
}
