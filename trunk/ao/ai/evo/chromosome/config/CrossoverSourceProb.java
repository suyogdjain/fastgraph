package ao.ai.evo.chromosome.config;

import ao.ai.evo.gene.GpGene;
import ao.ai.evo.gene.Locus;

/**
 *
 */
public class CrossoverSourceProb implements LocusChooser
{
    //--------------------------------------------------------------------
    private int    maxCrossSize;
    private GpGene crossDestination;


    //--------------------------------------------------------------------
    public CrossoverSourceProb(
            int     maxCrossSize,
            GpGene crossDestination)
    {
        this.maxCrossSize     = maxCrossSize;
        this.crossDestination = crossDestination;
    }


    //--------------------------------------------------------------------
    public LocusWeight branchUseWeight(Locus details)
    {
        if (details.size() <= maxCrossSize &&
                crossDestination.promoter().productAffinity().isAffine(
                        details.gene().promoter().productAffinity() ))
        {
            return LocusWeight.randomized(
                        details.size() * details.size());
        }
        else
        {
            return LocusWeight.UNUSABLE;
        }
    }
}
