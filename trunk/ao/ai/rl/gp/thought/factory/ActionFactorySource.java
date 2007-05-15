package ao.ai.rl.gp.thought.factory;

import ao.ai.evo.genetic_material.IoGeneticMaterial;
import ao.ai.evo.primordial_soup.GenePool;

/**
 *
 */
public interface ActionFactorySource
{
    public IoGeneticMaterial instance();

    public GenePool genes();

    public Object instanceObject();
}
