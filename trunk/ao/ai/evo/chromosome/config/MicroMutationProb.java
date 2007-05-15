package ao.ai.evo.chromosome.config;

import ao.ai.evo.gene.Locus;

/**
 *
 */
public class MicroMutationProb implements LocusChooser
{
    //--------------------------------------------------------------------
    public MicroMutationProb() {}


    //--------------------------------------------------------------------
    public LocusWeight branchUseWeight(
            Locus branchDetails)
    {
        return LocusWeight.randomized(1.0);
    }
}
