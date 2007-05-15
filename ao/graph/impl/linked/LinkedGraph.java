package ao.graph.impl.linked;

import ao.graph.Graph;
import ao.graph.impl.common.struct.LabeledNode;
import ao.graph.impl.linked.index.BidiMultiNodeIndex;
import ao.graph.impl.linked.chain.MultiNode;
import ao.graph.impl.linked.chain.MultiNodeChain;
import ao.graph.struct.DataAndWeight;
import ao.graph.struct.NodeDataPair;
import ao.graph.struct.Endpoints;
import ao.graph.user.EdgeWeight;
import ao.graph.user.NodeData;

/**
 * The main emphasis of this implementation is on performance.
 */
public class LinkedGraph<N extends NodeData<N>, A extends EdgeWeight<A>>
        implements Graph<N, A>
{
    private final MultiNodeChain<N, A>     CHAIN;
    private final NodeSet<N>               ALL_NODES;
    private final BidiMultiNodeIndex<N, A> BIDI_INDEX;
    private final A                        DEFAULT_NULL_ARCH;

    public LinkedGraph(A defaultNullArch)
    {
        ALL_NODES  = new NodeSet<N>();
        CHAIN      = new MultiNodeChain<N, A>(ALL_NODES);
        BIDI_INDEX = new BidiMultiNodeIndex<N, A>(CHAIN, ALL_NODES);

        DEFAULT_NULL_ARCH = defaultNullArch;
    }


    public int add(N node)
    {
        return ALL_NODES.index(node).label();
    }

    // worst case O(log n) time, where n is number of existing nodes.
    public boolean join(N nodeA, N nodeB, A arch)
    {
        if (arch == null ||
            nodeA == null || nodeB == null || nodeA.equals(nodeB)) return false;

        MultiNode<N, A> multiNode = new MultiNode<N, A>( nodeA, nodeB, arch, ALL_NODES );
        boolean wasAbsent = BIDI_INDEX.addToChain(multiNode);

        if (wasAbsent)
        {
            CHAIN.indexMultiNode( multiNode );
        }

        return wasAbsent;
    }

    /**
     * Worst case O(n) time, where n is the number of unique nodes
     *  linked to either nodeDataA or nodeDataB.
     * If sorting of archs is used worst case becomes O(n lg n).
     */
    public DataAndWeight<N, A> merge(N nodeA, N nodeB)
    {
        return merge( nodeA, nodeB, DEFAULT_NULL_ARCH );
    }

    public DataAndWeight<N, A> merge(N nodeA, N nodeB, A nullArch)
    {
        LabeledNode<N> labeledNodeA = ALL_NODES.index( nodeA );
        LabeledNode<N> labeledNodeB = ALL_NODES.index( nodeB );

        return (labeledNodeA != labeledNodeB)
                ? BIDI_INDEX.merge( labeledNodeA, labeledNodeB, nullArch )
                : null;
    }

    // worst case O(n) time, whre n is number of existing nodes.
    public NodeDataPair<N> antiEdge()
    {
        return BIDI_INDEX.unlinked();
    }

    // worst case O(log n) time, where n is number of (n1, n2) data links.
    public Endpoints<N, A> nodesIncidentHeaviestEdge()
    {
        return CHAIN.nodesIncidentHeaviestEdge();
    }

    public Endpoints<N, A> nodesIncidentLightestEdge() {
        return null;
    }


    //-----------------------------------------------------------------------------------
    public String toString()
    {
        return BIDI_INDEX.toString();
    }
}
