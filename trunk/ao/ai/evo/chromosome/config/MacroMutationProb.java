package ao.ai.evo.chromosome.config;

import ao.ai.evo.gene.Locus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * 
 */
@Singleton
public class MacroMutationProb implements LocusChooser
{
    //--------------------------------------------------------------------
    public static final String MAX_LEAF_DISTANCE_ID = "mmp.maxDist";
    public static final String MAX_MUTATION_SIZE_ID = "mmp.maxSize";

    
    //--------------------------------------------------------------------
    private int maxLeafDistance;
    private int maxMutationSize;


    //--------------------------------------------------------------------
    public MacroMutationProb() {}


    //--------------------------------------------------------------------
    @Inject
    public void injectMaxLeafDistance(
            @Named(MAX_LEAF_DISTANCE_ID)
                int injectedMaxLeafDistance)
    {
        maxLeafDistance = injectedMaxLeafDistance; 
    }

    @Inject
    public void injectMaxMutationSize(
            @Named(MAX_MUTATION_SIZE_ID)
                int injectedMaxMutationSize)
    {
        maxMutationSize = injectedMaxMutationSize;
    }


    //--------------------------------------------------------------------
    public LocusWeight branchUseWeight(
            Locus branchDetails)
    {
        if (branchDetails.size() <= maxMutationSize &&
            branchDetails.lociNestingDepth() <= maxLeafDistance)
        {
            double val = -Math.sqrt(branchDetails.size());
            return LocusWeight.randomized(val);
        }
        else
        {
            return LocusWeight.UNUSABLE;
        }
    }
}