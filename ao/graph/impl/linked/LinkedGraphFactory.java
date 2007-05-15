package ao.graph.impl.linked;

import ao.graph.Graph;
import ao.graph.GraphFactory;
import ao.graph.user.EdgeWeight;
import ao.graph.user.NodeData;


/**
 * Abstract factory for constructing Graphs of type LinkedGraph
 */

@SuppressWarnings("unchecked")
public class LinkedGraphFactory<N extends NodeData<N>, A extends EdgeWeight<A>>
        implements GraphFactory<N, A>
{
    public Graph<N, A> newInstance()
    {
        return new LinkedGraph<N, A>( null );
    }

    public Graph<N, A> newInstance(A defaultNullArch)
    {
        return new LinkedGraph<N, A>( defaultNullArch );
    }
}
