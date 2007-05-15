package ao.test;

import ao.graph.Graph;
import ao.graph.common.FlatNodeData;
import ao.graph.common.RealEdgeWeight;
import ao.graph.impl.common.SimpleAbsDomain;
import ao.graph.impl.incidence.IncidenceListGraph;
import ao.graph.struct.Endpoints;
import ao.graph.struct.NodeDataPair;
import ao.util.Rand;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Benchmark implements Runnable
{
    public void run()
    {
        try
        {
            unsafeRun();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void unsafeRun() throws Exception
    {
        // warmup
        runGC();
        usedMemory();

        int runCount = 0;
        for (int numNodes = 0;
                 numNodes <= 50;
                 numNodes += (++runCount % 5 == 0) ? 50 : 0)
        {
            System.out.println();
            System.out.println(numNodes + " nodes");
            for (double density = 0.05; density <= 1.01; density += 0.05)
            {
                List<FlatNodeData> nodes = randomNodes(numNodes);
                List<RealEdgeWeight> edges = randomEdges(256);

                runGC();
                long startMem = usedMemory();

                Graph<FlatNodeData, RealEdgeWeight> graph =
                    new IncidenceListGraph<FlatNodeData, RealEdgeWeight>(
                            new SimpleAbsDomain<RealEdgeWeight>(64), null);
//                FastGraph<FlatNodeData, RealEdgeWeight> graph =
//                    new FastGraph<FlatNodeData, RealEdgeWeight>(
//                            new SimpleAbsDomain<RealEdgeWeight>(64), null);
//                MatrixBuffer<FlatNodeData, RealEdgeWeight> buffer =
//                        new MatrixBuffer<FlatNodeData, RealEdgeWeight>(graph);

                long popStart = System.currentTimeMillis();
                int numUniqueEdges = 0;
                for (int i = 0; i < numNodes; i++)
                {
                    for (int j = i + 1; j < numNodes; j++)
                    {
                        if (Rand.nextDouble() < density)
                        {
//                            if (buffer.join(
//                                    nodes.get(i),
//                                    nodes.get(j),
//                                    randomFromList(edges)))
                            if (graph.join(
                                    nodes.get(i),
                                    nodes.get(j),
                                    randomFromList(edges)))
                            {
                                numUniqueEdges++;
                            }
                        }
                    }
                }
                long popEnd = System.currentTimeMillis();

                runGC();
                long beforeFlush = usedMemory();
                long flushStart = System.currentTimeMillis();
//                buffer.flush();
                long flushEnd = System.currentTimeMillis();
//                runGC();
                long afterInit = usedMemory();

                long agglomStart = System.currentTimeMillis();
                int numMergedNodes = 0;
                while (true)
                {
                    Endpoints<FlatNodeData, RealEdgeWeight>
                            nodesWithHeaviestEdge =
                                graph.nodesIncidentHeaviestEdge();

                    NodeDataPair<FlatNodeData> toMerge =
                            (nodesWithHeaviestEdge == null
                            ? graph.antiEdge()
                            : nodesWithHeaviestEdge.nodes());

                    if (toMerge == null) break;

                    graph.merge( toMerge.dataA(), toMerge.dataB() );
                    numMergedNodes++;
                }
                long agglomEnd = System.currentTimeMillis();

                runGC();
                long afterCluster = usedMemory();

                output(numMergedNodes,
                       numUniqueEdges,
                       popEnd    - popStart,
                       flushEnd  - flushStart,
                       agglomEnd - agglomStart,
                       beforeFlush  - startMem,
                       afterInit    - startMem,
                       afterCluster - startMem);
                System.out.print(".");

                if (((popEnd    - popStart) +
                     (flushEnd  - flushStart) +
                     (agglomEnd - agglomStart)) > 60000) break;
            }
        }
    }

    private static PrintStream out;
    static
    {
        try
        {
            out = new PrintStream(
                    new FileOutputStream(
                            "bench_hash_mem3_" +
                                System.currentTimeMillis() +
                            ".txt"));
            out.println("nodes\tedges\tpop t\tflush t\tagglom t\tpop mem\tb4 agglom mem\tagglom mem");
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }
    private static void output(
            int  numMergedNodes,
            int  numUniqueEdges,
            long popMillis,
            long flushMillis,
            long agglomMillis,
            long populateMem,
            long onAgglomMem,
            long agglomMem) throws Exception
    {
        out.println(
                numMergedNodes + "\t" +
                numUniqueEdges + "\t" +
                popMillis      + "\t" +
                flushMillis    + "\t" +
                agglomMillis   + "\t" +
                populateMem    + "\t" +
                onAgglomMem    + "\t" +
                agglomMem);

        if (Rand.nextInt(100) == 0)
        {
            out.flush();
        }
    }


    private static <T> T randomFromList(List<T> list)
    {
        return list.get( Rand.nextInt(list.size()) );
    }

    private static List<RealEdgeWeight> randomEdges(int howMany)
    {
        List<RealEdgeWeight> weights = new ArrayList<RealEdgeWeight>();
        for (int i = 0; i < howMany; i++)
        {
            weights.add( new RealEdgeWeight(Rand.nextFloat()) );
        }
        return weights;
    }

    private static List<FlatNodeData> randomNodes(int howMany)
    {
        List<FlatNodeData> nodes = new ArrayList<FlatNodeData>();
        for (int i = 0; i < howMany; i++)
        {
            nodes.add( new FlatNodeData() );
        }
        return nodes;
    }


    //--------------------------------------------------------------------
    private static final Runtime s_runtime = Runtime.getRuntime ();

    private static void runGC() throws Exception
    {
        // It helps to call Runtime.gc()
        // using several method calls:
        for (int r = 0; r < 4; ++ r) _runGC();
    }
    private static void _runGC() throws Exception
    {
        long usedMem1 = usedMemory(), usedMem2 = Long.MAX_VALUE;
        for (int i = 0; (usedMem1 < usedMem2) && (i < 500); ++ i)
        {
            s_runtime.runFinalization();
            s_runtime.gc();
            Thread.yield();

            usedMem2 = usedMem1;
            usedMem1 = usedMemory();
        }
    }
    private static long usedMemory() throws Exception
    {
        return s_runtime.totalMemory() - s_runtime.freeMemory();
    }
}
