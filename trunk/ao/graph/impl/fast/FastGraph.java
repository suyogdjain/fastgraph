package ao.graph.impl.fast;

import ao.graph.Graph;
import ao.graph.impl.common.struct.NodeDataPairImpl;
import ao.graph.struct.Endpoints;
import ao.graph.struct.DataAndWeight;
import ao.graph.struct.NodeDataPair;
import ao.graph.user.EdgeWeightDomain;
import ao.graph.user.NodeData;
import ao.graph.user.EdgeWeight;
import javolution.util.FastMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * The fastest possible way I could think of implementing this.
// * @param D
// * @param W
 */
public class FastGraph<D extends NodeData<D>, W extends EdgeWeight<W>>
        implements Graph<D, W>
{
    //--------------------------------------------------------------------
    private final FastMap<D, FastNode<D, W>> INDEX;
    private final ByWeightIndex<D, W>        BY_WEIGHT_INDEX;
    private final W                          DEFAULT_NULL_WEIGHT;

    private int nextLabel = 0;


    //--------------------------------------------------------------------
    /**
     * @param edgeWeightDomain ...
     * @param defaultNullEdge ...
     */
    public FastGraph(
            EdgeWeightDomain<W> edgeWeightDomain,
            W                   defaultNullEdge)
    {
        INDEX               = new FastMap<D, FastNode<D, W>>();
        BY_WEIGHT_INDEX     = new ByWeightIndex<D, W>(edgeWeightDomain);
        DEFAULT_NULL_WEIGHT = defaultNullEdge;
    }


    //--------------------------------------------------------------------
    public int add(D node)
    {
        return nodeOf(node).label();
    }


    //--------------------------------------------------------------------
    public boolean join(D dataA, D dataB, W edgeWeight)
    {
        HexEdge<D, W> hexEdge = edgeOf(dataA, dataB, edgeWeight);
        if (hexEdge == null) return false;
        
        if (hexEdge.minorEndpoint(false)
                .addIncidentEdge(
                        this, BY_WEIGHT_INDEX, hexEdge, true ))
        {
            hexEdge.majorEndpoint(false)
                    .addIncidentEdge(
                            this, BY_WEIGHT_INDEX, hexEdge, false );
            BY_WEIGHT_INDEX.add(hexEdge);
            //validate();
            return true;
        }
        else return false;
    }
    
    /**
     * @param dataA ...
     * @param dataB ...
     * @param edgeWeight ...
     * @return  ...
     */
    public HexEdge<D, W> edgeOf(D dataA, D dataB, W edgeWeight)
    {
        if (edgeWeight == null ||
                 dataA == null ||
                 dataB == null ||
                 dataA.equals(dataB)
            ) return null;

        FastNode<D, W> nodeA = nodeOf( dataA );
        FastNode<D, W> nodeB = nodeOf( dataB );

        FastNode<D, W> lesserNode  = FastNode.lesserOf(  nodeA, nodeB );
        FastNode<D, W> greaterNode = FastNode.greaterOf( nodeA, nodeB );

        return new HexEdge<D, W>(lesserNode, greaterNode, edgeWeight);
    }
    
    /**
     * @param edge ...
     */
    public void indexEdgeByWeight(HexEdge<D, W> edge)
    {
        BY_WEIGHT_INDEX.add( edge );
    }
    
    

    //--------------------------------------------------------------------
    public DataAndWeight<D, W> merge(D nodeA, D nodeB)
    {
        return merge( nodeA, nodeB, DEFAULT_NULL_WEIGHT);
    }

    public DataAndWeight<D, W> merge(D dataA, D dataB, W nullEdgeWeight)
    {
        if (dataA == null || dataB == null) return null;

        FastNode<D, W> nodeA = nodeOf( dataA );
        FastNode<D, W> nodeB = nodeOf( dataB );
//        System.out.println("merging " + linkA + ", " + linkB);
        if (nodeA == nodeB) return null;

//        validate();
        return new NodeMerger<D, W>(nodeA, nodeB)
                        .merge(BY_WEIGHT_INDEX, this, nullEdgeWeight);
    }


    //--------------------------------------------------------------------
    public NodeDataPair<D> antiEdge()
    {
        if (INDEX.size() < 2) return null;

        int expectedSpanSize = INDEX.size() - 1;

        for (FastMap.Entry<D, FastNode<D, W>>
                e     = INDEX.tail(),
                start = INDEX.head().getNext();
             (e = e.getPrevious()) != start;)
        {
            NodeIncidence<D, W> hiLoIncidence =
                    e.getValue().hiLoIncidence();

            if (hiLoIncidence == null)
            {
                return givenAndPreviouseNode( e );
            }
            else if (hiLoIncidence.degree() < expectedSpanSize)
            {
                return givenAndMissingFromChain(e, hiLoIncidence);
            }

            expectedSpanSize--;
        }

        return null;
    }

    private NodeDataPair<D> givenAndMissingFromChain(
            FastMap.Entry<D, FastNode<D, W>> majorOfSpan,
            NodeIncidence<D, W> hiLoIncidence)
    {
        FastMap.Entry<D, FastNode<D, W>> entryCursor =
                majorOfSpan.getPrevious();
        HexEdge<D, W>                    spanCursor  =
                hiLoIncidence.last();
        //HexEdge<ND, W> spanCursor  = hiLoIncidence.first();

        while ( spanCursor != null &&
                entryCursor.getValue() == spanCursor.minorEndpoint(false) )
        {
            entryCursor = entryCursor.getPrevious();
            spanCursor  = spanCursor.previous(false);
        }

        return new NodeDataPairImpl<D>(
                    majorOfSpan.getKey(), entryCursor.getKey());
    }

    private NodeDataPair<D> givenAndPreviouseNode(
            FastMap.Entry<D, FastNode<D, W>> entry)
    {
        return new NodeDataPairImpl<D>(
                    entry.getKey(),
                    entry.getPrevious().getKey());
    }


    //--------------------------------------------------------------------
    public Endpoints<D, W> nodesIncidentHeaviestEdge()
    {
        return BY_WEIGHT_INDEX.endpointsOfHeaviestEdge();
    }

    public Endpoints<D, W> nodesIncidentLightestEdge()
    {
        return BY_WEIGHT_INDEX.endpointsOfLightestEdge();
    }


    //--------------------------------------------------------------------
    /**
     * @param data ...
     * @param loHiOrder ...
     */
    public void removeIncidenceOf(D data, boolean loHiOrder)
    {
        FastNode<D, ? super W> node = INDEX.get( data );

        node.removeIncidence( loHiOrder );
    }

    /**
     * @param data ...
     */
    public void removeNodeOf(D data)
    {
        FastNode<D, W> removed = INDEX.remove( data );
        assert removed != null;
    }


    //--------------------------------------------------------------------
    private FastNode<D, W> nodeOf(D data)
    {
        FastNode<D, W> node = INDEX.get( data );

        return (node == null)
                ? addNode(data)
                : node;
    }

    // optimization for merging
    /**
     * @param node ...
     * @return  ...
     */
    public FastNode<D, W> addNode(D node)
    {
        assert !INDEX.containsKey( node );

        FastNode<D, W> newNode =
                new FastNode<D, W>( node, nextLabel++ );

        INDEX.put( node, newNode);

        return newNode;
    }
    
    
    //--------------------------------------------------------------------
    /**
     * @param node ...
     * @return   ...
     */
    public NodeIncidence<D, W> loHiIncidenceOf(FastNode<D, W> node)
    {
        return node.retrieveOrCreateLoHiIncidence(this, BY_WEIGHT_INDEX);
    }
    
    /**
     * @param node ...
     * @return   ...
     */
    public NodeIncidence<D, W> hiLoIncidenceOf(FastNode<D, W> node)
    {
        return node.retrieveOrCreateHiLoIncidence(this, BY_WEIGHT_INDEX);
    }
    
    
    //--------------------------------------------------------------------
    // used for testing purposes only
    /**
     */
    public void validate()
    {
        Collection<HexEdge<D, W>> allEdges =
                new HashSet<HexEdge<D, W>>();

        for (FastMap.Entry<D, FastNode<D, W>> c   = INDEX.head(),
                                              end = INDEX.tail();
             (c = c.getNext()) != end;)
        {
            allEdges.addAll(
                    validateIncidence(
                            c.getValue().incidentEdges(true),
                            true)  );
            allEdges.addAll(
                    validateIncidence(
                            c.getValue().incidentEdges(false),
                            false) );
        }

        BY_WEIGHT_INDEX.validate( allEdges );
    }

    private Collection<HexEdge<D, W>> validateIncidence(
            NodeIncidence<D, W> incidence,
            boolean loHiOrder)
    {
        Collection<HexEdge<D, W>> nodesInSpan =
                new ArrayList<HexEdge<D, W>>();

        if (incidence == null) return nodesInSpan;

        //System.out.println("validating incidence = " + incidence);
        HexEdge<D, W> prev   = null;
        HexEdge<D, W> cursor = incidence.first();

        while (true)
        {
            nodesInSpan.add( cursor );

            assert prev == null || prev.next(loHiOrder) == cursor;
            assert prev == cursor.previous(loHiOrder);

            prev = cursor;

            if (cursor == incidence.last()) break;
            cursor = cursor.next(loHiOrder);
        }

        assert cursor.next(loHiOrder) == null;
        return nodesInSpan;
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return indexToString(true) + "\n" + indexToString(false);
    }

    private String indexToString(boolean loHiOrder)
    {
        StringBuilder str = new StringBuilder();

        for (FastMap.Entry<D, FastNode<D, W>> c   = INDEX.head(),
                                              end = INDEX.tail();
             (c = c.getNext()) != end;)
        {
            NodeIncidence<D, W> incidence =
                    c.getValue().incidentEdges(loHiOrder);

            if (incidence != null)
            {
                str.append(incidence.toString());
                str.append(' ');
            }
        }

        return str.toString();
    }
}
