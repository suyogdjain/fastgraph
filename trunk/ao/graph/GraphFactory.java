package ao.graph;

import ao.graph.user.NodeData;
import ao.graph.user.EdgeWeight;

/**
 * Abstract factory for constructing Graphs
 */
public interface GraphFactory<N extends NodeData<N>, A extends EdgeWeight<A>>
{
    public Graph<N, A> newInstance();
    public Graph<N, A> newInstance(A defaultNullEdge);
}
