package ao.graph.impl.fast;

import ao.graph.impl.common.EdgeWeights;
import ao.graph.impl.common.struct.EndpointsImpl;
import ao.graph.struct.Endpoints;
import ao.graph.user.NodeData;
import ao.graph.user.EdgeWeight;

/**
 * A HexEdge is composed of two user given Nodes and the user given
 *      EdgeWeight that connects them:
 *  LO_NODE is the lesser (by label) of the two connected nodes
 *  HI_NODE is the greater (by label) of the two connected nodes
 *  weight is their connection
 *
 * When comparing HexEdges there are two options:
 *  (a) First compare by LO_NODE then by HI_NODE. This is called loHi order.
 *  (b) First compare by HI_NODE then by LO_NODE. This is called hiLo order.
 *
 * Just like in a linked list, the MultiNode has a concept of a next and
 *     previous MultiNode.
 * The interesting thing is that there are two types of next and
 *      previous: loHi and hiLo.
 * The MultiNode chain as a whole is always sorted in BOTH of those orders.
 *
 */
public class HexEdge<D extends NodeData<D>, W extends EdgeWeight<W>>
{
    //--------------------------------------------------------------------
    private FastNode<D, W> loEndpoint;
    private FastNode<D, W> hiEndpoint;
    private W              weight;

    private HexEdge<D, W> hiLoPrev;
    private HexEdge<D, W> hiLoNext;

    private HexEdge<D, W> loHiPrev;
    private HexEdge<D, W> loHiNext;

    private HexEdge<D, W> byWeightPrev;
    private HexEdge<D, W> byWeightNext;


    //--------------------------------------------------------------------
    /**
     * @param lesserNode ...
     * @param greaterNode ...
     * @param wedgeWeight ...
     */
    public HexEdge(FastNode<D, W> lesserNode,
                   FastNode<D, W> greaterNode,
                   W wedgeWeight)
    {
        assert lesserNode.label() < greaterNode.label();

        this.loEndpoint = lesserNode;
        this.hiEndpoint = greaterNode;
        this.weight     = wedgeWeight;
    }


    //--------------------------------------------------------------------
    // Accessors

    /**
     * @param loHiOrder ...
     * @return ...
     */
    public FastNode<D, W> minorEndpoint(boolean loHiOrder)
    {
        return (loHiOrder ? hiEndpoint : loEndpoint);
    }

    /**
     * @param loHiOrder ...
     * @return ...
     */
    public FastNode<D, W> majorEndpoint(boolean loHiOrder)
    {
        return (loHiOrder ? loEndpoint : hiEndpoint);
    }

    /**
     * @return ...
     */
    public W weight()
    {
        return weight;
    }

    /**
     * @param loHiOrder ...
     * @return ...
     */
    public HexEdge<D, W> previous(boolean loHiOrder)
    {
        return (loHiOrder ? loHiPrev : hiLoPrev);
    }

    /**
     * @param loHiOrder ...
     * @return ...
     */
    public HexEdge<D, W> next(boolean loHiOrder)
    {
        return (loHiOrder ? loHiNext : hiLoNext);
    }

    /**
     * @return ...
     */
    public HexEdge<D, W> byWeightPrevious()
    {
        return byWeightPrev;
    }

    /**
     * @return ...
     */
    public HexEdge<D, W> byWeightNext()
    {
        return byWeightNext;
    }


    //--------------------------------------------------------------------
    // Mutators
    // WARNING: THESE WILL CORRUPT DATA STRUCTURES UNLESS USED PROPERLY.
    
    /**
     * @param weight ...
     */
    public void mergeWeightWith(W weight)
    {
        this.weight = EdgeWeights.merge(this.weight, weight);
    }

    /**
     * @param major ...
     * @param loHiOrder ...
     */
    public void setMajorNode(FastNode<D, W> major, boolean loHiOrder)
    {
        if (loHiOrder)
        {
            if ( hiEndpoint.label() < major.label() )
            {
                loEndpoint = hiEndpoint;
                hiEndpoint = major;
            }
            else
            {
                loEndpoint = major;
            }
        }
        else
        {
            if ( loEndpoint.label() < major.label() )
            {
                hiEndpoint = major;
            }
            else
            {
                hiEndpoint = loEndpoint;
                loEndpoint = major;
            }
        }

        loHiPrev = null;
        loHiNext = null;
        hiLoPrev = null;
        hiLoNext = null;
    }

    /**
     * @param loHiOrder ...
     */
    public void removeFromSpan(boolean loHiOrder)
    {
        NodeIncidence<D, W> incidence =
                majorEndpoint(loHiOrder).incidentEdges(loHiOrder);

        if (incidence != null)
        {
            incidence.remove( this );
        }
    }


    //--------------------------------------------------------------------
    // For placing in a list of BidiNodes with equal edges
    /**
     * @param lastInList ...
     * @return ...
     */
    public HexEdge<D, W> removeFromEqualWeightLabelList(
            HexEdge<D, W> lastInList)
    {
        HexEdge<D, W> newLastInList = (lastInList == this
                                        ? byWeightPrev
                                        : lastInList);

        connectEqualByWeightLabel(byWeightPrev, byWeightNext);

        byWeightPrev = null;
        byWeightNext = null;

        return newLastInList;
    }

    /**
     * @param after ...
     */
    public void addToEqualWeightLabelList(HexEdge<D, W> after)
    {
        connectEqualByWeightLabel(after, this);
    }


    //--------------------------------------------------------------------
    // Misc methods

    /**
     * @return ...
     */
    public Endpoints<D, W> toEndpoints()
    {
        return EndpointsImpl.newInstance(
                loEndpoint.data(), hiEndpoint.data(), weight);
    }


    //--------------------------------------------------------------------
    // Object methods overwrides

    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || !(o instanceof HexEdge)) return false;

        HexEdge hexEdge = (HexEdge) o;

        return  hiEndpoint.equals(hexEdge.hiEndpoint) &&
                loEndpoint.equals(hexEdge.loEndpoint) &&
                (weight == null
                 ? hexEdge.weight == null
                 : weight.equals(hexEdge.weight));
    }

    public int hashCode()
    {
        int edgeHash = (weight != null ? weight.hashCode() : 0);

        return edgeHash +
                (27 * loEndpoint.hashCode()) +
                (31 * hiEndpoint.hashCode());
    }

    public String toString()
    {
        return "[" + loEndpoint + ", " + hiEndpoint + "]";
        //return loEndpoint + " |" + weight + "| " + hiEndpoint;
    }


    //--------------------------------------------------------------------
    // Used to manipulate linked a list of HexEdges with
    //  equal weight weight labels
    private static <ND extends NodeData<ND>, EW extends EdgeWeight<EW>>
            void connectEqualByWeightLabel(
                    HexEdge<ND, EW> hexEdgeA,
                    HexEdge<ND, EW> hexEdgeB)
    {
        if (hexEdgeA != null)
        {
            hexEdgeA.byWeightNext = hexEdgeB;
        }
        if (hexEdgeB != null)
        {
            hexEdgeB.byWeightPrev = hexEdgeA;
        }
    }

    // Used to manipulate NodeIncidence chain structure.
    /**
     * @param hexEdge ...
     * @param loHiPrev ...
     * @param loHiNext ...
     * @param hiLoPrev ...
     * @param hiLoNext ...
     */
    public static <ND extends NodeData<ND>, EW extends EdgeWeight<EW>>
            void placeIncidenceChain(
                    HexEdge<ND, EW> hexEdge,
                    HexEdge<ND, EW> loHiPrev, HexEdge<ND, EW> loHiNext,
                    HexEdge<ND, EW> hiLoPrev, HexEdge<ND, EW> hiLoNext)
    {
        connect(loHiPrev, hexEdge, loHiNext, true);
        connect(hiLoPrev, hexEdge, hiLoNext, false);
    }

    /**
     * @param lesser ...
     * @param mid ...
     * @param greater ...
     * @param loHiOrder ...
     */
    public static <ND extends NodeData<ND>, EW extends EdgeWeight<EW>>
            void connect(
                    HexEdge<ND, EW> lesser,
                    HexEdge<ND, EW> mid,
                    HexEdge<ND, EW> greater,
                    boolean loHiOrder)
    {
        connect(lesser, mid, loHiOrder);
        connect(mid, greater, loHiOrder);
    }

    /**
     * @param lesser ...
     * @param greater ...
     * @param loHiOrder ...
     */
    public static <ND extends NodeData<ND>, EW extends EdgeWeight<EW>>
            void connect(
                    HexEdge<ND, EW> lesser,
                    HexEdge<ND, EW> greater,
                    boolean loHiOrder)
    {
        if (loHiOrder)
            connectLoHiOrder( lesser, greater );
        else
            connectHiLoOrder( lesser, greater );
    }

    private static <ND extends NodeData<ND>, EW extends EdgeWeight<EW>>
            void connectLoHiOrder(
                    HexEdge<ND, EW> lesser,
                    HexEdge<ND, EW> greater)
    {
        if ( greater != null )
        {
            greater.loHiPrev = lesser;
        }

        if ( lesser != null )
        {
            lesser.loHiNext = greater;
        }
    }

    private static <ND extends NodeData<ND>, EW extends EdgeWeight<EW>>
            void connectHiLoOrder(
                    HexEdge<ND, EW> lesser,
                    HexEdge<ND, EW> greater)
    {
        if ( greater != null )
        {
            greater.hiLoPrev = lesser;
        }

        if ( lesser != null )
        {
            lesser.hiLoNext = greater;
        }
    }
}
