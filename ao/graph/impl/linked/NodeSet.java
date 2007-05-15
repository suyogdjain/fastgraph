package ao.graph.impl.linked;

import ao.graph.impl.common.struct.LabeledNode;
import ao.graph.impl.common.struct.NodeDataPairImpl;
import ao.graph.impl.linked.chain.MultiNode;
import ao.graph.impl.linked.index.ChainSpan;
import ao.graph.user.NodeData;
import ao.graph.struct.NodeDataPair;
import javolution.util.FastMap;

import java.util.Iterator;

/**
 * Tracks the current working set of NODES for a LinkedGraph.
 *
 * When a data is added to a graph it must first be added here.
 * When a data is removed from a graph it must lastly be removed from here.
 *
 * NOT THREAD SAFE!!
 */
public class NodeSet<D extends NodeData<D>>
{
    private final FastMap<D, LabeledNode<D>> NODES; // maybe convert to Integer2Object map?
    private int latestIndex;

    public NodeSet()
    {
        NODES = new FastMap<D, LabeledNode<D>>();
        latestIndex = 0;
    }

    /**
     * This implemintation has time efficiency of O(n).
     * It takes advantage of the fact that a fully liked MultiNode chain
     * has predictable structure:
     *  ChainSpan(n + 1).capacity == ChainSpan(n).capacity + 1   
     *
     * As soon as a ChainSpan breaks that pattern we know that there
     * exists some data with label &lt; ChainSpan.majorIndex that is not
     * yet in the MultiNode chain.
     *
     * @param hiLoItr an iterator of ChainSpans in hiLo order.
     * @return a pair of nodes that are not linked by an weight.
     */
    public NodeDataPair<D> unlinked(Iterator<ChainSpan> hiLoItr)
    {
        if (NODES.size() < 2) return null;

        int expectedSpanSize = NODES.size() - 1;

        for (FastMap.Entry<D, LabeledNode<D>> e = NODES.tail(), start = NODES.head().getNext();
             (e = e.getPrevious()) != start;)
        {
            if (! hiLoItr.hasNext())
            {
                return givenAndPreviouseNode(e);
            }

            ChainSpan span = hiLoItr.next();

            if (! span.majorIndexEquals( e.getValue().label() ))
            {
                return givenAndPreviouseNode(e);
            }
            else if (span.size() < expectedSpanSize)
            {
                return givenAndMissingFromChain(e, span);
            }

            expectedSpanSize--;
        }

        return null;
    }

    private NodeDataPair<D> givenAndMissingFromChain(
            FastMap.Entry<D, LabeledNode<D>> majorOfSpan,
            ChainSpan span)
    {
        FastMap.Entry<D, LabeledNode<D>> entryCursor = majorOfSpan.getPrevious();
        MultiNode                        spanCursor  = span.firstOfSpan();

        while ( spanCursor != null &&
                entryCursor.getValue().equals(spanCursor.minor(false)) )
        {
            entryCursor = entryCursor.getPrevious();
            spanCursor  = spanCursor.next(false);
        }

        return new NodeDataPairImpl<D>(majorOfSpan.getKey(), entryCursor.getKey());
    }

    private NodeDataPair<D> givenAndPreviouseNode(FastMap.Entry<D, LabeledNode<D>> entry)
    {
        return new NodeDataPairImpl<D>(entry.getKey(), entry.getPrevious().getKey());
    }


    /**
     * Every time a data is added its label is:
     *  label = latestIndex
     *  latestIndex = latestIndex + 1
     *
     * The first data has label 0.
     * If the requested data is not present in the set then it is added.
     *
     * @param node nodeData
     * @return the label (i.e. order of creation) of this data, plus the data itself.
     */
    public LabeledNode<D> index(D node)
    {
        assert node != null;

        LabeledNode<D> index = NODES.get(node);

        if (index == null)
        {
            index = new LabeledNode<D>(node, latestIndex++);
            NODES.put(node, index);
        }

        return index;
    }


    public void remove(LabeledNode<D> labeledNode)
    {
        remove( labeledNode.node() );
    }
    public void remove(D node)
    {
        NODES.remove(node);
    }
}
