package ao.graph.impl.linked.index;

import ao.graph.user.NodeData;
import ao.graph.user.EdgeWeight;
import ao.graph.impl.linked.chain.MultiNode;

/**
 * I should put together more organaized docs.
 *
 * Used in the LinkedGraph implemintation of the Graph interface.
 * The LinkedGraph contains a chain of quadruply linked MultiNodes.
 * By quadrupaly linked I mean:
 *  The chain is sorted in two distinct orders        2
 *  In each order it is doubly linked               x 2
 *  The nodes are quadruply linked                  = 4
 *
 * The ordered pair (major_index, minor_index) represents a MultiNode
 * holding two IndexedNodes.
 *
 * The ordered tuple
 *  (major_index|minor_index_1, minor_index_2, minor_index_n)
 * represents a incidentEdges of (major_index, minor_index) pairs in which
 * the major_index is equal.
 * The interface ChainSpan represents this in code.
 *
 * To find all the nodes that join to some data X:
 *  Iterate over the minor_indexes of ChainSpans with majorEndpoint label
 *  equal to X.label().
 */
public interface ChainSpan<N extends NodeData<N>, A extends EdgeWeight<A>>
{
    int size();

    boolean majorIndexEquals( int index );

    MultiNode<N, A> firstOfSpan();

    MultiNode<N, A> lastOfSpan();

    boolean add(MultiNode<N, A> multiNode);

    void remove(MultiNode<N, A> multiNode);
}
