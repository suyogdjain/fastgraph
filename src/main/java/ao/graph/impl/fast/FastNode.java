package ao.graph.impl.fast;

import ao.graph.user.NodeData;
import ao.graph.user.EdgeWeight;

/**
 * FastMap{Object => FastNode{ label, loHiIncidence, hiLoIncidence }}
 *
 * Used as a struct, not as a real OO object.
// * @param D
// * @param W
 */
public class FastNode<D extends NodeData<D>, W extends EdgeWeight<W>>
        implements Comparable<FastNode<D, W>>
{
    //--------------------------------------------------------------------
    private final D   DATA;
    private final int LABEL;

    private NodeIncidence<D, W> loHiIncidence;
    private NodeIncidence<D, W> hiLoIncidence;

    
    //--------------------------------------------------------------------
    /**
     * @param node ...
     * @param label ...
     */
    public FastNode(D node, int label)
    {
        DATA = node;
        LABEL = label;
    }

    
    //--------------------------------------------------------------------
    /**
     * @return ...
     */
    public D data()
    {
        return DATA;
    }

    /**
     * @return ...
     */
    public int label()
    {
        return LABEL;
    }

    /**
     * @return ...
     */
    public NodeIncidence<D, W> loHiIncidence()
    {
        return loHiIncidence;
    }

    /**
     * @return ...
     */
    public NodeIncidence<D, W> hiLoIncidence()
    {
        return hiLoIncidence;
    }

    /**
     * @param loHiOrder ...
     * @return ...
     */
    public NodeIncidence<D, W> incidentEdges(boolean loHiOrder)
    {
        return (loHiOrder ?
                loHiIncidence :
                hiLoIncidence);
    }


    //--------------------------------------------------------------------
    /**
     * @param graph ...
     * @param byWeightIndex ...
     * @param addend ...
     * @param loHiOrder ...
     * @return ...
     */
    public boolean addIncidentEdge(
            FastGraph<D, W> graph,
            ByWeightIndex<D, W> byWeightIndex,
            HexEdge<D, W> addend,
            boolean loHiOrder)
    {
        NodeIncidence<D, W> incidence =
                retrieveOrCreateIncidence(
                        graph, byWeightIndex, loHiOrder);

        return incidence.add( addend );
    }

    // optimization for merging
    /**
     * @param graph ...
     * @param byWeightIndex ...
     * @param first ...
     * @param last ...
     * @param count ...
     * @param hiLoOrder ...
     */
    public void addHiLoIncidence(
            FastGraph<D, W> graph, ByWeightIndex<D, W> byWeightIndex,
            HexEdge<D, W> first, HexEdge<D, W> last,
            int count,
            boolean hiLoOrder)
    {
        assert hiLoIncidence == null;
        hiLoIncidence =
                new NodeIncidence<D, W>(
                        graph, byWeightIndex,
                        first, last,
                        count,
                        hiLoOrder);
    }
    
    /**
     * @param graph ...
     * @param byWeightIndex ...
     * @param loHiOrder ...
     * @return ...
     */
    public NodeIncidence<D, W> retrieveOrCreateIncidence(
            FastGraph<D, W> graph,
            ByWeightIndex<D, W> byWeightIndex,
            boolean loHiOrder)
    {
        return (loHiOrder)
                ? retrieveOrCreateLoHiIncidence(graph, byWeightIndex)
                : retrieveOrCreateHiLoIncidence(graph, byWeightIndex);
    }
    
    /**
     * @param graph ...
     * @param byWeightIndex ...
     * @return ...
     */
    public NodeIncidence<D, W> retrieveOrCreateLoHiIncidence(
            FastGraph<D, W> graph,
            ByWeightIndex<D, W> byWeightIndex)
    {
        if (loHiIncidence == null)
        {
            loHiIncidence = new NodeIncidence<D, W>(
                                    graph, byWeightIndex, true);
        }

        return loHiIncidence;
    }
    
    /**
     * @param graph ...
     * @param byWeightIndex ...
     * @return ...
     */
    public NodeIncidence<D, W> retrieveOrCreateHiLoIncidence(
            FastGraph<D, W> graph,
            ByWeightIndex<D, W> byWeightIndex)
    {
        if (hiLoIncidence == null)
        {
            hiLoIncidence = new NodeIncidence<D, W>(
                                    graph, byWeightIndex, false);
        }

        return hiLoIncidence;
    }


    //--------------------------------------------------------------------
    /**
     * @param loHiOrder ...
     * @return ...
     */
    public IncidenceErrasor<D, W> erraseIncdence(boolean loHiOrder)
    {
        NodeIncidence<D, W> toRemove = incidentEdges( loHiOrder );

        if (toRemove == null) return null;

        if (loHiOrder)
        {
            loHiIncidence = null;
        }
        else
        {
            hiLoIncidence = null;
        }

        return toRemove.errase();
    }

    /**
     * @param loHiOrder ...
     */
    public void removeIncidence(boolean loHiOrder)
    {
        if (loHiOrder)
        {
            loHiIncidence = null;
        }
        else
        {
            hiLoIncidence = null;
        }
    }


    //--------------------------------------------------------------------
    public int compareTo(FastNode<D, W> o)
    {
        return (LABEL - o.LABEL);
    }

    @Override
    public boolean equals(Object o)
    {
        return o != null &&
                (o instanceof FastNode) &&
                LABEL == ((FastNode) o).LABEL;
    }

    @Override
    public int hashCode()
    {
        return LABEL;
    }

    @Override
    public String toString()
    {
        return String.valueOf(LABEL);
    }


    //--------------------------------------------------------------------
    /**
     * @param nodeA ...
     * @param nodeB ...
     * @return ...
     */
    public static <N extends NodeData<N>, A extends EdgeWeight<A>>
            FastNode<N, A> lesserOf(
                FastNode<N, A> nodeA,
                FastNode<N, A> nodeB)
    {
        assert nodeA != null && nodeB != null;
        assert ! nodeA.equals(nodeB);

        return (nodeA.LABEL < nodeB.LABEL)
                ? nodeA
                : nodeB;
    }

    /**
     * @param nodeA ...
     * @param nodeB ...
     * @return ...
     */
    public static <N extends NodeData<N>, A extends EdgeWeight<A>>
            FastNode<N, A> greaterOf(
                FastNode<N, A> nodeA,
                FastNode<N, A> nodeB)
    {
        assert nodeA != null && nodeB != null;
        assert ! nodeA.equals(nodeB);

        return (nodeA.LABEL > nodeB.LABEL)
                ? nodeA
                : nodeB;
    }
}

