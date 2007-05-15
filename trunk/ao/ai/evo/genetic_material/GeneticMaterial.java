package ao.ai.evo.genetic_material;

import ao.ai.evo.coding.Coding;
import ao.ai.evo.gene.GpGene;
import ao.ai.evo.promoter.Promoter;

/**
 * ThoughtFactory
 */
public interface GeneticMaterial
{
    public Coding coding();
    
    public Promoter promoter();

    public GpGene construct();
}
