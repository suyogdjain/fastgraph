package ao.graph.impl.incidence;

import ao.graph.Graph;
import ao.graph.impl.common.struct.EndpointsImpl;
import ao.graph.impl.common.struct.NodeDataPairImpl;
import ao.graph.impl.common.struct.DataAndWeightImpl;
import ao.graph.impl.common.EdgeWeights;
import ao.graph.impl.common.index.EndpointIndex;
import ao.graph.struct.Endpoints;
import ao.graph.struct.DataAndWeight;
import ao.graph.struct.NodeDataPair;
import ao.graph.user.EdgeWeightDomain;
import ao.graph.user.NodeData;
import ao.graph.user.EdgeWeight;
import javolution.util.FastMap;

import java.util.Map;

/**
 * The same algorithmic performace as a matrix graph implemintation.
 * Uses hash tables to minimize memory usage for sparse graphs.
// * @param D
// * @param W
 */
public class IncidenceListGraph<D extends NodeData<D>, W extends EdgeWeight<W>>
        implements Graph<D, W>
{
    private final FastMap<D, Map<D, W>> GRAPH;
    private final EndpointIndex<D, W>   BY_EDGE_INDEX;
    private final W                     DEFAULT_NULL_EDGE;

    /**
     * @param edgeWeightDomain ...
     * @param defaultNullEdge ...
     */
    public IncidenceListGraph(
            EdgeWeightDomain<W> edgeWeightDomain, W defaultNullEdge)
    {
        GRAPH             = newGraphInstance();
        BY_EDGE_INDEX     = new EndpointIndex<D, W>(edgeWeightDomain);
        DEFAULT_NULL_EDGE = defaultNullEdge;
    }


    //----------------------------------------------------------------------------------
    public int add(D node)
    {
        if (GRAPH.containsKey(node)) return -1;

        retrieveOrCreateSubGraph(node);
        return GRAPH.size() - 1;
    }

    //----------------------------------------------------------------------------------
    public boolean join(D nodeA, D nodeB, W edge)
    {
        if (nodeA == null ||
            nodeB == null ||
            edge == null  ||
            nodeA.equals( nodeB )) return false;

        Map<D, W> subgraphA = retrieveOrCreateSubGraph(nodeA);
        Map<D, W> subgraphB = retrieveOrCreateSubGraph(nodeB);

        W existingEdge = subgraphA.get( nodeB );

        W newEdge = (existingEdge == null)
                    ? edge
                    : existingEdge.mergeWith( edge );

        subgraphA.put( nodeB, newEdge );
        subgraphB.put( nodeA, newEdge );

        if (existingEdge != null)
        {
            BY_EDGE_INDEX.remove( EndpointsImpl.newInstance(nodeA, nodeB, existingEdge) );
        }
        BY_EDGE_INDEX.add( EndpointsImpl.newInstance(nodeA, nodeB, newEdge) );

        return (existingEdge == null);
    }

    private Map<D, W> retrieveOrCreateSubGraph(D node)
    {
        Map<D, W> existing = GRAPH.get( node );

        if (existing == null)
        {
            Map<D, W> subgraph = newSubgraphInstance();
            GRAPH.put(node, subgraph);
            return subgraph;
        }
        else
        {
            return existing;
        }
    }

    //----------------------------------------------------------------------------------
    public DataAndWeight<D, W> merge(D nodeA, D nodeB)
    {
        return merge(nodeA, nodeB, DEFAULT_NULL_EDGE);
    }

    public DataAndWeight<D, W> merge(D nodeA, D nodeB, W nullEdge)
    {
        if (nodeA == null ||
            nodeB == null ||
            nodeA.equals( nodeB )) return null;

        Map<D, W> subgraphA = retrieveOrCreateSubGraph(nodeA);
        Map<D, W> subgraphB = retrieveOrCreateSubGraph(nodeB);

        GRAPH.remove( nodeA );
        GRAPH.remove( nodeB );

        W edgeAB = subgraphA.get( nodeB );
        D union  = nodeA.mergeWith( nodeB );

        subgraphA.remove( nodeB );
        subgraphB.remove( nodeA );

        if (edgeAB != null)
        {
            BY_EDGE_INDEX.remove( EndpointsImpl.newInstance(nodeA, nodeB, edgeAB) );
        }

        for (D incidentNode : subgraphA.keySet())
        {
            W edgeRA = GRAPH.get( incidentNode ).remove( nodeA );
            W edgeRB = GRAPH.get( incidentNode ).remove( nodeB );

            W mergedEdge = EdgeWeights.merge(edgeRA, edgeRB, nullEdge);
            join( incidentNode, union, mergedEdge ); // adds to BY_EDGE_INDEX

            BY_EDGE_INDEX.remove( EndpointsImpl.newInstance(nodeA, incidentNode, edgeRA) );
            if (edgeRB != null)
            {
                BY_EDGE_INDEX.remove( EndpointsImpl.newInstance(nodeB, incidentNode, edgeRB) );
            }
            //BY_EDGE_INDEX.additiveMerge( EndpointsImpl.newInstance(union, incidentNode, mergedEdge) );
        }
        subgraphB.keySet().removeAll( subgraphA.keySet() );

        for (D incidentNode : subgraphB.keySet())
        {
            W edgeRB = GRAPH.get( incidentNode ).remove( nodeB );

            W mergedEdge = EdgeWeights.merge(edgeRB, nullEdge);
            join( incidentNode, union, mergedEdge );

            BY_EDGE_INDEX.remove( EndpointsImpl.newInstance(nodeB, incidentNode, edgeRB) );
            //BY_EDGE_INDEX.additiveMerge( EndpointsImpl.newInstance(union, incidentNode, mergedEdge) );
        }

        return new DataAndWeightImpl<D, W>(union, edgeAB);
    }


    //----------------------------------------------------------------------------------
    public NodeDataPair<D> antiEdge()
    {
        if (GRAPH.size() < 2) return null;

        for (FastMap.Entry<D, Map<D, W>> e   = GRAPH.head(),
                                         end = GRAPH.tail();
             (e = e.getNext()) != end;)
        {
            if ( e.getValue().size() < (GRAPH.size() - 1) )
            {
                return missingFromSubgraph(e.getKey(), e.getValue());
            }
        }

        return null;
    }

    private NodeDataPair<D> missingFromSubgraph(D of, Map<D, W> subgraph)
    {
        for (FastMap.Entry<D, Map<D, W>> e   = GRAPH.head(),
                                         end = GRAPH.tail();
             (e = e.getNext()) != end;)
        {
            if (of != e.getKey() && !subgraph.containsKey(e.getKey()))
            {
                return new NodeDataPairImpl<D>(of, e.getKey());
            }
        }

        return null;
    }



    //----------------------------------------------------------------------------------
    public Endpoints<D, W> nodesIncidentHeaviestEdge()
    {
        return BY_EDGE_INDEX.nodesIncidentHeaviestEdge();
    }

    public Endpoints<D, W> nodesIncidentLightestEdge()
    {
        return BY_EDGE_INDEX.nodesIncidentLightestEdge();
    }


    //----------------------------------------------------------------------------------
    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();

        for (FastMap.Entry<D, Map<D, W>> e   = GRAPH.head(),
                                         end = GRAPH.tail();
             (e = e.getNext()) != end;)
        {
            str.append("\t|").append(e.getKey());
        }
        str.append("\n");

        for (FastMap.Entry<D, Map<D, W>> e   = GRAPH.head(),
                                         end = GRAPH.tail();
             (e = e.getNext()) != end;)
        {
            str.append(e.getKey());

            for (FastMap.Entry<D, Map<D, W>> c = GRAPH.head(); (c = c.getNext()) != end;)
            {
                str.append("\t|");

                Map<D, W> subgraph = GRAPH.get(e.getKey());
                if (subgraph.containsKey( c.getKey() ))
                {
                    str.append( subgraph.get(c.getKey()) );
                }
            }
            str.append("\n");
        }

        return str.toString();
    }



    //----------------------------------------------------------------------------------
    private FastMap<D, Map<D, W>> newGraphInstance()
    {
        return new FastMap<D, Map<D, W>>();
    }
    private Map<D, W> newSubgraphInstance()
    {
        return new FastMap<D, W>();
    }
}
