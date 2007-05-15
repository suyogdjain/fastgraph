package ao.prophet.impl.appraisal;

import ao.prophet.impl.relation.Relation;
import ao.prophet.impl.ItemWeights;
import ao.prophet.impl.cluster.FinalPly;
import ao.prophet.impl.cluster.LeafCluster;
import ao.prophet.ItemFilter;

import java.util.Collection;
import java.util.BitSet;

/**
 * holds item + appraisal
 */
public class AppraisalHistory<I>
{
    private static final float PREV_GEN_APPRAISAL_DAMPER = 0.8f;


    private final I[]         ITEMS;
    private final Appraisal[] APPRAISALS;
    private final BitSet      NEED_UP_PROPAGATION;

    private       int         nextIndex;
    private       int         size;

    private FinalPly.Lookup<I> leafClusterLookup;
    private ItemWeights<I>     weights;


    @SuppressWarnings("unchecked")
    public AppraisalHistory(int maxSize, FinalPly.Lookup<I> currFinalPly)
    {
        assert maxSize > 0;

        ITEMS      = (I[])( new Object[maxSize] );
        APPRAISALS = new Appraisal[maxSize];
        nextIndex  = 0;

        NEED_UP_PROPAGATION = new BitSet(maxSize);

        leafClusterLookup = currFinalPly;
        weights = (currFinalPly == null ? null : currFinalPly.newWeights());
    }

    public void clear()
    {
        for (int c = 0, i = absIndexOf(0);
             c < size;
             c++,       i = incIndex(i))
        {
            ITEMS     [ i ] = null;
            APPRAISALS[ i ] = null;
        }

        size      = 0;
        nextIndex = 0;
        NEED_UP_PROPAGATION.clear();

        if (weights != null) weights.clear();
    }

    public void examine(Historian<I> historian)
    {
        for (int countA = 0, indexA = absIndexOf(0);
             countA < size;
             countA++,       indexA = incIndex(indexA))
        {
            for (int countB = countA + 1, indexB = incIndex(indexA);
                 countB < size;
                 countB++,                indexB = incIndex(indexB))
            {
                historian.study(
                        ITEMS[ indexA ], ITEMS[ indexB ],
                        APPRAISALS[ indexA ].relationTo(APPRAISALS[ indexB ]));
            }
        }
    }

    //-----------------------------------------------------------
    public synchronized void add(I item, Appraisal appraisal)
    {
        int addAt = nextIndex;

        ITEMS     [ addAt ] = item;
        APPRAISALS[ addAt ] = appraisal;

        nextIndex = incIndex(nextIndex);
        if (size < ITEMS.length)
        {
            size++;
        }

        if (leafClusterLookup != null)
        {
            LeafCluster<I> leafCluster = leafClusterLookup.leafClusterOf( item );

            if (leafCluster != null)
            {
                leafCluster.upPropagate( weights, appraisal.value() );
                NEED_UP_PROPAGATION.clear( addAt );
            }
            else NEED_UP_PROPAGATION.set( addAt );
        }
        else NEED_UP_PROPAGATION.set( addAt );
    }

    public synchronized void removeEarliest()
    {
        assert size > 0;

        int toRemove = absIndexOf(0);

        ITEMS     [ toRemove ] = null;
        APPRAISALS[ toRemove ] = null;

        NEED_UP_PROPAGATION.clear( toRemove );

        size--;
    }

    public synchronized boolean remove(I item)
    {
        int firstAbsIndex = absIndexOf(0);

        for (int count = 0,   index = firstAbsIndex;
                 count < size;
                 count++,     index = incIndex(index))
        {
            if (ITEMS[ index ].equals( item ))
            {
                if (index != firstAbsIndex)
                {
                    ITEMS     [ index ] = ITEMS[ firstAbsIndex ];
                    APPRAISALS[ index ] = APPRAISALS[ firstAbsIndex ];

                    NEED_UP_PROPAGATION.set( index, NEED_UP_PROPAGATION.get(firstAbsIndex) );
                }

                ITEMS     [ firstAbsIndex ] = null;
                APPRAISALS[ firstAbsIndex ] = null;

                NEED_UP_PROPAGATION.clear( firstAbsIndex );

                size--;

                return true;
            }
        }

        return false;
    }


    //-----------------------------------------------------------
    public Collection<I> mostLikely(int howMany, ItemFilter<I> filter)
    {
        return (weights == null
                ? null
                : weights.heaviest(howMany, filter));
    }


    //-----------------------------------------------------------
    public synchronized void updateClusters(
            FinalPly.Lookup<I> latestVersion, ItemWeights.Translator<I> translator)
    {
        weights = (leafClusterLookup == null)
                    ? latestVersion.newWeights()
                    : translator.translate(weights, PREV_GEN_APPRAISAL_DAMPER);

        leafClusterLookup = latestVersion;
        
        upPropagateLeftovers();
    }

    private void upPropagateLeftovers()
    {
        for (int i = NEED_UP_PROPAGATION.nextSetBit(0);
                 i >= 0;
                 i = NEED_UP_PROPAGATION.nextSetBit(i + 1))
        {
            LeafCluster<I> leafCluster = leafClusterLookup.leafClusterOf( ITEMS[i] );

            if (leafCluster != null)
            {
                leafCluster.upPropagate( weights, APPRAISALS[ i ].value() );
            }
        }

        NEED_UP_PROPAGATION.clear();
    }


    //-----------------------------------------------------------
    public int size()
    {
        return size;
    }


    //-----------------------------------------------------------
    private int absIndexOf(int index)
    {
        assert index < size;
        return ((nextIndex - (size - index)) + ITEMS.length) % ITEMS.length;
    }

    private int incIndex( int indexToIncrement )
    {
        return (indexToIncrement + 1) % ITEMS.length;
    }


    //-----------------------------------------------------------
    public static interface Historian<I>
    {
//        void study(
//                I itemA, Appraisal appraisalA,
//                I itemB, Appraisal appraisalB);

        void study(I itemA, I itemB, Relation edge);

//        void studyDone();
    }
}
