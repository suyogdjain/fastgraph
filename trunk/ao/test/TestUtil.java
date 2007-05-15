package ao.test;

import ao.graph.common.FlatNodeData;
import ao.graph.user.NodeData;
import ao.prophet.impl.relation.Relation;
import ao.prophet.impl.appraisal.Appraisal;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods commonly used for unit testing.
 * Mostly random data generation stuff.
 */
public class TestUtil
{
    private TestUtil() {}


    //------------------------------------------------------------------------
    /**
     * 
     * @param howMany 
     * @return 
     */
    public static List<NodeData> randomNodes(int howMany)
    {
        List<NodeData> randomItems = new ArrayList<NodeData>();

        for (int i = 0; i < howMany; i++)
        {
            randomItems.add( new FlatNodeData() );
        }

        return randomItems;
    }

    //------------------------------------------------------------------------
    /**
     * 
     * @return 
     */
    public static Relation randomRelation()
    {
        Relation similarity = randomSimpleRelation();

        while ( Math.random() < 0.75 )
        {
            similarity = similarity.mergeWith( randomSimpleRelation() );
        }

        return similarity;
    }

    private static Relation randomSimpleRelation()
    {
        Appraisal appraisalA = randomAppraisal();
        Appraisal appraisalB = randomAppraisal();

        return appraisalA.relationTo( appraisalB );
    }


    //------------------------------------------------------------------------
//    public static List<AppraisedItem> randomAppraisedItems(int count)
//    {
//        AppraisedItem[] appraisedItems = new AppraisedItem[count];
//        for (int i = 0; i < count; i++)
//        {
//            appraisedItems[i] = randomAppraisedItem();
//        }
//
//
//        return Arrays.asList(appraisedItems);
//    }

//    public static AppraisedItem randomAppraisedItem()
//    {
//        Id        item      = Id.nextInstance();
//        Appraisal appraisal = randomAppraisal();
//
//        return new AppraisedItem(item, appraisal);
//    }


    //------------------------------------------------------------------------
    /**
     * 
     * @return 
     */
    public static Appraisal randomAppraisal()
    {
        return (Math.random() < 0.5)
               ? Appraisal.POSITIVE
               : Appraisal.NEGATIVE;
    }
}
