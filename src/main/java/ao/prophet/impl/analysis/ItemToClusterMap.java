package ao.prophet.impl.analysis;

import ao.prophet.impl.cluster.Cluster;

/**
 * For decupling the storing of items and adding them to a graph.
 */
public interface ItemToClusterMap<I>
{
    public Cluster<I> clusterOf( I item );
}
