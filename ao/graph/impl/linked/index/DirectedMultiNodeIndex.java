package ao.graph.impl.linked.index;

import ao.graph.impl.common.struct.LabeledNode;
import ao.graph.impl.linked.chain.MultiNodeChain;
import ao.graph.impl.linked.chain.MultiNode;
import ao.graph.impl.linked.merge.NodeSpanErrasor;
import ao.graph.user.EdgeWeight;
import ao.graph.user.NodeData;
import it.unimi.dsi.fastutil.ints.AbstractIntComparator;
import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import it.unimi.dsi.fastutil.ints.IntComparator;

import java.util.Iterator;

/**
 * Used to organize a chain of MultiNodes.
 * Two indexes are needed for one chain, loHi and hiLo.
 *
 * Example chain when looked at in loHi order:
 * (a, b) (a, c) (a, f) (b, c) (b, d) (c, f) (d, f)
 *  where (lo, hi) means an weight is connecting data lo to hi.
 *
 * The label keeps track of chain spans that start with the same data.
 * For example [(a, b) (a, c) (a, f)] is concidered a incidentEdges.
 *
 * This is how the whole chain would be grouped in loHi order:
 * [(a, b) (a, c) (a, f)] [(b, c) (b, d)] [(c, f)] [(d, f)]
 *
 * Same chain when looked at in hiLo order:
 * [(f, a) (f, c) (f, d)] [(d, b)] [(c, a) (c, b)] [(b, a)]
 *
 *
 * NOT THREAD SAFE!!
 */

// coz Int2ObjectSortedMap doesn't support generics 
@SuppressWarnings("unchecked")
public class DirectedMultiNodeIndex<N extends NodeData<N>, A extends EdgeWeight<A>>
{
    // need to figure out faster way.
    private static final IntComparator DESC_COMPARATOR = new AbstractIntComparator()
    {
        public final int compare(int indexA, int indexB)
        {
            return (indexA < indexB ? 1 :
                    (indexA > indexB ? -1 : 0));
        }
    };


    private final boolean LO_HI_ORDER;
    private final MultiNodeChain<N, A> CHAIN;
    private final Int2ObjectSortedMap/*<ChainSpanImpl>*/ INDEX;

    public DirectedMultiNodeIndex(MultiNodeChain<N, A> chain, boolean loHiOrder)
    {
        this.CHAIN       = chain;
        this.LO_HI_ORDER = loHiOrder;

        this.INDEX = (LO_HI_ORDER
                      ? new Int2ObjectRBTreeMap/*<ChainSpanImpl>*/() // ascending by default
                      : new Int2ObjectRBTreeMap/*<ChainSpanImpl>*/(DESC_COMPARATOR) );
    }

    //-----------------------------------------------------------------------------------
    // return true if data structure was modefied as a result of this call.
    public boolean addToChain(MultiNode<N, A> node)
    {
        int majorIndex = node.major( LO_HI_ORDER ).label();
        ChainSpan<N, A> span = (ChainSpan<N, A>) INDEX.get(majorIndex);

        if (span == null)
        {
            Int2ObjectSortedMap lesser  = INDEX.headMap(majorIndex);
            Int2ObjectSortedMap greater = INDEX.tailMap(majorIndex);

            ChainSpan<N, A> prev =
                    (lesser.isEmpty()  ? null
                                       : (ChainSpan<N, A>) lesser.get(lesser.lastIntKey()));
            ChainSpan<N, A> next =
                    (greater.isEmpty() ? null
                                       : (ChainSpan<N, A>) greater.get(greater.firstIntKey()));

            INDEX.put(majorIndex, new ChainSpanImpl(node, prev, next));
            return true;
        }
        else
        {
            return span.add(node);
        }
    }

    public boolean appendSpan(
            MultiNode<N, A> start, MultiNode<N, A> end, int size)
    {
        if (start == null) return false;
        assert start.major(LO_HI_ORDER).label() == end.major(LO_HI_ORDER).label();

        ChainSpanImpl prev =
                INDEX.isEmpty()
                ? null
                : (ChainSpanImpl) INDEX.get( INDEX.lastIntKey() );

        ChainSpanImpl addend = new ChainSpanImpl(start);
        addend.lastOfSpan = end;
        addend.size       = size;

        if (prev != null)
        {
            assert prev.majorIndex() < addend.majorIndex();

            MultiNode.connect(prev.lastOfSpan, start, LO_HI_ORDER);
        }

        INDEX.put( addend.majorIndex(), addend );

        return true;
    }


    //-----------------------------------------------------------------------------------
    public void removeFromChain(MultiNode<N, A> node)
    {
        ChainSpan<N, A> existingSpan = (ChainSpan<N, A>) INDEX.get( node.major(LO_HI_ORDER).label() );

        if (existingSpan == null) return;

        // unindexes itself if empty
        existingSpan.remove( node );
    }

    public NodeSpanErrasor<N, A> removeSpan(
            LabeledNode<N> ofNode,
            DirectedMultiNodeIndex<N, A> otherIndex)
    {
        ChainSpan<N, A> span = (ChainSpan<N, A>) INDEX.remove( ofNode.label() );

        return (span == null)
                ? null
                : new NodeSpanErrasor<N, A>(
                        span.firstOfSpan(),
                        span.lastOfSpan(),
                        otherIndex,
                        CHAIN,
                        LO_HI_ORDER);
    }


    //-----------------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public Iterator<ChainSpanImpl> chainSpanIterator()
    {
        return (Iterator<ChainSpanImpl>) INDEX.values().iterator();
    }


    //-----------------------------------------------------------------------------------
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for (Object span : INDEX.values())
        {
            sb.append(span.toString());
            sb.append(' ');
        }

        return sb.toString();
    }


    //-----------------------------------------------------------------------------------
    public class ChainSpanImpl implements ChainSpan<N, A>
    {
        private int             size;
        private MultiNode<N, A> firstOfSpan;
        private MultiNode<N, A> lastOfSpan;

        public ChainSpanImpl(
                MultiNode<N, A> multiNode,
                ChainSpan<N, A> previouse,
                ChainSpan<N, A> next)
        {
            this( multiNode, previouse, next, true );
        }

        public ChainSpanImpl(MultiNode<N, A> firstMultiNode)
        {
            this( firstMultiNode, null, null, false);
        }

        private ChainSpanImpl(
                MultiNode<N, A> multiNode,
                ChainSpan<N, A> previouse,
                ChainSpan<N, A> next,
                boolean updateMultiNodeLinks)
        {
            size        = 1;
            firstOfSpan = multiNode;
            lastOfSpan  = multiNode;

            if (! updateMultiNodeLinks) return;

            MultiNode<N, A> nextMultiNode = ( next      == null ? null : next.firstOfSpan()     );
            MultiNode<N, A> prevMultiNode = ( previouse == null ? null : previouse.lastOfSpan() );

            MultiNode.insertBetween(prevMultiNode, multiNode, nextMultiNode, LO_HI_ORDER);
        }

        private int majorIndex()
        {
            return firstOfSpan.major( LO_HI_ORDER ).label();
        }

        public int size()
        {
            return size;
        }

        public boolean majorIndexEquals( int index )
        {
            return index == majorIndex();
        }

        public MultiNode<N, A> firstOfSpan()
        {
            return firstOfSpan;
        }

        public MultiNode<N, A> lastOfSpan()
        {
            return lastOfSpan;
        }

        public boolean add(MultiNode<N, A> multiNode)
        {
            assert multiNode.major(LO_HI_ORDER).label() == majorIndex();

            int minorIndex = multiNode.minor( LO_HI_ORDER ).label();

            boolean wasPreviouslyAbsent =
                    isBeyondEdge(minorIndex)
                        ? addBeyondEdges(multiNode, minorIndex)
                        : isAtEdge(minorIndex)
                            ? addAtEdges(multiNode, minorIndex)
                            : addStrictlyBetweenEdges(multiNode, minorIndex);

            if (wasPreviouslyAbsent) size++;

            return wasPreviouslyAbsent;
        }

        private boolean addAtEdges(MultiNode<N, A> multiNode, int minorIndex)
        {
            if (firstOfSpan.minor(LO_HI_ORDER).label() == minorIndex)
                CHAIN.merge(firstOfSpan, multiNode.edge());
            else
                CHAIN.merge(lastOfSpan, multiNode.edge());

            return false;
        }

        private boolean addBeyondEdges(MultiNode<N, A> multiNode, int minorIndex)
        {
            MultiNode<N, A> prev, next;

            if (lastOfSpan.minor(LO_HI_ORDER).label() < minorIndex)
            {
                prev       = lastOfSpan;
                next       = lastOfSpan.next(LO_HI_ORDER);
                lastOfSpan = multiNode;
            }
            else /* if (first.minorEndpoint(LO_HI_ORDER).label() > minorIndex) */
            {
                prev        = firstOfSpan.previous(LO_HI_ORDER);
                next        = firstOfSpan;
                firstOfSpan = multiNode;
            }

            MultiNode.insertBetween(prev, multiNode, next, LO_HI_ORDER);
            return true;
        }

        // This algorithm inspired by Collections.binarySearch for non RandomAccess lists
        // Completes with n join traversals and log(n) label comparisons.
        private boolean addStrictlyBetweenEdges(final MultiNode<N, A> multiNode, final int minorIndex)
        {
            MultiNode<N, A> connected = retrieveOrCreate(multiNode, minorIndex);

            boolean wasPreviouslyAbsent = (connected == multiNode);

            if (! wasPreviouslyAbsent)
                CHAIN.merge(connected, multiNode.edge());

            return wasPreviouslyAbsent;
        }


        //----------------------------------------------------------------------------------------
        public void remove(final MultiNode<N, A> multiNode)
        {
            int minorIndex = multiNode.minor(LO_HI_ORDER).label();

            assert multiNode.major(LO_HI_ORDER).label() == majorIndex();
            assert !isBeyondEdge( minorIndex );

            if (isAtEdge(minorIndex))
                removeAtEdges(minorIndex);
            else
                removeStrictlyBetweenEdges(multiNode);

            size--;
        }

        private void removeAtEdges(int minorIndex)
        {
            MultiNode<N, A> prev, next;

            if (firstOfSpan.minor(LO_HI_ORDER).label() == minorIndex)
            {
                if (lastOfSpan.minor(LO_HI_ORDER).label() == minorIndex)
                {
                    INDEX.remove( majorIndex() );

                    prev = firstOfSpan.previous(LO_HI_ORDER);
                    next = lastOfSpan.next(LO_HI_ORDER);
                }
                else
                {
                    prev = firstOfSpan.previous(LO_HI_ORDER);
                    next = firstOfSpan.next(LO_HI_ORDER);

                    firstOfSpan = next;
                }
            }
            else /*if (last.minorEndpoint(LO_HI_ORDER).label() == minorIndex)*/
            {
                prev = lastOfSpan.previous(LO_HI_ORDER);
                next = lastOfSpan.next(LO_HI_ORDER);

                lastOfSpan = prev;
            }

            MultiNode.connect(prev, next, LO_HI_ORDER);
        }

        private void removeStrictlyBetweenEdges(MultiNode<N, A> multiNode)
        {
            MultiNode.connect(
                    multiNode.previous(LO_HI_ORDER),
                    multiNode.next(LO_HI_ORDER),
                    LO_HI_ORDER);
        }


        //----------------------------------------------------------------------------------------
//        private boolean isEdgeCase(int minorIndex)
//        {
//            return isBeyondEdge(minorIndex) ||
//                    isAtEdge(minorIndex);
//        }

        private boolean isAtEdge(int minorIndex)
        {
            return firstOfSpan.minor(LO_HI_ORDER).label() == minorIndex ||
                    lastOfSpan.minor(LO_HI_ORDER).label() == minorIndex;
        }

        private boolean isBeyondEdge(int minorIndex)
        {
            return firstOfSpan.minor(LO_HI_ORDER).label() > minorIndex ||
                    lastOfSpan.minor(LO_HI_ORDER).label() < minorIndex;
        }


        //----------------------------------------------------------------------------------------
        public String toString()
        {
            StringBuilder sb = new StringBuilder();

            sb.append('(');
            sb.append(firstOfSpan().major(LO_HI_ORDER).label());
            sb.append('|');

            for (MultiNode<N, A> cursor = firstOfSpan();
                 cursor != lastOfSpan();
                 cursor  = cursor.next(LO_HI_ORDER))
            {
                sb.append(cursor.minor(LO_HI_ORDER).label());
                sb.append(", ");
            }

            sb.append(lastOfSpan().minor(LO_HI_ORDER).label());
            sb.append(')');

            return sb.toString();
        }

        //----------------------------------------------------------------------------------------
        private MultiNode<N, A> retrieveOrCreate(
                final MultiNode<N, A> multiNode, final int minorIndex)
        {
            MultiNode<N, A> insertBefore = lastOfSpan;

            for (MultiNode<N, A> cursor = firstOfSpan;
                    cursor != lastOfSpan;
                    cursor = cursor.next(LO_HI_ORDER))
            {
                int cursorMinorIndex = cursor.minor(LO_HI_ORDER).label();

                if (minorIndex == cursorMinorIndex)
                {
                    return cursor;
                }
                else if (cursorMinorIndex > minorIndex)
                {
                    insertBefore = cursor;
                    break;
                }
            }

            MultiNode.insertBetween(
                    insertBefore.previous(LO_HI_ORDER), multiNode, insertBefore, LO_HI_ORDER);

            return multiNode;
        }


        // This algorithm inspired by Collections.binarySearch for non RandomAccess lists
        // Completes with n join traversals and log(n) label comparisons.
//        private MultiNode<N, A> retrieveOrCreate(
//                final MultiNode<N, A> multiNode, final int minorIndex)
//        {
//            int low        = 0;
//            int high       = degree - 1;
//            Cursor cursor  = new Cursor();
//
//            boolean         afterMiddle  = false;
//            MultiNode<N, A> midMultiNode = null;
//            while (low <= high)
//            {
//                int mid           = (low + high) >> 1;
//                    midMultiNode  = cursor.get(mid);
//                int midMinorIndex = midMultiNode.minorEndpoint(LO_HI_ORDER).label();
//
//                if (midMinorIndex < minorIndex)
//                {
//                    low = mid + 1;
//                    afterMiddle = true;
//                }
//                else if (midMinorIndex > minorIndex)
//                {
//                    high = mid - 1;
//                    afterMiddle = false;
//                }
//                else
//                {
//                    return midMultiNode;
//                }
//            }
//
//            assert midMultiNode != null;
//
//            MultiNode<N, A> prev = (afterMiddle ? midMultiNode : midMultiNode.previous(LO_HI_ORDER));
//            MultiNode<N, A> next = (afterMiddle ? midMultiNode.next(LO_HI_ORDER) : midMultiNode);
//
//            MultiNode.connect(prev, multiNode, next, LO_HI_ORDER);
//
//            return multiNode;
//        }
//
//        private class Cursor
//        {
//            private int pos;
//            private MultiNode<N, A> spanNode;
//
//            public Cursor()
//            {
//                pos = 0;
//                spanNode = first;
//            }
//
//            public MultiNode<N, A> get(int label)
//            {
//                if (pos < label)
//                {
//                    do
//                    {
//                        spanNode = spanNode.next(LO_HI_ORDER);
//                    }
//                    while (++pos != label);
//                }
//                else if (pos > label)
//                {
//                    do
//                    {
//                        spanNode = spanNode.previous(LO_HI_ORDER);
//                    }
//                    while (--pos != label);
//                }
//
//                return spanNode;
//            }
//        }
    }
}
