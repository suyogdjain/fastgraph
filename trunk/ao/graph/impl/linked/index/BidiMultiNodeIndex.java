package ao.graph.impl.linked.index;

import ao.graph.struct.NodeDataPair;
import ao.graph.struct.DataAndWeight;
import ao.graph.user.EdgeWeight;
import ao.graph.user.NodeData;
import ao.graph.impl.common.struct.LabeledNode;
import ao.graph.impl.linked.chain.MultiNodeChain;
import ao.graph.impl.linked.NodeSet;
import ao.graph.impl.linked.chain.MultiNode;
import ao.graph.impl.linked.merge.NodeMerger;

/**
 * Encapsulates two DirectedMultiNodeIndex objects: loHi and hiLo
 */
public class BidiMultiNodeIndex<N extends NodeData<N>, A extends EdgeWeight<A>>
{
    private final MultiNodeChain<N, A>         CHAIN;
    private final NodeSet<N>                   ALL_NODES;
    private final DirectedMultiNodeIndex<N, A> LO_HI_INDEX;
    private final DirectedMultiNodeIndex<N, A> HI_LO_INDEX;

    public BidiMultiNodeIndex(MultiNodeChain<N, A> chain, NodeSet<N> allNodes)
    {
        CHAIN       = chain;
        ALL_NODES   = allNodes;
        LO_HI_INDEX = new DirectedMultiNodeIndex<N, A>(chain, true);
        HI_LO_INDEX = new DirectedMultiNodeIndex<N, A>(chain, false);
    }

    // cannot satify exact Generics type because of some weird Generics Erasure issue
    @SuppressWarnings("unchecked")
    public NodeDataPair<N> unlinked()
    {
        return ALL_NODES.unlinked( ((DirectedMultiNodeIndex) HI_LO_INDEX).chainSpanIterator() );
    }

    /**
     * Adds the given data to the appropriate place in the MultiNode chain.
     *
     * @param node the data to additiveMerge.
     * @return true if the given data was previously absent.
     */
    public boolean addToChain(MultiNode<N, A> node)
    {
        boolean wasPreviouslyAbsent = LO_HI_INDEX.addToChain(node);

        if (wasPreviouslyAbsent)
        {
            HI_LO_INDEX.addToChain(node);
        }

        return wasPreviouslyAbsent;
    }

    public DataAndWeight<N, A> merge(
            LabeledNode<N> xparentA,
            LabeledNode<N> xparentB,
            A nullArch)
    {
        NodeMerger<N, A> merger =
                new NodeMerger<N, A>(
                        xparentA,
                        xparentB,
                        LO_HI_INDEX.removeSpan(xparentA, HI_LO_INDEX),
                        HI_LO_INDEX.removeSpan(xparentA, LO_HI_INDEX),
                        LO_HI_INDEX.removeSpan(xparentB, HI_LO_INDEX),
                        HI_LO_INDEX.removeSpan(xparentB, LO_HI_INDEX));

        return merger.merge(
                ALL_NODES, CHAIN, LO_HI_INDEX, HI_LO_INDEX, nullArch);
    }


    //-------------------------------------------------------------------------------
    public String toString()
    {
        return LO_HI_INDEX.toString() + "\n" + HI_LO_INDEX.toString();
    }
}
