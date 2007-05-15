package ao.ai.evo.primordial_soup;

import ao.ai.evo.promoter.affinity.Affinity;
import ao.ai.evo.promoter.Promoter;
import ao.ai.evo.genetic_material.GeneticMaterial;

import java.util.Set;
import java.util.HashSet;
import java.util.Collection;

/**
 * 
 */
public class GeneDistiller
{
    /**
     * Useful Affinities are those returned by an independant promoter
     *
     * Only genes with promoters consisting entirely
     *  of useful Affinities are returned.
     *
     * @param thoughts toDistill
     * @return distilled
     */
    public GenePool distill(GenePool thoughts)
    {
        Set<Affinity> inependantlyProduced = new HashSet<Affinity>();
        for (GeneticMaterial material : thoughts.material())
        {
            if (material.promoter().isIndependent())
            {
                inependantlyProduced
                        .add( material.promoter().productAffinity() );
            }
        }

        GenePool distilled = new GenePool();
        for (GeneticMaterial material : thoughts.material())
        {
            if (onlyUses(material.promoter(), inependantlyProduced))
            {
                distilled.add( material );
            }
        }

        return distilled;
    }

    private boolean onlyUses(
            Promoter             promoter,
            Collection<Affinity> affinities)
    {
        if (! affinities.contains(
                promoter.productAffinity() )) return false;

        for (Affinity locus : promoter.lociAffinities())
        {
            if (! affinities.contains(locus) ) return false;
        }

        return true;
    }

}
