package ao.graph.impl.linked.merge;

import ao.graph.impl.linked.NodeSet;
import ao.graph.impl.linked.chain.MultiNode;
import ao.graph.impl.linked.chain.MultiNodeChain;
import ao.graph.impl.linked.index.DirectedMultiNodeIndex;
import ao.graph.impl.common.struct.LabeledNode;
import ao.graph.impl.common.struct.DataAndWeightImpl;
import ao.graph.struct.DataAndWeight;
import ao.graph.user.EdgeWeight;
import ao.graph.user.NodeData;

/**
 *
 */
public class NodeMerger<N extends NodeData<N>, A extends EdgeWeight<A>>
{
    private static final int MAX_ERRASORS = 4;

    private final LabeledNode<N> LESSER_XPARENT;
    private final LabeledNode<N> GREATER_XPARENT;

    private final NodeSpanErrasor<N, A>[] ERRASORS;

    private int size = MAX_ERRASORS;

    private int             hiLoSpanSize = 0;
    private MultiNode<N, A> hiLoFirst    = null;
    private MultiNode<N, A> hiLoLast     = null;


    @SuppressWarnings("unchecked")
    public NodeMerger(
            LabeledNode<N> xparentA,
            LabeledNode<N> xparentB,
            NodeSpanErrasor<N, A> loHiErrasorA,
            NodeSpanErrasor<N, A> hiLoErrasorA,
            NodeSpanErrasor<N, A> loHiErrasorB,
            NodeSpanErrasor<N, A> hiLoErrasorB)
    {
        LESSER_XPARENT  = LabeledNode.lesserOf(  xparentA, xparentB );
        GREATER_XPARENT = LabeledNode.greaterOf( xparentA, xparentB );

        ERRASORS  = new NodeSpanErrasor[]
                    {loHiErrasorA, hiLoErrasorA, loHiErrasorB, hiLoErrasorB};

        compactErrasors();
        sortErrasors();
    }


    // returns parent
    public DataAndWeight<N, A> merge(
            NodeSet<N> allNodes,
            MultiNodeChain<N, A> nodeChain,
            DirectedMultiNodeIndex<N, A> loHiIndex,
            DirectedMultiNodeIndex<N, A> hiLoIndex,
            A nullArch)
    {
        A              arch   = null;
        N              parent = LESSER_XPARENT.node().mergeWith(
                                            GREATER_XPARENT.node());
        LabeledNode<N> labeledParent = allNodes.index( parent );

        LabeledNode<N>  previousMinor     = null;
        MultiNode<N, A> previousMultiNode = null;
        for (NodeSpanErrasor<N, A> errasor;
                (errasor = ERRASORS[0]) != null;)
        {
            if ( LESSER_XPARENT == errasor.current().minor(false) &&
                    GREATER_XPARENT == errasor.current().major(false))
            {
                arch = errasor.currentArch();

                advanceParentRelatingErrasor();
            }
            else
            {
                boolean nodeWasConnectedToBothXparents = false;

                if (previousMinor != null)
                {
                    nodeWasConnectedToBothXparents =
                            (previousMinor == errasor.currentMinor());

                    MultiNode<N, A> addend;

                    if ( nodeWasConnectedToBothXparents )
                    {
                        addend = nodeChain.createAndIndexMultiNode(
                                errasor.currentMinor(),
                                labeledParent,
                                previousMultiNode.edge().mergeWith(
                                                errasor.currentArch()));
                    }
                    else
                    {
                        addend = nodeChain.createAndIndexMultiNode(
                                previousMinor,
                                labeledParent,
                                mergeArchs(nullArch, previousMultiNode.edge()));
                    }

                    adjustHiLoBounds( addend );
                    loHiIndex.addToChain( addend );
                }

                if (nodeWasConnectedToBothXparents)
                {
                    previousMinor     = null;
                    previousMultiNode = null;
                }
                else
                {
                    previousMinor     = errasor.currentMinor();
                    previousMultiNode = errasor.current();
                }

                advanceFirstErrasor();
            }
        }

        // ugly repetition, need to figure out some way to make this nicer.
        if (previousMinor != null &&
                previousMinor != LESSER_XPARENT &&
                previousMinor != GREATER_XPARENT)
        {
            MultiNode<N, A> addend = nodeChain.createAndIndexMultiNode(
                    previousMinor,
                    labeledParent,
                    mergeArchs(nullArch, previousMultiNode.edge()));

            adjustHiLoBounds( addend );
            loHiIndex.addToChain( addend );
        }

        if (hiLoSpanSize > 0)
        {
            hiLoIndex.appendSpan(hiLoFirst, hiLoLast, hiLoSpanSize);
        }

        allNodes.remove( LESSER_XPARENT  );
        allNodes.remove( GREATER_XPARENT );

        return new DataAndWeightImpl<N, A>(parent, arch);
    }

    private void adjustHiLoBounds(MultiNode<N, A> multiNode)
    {
        MultiNode.connect( hiLoLast, multiNode, false );

        if (hiLoFirst == null)
        {
            hiLoFirst = multiNode;
        }
        hiLoLast = multiNode;

        hiLoSpanSize++;
    }

    private A mergeArchs(A archA, A archB)
    {
        return archA == null
                ? archB
                : archA.mergeWith(archB);
    }


    //-----------------------------------------------------------------------
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
        MultiNode<N, A> parentRelator = ERRASORS[0].current();
        ERRASORS[0].erraseCurrent();

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
            else /*if (isFirstTime)*/
            {
                ERRASORS[i].shiftEndIfEquals( parentRelator );
            }
        }

        if ( endReached )
        {
            compactErrasors();
        }
        sortErrasors();
    }

    //-------------------------------------------------------------------------
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

            if ( ERRASORS[i] != null && ERRASORS[i].current() != null)
            {
                index++;
            }
        }

        for (int remainder = index; remainder < size; remainder++)
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
            NodeSpanErrasor<N, A> temp = ERRASORS[ 0 ];
            ERRASORS[ 0 ] = ERRASORS[ 1 ];
            ERRASORS[ 1 ] = temp;
        }

        if ( size < 3 ) return;
        if ( ERRASORS[1].compareTo( ERRASORS[2] ) > 0 )
        {
            if ( ERRASORS[0].compareTo( ERRASORS[2] ) > 0 )
            {
                NodeSpanErrasor<N, A> temp = ERRASORS[ 2 ];
                ERRASORS[ 2 ] = ERRASORS[ 1 ];
                ERRASORS[ 1 ] = ERRASORS[ 0 ];
                ERRASORS[ 0 ] = temp;
            }
            else
            {
                NodeSpanErrasor<N, A> temp = ERRASORS[ 2 ];
                ERRASORS[ 2 ] = ERRASORS[ 1 ];
                ERRASORS[ 1 ] = temp;
            }
        }

        if ( size < 4 ) return;
        int cmp = ERRASORS[1].compareTo( ERRASORS[3] );

        if ( cmp == 0 || (cmp < 0 && ERRASORS[2].compareTo( ERRASORS[3] ) > 0))
        {
            NodeSpanErrasor<N, A> temp = ERRASORS[ 3 ];
            ERRASORS[ 3 ] = ERRASORS[ 2 ];
            ERRASORS[ 2 ] = temp;
        }
        else if ( cmp > 0 )
        {
            if ( ERRASORS[0].compareTo( ERRASORS[3] ) > 0 )
            {
                NodeSpanErrasor<N, A> temp = ERRASORS[ 3 ];
                ERRASORS[ 3 ] = ERRASORS[ 2 ];
                ERRASORS[ 2 ] = ERRASORS[ 1 ];
                ERRASORS[ 1 ] = ERRASORS[ 0 ];
                ERRASORS[ 0 ] = temp;
            }
            else
            {
                NodeSpanErrasor<N, A> temp = ERRASORS[ 3 ];
                ERRASORS[ 3 ] = ERRASORS[ 2 ];
                ERRASORS[ 2 ] = ERRASORS[ 1 ];
                ERRASORS[ 1 ] = temp;
            }
        }
    }
}
