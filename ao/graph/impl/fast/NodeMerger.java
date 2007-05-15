package ao.graph.impl.fast;

import ao.graph.impl.common.struct.DataAndWeightImpl;
import ao.graph.struct.DataAndWeight;
import ao.graph.user.EdgeWeight;
import ao.graph.user.NodeData;

/**
 *
// * @param D
// * @param W
 */
public class NodeMerger<D extends NodeData<D>, W extends EdgeWeight<W>>
{
    //--------------------------------------------------------------------
    private static final int MAX_ERRASORS = 4;

    private final FastNode<D, W> LESSER_XPARENT;
    private final FastNode<D, W> GREATER_XPARENT;

    private final IncidenceErrasor<D, W>[] ERRASORS;

    private int size = MAX_ERRASORS;

    private int           hiLoSpanSize = 0;
    private HexEdge<D, W> hiLoFirst    = null;
    private HexEdge<D, W> hiLoLast     = null;


    //--------------------------------------------------------------------
    /**
     * @param xparentA ...
     * @param xparentB ...
     */
    @SuppressWarnings("unchecked")
    public NodeMerger(
            FastNode<D, W> xparentA, FastNode<D, W> xparentB)
    {
        LESSER_XPARENT  = FastNode.lesserOf(  xparentA, xparentB );
        GREATER_XPARENT = FastNode.greaterOf( xparentA, xparentB );

        ERRASORS = new IncidenceErrasor[]
                    {   xparentA.erraseIncdence(true)
                    ,   xparentA.erraseIncdence(false)
                    ,   xparentB.erraseIncdence(true)
                    ,   xparentB.erraseIncdence(false)};

        compactErrasors();
        sortErrasors();
    }


    //--------------------------------------------------------------------
    // returns parent
    /**
     * @param byWeight ...
     * @param graph ...
     * @param nullWeight ...
     * @return ...
     */
    public DataAndWeight<D, W> merge(
            ByWeightIndex<D, W> byWeight,
            FastGraph<D, W>     graph,
            W                   nullWeight)
    {
        W weight = null;
        D parent = LESSER_XPARENT.data().mergeWith(
                                            GREATER_XPARENT.data());

        FastNode<D, W> indexedParent = graph.addNode( parent );

        boolean        previousLoHiOrder = true; // "= true" not needed
                                                 // but gives error if
                                                 // left unspecified
        FastNode<D, W> previousMinor     = null;
        HexEdge<D, W>  previousHexEdge   = null;
        for (IncidenceErrasor<D, W> errasor;
             (errasor = ERRASORS[0]) != null;)
        {
            if ( LESSER_XPARENT ==
                        errasor.current().minorEndpoint(false) &&
                    GREATER_XPARENT ==
                            errasor.current().majorEndpoint(false))
            {
                weight = errasor.currentWeight();
                byWeight.remove( errasor.current() );

                advanceParentRelatingErrasor();
            }
            else
            {
                boolean nodeWasConnectedToBothXparents = false;

                if (previousMinor != null)
                {
                    nodeWasConnectedToBothXparents =
                            (previousMinor == errasor.currentMinor());

                    W currentWeight = (nodeWasConnectedToBothXparents
                                       ? errasor.currentWeight()
                                       : nullWeight);

                    previousHexEdge.setMajorNode(
                            indexedParent, previousLoHiOrder );
                    byWeight.merge( previousHexEdge, currentWeight );

                    adjustHiLoBounds( previousHexEdge );
                    previousMinor
                            .retrieveOrCreateIncidence(
                                    graph, byWeight, true)
                                .addGreatest(previousHexEdge);
                }

                if (nodeWasConnectedToBothXparents)
                {
                    previousMinor     = null;
                    previousHexEdge = null;

                    byWeight.remove( errasor.current() );
                }
                else
                {
                    previousMinor     = errasor.currentMinor();
                    previousHexEdge = errasor.current();
                    previousLoHiOrder = errasor.loHiOrder();
                }

//                System.out.println(
//                      errasor.currentMinor().incidentEdges(
//                              !errasor.loHiOrder() ));
                advanceFirstErrasor();
            }
        }

        // ugly repetition, need to figure out
        //  some way to make this nicer.
        if (previousMinor != null &&
                previousMinor != LESSER_XPARENT &&
                previousMinor != GREATER_XPARENT)
        {
            previousHexEdge.setMajorNode(
                    indexedParent, previousLoHiOrder );
            byWeight.merge( previousHexEdge, nullWeight );

            adjustHiLoBounds( previousHexEdge );
            previousMinor
                    .retrieveOrCreateIncidence(
                            graph, byWeight, true)
                        .addGreatest(previousHexEdge);
        }

        if (hiLoSpanSize > 0)
        {
            indexedParent.addHiLoIncidence(
                    graph, byWeight,
                    hiLoFirst, hiLoLast,
                    hiLoSpanSize,
                    false);
        }

        graph.removeNodeOf(LESSER_XPARENT.data());
        graph.removeNodeOf(GREATER_XPARENT.data());

        return new DataAndWeightImpl<D, W>(parent, weight);
    }

    private void adjustHiLoBounds(HexEdge<D, W> hexEdge)
    {
        HexEdge.connect( hiLoLast, hexEdge, false );

        if (hiLoFirst == null)
        {
            hiLoFirst = hexEdge;
        }
        hiLoLast = hexEdge;

        hiLoSpanSize++;
    }


    //--------------------------------------------------------------------
    private void advanceFirstErrasor()
    {
        ERRASORS[0].erraseCurrent();

        if (! ERRASORS[0].advance())
        {
            compactErrasors();
        }
        else
        {
            sortErrasors();
        }
    }

    private void advanceParentRelatingErrasor()
    {
        HexEdge<D, W> parentRelator = ERRASORS[0].current();

        boolean endReached = false;
        for (int i = 0; i < size; i++)
        {
            if ( ERRASORS[i].current() == parentRelator )
            {
                if (! ERRASORS[i].advance())
                {
                    endReached = true;
                }
            }
            else
            {
                // dont need to mark endReached since
                //  ERRASORS[i].current != ERRASORS[i].end
                ERRASORS[i].shiftEndIfEquals( parentRelator );
            }
        }

        if ( endReached )
        {
            compactErrasors();
        }
        sortErrasors();
    }

    //--------------------------------------------------------------------
    // makes sure all 'null' values appear towards the end.
    private void compactErrasors()
    {
        int index = 0;
        for (int i = 0; i < size; i++)
        {
            if (index != i)
            {
                ERRASORS[ index ] = ERRASORS[ i ];
            }

            if ( ERRASORS[i] != null &&
                    ERRASORS[i].current() != null)
            {
                index++;
            }
        }

        for (int remainder = index;
                 remainder < size;
                 remainder++)
        {
            ERRASORS[ remainder ] = null;
        }

        size = index;
    }

    private void sortErrasors()
    {
        if ( size < 2 ) return;

        if ( ERRASORS[0].compareTo( ERRASORS[1] ) > 0 )
        {
            IncidenceErrasor<D, W> temp = ERRASORS[ 0 ];
            ERRASORS[ 0 ] = ERRASORS[ 1 ];
            ERRASORS[ 1 ] = temp;
        }

        switch (size)
        {
            case 3:
                if ( ERRASORS[1].compareTo( ERRASORS[2] ) > 0 )
                {
                    if ( ERRASORS[0].compareTo( ERRASORS[2] ) > 0 )
                    {
                        IncidenceErrasor<D, W> temp = ERRASORS[ 2 ];
                        ERRASORS[ 2 ] = ERRASORS[ 1 ];
                        ERRASORS[ 1 ] = ERRASORS[ 0 ];
                        ERRASORS[ 0 ] = temp;
                    }
                    else
                    {
                        IncidenceErrasor<D, W> temp = ERRASORS[ 2 ];
                        ERRASORS[ 2 ] = ERRASORS[ 1 ];
                        ERRASORS[ 1 ] = temp;
                    }
                }
                break;

            case 4:
                if ( ERRASORS[2].compareTo( ERRASORS[3] ) > 0 )
                {
                    IncidenceErrasor<D, W> temp = ERRASORS[ 2 ];
                    ERRASORS[ 2 ] = ERRASORS[ 3 ];
                    ERRASORS[ 3 ] = temp;
                }

                if ( ERRASORS[2].compareTo( ERRASORS[0] ) <= 0 )
                {
                    IncidenceErrasor<D, W> temp = ERRASORS[ 2 ];
                    ERRASORS[ 2 ] = ERRASORS[ 1 ];
                    ERRASORS[ 1 ] = ERRASORS[ 0 ];
                    ERRASORS[ 0 ] = temp;

                    if ( ERRASORS[3].compareTo( ERRASORS[1] ) <= 0 )
                    {
                        IncidenceErrasor<D, W> tempB = ERRASORS[ 3 ];
                        ERRASORS[ 3 ] = ERRASORS[ 2 ];
                        ERRASORS[ 2 ] = ERRASORS[ 1 ];
                        ERRASORS[ 1 ] = tempB;
                    }
                    else if ( ERRASORS[3].compareTo( ERRASORS[2] ) <= 0 )
                    {
                        IncidenceErrasor<D, W> tempB = ERRASORS[ 1 ];
                        ERRASORS[ 1 ] = ERRASORS[ 2 ];
                        ERRASORS[ 2 ] = tempB;
                    }
                }
                else if ( ERRASORS[2].compareTo( ERRASORS[1] ) <= 0 )
                {
                    IncidenceErrasor<D, W> temp = ERRASORS[ 1 ];
                    ERRASORS[ 1 ] = ERRASORS[ 2 ];
                    ERRASORS[ 2 ] = temp;

                    if ( ERRASORS[3].compareTo( ERRASORS[2] ) <= 0 )
                    {
                        IncidenceErrasor<D, W> tempB = ERRASORS[ 2 ];
                        ERRASORS[ 2 ] = ERRASORS[ 3 ];
                        ERRASORS[ 3 ] = tempB;
                    }
                }
                break;
        }
    }
}
