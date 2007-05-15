package ao.graph.impl.common.struct;

import ao.graph.user.NodeData;

/**
 * Adds an int label to a data.
 */
public class LabeledNode<N extends NodeData<N>>
        implements Comparable<LabeledNode<N>>
{
    //--------------------------------------------------------------------
    public static <ND extends NodeData<ND>> LabeledNode<ND>
            greaterOf(
                LabeledNode<ND> nodeA,
                LabeledNode<ND> nodeB)
    {
        assert nodeA.LABEL != nodeB.LABEL;

        return (nodeA.LABEL < nodeB.LABEL)
                ? nodeB
                : nodeA;
    }

    public static <ND extends NodeData<ND>> LabeledNode<ND>
            lesserOf(
                LabeledNode<ND> nodeA,
                LabeledNode<ND> nodeB)
    {
        assert nodeA.LABEL != nodeB.LABEL;

        return (nodeA.LABEL < nodeB.LABEL)
                ? nodeA
                : nodeB;
    }


    //--------------------------------------------------------------------
    private final N   NODE;
    private final int LABEL;


    //--------------------------------------------------------------------
    public LabeledNode(N node, int label)
    {
        this.NODE  = node;
        this.LABEL = label;
    }


    //--------------------------------------------------------------------
    public N node()
    {
        return NODE;
    }

    public int label()
    {
        return LABEL;
    }


    //--------------------------------------------------------------------
    public int compareTo(LabeledNode<N> that)
    {
        return (this.LABEL < that.LABEL ? -1 :
                (this.LABEL > that.LABEL ? 1 : 0));
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return LABEL + "  " + NODE.toString();
    }

    public boolean equals(Object obj)
    {
        return obj != null &&
                obj instanceof LabeledNode &&
                LABEL == ((LabeledNode) obj).LABEL;
    }

    public int hashCode()
    {
        return LABEL;
    }
}
