package ao.prophet.impl.cluster;

import ao.graph.user.NodeData;
import ao.prophet.impl.ItemWeights;

/**
 * A composit used in a clustering algorithm to determine item relations.
 */
public abstract class Cluster<I> implements NodeData<Cluster<I>>
{
    //--------------------------------------------------------------------
    private InternalCluster<I> parent;


    //--------------------------------------------------------------------
    public InternalCluster<I> parent()
    {
        return parent;
    }


    //--------------------------------------------------------------------
    public Cluster<I> mergeWith(Cluster<I> that)
    {
        assert this.parent == null && that.parent == null;

        InternalCluster<I> merged = new InternalCluster<I>(this, that);

        this.parent = merged;
        that.parent = merged;

        return merged;
    }


    //--------------------------------------------------------------------
    public void downPropagate(ItemWeights<I> itemWeights, float weight)
    {
        if (Math.abs(weight) > 0.0001)
        {
            itemWeights.add(
                    leftmostLeafLabel(),
                    rightmostLeafLabel(),
                    weight );
        }
    }

    //--------------------------------------------------------------------
    public int size()
    {
        return rightmostLeafLabel() - leftmostLeafLabel() + 1;
    }


    //--------------------------------------------------------------------
    public abstract int rightmostLeafLabel();
    public abstract int leftmostLeafLabel();


    //--------------------------------------------------------------------
    public abstract void preOrderTraverse(Visitor<I> visitor );


    //--------------------------------------------------------------------
    public static interface Visitor<I>
    {
        public void visit(Cluster<I> cluster);
        public void visitLeaf(LeafCluster<I> cluster);
        public void visitInternal(InternalCluster<I> cluster);
    }
}
