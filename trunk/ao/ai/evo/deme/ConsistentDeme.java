package ao.ai.evo.deme;

import ao.ai.evo.genome.Genome;
import ao.ai.evo.primordial_soup.PrimordialSoup;
import ao.util.rand.Rand;

import java.util.List;


/**
 *
 */
public class ConsistentDeme extends AbstractDeme
{
    //--------------------------------------------------------------------
    private Genome active;


    //--------------------------------------------------------------------
    protected Genome nextIndividual(List<Genome> outOf)
    {
        if (active == null)
        {
            active = Rand.fromList( outOf );
        }
        return active;
    }


    //--------------------------------------------------------------------
    public void didNotAct(Genome genome)
    {
        super.didNotAct( genome );
        active = null;
    }

    protected void afterSelection(PrimordialSoup soup)
    {
        if (Rand.nextDouble() < 0.2)
        {
            learn( soup );
        }
    }
}
