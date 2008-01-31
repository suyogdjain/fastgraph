package ao.prophet.impl.analysis;

import ao.graph.Graph;
import ao.prophet.impl.cluster.Cluster;
import ao.prophet.impl.cluster.LeafCluster;
import ao.prophet.impl.relation.Relation;
import javolution.util.FastMap;

/**
 * Stores an item, its label, and those items that are
 *  incident and greater than it.
 */
public class ItemIncidence<I> implements Comparable<ItemIncidence<I>>
{
    //--------------------------------------------------------------------
    public static <I> ItemIncidence<I>
            greaterOf(
                ItemIncidence<I> itemA,
                ItemIncidence<I> itemB)
    {
        return (itemA.label < itemB.label)
                ? itemB
                : itemA;
    }

    public static <I> ItemIncidence<I>
            lesserOf(
                ItemIncidence<I> itemA,
                ItemIncidence<I> itemB)
    {
        return (itemA.label < itemB.label)
                ? itemA
                : itemB;
    }


    //--------------------------------------------------------------------
    private int            label;
    private LeafCluster<I> cluster;

    private FastMap<ItemIncidence<I>, Relation> hiIncident = null;

    public ItemIncidence(I item, int label)
    {
        this.label   = label;
        this.cluster = new LeafCluster<I>(item);
    }

    //--------------------------------------------------------------------
    // returns how many relations were added
    public int addToGraph(Graph<Cluster<I>, Relation> graph)
    {
        if (hiIncident == null || hiIncident.isEmpty()) return 0;

        int relationCount = 0;
        Cluster<I> loCluster = toCluster();

        for (FastMap.Entry<ItemIncidence<I>, Relation>
                e   = hiIncident.head(),
                end = hiIncident.tail();
             (e = e.getNext()) != end;)
        {
            graph.join(
                    loCluster,
                    e.getKey().toCluster(),
                    e.getValue() );
            relationCount++;
        }

        return relationCount;
    }


    //--------------------------------------------------------------------
    public void addHiIncident(
            ItemIncidence<I> hiItem,
            Relation relation)
    {
        FastMap.Entry<ItemIncidence<I>, Relation> existant =
                hiIncident().getEntry( hiItem );

        if (existant == null)
        {
            hiIncident.put(hiItem, relation);
        }
        else
        {
            existant.setValue(
                    existant.getValue().additiveMerge(relation) );
        }
    }

    private FastMap<ItemIncidence<I>, Relation> hiIncident()
    {
        return (hiIncident == null)
                ? (hiIncident =
                    new FastMap<ItemIncidence<I>, Relation>())
                : hiIncident;
    }


    //--------------------------------------------------------------------
    public int label()
    {
        return label;
    }

    public LeafCluster<I> toCluster()
    {
        return cluster;
    }


    //--------------------------------------------------------------------
    public int compareTo(ItemIncidence<I> o)
    {
        return (label - o.label);
    }
}
