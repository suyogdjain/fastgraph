package ao.graph.user;

/**
 * Things that can be added to a graph.
// * @param D
 */
public interface NodeData<D extends NodeData>
{
    /**
     * When LinkedGraph.merge(NodeData n1, NodeData n2) is called and there is an weight
     * connecting (n1, n2) they are merged into one data.
     * As a result of this method call neither of the merged Nodes should change.
     *
     * @param other the data to merge with
     * @return the merged data
     */
    public D mergeWith(D other);

    // both should be overwritten for use in hash table.
    @Override public boolean equals(Object obj);
    @Override public int     hashCode();
}
