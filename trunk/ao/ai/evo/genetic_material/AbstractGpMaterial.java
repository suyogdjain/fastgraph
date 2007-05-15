package ao.ai.evo.genetic_material;

import ao.ai.evo.coding.Coding;
import ao.ai.evo.gene.GpGene;
import ao.ai.evo.promoter.Promoter;

/**
 *
 */
public abstract class AbstractGpMaterial
        implements GeneticMaterial
{
    //--------------------------------------------------------------------
    public abstract Coding   coding();
    public abstract Promoter promoter();


    //--------------------------------------------------------------------
    public GpGene construct()
    {
        return new GpGene(coding(), promoter());
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return String.valueOf( coding() );
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || !(o instanceof AbstractGpMaterial)) return false;

        AbstractGpMaterial that = (AbstractGpMaterial) o;
        return coding().equals( that.coding() ) &&
                promoter().equals( that.promoter() );
    }

    @Override
    public int hashCode()
    {
        return coding().hashCode() +
               31 * promoter().hashCode();
    }
}
