package ao.prophet.impl;

import ao.graph.user.EdgeWeightDomain;
import ao.graph.impl.common.SimpleAbsDomain;
import ao.prophet.Prophet;
import ao.prophet.ItemFilter;
import ao.prophet.impl.analysis.ItemAppraisalClusterAnalysis;
import ao.prophet.impl.appraisal.Appraisal;
import ao.prophet.impl.appraisal.AppraisalTally;
import ao.prophet.impl.cluster.Cluster;
import ao.prophet.impl.cluster.FinalPly;
import ao.prophet.impl.cluster.LeafCluster;
import ao.prophet.impl.cluster.InternalCluster;
import ao.prophet.impl.relation.Relation;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Uses Graph.
// * @param U
// * @param I
 */
public class ProphetImpl<U, I> implements Prophet<U, I>
{
    //--------------------------------------------------------------------
    private final AllItems<I>          ITEMS;
    private final AppraisalTally<U, I> APPRAISALS;

    private final ThreadPoolExecutor ANALYSIS_EXECUTOR;
    private final Runnable           CLUSTER_ANALYSIS_TASK;

    private final int MAX_ITEMS_TIMES_USERS = 1024 * 1024 * 128 / 4;


    //--------------------------------------------------------------------
    /** */
    public ProphetImpl()
    {
        System.out.println("initializing");
        ITEMS              = new AllItems<I>();
        APPRAISALS         = new AppraisalTally<U, I>(30000, 3000000, 60);
        ANALYSIS_EXECUTOR  = new ThreadPoolExecutor(
                                    1, 1,
                                    0, TimeUnit.MILLISECONDS,
                                    new LinkedBlockingQueue<Runnable>());
        CLUSTER_ANALYSIS_TASK =
                new ClusterAnalysis<U, I>(
                        APPRAISALS,
                        new SimpleAbsDomain<Relation>(512),
                        ITEMS);
    }


    //------------------------------------------------------------------------
    public void addItem(I item)
    {
        if (item == null) return;

        if (ITEMS.add(item))
        {
            requestClusterAnalysis();
        }
    }

    public void removeItem(I item)
    {
        if (item == null) return;

        if (ITEMS.remove(item))
        {
            APPRAISALS.removeItem( item );

            requestClusterAnalysis();
        }
    }

    public void removeUser(U user)
    {
        if (user == null) return;

        APPRAISALS.removeUser( user );

        requestClusterAnalysis();
    }


    //--------------------------------------------------------------------
    public void likes(U user, I item)
    {
        appraise(user, item, Appraisal.POSITIVE);
    }

    public void dislikes(U user, I item)
    {
        appraise(user, item, Appraisal.NEGATIVE);
    }

    private void appraise(U user, I item, Appraisal appraisal)
    {
        if (item == null) return;

        if (user != null)
        {
            ITEMS.add( item, appraisal );
            APPRAISALS.tally(user, item, appraisal);

            requestClusterAnalysis();
        }
        else
        {
            addItem( item );
        }
    }


    //--------------------------------------------------------------------
    /** */
    public void evictUserOverflow()
    {
        while (ITEMS.itemCount() * APPRAISALS.userCount()
                    > MAX_ITEMS_TIMES_USERS)
        {
            removeUser( APPRAISALS.leastRecentlyUsed() );
        }
    }



    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public Collection<I> predict(U user, int howMany)
    {
        return predict(user, howMany, ItemFilter.Impl.ACCEPT_ALL);
    }

    public Collection<I> predict(
            U user, int howMany, ItemFilter<I> filter)
    {
        assert howMany >= 0;
        assert filter != null;

        if (user == null)
        {
            return ITEMS.anonymousPrediction( howMany, filter );
        }

        Collection<I> prediction =
                        APPRAISALS.mostLikely( user, howMany, filter );

        return (prediction == null)
               ? ITEMS.anonymousPrediction( howMany, filter )
               : prediction;
    }


    //--------------------------------------------------------------------
    private void requestClusterAnalysis()
    {
        if (ANALYSIS_EXECUTOR.getQueue().isEmpty())
        {
            ANALYSIS_EXECUTOR.submit( CLUSTER_ANALYSIS_TASK );
        }
    }

    private static class ClusterAnalysis<U, I> implements Runnable
    {
        private AppraisalTally<U, I>       APPRAISALS;
        private EdgeWeightDomain<Relation> EDGE_WEIGHT_DOMAIN;
        private AllItems<I>                ITEMS;

        public ClusterAnalysis(
                AppraisalTally<U, I>       appraisals,
                EdgeWeightDomain<Relation> edgeWeightDomain,
                AllItems<I>                items)
        {
            this.APPRAISALS = appraisals;
            this.EDGE_WEIGHT_DOMAIN = edgeWeightDomain;
            this.ITEMS = items;
        }

        public void run()
        {
            try
            {
                analyse();
            }
            catch (Throwable exception)
            {
                exception.printStackTrace();
            }
        }

        private void analyse()
        {
            long timeBefore = System.currentTimeMillis();
            ItemAppraisalClusterAnalysis<U, I> analyzer =
                new ItemAppraisalClusterAnalysis<U, I>(APPRAISALS);

            Cluster<I> root =
                    analyzer.agglomerative(ITEMS, EDGE_WEIGHT_DOMAIN);
//            System.out.println("clusters: " + root);

            final FinalPly<I> finalPly = new FinalPly<I>();
            root.preOrderTraverse( new Cluster.Visitor<I>() {
                public void visit(Cluster<I> cluster) {}
                public void visitInternal(InternalCluster<I> internalCluster){}

                public void visitLeaf(LeafCluster<I> leafCluster) {
                    finalPly.add( leafCluster );
                }
            } );

            APPRAISALS.updateClusters( finalPly.buildLookup() );
            System.out.println(
                    "cluster analysis took " +
                            (System.currentTimeMillis() - timeBefore));
        }
    }


    //--------------------------------------------------------------------
//    public void dispose()
//    {
//        if (! ANALYSIS_EXECUTOR.isTerminated())
//        {
//            ANALYSIS_EXECUTOR.shutdownNow();
//        }
//    }

    @Override
    protected void finalize() throws Throwable
    {
//        System.out.println("finalizing");
        try
        {
            if (! ANALYSIS_EXECUTOR.isTerminated())
            {
                ANALYSIS_EXECUTOR.shutdownNow();
            }
        }
        finally
        {
            super.finalize();
        }
    }
}
