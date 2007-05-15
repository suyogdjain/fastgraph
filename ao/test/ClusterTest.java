package ao.test;

import ao.graph.Graph;
import ao.graph.impl.common.SimpleAbsDomain;
import ao.graph.impl.fast.BufferedFastGraph;
import ao.graph.struct.DataAndWeight;
import ao.graph.struct.Endpoints;
import ao.graph.struct.NodeDataPair;
import ao.prophet.impl.cluster.Cluster;
import ao.prophet.impl.cluster.InternalCluster;
import ao.prophet.impl.cluster.LeafCluster;
import ao.prophet.impl.cluster.gui.Topology;
import ao.prophet.impl.relation.Relation;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ClusterTest implements Runnable
{
    //--------------------------------------------------------------------
    public void run()
    {
        try
        {
            clusterVisual();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //--------------------------------------------------------------------
    private void clusterVisual() throws Exception
    {
        Graph<Cluster<Integer>, Relation> relations =
                new BufferedFastGraph<Cluster<Integer>, Relation>(
                        new SimpleAbsDomain<Relation>(64),
                        Relation.NEUTRAL);

        List<Cluster<Integer>> nodes =
                new ArrayList<Cluster<Integer>>();
        for (int i = 0; i < 9; i++)
        {
            nodes.add( new LeafCluster<Integer>(i) );
        }

//        for (int i = 0; i < nodes.size(); i++)
//        {
//            for (int j = i + 1; j < nodes.size(); j++)
//            {
//                if (Rand.nextDouble() < 0.4)
//                {
//                    relations.join(
//                            nodes.get(i),
//                            nodes.get(j),
//                            (Rand.nextBoolean()
//                                ? Relation.POSITIVE
//                                : Relation.NEGATIVE)
//                    );
//                }
//            }
//        }

        relations.join(nodes.get(0), nodes.get(1), Relation.POSITIVE);
        relations.join(nodes.get(0), nodes.get(2), Relation.POSITIVE);
        relations.join(nodes.get(1), nodes.get(2), Relation.POSITIVE);

        relations.join(nodes.get(3), nodes.get(4), Relation.POSITIVE);
        relations.join(nodes.get(3), nodes.get(5), Relation.POSITIVE);
        relations.join(nodes.get(4), nodes.get(5), Relation.POSITIVE);

        relations.join(nodes.get(6), nodes.get(7), Relation.POSITIVE);
        relations.join(nodes.get(6), nodes.get(8), Relation.POSITIVE);
        relations.join(nodes.get(7), nodes.get(8), Relation.POSITIVE);

//        relations.join(nodes.get(2), nodes.get(7), Relation.POSITIVE);

        Cluster<Integer> root = agglomerativeClusterAnalysis(relations);
        System.out.println(root);

        start();
        frame.getContentPane().add( Topology.visualize(root) );
        frame.pack();
    }


    //--------------------------------------------------------------------
    // doesn't work when there is only one cluster.
    private <I> Cluster<I> agglomerativeClusterAnalysis(
            Graph<Cluster<I>, Relation> graph)
    {
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
                    return null;
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


    //--------------------------------------------------------------------
    private JFrame frame;

    public void start()
    {
        if (frame == null)
        {
            frame = new JFrame("Cluster Analysis Demo");
            frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

            frame.setVisible(true);
        }
    }

    public void end()
    {
        frame.dispose();
        frame = null;
    }

    //--------------------------------------------------------------------
//    @Override
//    protected void finalize() throws Throwable
//    {
//        end();
//        super.finalize();
//    }
}
