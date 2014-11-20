package ao.prophet.impl.cluster;

import ao.prophet.impl.ItemWeights;

/**
 * Contains a single item.
 */
public class LeafCluster<I> extends Cluster<I>
{
    //--------------------------------------------------------------------
    private       int label;
    private final I   ITEM;


    //--------------------------------------------------------------------
    public LeafCluster(I item)
    {
        ITEM  = item;
        label = -1;
    }


    //--------------------------------------------------------------------
    public void label(int label)
    {
        assert this.label == -1;

        this.label = label;
    }

    // used when mapping one label system onto another.
    public int label()
    {
        return label;
    }


    public I item()
    {
        return ITEM;
    }


    //--------------------------------------------------------------------
    public int rightmostLeafLabel()
    {
        return label;
    }
    public int leftmostLeafLabel()
    {
        return label;
    }


    //--------------------------------------------------------------------
    public void preOrderTraverse(
            Visitor<I> visitor)
    {
        visitor.visit( this );
        visitor.visitLeaf( this );
    }

    public void upPropagate(
            ItemWeights<I> itemWeights,
            float appraisalValue )
    {
        // makes sure that it does not come up again for the same person.
        itemWeights.add( label, -4000000.0f );

        InternalCluster<I> parent = parent();
        if (parent != null)
        {
            parent.upPropagate( itemWeights, this, appraisalValue);
        }
    }

    public void downPropagate(ItemWeights<I> itemWeights, float weight)
    {
        itemWeights.add( label, weight );
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return ITEM.toString();
    }
}
