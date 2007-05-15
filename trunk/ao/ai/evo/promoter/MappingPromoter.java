package ao.ai.evo.promoter;

import ao.ai.evo.promoter.affinity.Affinity;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 */
public class MappingPromoter extends AbstractPromoter
{
    //--------------------------------------------------------------------
    private final Promoter basePromoter;
    private final int      promoterMap[];


    //--------------------------------------------------------------------
    public MappingPromoter(Promoter from, int map[])
    {
        basePromoter = from;
        promoterMap  = map;
    }


    //--------------------------------------------------------------------
    public MappingPromoter withLocus(int locus, Affinity ofType)
    {
        return new MappingPromoter(
                basePromoter.withLocus(
                        promoterMap[locus], ofType),
                promoterMap);
    }

    public MappingPromoter withProduct(Affinity ofType)
    {
        return new MappingPromoter(
                basePromoter.withProduct( ofType ),
                promoterMap);
    }


    //--------------------------------------------------------------------
    public int loci()
    {
        return basePromoter.loci();
    }

    public boolean isIndependent()
    {
        return basePromoter.isIndependent();
    }

    public Affinity productAffinity()
    {
        return basePromoter.productAffinity();
    }

    public Affinity locusAffinity(int loci)
    {
        return basePromoter.locusAffinity( promoterMap[loci] );
    }

    public Collection<Affinity> lociAffinities()
    {
        Collection<Affinity> lociAffinities =
                new ArrayList<Affinity>(loci());
        for (int i = 0; i < loci(); i++)
        {
            lociAffinities.add( locusAffinity(i) );
        }
        return lociAffinities;
    }
}
