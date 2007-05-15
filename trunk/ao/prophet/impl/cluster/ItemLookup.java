package ao.prophet.impl.cluster;

/**
 * Bidirectional map between items and their labels.
 */
public class ItemLookup<I>
{
    public int labelOf(I item)
    {
        return -1;
    }

    public LeafCluster<I> leafClusterLabeled(int label)
    {
        return null;
    }
}
