package ao.graph;

import ao.graph.user.NodeData;
import ao.graph.user.EdgeWeight;
import ao.graph.struct.DataAndWeight;
import ao.graph.struct.NodeDataPair;
import ao.graph.struct.Endpoints;

/**
 * An abstract data structure that is not included in the default distro.
// * @param D
// * @param W
 */
public interface Graph<D extends NodeData<D>, W extends EdgeWeight<W>>
{
    /**
     * Adds the given data to the graph but does not join it to anything.
     * If the data is already present then nothing changes.
     * Some implementations may support labeling each data
     *  from 0..degree-1.
     * If so then the returned number is the associated label,
     *  otherwise  -1 is returned.
     *
     * @param nodeData item being added
     * @return the label associated with the data.
     */
    int add(D nodeData);


    /**
     * Joins nodeDataA and nodeDataB with and edge
     *  having the given weight.
     * If they are already linked then their new weight is
     *  existingEdge.mergeWith(givenEdge)
     *
     * If nodeDataA.equals(nodeDataB) then
     *     nothing happens and false is returned.
     *
     * Note: null edges are NOT allowed, use EdgeWeight.DUMMY.INSTANCE
     *          instead.
     *
     * @param nodeDataA the first of two nodes to connect.
     * @param nodeDataB the second of two nodes to connect.
     * @param edgeWeight the weight that will be used to
     *                      conenct (nodeDataA, nodeDataB)
     * @return true if the join was not previously present,
     *          false if join(nodeDataA, nodeDataB) already existed and
     *          was merged with the given weight.
     */
    boolean join(D nodeDataA, D nodeDataB, W edgeWeight);

    /**
     * The given nodes are merged into a single node.
     *  D merged = nodeDataA.mergeWith( nodeDataB )
     * Any weight that connected to either of them will be updated
     *  to point to <i>merged</i>.
     *
     * 
     * Psuedo code
     *  ------------------------------------------------------------------
     *  public Node_And_Edge merge( nodeData_A, nodeData_B)
     *      // used only as return value
     *      edge_AB = remove_edge_connecting( nodeData_A, nodeData_B )
     *
     *      merged_node = nodeData_A.merge_with( nodeData_B )
     *
     *      for (related_node : nodes_incident_to_either(
     *                                  nodeData_A, nodeData_B ))
     *          edge_RA = remove_edge_connecting( related_node, nodeData_A )
     *          edge_RB = remove_edge_connecting( related_node, nodeData_B )
     *
     *          merged_edge = merge_nullable_edges( edge_RA, edge_RB )
     *          join( related_node, merged_node, merged_edge )
     *
     *      return new_Node_And_Edge( merged_node, edge_AB )
     *
     *  ------------------------------------------------------------------
     *  private EdgeWeight merge_nullable_edges(
     *              edgeWeight_A, edgeWeight_B )
     *      if niether_are_null( edgeWeight_A, edgeWeight_B )
     *          return edgeWeight_A.merge_with( edgeWeight_B )
     *
     *      // willl never happen
     *      //if both_are_null( edgeWeight_A, edgeWeight_B )
     *      //    return null
     *
     *      if is_not_null( edgeWeight_A )
     *          return edgeWeight_A.mergeWith( edgeWeight_null )
     *
     *      //if is_not_null( edgeWeight_B )
     *      return edgeWeight_B.mergeWith( edgeWeight_null )
     *
     *  ------------------------------------------------------------------
     *  private EdgeWeight remove_edge_connecting( nodeData_A, nodeData_B )
     *      if an_edge_connects( nodeData_A, nodeData_B )
     *          existing_edge = edge_connecting( nodeData_A, nodeData_B )
     *          remove_connection( nodeData_A, nodeData_B )
     *          return existing_edge
     *      else
     *          return null
     *
     * @param nodeDataA first of two nodes to merge.
     * @param nodeDataB second of two nodes to merge.
     * @return The union resulting from nodeDataA.mergeWith(nodeDataB),
     *          and the weight that used to
     *          connect (nodeDataA, nodeDataB).
     */
    DataAndWeight<D, W> merge(D nodeDataA, D nodeDataB);

    /**
     * Same as merge(D nodeDataA, D nodeDataB) but with one exception.
     * If nodeR is connected to nodeDataA but not nodeDataB then
     *  the weight of (parent, nodeR) will be edgeR.mergeWith(nullWeight).
     *
     * Note that implementations may offer a way to specity a
     *  default nullWeight.
     *
     * @see Graph#merge(NodeData,NodeData)
     * @param nodeDataA first of two nodes to merge.
     * @param nodeDataB second of two nodes to merge.
     * @param nullWeight refer to above
     * @return The parent resulting from nodeDataA.mergeWith(nodeDataB),
     *          and the weight that used to connect (nodeDataA, nodeDataB).
     */
    DataAndWeight<D, W> merge(D nodeDataA, D nodeDataB, W nullWeight);


    /**
     * Searches for a pair of nodes that have no weight connecting them.
     * If more than one such pair exists, implemintations get to choose
     * which one to return.
     *
     * @return a pair of nodes that are not connected by an weight,
     *          or null if no such pair exisits.
     */
    NodeDataPair<D> antiEdge();

    /**
     * @return the pair of nodes who's connecting weight compares as
     *              the greatest.
     */
    Endpoints<D, W> nodesIncidentHeaviestEdge();

    /**
     * @return the pair of nodes who's connecting weight compares as
     *              the lest.
     */
    Endpoints<D, W> nodesIncidentLightestEdge();


    /*
     * Opperations that I do not need for now:
     *
     * public W disjoin(D nodeDataA, D nodeDataB);
     * public Collection<D> incidentTo(D nodeData);
     * public Collection<W> remove(D nodeData);
     * public W edgeConnecting(D nodeDataA, D nodeDataB);
     */
}
