package ao.ai.evo.deme;

import ao.ai.evo.primordial_soup.PrimordialSoup;
import ao.ai.evo.genome.Genome;
import ao.ai.evo.fitness.Feedback;

/**
 * A Deme occupies a Niche.
 */
public interface Deme
{
    public Genome nextIndividual(PrimordialSoup soup);


    /**
     * Reports that the given genome did
     *  not take any desired action upon request.
     * Presumably this will get rid of it or give
     *  it some penalty.
     *
     * @param genome ...
     */
    public void didNotAct(Genome genome);

    public void actionPerformed(Genome genome);
    

    public void select(
            Feedback pressure,
            PrimordialSoup soup);
}

