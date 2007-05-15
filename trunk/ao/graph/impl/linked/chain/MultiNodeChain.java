package ao.graph.impl.linked.chain;

import ao.graph.impl.linked.NodeSet;
import ao.graph.impl.common.struct.LabeledNode;
import ao.graph.struct.Endpoints;
import ao.graph.user.EdgeWeight;
import ao.graph.user.NodeData;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * All multi nodes of a given chain are stored here.
 * Sorting by weight also takes place here.
 */
public class MultiNodeChain<N extends NodeData<N>, E extends EdgeWeight<E>>
{
    private final NodeSet<N> ALL_NODES;
    private final SortedSet<MultiNode<N, E>> ALL_MULTI_NODES;

    public MultiNodeChain(NodeSet<N> allNodes)
    {
        assert allNodes != null;

        ALL_NODES = allNodes;

        Comparator<MultiNode<N, E>> comparator = new Comparator<MultiNode<N, E>>()
        {
            public int compare(MultiNode<N, E> multiNodeA, MultiNode<N, E> multiNodeB)
            {
                // by weight
                int archCmp = Double.compare( multiNodeA.edge().asFloat(), multiNodeB.edge().asFloat() );
                if (archCmp != 0) return archCmp;

                // then by greater data
                int greaterNodeCmp = multiNodeA.major(true).compareTo(
                                        multiNodeB.major(true));
                if (greaterNodeCmp != 0) return greaterNodeCmp;

                // finally by lesser data
                return multiNodeA.minor(true).compareTo(
                        multiNodeB.minor(true));
            }
        };

        ALL_MULTI_NODES = new TreeSet<MultiNode<N, E>>(comparator);
    }

    /**
     * Creates a MultiNode, indexes it by weight, returns it.
     *
     * //@param nodesAndArch Modes and weight of the conscructed MultiNode
     * @return The constructed and indexed MultiNode
     * @param nodeA first endpoint
     * @param nodeB second endpoint
     * @param arch connecting weight
     */
    public MultiNode<N, E> createAndIndexMultiNode(N nodeA, N nodeB, E arch)
    {
        MultiNode<N, E> addend = new MultiNode<N, E>(nodeA, nodeB, arch, ALL_NODES);

        return indexMultiNode( addend );
    }

    public MultiNode<N, E> createAndIndexMultiNode(
            LabeledNode<N> lesserNode,
            LabeledNode<N> greaterNode,
            E arch)
    {
        MultiNode<N, E> addend = new MultiNode<N, E>(lesserNode, greaterNode, arch);

        return indexMultiNode( addend );
    }


    /**
     * Unindexes the existing MultiNode.
     * Merges the existing MultiNode with the given weight.
     * Indexes the newly mutated (by merging) MultiNode.
     * //Returns the merged MultiNode.
     *
     * @param multiNode Who's weight is to be merged with the given weight.
     * @param arch EdgeWeight to merge with.
     * //@return Merged and properly indexed MultiNode.
     */
    public void merge(MultiNode<N, E> multiNode, E arch)
    {
        unIndexMultiNode( multiNode );

        multiNode.mergeArchWith( arch );

        indexMultiNode( multiNode );
    }


    /**
     * Unindexes MultiNode
     * @param multiNode MultiNode to unindex.
     */
    public void remove(MultiNode<N, E> multiNode)
    {
        unIndexMultiNode( multiNode );
    }


    /**
     * Uses label to return the weight with the greatest (last in ascending order) weight.
     * @return Endpoints with the greatest EdgeWeight by natural Comparable ordering.
     */
    public Endpoints<N, E> nodesIncidentHeaviestEdge()
    {
        return (ALL_MULTI_NODES.isEmpty()
                ? null
                : ALL_MULTI_NODES.last().toIncidentNodes());
    }

    public Endpoints<N, E> nodesIncidentLightestEdge()
    {
        return (ALL_MULTI_NODES.isEmpty()
                ? null
                : ALL_MULTI_NODES.first().toIncidentNodes());
    }


    //------------------------------------------------------------------------
    public MultiNode<N, E> indexMultiNode(MultiNode<N, E> multiNode)
    {
        ALL_MULTI_NODES.add( multiNode );
        return multiNode;
    }

    private MultiNode<N, E> unIndexMultiNode(MultiNode<N, E> multiNode)
    {
        ALL_MULTI_NODES.remove( multiNode );
        return multiNode;
    }
}
