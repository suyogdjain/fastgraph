package ao.ai.evo.chromosome.config;

import ao.ai.evo.gene.GpGene;
import ao.ai.evo.gene.synthesis.WeightedLocus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 *
 */
@Singleton
public class TreeGenParams
{
    //--------------------------------------------------------------------
    public static final String MAX_DIST_ID = "tgp.leafDistance";
    public static final String MAX_SIZE_ID = "tgp.size";


    //--------------------------------------------------------------------
    private int size;
    private int leafDistance;


    //--------------------------------------------------------------------
    public TreeGenParams() {}


    //--------------------------------------------------------------------
    @Inject
    public void injectMaxDepth(@Named(MAX_DIST_ID) int maxDepth)
    {
        leafDistance = maxDepth;
    }

    @Inject
    public void injectMaxSize(@Named(MAX_SIZE_ID) int maxSize)
    {
        size = maxSize;
    }


    //--------------------------------------------------------------------
    private TreeGenParams(
            int maxDepth,
            int maxSize)
    {
        size = maxSize;
        leafDistance = maxDepth;
    }


    //--------------------------------------------------------------------
    public int maxLeafDistance()
    {
        return leafDistance;
    }

    public int maxSize()
    {
        return size;
    }

    public int nextTreeSize()
    {
        return size;
    }


    //--------------------------------------------------------------------
    public TreeGenParams constrain(int maxDepth, int maxSize)
    {
        return new TreeGenParams(
                Math.min(leafDistance, maxDepth),
                Math.min(size,  maxSize));
    }

    public TreeGenParams constrain(GpGene tree, WeightedLocus at)
    {
        int cappedDepth  = maxLeafDistance() - at.lociNestingDepth();
        int sizeCapDelta = nextTreeSize()    - tree.size();

        return constrain(cappedDepth,
                         Math.max(at.size() + sizeCapDelta, 1));
    }
}
