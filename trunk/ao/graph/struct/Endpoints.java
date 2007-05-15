package ao.graph.struct;

import ao.graph.user.NodeData;
import ao.graph.user.EdgeWeight;

/**
 * Used like an immutable struct for returning Graph.merge().
 */
public interface Endpoints
        <D extends NodeData<D>, W extends EdgeWeight<W>>
{
    D nodeDataA();

    D nodeDataB();

    W weight();

    NodeDataPair<D> nodes();
}
