package ao.prophet.impl.cluster;

import ao.prophet.impl.ItemWeights;
import javolution.util.FastList;

import java.util.HashMap;
import java.util.Map;

/**
 * Bidirectional map between items and their labels.
 */
public class FinalPly<I>
{
    //--------------------------------------------------------------------
    private final FastList<LeafCluster<I>> LEAFS;


    //--------------------------------------------------------------------
    public FinalPly()
    {
        LEAFS = new FastList<LeafCluster<I>>();
    }


    //--------------------------------------------------------------------
    public void add(LeafCluster<I> leafCluster)
    {
        LEAFS.add( leafCluster );
    }

    public Lookup<I> buildLookup()
    {
        return new Lookup<I>(LEAFS);
    }


    //--------------------------------------------------------------------
    public static class Lookup<I>
    {
        private final I[] ITEMS;
        private final Map<I, LeafCluster<I>> LEAF_CLUSTERS;

        @SuppressWarnings("unchecked")
        private Lookup( FastList<LeafCluster<I>> leafs )
        {
            ITEMS         = (I[])( new Object[leafs.size()] );
            LEAF_CLUSTERS =
                    new HashMap<I, LeafCluster<I>>( leafs.size() );

            populateItems(leafs);
        }

        private void populateItems(FastList<LeafCluster<I>> leafs)
        {
            int label = 0;
            for (FastList.Node<LeafCluster<I>> leaf = leafs.head(),
                                               end  = leafs.tail();
                 (leaf = leaf.getNext()) != end;)
            {

                leaf.getValue().label( label );
                ITEMS[ label ] = leaf.getValue().item();
                label++;

                LEAF_CLUSTERS.put(
                        leaf.getValue().item(),
                        leaf.getValue() );
            }
        }


        //----------------------------------------------------------------
        public LeafCluster<I> leafClusterOf(I item)
        {
            return LEAF_CLUSTERS.get(item);
        }

        public I itemLabled( int label )
        {
            return (0 <= label && label < ITEMS.length)
                   ? ITEMS[ label ]
                   : null;
        }


        //----------------------------------------------------------------
        public ItemWeights.Translator<I> newWeightTranslator(Lookup<I> to)
        {
            int newFromOld[] = new int[ to.ITEMS.length ];

            for (int i = 0; i < newFromOld.length; i++)
            {
                I              newItem = to.ITEMS[i];
                LeafCluster<I> oldLeafForNewItem =
                                leafClusterOf( newItem );

                newFromOld[ i ] =
                        (oldLeafForNewItem == null
                                ? -1
                                : oldLeafForNewItem.label());
            }

            return new ItemWeights.Translator<I>(newFromOld, this, to);
        }

        public ItemWeights<I> newWeights()
        {
            return new ItemWeights<I>( ITEMS.length, this );
        }
    }
}
