package ao.ai.evo.gene.synthesis;

import ao.ai.evo.chromosome.config.LocusWeight;
import ao.ai.evo.gene.Locus;

/**
 *
 */
public class WeightedLocus extends Locus
{
    //--------------------------------------------------------------------
    private final LocusWeight weight;


    //--------------------------------------------------------------------
    public WeightedLocus(
            Locus details,
            LocusWeight weight)
    {
        super(details);
        this.weight = weight;
    }


    //--------------------------------------------------------------------
    public LocusWeight weight()
    {
        return weight;
    }
}
