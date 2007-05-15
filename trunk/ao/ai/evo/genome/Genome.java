package ao.ai.evo.genome;

import ao.ai.evo.heredity.Heritable;
import ao.ai.evo.fitness.Fitness;

/**
 * One individual
 */
public interface Genome<T extends Genome>
        extends Heritable<T>,
                Comparable<T>
{
    public int fitnessConfidence();
    public int evaluationCount();

    public void cumulate(Fitness fitness);
    public void reduceFitness();

    public void evaluate();
}
