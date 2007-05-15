package ao.graph.impl.linked.merge;

import ao.graph.user.NodeData;
import ao.graph.user.EdgeWeight;
import ao.graph.impl.common.struct.LabeledNode;
import ao.graph.impl.linked.index.DirectedMultiNodeIndex;
import ao.graph.impl.linked.chain.MultiNode;
import ao.graph.impl.linked.chain.MultiNodeChain;

/**
 * Allows iteration over a MiltiNode incidentEdges.
 */
public class NodeSpanErrasor<N extends NodeData<N>, A extends EdgeWeight<A>>
        implements Comparable<NodeSpanErrasor<N, A>>
{
    private MultiNode<N, A> end;
    private MultiNode<N, A> current;

    private final DirectedMultiNodeIndex<N, A> OTHER_INDEX;
    private final MultiNodeChain<N, A>         ALL_NODES;
    private final boolean                      LO_HI_ORDER;

    public NodeSpanErrasor(
            MultiNode<N, A> from,
            MultiNode<N, A> to,
            DirectedMultiNodeIndex<N, A> otherIndex,
            MultiNodeChain<N, A> allNodes,
            boolean loHiOrder)
    {
        this.end     = to;
        this.current = from;

        this.ALL_NODES   = allNodes;
        this.OTHER_INDEX = otherIndex;
        this.LO_HI_ORDER = loHiOrder;

        MultiNode.connect(
                from.previous(loHiOrder),
                to.next(loHiOrder),
                LO_HI_ORDER);
    }


    //-------------------------------------------------------------------------
    public MultiNode<N, A> current()
    {
        return current;
    }
    public LabeledNode<N> currentMajor()
    {
        return current.major( LO_HI_ORDER );
    }

    public LabeledNode<N> currentMinor()
    {
        return current.minor( LO_HI_ORDER );
    }

    public A currentArch()
    {
        return current.edge();
    }


    //-------------------------------------------------------------------------
    public boolean advance()
    {
        if (current == null) return false;

        current =
                (current == end)
                ? null
                : current.next( LO_HI_ORDER );

        return (current != null);

    }

    public void erraseCurrent()
    {
        if (current == null) return;

        OTHER_INDEX.removeFromChain(current);
        ALL_NODES.remove(current);
    }

    public void shiftEndIfEquals( MultiNode<N, A> multiNode )
    {
        if (end != null && end == multiNode)
        {
            end = end.previous( LO_HI_ORDER );

//            end = (current == end)
//                    ? null
//                    : end.previous( LO_HI_ORDER );
        }
    }


    public boolean minorEquals( MultiNode<N, A> multiNode )
    {
        return currentMinor() == multiNode.minor( LO_HI_ORDER );
    }

    public int compareTo(NodeSpanErrasor<N, A> that)
    {
        return currentMinor().compareTo(
                that.currentMinor());
    }
}
