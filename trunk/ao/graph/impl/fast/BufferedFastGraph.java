package ao.graph.impl.fast;

import ao.graph.user.NodeData;
import ao.graph.user.EdgeWeight;
import ao.graph.user.EdgeWeightDomain;
import ao.graph.Graph;
import ao.graph.struct.DataAndWeight;
import ao.graph.struct.NodeDataPair;
import ao.graph.struct.Endpoints;

/**
 *
 */
public class BufferedFastGraph<D extends NodeData<D>, W extends EdgeWeight<W>>
        implements Graph<D, W>
{
    //--------------------------------------------------------------------
    private MatrixBuffer<D, W> buffer;
    private Graph<D, W>        deleget;

    
    //--------------------------------------------------------------------
    public BufferedFastGraph(
            EdgeWeightDomain<W> edgeWeightDomain,
            W                   defaultNullEdge)
    {
        buffer = new MatrixBuffer<D, W>(
                        edgeWeightDomain,
                        defaultNullEdge);
        deleget = buffer.asGraph();
    }


    //--------------------------------------------------------------------
    public int add(D nodeData)
    {
        return deleget.add(nodeData);
    }

    public boolean join(D dataA, D dataB, W edgeWeight)
    {
        return deleget.join(dataA, dataB, edgeWeight);
    }

    public DataAndWeight<D, W> merge(D nodeA, D nodeB)
    {
        debuffer();
        return deleget.merge(nodeA, nodeB);
    }

    public DataAndWeight<D, W> merge(D nodeA, D nodeB, W nullWeight)
    {
        debuffer();
        return deleget.merge(nodeA, nodeB, nullWeight);
    }

    public NodeDataPair<D> antiEdge()
    {
        debuffer();
        return deleget.antiEdge();
    }

    public Endpoints<D, W> nodesIncidentHeaviestEdge()
    {
        debuffer();
        return deleget.nodesIncidentHeaviestEdge();
    }

    public Endpoints<D, W> nodesIncidentLightestEdge()
    {
        debuffer();
        return deleget.nodesIncidentLightestEdge();
    }


    //--------------------------------------------------------------------
    private void debuffer()
    {
        if (buffer != null)
        {
            deleget = buffer.flush();
            buffer  = null;
        }
    }
}
