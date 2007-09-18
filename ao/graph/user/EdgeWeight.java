package ao.graph.user;

/**
 * For our purpose, edges and edge weights are the same thing.
 *
 * E connection between two Nodes.
// * @param E
 */
public interface EdgeWeight<E extends EdgeWeight<E>>/* extends Comparable<E>*/
{
    /**
     * When LinkedGraph.merge(NodeData n1, NodeData n2) is called and they are both connected
     * to another data n3 then edges (n1, n3) and (n2, n3) are merged.
     * As a result of this method call neither of the merged Nodes should change.
     *
     * @param other the data to merge with
     * @return the merged data
     */
    public E mergeWith(E other);


    /**
     * Weighted edges symbolize relationships between nodes which are considered
     *  to have some value, for instance, distance or lag time.
     *
     * @return the asFloat
     */
    public float asFloat();


    /**
     * Use when you don't care about EdgeWeight order.
     */
    public static class DUMMY implements EdgeWeight<DUMMY>
    {
        /**
         */
        public static EdgeWeight INSTANCE = new DUMMY();

        // all DUMMY edge weights are the same, so no need for constructor.
        private DUMMY() {}

        public DUMMY mergeWith(DUMMY other)
        {
            return this;
        }

        public float asFloat()
        {
            return 0;
        }

//        public int compareTo(DUMMY o)
//        {
//            return 0;
//        }
    }
}
