package ao.ai.evo.deme;

import ao.ai.evo.genome.Genome;
import ao.ai.evo.primordial_soup.PrimordialSoup;
import ao.util.rand.Rand;

import java.util.List;

/**
 *
 */
public class RandomDeme extends AbstractDeme
{
    //--------------------------------------------------------------------
    protected Genome nextIndividual(List<Genome> outOf)
    {
        return Rand.fromList( outOf );
    }


    //--------------------------------------------------------------------
    protected void afterSelection(PrimordialSoup soup)
    {
        learn( soup );
    }
}
