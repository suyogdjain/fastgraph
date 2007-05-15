package ao.graph.impl.linked.chain;

import ao.graph.user.EdgeWeight;
import ao.graph.user.NodeData;
import ao.graph.impl.common.struct.EndpointsImpl;
import ao.graph.impl.common.struct.LabeledNode;
import ao.graph.impl.linked.NodeSet;
import ao.graph.struct.Endpoints;

/**
 * E MultiNode is a single join in the chain. The MultiNode chain as a whole
 * represents every pair of Nodes that are connected by an EdgeWeight in some LinkedGraph.
 *
 * E MultiNode is composed of two user given Nodes and the user given EdgeWeight that connects them:
 *  LO_NODE is the lesser (by label) of the two connected nodes
 *  HI_NODE is the greater (by label) of the two connected nodes
 *  weight is their connection
 *
 * When comparing MultiNodes there are two options:
 *  (a) First compare by LO_NODE then by HI_NODE. This is called loHi order.
 *  (b) First compare by HI_NODE then by LO_NODE. This is called hiLo order.
 *
 * Just like in a linked list, the MultiNode has a concept of a next and previous MultiNode.
 * The interesting thing is that there are two types of next and previous: loHi and hiLo.
 * The MultiNode chain as a whole is always sorted in BOTH of those orders.
 */
public class MultiNode<N extends NodeData<N>, E extends EdgeWeight<E>>
{
    //-----------------------------------------------------------------------------------
    // Instance methods

    private final LabeledNode<N> LO_NODE;
    private final LabeledNode<N> HI_NODE;
    private       E              arch;

    private MultiNode<N, E> hiLoPrev;
    private MultiNode<N, E> hiLoNext;

    private MultiNode<N, E> loHiPrev;
    private MultiNode<N, E> loHiNext;

    public MultiNode(N nodeA, N nodeB, E connectingArch, NodeSet<N> allNodes)
    {
        LabeledNode<N> indexA = allNodes.index( nodeA );
        LabeledNode<N> indexB = allNodes.index( nodeB );

        assert indexA.label() != indexB.label();

        LO_NODE = LabeledNode.lesserOf(indexA, indexB);
        HI_NODE = LabeledNode.greaterOf(indexA, indexB);
        arch    = connectingArch;
    }

    public MultiNode(LabeledNode<N> lesserNode,
                     LabeledNode<N> greaterNode,
                     E connectingArch)
    {
        assert lesserNode.label() < greaterNode.label();

        this.LO_NODE = lesserNode;
        this.HI_NODE = greaterNode;
        this.arch    = connectingArch;
    }


    //-----------------------------------------------------------------------------------
    // Accessors

    public LabeledNode<N> minor(boolean loHiOrder)
    {
        return (loHiOrder ? HI_NODE : LO_NODE);
    }

    public LabeledNode<N> major(boolean loHiOrder)
    {
        return (loHiOrder ? LO_NODE : HI_NODE);
    }

    public E edge()
    {
        return arch;
    }

    public MultiNode<N, E> previous(boolean loHiOrder)
    {
        return (loHiOrder ? loHiPrev : hiLoPrev);
    }

    public MultiNode<N, E> next(boolean loHiOrder)
    {
        return (loHiOrder ? loHiNext : hiLoNext);
    }


    //-----------------------------------------------------------------------------------
    // Misc methods

    public Endpoints<N, E> toIncidentNodes()
    {
        return EndpointsImpl.newInstance(
                LO_NODE.node(), HI_NODE.node(), arch);
    }

    // Dont worry about EdgeWeight indexing.
    public /*MultiNode<N, E>*/ void mergeArchWith(E arch)
    {
        this.arch = this.arch.mergeWith( arch );

        //MultiNode<N, E> merged =
        //       new MultiNode<N, E>(LO_NODE, HI_NODE, ARCH.mergeWith(weight));
        //placeIncidenceChain(merged, loHiPrev, loHiNext, hiLoPrev, hiLoNext);
        //return merged;
    }

    //-----------------------------------------------------------------------------------
    // Object methods overwrides

    public String toString()
    {
        //return "[" + HI_NODE + ", " + LO_NODE + "]";
        return HI_NODE + " |" + arch + "| " + LO_NODE;
    }


    //-----------------------------------------------------------------------------------
    // Convenient view interface
//    public class Directed
//    {
//        private final boolean LO_HI_ORDER;
//        private Directed(boolean loHiOrder)
//        {
//            this.LO_HI_ORDER = loHiOrder;
//        }
//
//        public N majorEndpoint()
//        {
//            return MultiNode.this.majorEndpoint( LO_HI_ORDER );
//        }
//
//        public int majorIndex()
//        {
//            return MultiNode.this.majorIndex( LO_HI_ORDER );
//        }
//
//        public N minorEndpoint()
//        {
//            return MultiNode.this.minorEndpoint( LO_HI_ORDER );
//        }
//
//        public int minorIndex()
//        {
//            return MultiNode.this.minorIndex( LO_HI_ORDER );
//        }
//
//        public E weight()
//        {
//            return MultiNode.this.weight();
//        }
//    }

    //---------------------------------------------------------------------------------------------------
    // Used to manipulate MultiNode chain structure.
    public static <N extends NodeData<N>, A extends EdgeWeight<A>>
            void placeInChain(
                    MultiNode<N, A> multiNode,
                    MultiNode<N, A> loHiPrev, MultiNode<N, A> loHiNext,
                    MultiNode<N, A> hiLoPrev, MultiNode<N, A> hiLoNext)
    {
        insertBetween(loHiPrev, multiNode, loHiNext, true);
        insertBetween(hiLoPrev, multiNode, hiLoNext, false);
    }

    public static <N extends NodeData<N>, A extends EdgeWeight<A>>
            void insertBetween(
                    MultiNode<N, A> previouse,
                    MultiNode<N, A> toInsert,
                    MultiNode<N, A> next,
                    boolean loHiOrder)
    {
        connect(toInsert, next, loHiOrder);
        connect(previouse, toInsert, loHiOrder);
    }

    public static <N extends NodeData<N>, A extends EdgeWeight<A>>
            void connect( MultiNode<N, A> lesser, MultiNode<N, A> greater, boolean loHiOrder )
    {
        if (loHiOrder)
            connectLoHiOrder( lesser, greater );
        else
            connectHiLoOrder( lesser, greater );
    }

    private static <N extends NodeData<N>, E extends EdgeWeight<E>>
            void connectLoHiOrder(
                    MultiNode<N, E> lesser,
                    MultiNode<N, E> greater )
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

    private static <N extends NodeData<N>, E extends EdgeWeight<E>>
            void connectHiLoOrder(
                    MultiNode<N, E> lesser,
                    MultiNode<N, E> greater )
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
