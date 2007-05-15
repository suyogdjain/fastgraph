package ao.graph.impl.fast;

import ao.graph.user.EdgeWeight;
import ao.graph.user.NodeData;

/**
 * Sorted set of edges that are incident to a node.
 * Each node contains two NodeIncidences:
 *  - one defined with loHi order (LO_HI_ORDER == true), and
 *  - one defined with hiLo order (LO_HI_ORDER == false).
 * The majorEndpoint(LO_HI_ORDER) nodes of all edges in a
 *  NodeIncidence are equal.
 * Nodes in a NodeIncidence are sorted in ascending
 *  minorEndpoint(LO_HI_ORDER).label() order.
 *
 * Two NodeIncidence are needed for each Node, loHi and hiLo.
 * For example, given the edges:
 * (d, f) (a, b) (a, c) (b, c) (b, d) (c, f) (a, f)
 *  where (x, y) means an weight is connecting Node x to y.
 *
 * In loHi order, they are grouped as:
 * [(a, b) (a, c) (a, f)] [(b, c) (b, d)] [(c, f)] [(d, f)]
 *  Each group is a NodeIncidence.
 *
 * Same edges grouped in hiLo order:
 * [(f, a) (f, c) (f, d)] [(d, b)] [(c, a) (c, b)] [(b, a)]
 */
public class NodeIncidence
        <D extends NodeData<D>, W extends EdgeWeight<W>>
{
    //--------------------------------------------------------------------
    private final FastGraph<D, W>     FAST_GRAPH;
    private final ByWeightIndex<D, W> BY_WEIGHT_INDEX;
    private final boolean             LO_HI_ORDER;

    // number of HexEdges in set
    private int degree;

    // domain bounds.
    // all edges must be sorted and between first and last.
    // dynamically updated as edges are added/removed.
    private HexEdge<D, W> first;
    private HexEdge<D, W> last;


    //--------------------------------------------------------------------
    /**
     * @param graph ...
     * @param byWeightIndex ...
     * @param loHiOrder ...
     */
    public NodeIncidence(
            FastGraph<D, W> graph,
            ByWeightIndex<D, W> byWeightIndex,
            boolean loHiOrder)
    {
        this(graph, byWeightIndex, null, null, 0, loHiOrder);
    }
    
    /**
     * @param graph ...
     * @param byWeightIndex ...
     * @param first ...
     * @param last ...
     * @param count ...
     * @param loHiOrder ...
     */
    public NodeIncidence(
            FastGraph<D, W> graph, ByWeightIndex<D, W> byWeightIndex,
            HexEdge<D, W> first, HexEdge<D, W> last,
            int count, boolean loHiOrder)
    {
        assert !(first != null ^ last != null);

        FAST_GRAPH    = graph;
        BY_WEIGHT_INDEX = byWeightIndex;
        LO_HI_ORDER   = loHiOrder;

        degree = count;
        this.first = first;
        this.last = last;
    }


    //--------------------------------------------------------------------
    /**
     * @return ...
     */
    public HexEdge<D, W> first()
    {
        return first;
    }

    /**
     * @return ...
     */
    public HexEdge<D, W> last()
    {
        return last;
    }

    /**
     * @return ...
     */
    public int degree()
    {
        return degree;
    }


    //--------------------------------------------------------------------
    /**
     * Adds the given hexEdge to this incidence list view.
     *
     * Precondition:
     *  The hexEdge.majorEndpoint( LO_HI_ORDER ) must equal
     *      the HexEdge.majorEndpoint of all other edges already
     *      in the in the NodeIncidence.
     *
     * If there is no other weight in the NodeIncidence
     *          with the same HexEdge.minorEndpoint( LO_HI_ORDER ):
     *     The weight will be inserted in the right place to keep
     *         the NodeIncidence's weight set in
     *         sorted HexEdge.minorEndpoint( LO_HI_ORDER ) order.
     * Else:
     *     The existing weight has its weight merged with the
     *         hexEdge being added:
     *             BY_WEIGHT_INDEX.merge(
     *                 existingEdge, hexEdge.weight());
     *
     * @param hexEdge addend
     * @return true if there is no other weight in the NodeIncidence
     *          with the same HexEdge.minorEndpoint( LO_HI_ORDER )
     */
    public boolean add(HexEdge<D, W> hexEdge)
    {
        assert first == null ||
                hexEdge.majorEndpoint(LO_HI_ORDER).label() ==
                    first.majorEndpoint(LO_HI_ORDER).label();

        if (first == null)
        {
            return addInitial(hexEdge);
        }

        FastNode<D, W> minorNode  = hexEdge.minorEndpoint( LO_HI_ORDER );
        int            minorIndex = minorNode.label();

        boolean wasPreviouslyAbsent =
                isBeyondBounds(minorIndex)
                    ? addBeyondBounds(hexEdge, minorIndex)
                    : boundEquals(minorNode)
                        ? mergeWithBound(hexEdge, minorNode)
                        : addStrictlyWithinBounds(hexEdge, minorIndex);

        if (wasPreviouslyAbsent) degree++;

        return wasPreviouslyAbsent;
    }

    // optimization for merging/populating.
    /**
     * @param hexEdge ...
     */
    public void addGreatest(HexEdge<D, W> hexEdge)
    {
        if (last == null)
        {
            addInitial(hexEdge);
        }
        else
        {
            HexEdge.connect(last, hexEdge, LO_HI_ORDER);
            last = hexEdge;
            degree++;
        }
    }
    
    // optimization for population
    /**
     * @param hexEdge ...
     */
    public void addLeast(HexEdge<D, W> hexEdge)
    {
        if (first == null)
        {
            addInitial(hexEdge);
        }
        else
        {
            HexEdge.connect(hexEdge, first, LO_HI_ORDER);
            first = hexEdge;
            degree++;
        }
    }

    private boolean addInitial(HexEdge<D, W> hexEdge)
    {
        first  = hexEdge;
        last   = hexEdge;
        degree = 1;

        return true;
    }

    private boolean mergeWithBound(
            HexEdge<D, W> hexEdge, FastNode<D, W> minorNode)
    {
        HexEdge<D, W> edgeToMergeWith =
                (first.minorEndpoint(LO_HI_ORDER) == minorNode)
                ? first
                : last;

        BY_WEIGHT_INDEX.merge( edgeToMergeWith, hexEdge.weight() );

        return false;
    }

    private boolean addBeyondBounds(
            HexEdge<D, W> hexEdge, int minorIndex)
    {
        HexEdge<D, W> prev, next;

        if (last.minorEndpoint(LO_HI_ORDER).label() < minorIndex)
        {
            prev       = last;
            //next       = last.next(LO_HI_ORDER);
            next       = null;
            last = hexEdge;
        }
        else /* if (first.minorEndpoint(LO_HI_ORDER).label()
                        > minorIndex) */
        {
            //prev        = first.previous(LO_HI_ORDER);
            prev        = null;
            next        = first;
            first = hexEdge;
        }

        HexEdge.connect(prev, hexEdge, next, LO_HI_ORDER);
        return true;
    }

    private boolean addStrictlyWithinBounds(
            HexEdge<D, W> hexEdge, int minorIndex)
    {
        HexEdge<D, W> connected = retrieveOrCreate(hexEdge, minorIndex);

        boolean wasPreviouslyAbsent = (connected == hexEdge);

        if (! wasPreviouslyAbsent)
        {
            BY_WEIGHT_INDEX.merge(connected, hexEdge.weight());
        }

        return wasPreviouslyAbsent;
    }

    // strictly between edges
    private HexEdge<D, W> retrieveOrCreate(
            HexEdge<D, W> hexEdge, int minorIndex)
    {
        int distanceFromLeft  =
                minorIndex - first.minorEndpoint(LO_HI_ORDER).label();
        int distanceFromRight =
                last.minorEndpoint(LO_HI_ORDER).label() - minorIndex;

        return (distanceFromLeft < distanceFromRight)
                ? retrieveOrCreateFromLeft(hexEdge, minorIndex)
                : retrieveOrCreateFromRight(hexEdge, minorIndex);
    }

    private HexEdge<D, W> retrieveOrCreateFromLeft(
            HexEdge<D, W> hexEdge, int minorIndex)
    {
        HexEdge<D, W> insertBefore = last;

        for (HexEdge<D, W> cursor = first.next(LO_HI_ORDER);
             cursor != last;
             cursor = cursor.next(LO_HI_ORDER))
        {
            int cursorMinorIndex =
                    cursor.minorEndpoint(LO_HI_ORDER).label();

            if (minorIndex == cursorMinorIndex)
            {
                return cursor;
            }
            else if (cursorMinorIndex > minorIndex)
            {
                insertBefore = cursor;
                break;
            }
        }

        HexEdge.connect(
                insertBefore.previous(LO_HI_ORDER),
                hexEdge,
                insertBefore,
                LO_HI_ORDER);

        return hexEdge;
    }

    private HexEdge<D, W> retrieveOrCreateFromRight(
            HexEdge<D, W> hexEdge, int minorIndex)
    {
        HexEdge<D, W> insertAfter = first;

        for (HexEdge<D, W> cursor = last.previous(LO_HI_ORDER);
             cursor != first;
             cursor = cursor.previous(LO_HI_ORDER))
        {
            int cursorMinorIndex =
                    cursor.minorEndpoint(LO_HI_ORDER).label();

            if (minorIndex == cursorMinorIndex)
            {
                return cursor;
            }
            else if (cursorMinorIndex < minorIndex)
            {
                insertAfter = cursor;
                break;
            }
        }

        HexEdge.connect(
                insertAfter,
                hexEdge,
                insertAfter.next(LO_HI_ORDER),
                LO_HI_ORDER);

        return hexEdge;
    }


    //--------------------------------------------------------------------
    /**
     * @param hexEdge ...
     */
    public void remove(HexEdge<D, W> hexEdge)
    {
        FastNode<D, W> minorNode = hexEdge.minorEndpoint( LO_HI_ORDER );

        assert hexEdge.majorEndpoint(LO_HI_ORDER).label() ==
                first.majorEndpoint(LO_HI_ORDER).label();
        assert !isBeyondBounds( minorNode.label() );

        if (boundEquals(minorNode))
            removeAtEdge(minorNode);
        else
            removeStrictlyBetweenEdges(hexEdge);

        degree--;
    }

    private void removeAtEdge(FastNode<D, W> minorNode)
    {
        HexEdge<D, W> prev, next;

        if (first.minorEndpoint(LO_HI_ORDER) == minorNode)
        {
            if (last.minorEndpoint(LO_HI_ORDER) == minorNode)
            {
                FAST_GRAPH.removeIncidenceOf(
                        last.majorEndpoint(LO_HI_ORDER).data(),
                        LO_HI_ORDER );

                prev = first.previous(LO_HI_ORDER);
                next = last.next(LO_HI_ORDER);
            }
            else
            {
                prev = first.previous(LO_HI_ORDER);
                next = first.next(LO_HI_ORDER);

                first = next;
            }
        }
        else /*if (last.minorEndpoint(LO_HI_ORDER).label() == minorIndex)*/
        {
            prev = last.previous(LO_HI_ORDER);
            next = last.next(LO_HI_ORDER);

            last = prev;
        }

        HexEdge.connect(prev, null, next, LO_HI_ORDER);
    }

    private void removeStrictlyBetweenEdges(HexEdge<D, W> multiNode)
    {
        HexEdge.connect(
                multiNode.previous(LO_HI_ORDER),
                multiNode.next(LO_HI_ORDER),
                LO_HI_ORDER);
    }

    //--------------------------------------------------------------------
    /**
     * @return ...
     */
    public IncidenceErrasor<D, W> errase()
    {
        return new IncidenceErrasor<D, W>(first, last, LO_HI_ORDER );
    }


    //--------------------------------------------------------------------
    private boolean boundEquals(FastNode<D, W> minorNode)
    {
        return first.minorEndpoint(LO_HI_ORDER) == minorNode ||
                last.minorEndpoint(LO_HI_ORDER) == minorNode;
    }

    private boolean isBeyondBounds(int minorIndex)
    {
        return minorIndex < first.minorEndpoint(LO_HI_ORDER).label() ||
                minorIndex > last.minorEndpoint(LO_HI_ORDER).label();
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        if (degree == 0) return "()";

        StringBuilder sb = new StringBuilder();

        sb.append('(');
        sb.append(first().majorEndpoint(LO_HI_ORDER).label());
        sb.append('|');

        for (HexEdge<D, W> cursor = first();
             cursor != last();
             cursor  = cursor.next(LO_HI_ORDER))
        {
            sb.append(cursor.minorEndpoint(LO_HI_ORDER).label());
            sb.append(", ");
        }

        sb.append(last().minorEndpoint(LO_HI_ORDER).label());
        sb.append(')');

        return sb.toString();
    }
}

