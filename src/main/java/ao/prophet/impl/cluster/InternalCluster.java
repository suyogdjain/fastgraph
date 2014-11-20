package ao.prophet.impl.cluster;

import ao.prophet.impl.ItemWeights;
import ao.prophet.impl.relation.Relation;

/**
 * References two child clusters.
 */
public class InternalCluster<I> extends Cluster<I>
{
    //--------------------------------------------------------------------
    private final Cluster<I> LEFT_CHILD;
    private final Cluster<I> RIGHT_CHILD;

    private Relation childRelation;


    //--------------------------------------------------------------------
    public InternalCluster(Cluster<I> leftChild, Cluster<I> rightChild)
    {
        LEFT_CHILD  = leftChild;
        RIGHT_CHILD = rightChild;
    }


    //--------------------------------------------------------------------
    public void relationBetweenChildren(Relation relation)
    {
        assert childRelation == null;

        childRelation = (relation == null
                         ? Relation.NEUTRAL
                         : relation);
    }


    //--------------------------------------------------------------------
    public int rightmostLeafLabel()
    {
        return RIGHT_CHILD.rightmostLeafLabel();
    }

    public int leftmostLeafLabel()
    {
        return LEFT_CHILD.leftmostLeafLabel();
    }

    public double leftChildProportion()
    {
        return ((double) LEFT_CHILD.size()) / size();
    }

    public Relation childRelation()
    {
        return childRelation;
    }


    //--------------------------------------------------------------------
    public void preOrderTraverse(Visitor<I> visitor)
    {
        visitor.visit( this );
        visitor.visitInternal( this );
        LEFT_CHILD.preOrderTraverse( visitor );
        RIGHT_CHILD.preOrderTraverse( visitor );
    }


    //--------------------------------------------------------------------
    public void upPropagate(
            ItemWeights<I> itemWeights,
            Cluster<I> fromChild,
            float appraisalValue)
    {
        Cluster<I> otherChild = (LEFT_CHILD == fromChild
                                 ? RIGHT_CHILD : LEFT_CHILD );

        float valueToPropagate =
                appraisalValue * childRelation.itemWeightScaleFactor();

        otherChild.downPropagate( itemWeights, valueToPropagate );

        InternalCluster<I> parent = parent();
        if (parent != null)
        {
            parent.upPropagate( itemWeights, this, valueToPropagate );
        }
    }


    //-----------------------------------------------------------
    public String toString()
    {
//        return "(" + LEFT_CHILD    + "|" +
//                     childRelation + "|" +
//                     RIGHT_CHILD   + ")";
        return "(" + LEFT_CHILD + "|" + RIGHT_CHILD + ")";
    }
}
