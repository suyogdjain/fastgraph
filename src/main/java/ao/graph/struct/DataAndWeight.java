package ao.graph.struct;

import ao.graph.user.NodeData;
import ao.graph.user.EdgeWeight;

/**
 * Used like an immutable struct for
 *  returning Graph.endpointsOfHeaviestEdge().
 */
public interface DataAndWeight
        <N extends NodeData<N>, E extends EdgeWeight<E>>
{
    N data();

    E weight();
}
