package ao.ai.evo.coding.diversity;

import ao.ai.evo.genetic_material.AffineGeneticMaterial;
import ao.ai.evo.genetic_material.GeneticMaterial;
import ao.ai.evo.promoter.affinity.Affinity;
import ao.ai.evo.promoter.Promoter;
import ao.ai.evo.promoter.affinity.AffinityDomain;

import java.util.*;

/**
 *
 */
public class ByTypeDiversifier
{
    //--------------------------------------------------------------------
    public ByTypeDiversifier() {}


    //--------------------------------------------------------------------
    public Collection<GeneticMaterial> diversify(
            GeneticMaterial geneticMaterial,
            AffinityDomain  domain)
    {
        Set<Promoter> promoterSet = new HashSet<Promoter>();
        Promoter      promoter    = geneticMaterial.promoter();

        for (Affinity productAffinity :
                domain.loosen(promoter.productAffinity()))
        {
            Promoter productBound =
                    promoter.withProduct( productAffinity );
            promoterSet.add( productBound );

            for (Promoter argBound :
                    diversityArgs(productBound, 0, domain))
            {
                promoterSet.add( argBound );
            }
        }

        Collection<GeneticMaterial> diverse =
                new ArrayList<GeneticMaterial>();
        for (Promoter affinePromoter : promoterSet)
        {
            diverse.add(
                    new AffineGeneticMaterial(
                            geneticMaterial, affinePromoter));
        }
        return diverse;
    }

    private Collection<Promoter> diversityArgs(
            Promoter       of,
            int            atLocus,
            AffinityDomain domain)
    {
        if (atLocus >= of.loci()) return Collections.emptyList();

        LinkedList<Promoter> promoters =
                new LinkedList<Promoter>();
        for (Affinity argAffinity :
                domain.tighten(of.locusAffinity( atLocus )))
        {
            Promoter boundLocus =
                    of.withLocus( atLocus, argAffinity );
            promoters.add( boundLocus );
            promoters.addAll(
                    diversityArgs(boundLocus, atLocus + 1, domain) );
        }
        return promoters;
    }
}
