package ao.graph.struct;

import ao.graph.user.NodeData;

/**
 * Used like an immutable struct for returning Graph.antiEdge().
 */
public interface NodeDataPair<N extends NodeData<N>>
{
    N dataA();

    N dataB();
}
