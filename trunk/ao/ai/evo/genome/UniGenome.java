package ao.ai.evo.genome;

import ao.ai.evo.chromosome.Chromosome;
import ao.ai.evo.fitness.Fitness;
import ao.ai.evo.primordial_soup.PrimordialSoup;

/**
 *
 */
public class UniGenome implements Genome<UniGenome>
{
    //--------------------------------------------------------------------
    private Fitness    fitness;
    private Chromosome solver;
    private int        evaluationCount;

    //--------------------------------------------------------------------
    public UniGenome(Chromosome problemSolver)
    {
        fitness = new Fitness();
        solver  = problemSolver;
    }


    //--------------------------------------------------------------------
    public void cumulate(Fitness delta)
    {
        fitness = fitness.cumulate( delta );
    }
    public void reduceFitness()
    {
        fitness = fitness.reduce();
    }


    //--------------------------------------------------------------------
    public int fitnessConfidence()
    {
        return fitness.confidence();
    }

    public int evaluationCount()
    {
        return evaluationCount;
    }

    public void evaluate()
    {
        evaluationCount++;
        solver.execute();
    }


    //--------------------------------------------------------------------
    public UniGenome replicate()
    {
        return new UniGenome( solver.replicate() );
    }

    public UniGenome recombine(UniGenome with)
    {
        return new UniGenome( solver.recombine(with.solver) );
    }

    public UniGenome microMutate(PrimordialSoup soup)
    {
        return new UniGenome( solver.microMutate(soup) );
    }

    public UniGenome macroMutate(PrimordialSoup soup)
    {
        return new UniGenome( solver.macroMutate(soup) );
    }


    //--------------------------------------------------------------------
    public int compareTo(UniGenome o)
    {
        return fitness.compareTo( o.fitness );
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return solver.toString();
    }
}
