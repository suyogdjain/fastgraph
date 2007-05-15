package ao.prophet.impl;

import ao.prophet.impl.appraisal.CumulativeAppraisal;
import ao.prophet.impl.appraisal.Appraisal;
import ao.prophet.impl.cluster.Cluster;
import ao.prophet.impl.analysis.ItemToClusterMap;
import ao.prophet.ItemFilter;
import ao.graph.Graph;
import javolution.util.FastMap;

import java.util.*;

/**
 * Remembers all currently active items.
 */
public class AllItems<I>
{
    //--------------------------------------------------------------------
    private final FastMap<I, CumulativeAppraisal> ITEMS;


    //--------------------------------------------------------------------
    public AllItems()
    {
        ITEMS = new FastMap<I, CumulativeAppraisal>();
        ITEMS.setShared( true );
    }


    //--------------------------------------------------------------------
    public boolean add(I item)
    {
        CumulativeAppraisal cumAppraisal = ITEMS.get( item );

        boolean wasAbsent = (cumAppraisal == null);

        if (wasAbsent)
            ITEMS.put( item, new CumulativeAppraisal() );

        return wasAbsent;
    }

    public boolean add(I item, Appraisal appraisal)
    {
        CumulativeAppraisal cumAppraisal = ITEMS.get( item );

        boolean wasAbsent = (cumAppraisal == null);

        if (wasAbsent)
            ITEMS.put( item, new CumulativeAppraisal(appraisal) );
        else
            cumAppraisal.add( appraisal );

        return wasAbsent;
    }


    //--------------------------------------------------------------------
    public int itemCount()
    {
        return ITEMS.size();
    }
    

    //--------------------------------------------------------------------
    public boolean remove(I item)
    {
        return (ITEMS.remove( item ) != null);
    }


    //--------------------------------------------------------------------
    public void addToGraph(
            Graph<Cluster<I>, ?> graph,
            ItemToClusterMap<I> map)
    {
        for (FastMap.Entry<I, CumulativeAppraisal> e   = ITEMS.head(),
                                                   end = ITEMS.tail();
             (e = e.getNext()) != end;)
        {
            graph.add( map.clusterOf(e.getKey()) );
        }
    }


    //--------------------------------------------------------------------
    public Collection<I> anonymousPrediction(
            int howMany, ItemFilter<I> filter )
    {
        if (ITEMS.isEmpty() || howMany == 0)
        {
            return Collections.emptyList();
        }

        SortedSet<AppraisedItem<I>> appraisedItems =
                new TreeSet<AppraisedItem<I>>();

        int label = 0;
        AppraisedItem<I> minProbable = null;
        for (FastMap.Entry<I, CumulativeAppraisal> e   = ITEMS.head(),
                                                   end = ITEMS.tail();
             (e = e.getNext()) != end; label++)
        {
            if (! filter.accept(e.getKey())) continue;

            if ( appraisedItems.size() < howMany )
            {
                appraisedItems.add(
                        new AppraisedItem<I>(
                                e.getKey(),
                                label,
                                e.getValue().value()) );

                if (appraisedItems.size() == howMany)
                {
                    minProbable = appraisedItems.first();
                }
            }
            // a false "minProbable might be null" warning
            //  might be reported here.
            else if (e.getValue().value() > minProbable.value)
            {
                appraisedItems.remove( minProbable );

                minProbable.set(
                        e.getKey(),
                        label,
                        e.getValue().value() );
                appraisedItems.add( minProbable );

                minProbable = appraisedItems.first();
            }
        }

        LinkedList<I> heaviest = new LinkedList<I>(  );
        for (AppraisedItem<I> appraisedItem : appraisedItems)
        {
            heaviest.addFirst( appraisedItem.item );
        }

        return heaviest;
    }

    // used like struct, not like traditional object.
    private static class AppraisedItem<I>
            implements Comparable<AppraisedItem<I>>
    {
        public I     item;
        public int   label;
        public float value;

        public AppraisedItem(I item, int label, float value)
        {
            set( item, label, value );
        }

        public void set(I item, int label, float value)
        {
            this.item  = item;
            this.label = label;
            this.value = value;
        }

        public int compareTo(AppraisedItem<I> o)
        {
            int valueCmp = Float.compare(value, o.value);

            return (valueCmp != 0
                    ? valueCmp
                    : label - o.label);
        }

        public boolean equals(Object obj)
        {
            return obj != null &&
                    obj instanceof AppraisedItem &&
                    (label == ((AppraisedItem) obj).label);

        }
    }
}
