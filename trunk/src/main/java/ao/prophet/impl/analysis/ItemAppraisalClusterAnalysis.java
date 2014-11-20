package ao.prophet.impl.analysis;

import ao.graph.Graph;
import ao.graph.impl.fast.FastGraph;
import ao.graph.struct.Endpoints;
import ao.graph.struct.DataAndWeight;
import ao.graph.struct.NodeDataPair;
import ao.graph.user.EdgeWeightDomain;
import ao.prophet.impl.AllItems;
import ao.prophet.impl.appraisal.AppraisalHistory;
import ao.prophet.impl.appraisal.AppraisalTally;
import ao.prophet.impl.cluster.Cluster;
import ao.prophet.impl.cluster.InternalCluster;
import ao.prophet.impl.cluster.LeafCluster;
import ao.prophet.impl.relation.Relation;
import javolution.util.FastMap;

/**
 * Perform cluster analysis.
 */
public class ItemAppraisalClusterAnalysis<U, I>
{
    //--------------------------------------------------------------------
    // lo => hi
    private FastMap<I, ItemIncidence<I>> LO_HI_INDEX;
    private LeafCluster<I> firstLeaf;


    //--------------------------------------------------------------------
    public ItemAppraisalClusterAnalysis(AppraisalTally<U, I> appraisals)
    {
        LO_HI_INDEX = new FastMap<I, ItemIncidence<I>>();

        appraisals.examine( new AppraisalHistory.Historian<I>() {
            public void study(I itemA, I itemB, Relation edge)
            {
                add( itemA, itemB, edge );
            }
        });
    }


    //--------------------------------------------------------------------
    public Cluster<I> agglomerative(
            AllItems<I> allItems, EdgeWeightDomain<Relation> domain)
    {
        Graph<Cluster<I>, Relation> graph =
                new FastGraph<Cluster<I>, Relation>(
                        domain, Relation.NEUTRAL);

        allItems.addToGraph(graph, new ItemToClusterMapImpl());

        populateGraphWithRelations(graph);

        return agglomerativeClusterAnalysis(graph);
    }

    private Cluster<I> agglomerativeClusterAnalysis(
            Graph<Cluster<I>, Relation> graph)
    {
        if (firstLeaf == null) return null;

        //int n = 0;
        InternalCluster<I> root = null;
        while (true)
        {
            //System.out.println("n = " + (n++));
            Endpoints<Cluster<I>, Relation> mostRelatedClusters =
                    graph.nodesIncidentHeaviestEdge();

            NodeDataPair<Cluster<I>> toMerge;
            if (mostRelatedClusters == null)
            {
                toMerge = graph.antiEdge();

                if (toMerge == null && root == null)
                {
                    return firstLeaf;
                }
            }
            else
            {
                // not sure if this ever made much sense
                if (mostRelatedClusters.weight().isLighterThanUnrelated())
                {
                    toMerge = graph.antiEdge();
                    if (toMerge == null)
                    {
                        toMerge = mostRelatedClusters.nodes();
                    }
                }
                else
                {
                    toMerge = mostRelatedClusters.nodes();
                }
            }

            if (toMerge == null) break;

            DataAndWeight<Cluster<I>, Relation> merged =
                    graph.merge( toMerge.dataA(), toMerge.dataB() );

            root = (InternalCluster<I>) merged.data();
            root.relationBetweenChildren( merged.weight() );
        }
        return root;
    }

    private void populateGraphWithRelations(
            Graph<Cluster<I>, Relation> graph)
    {
        if (LO_HI_INDEX.isEmpty()) return;
        firstLeaf = LO_HI_INDEX.head().getNext().getValue().toCluster();

//        int relationCount = 0;
        for (FastMap.Entry<I, ItemIncidence<I>> e   = LO_HI_INDEX.head(),
                                                end = LO_HI_INDEX.tail();
             (e = e.getNext()) != end;)
        {
            e.getValue().addToGraph( graph );
//            relationCount += e.getValue().addToGraph( graph );
        }
//        System.out.println("relationCount = " + relationCount);

        LO_HI_INDEX = null;
    }


    //--------------------------------------------------------------------
    private void add(I itemA, I itemB, Relation relation)
    {
        ItemIncidence<I> incidenceA = itemIncidence( itemA );
        ItemIncidence<I> incidenceB = itemIncidence( itemB );

        ItemIncidence<I> lesserIncidence  =
                ItemIncidence.lesserOf(  incidenceA, incidenceB );
        ItemIncidence<I> greaterIncidence =
                ItemIncidence.greaterOf( incidenceA, incidenceB );

        lesserIncidence.addHiIncident( greaterIncidence, relation );
    }

    private ItemIncidence<I> itemIncidence(I of)
    {
        ItemIncidence<I> hiIncident = LO_HI_INDEX.get( of );

        if (hiIncident == null)
        {
            ItemIncidence<I> newHiIncidence =
                    new ItemIncidence<I>(of, LO_HI_INDEX.size());
            LO_HI_INDEX.put( of, newHiIncidence );

            return newHiIncidence;
        }
        else
        {
            return hiIncident;
        }
    }

    private class ItemToClusterMapImpl
            implements ItemToClusterMap<I>
    {
        public Cluster<I> clusterOf(I item)
        {
            return itemIncidence( item ).toCluster();
        }
    }
}
