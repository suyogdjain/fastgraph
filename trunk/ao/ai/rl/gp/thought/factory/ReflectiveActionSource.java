package ao.ai.rl.gp.thought.factory;

import ao.ai.evo.genetic_material.IoGeneticMaterial;
import ao.ai.evo.primordial_soup.GenePool;
import ao.ai.evo.primordial_soup.PrimordialSoup;

/**
 *
 */
public class ReflectiveActionSource implements ActionFactorySource
{
    //--------------------------------------------------------------------
    private PrimordialSoup soup;
    private Object         instance;

    //--------------------------------------------------------------------
    public ReflectiveActionSource(PrimordialSoup primordialSoup)
    {
        soup = primordialSoup;
    }


    //--------------------------------------------------------------------
    public IoGeneticMaterial instance()
    {
        return null;
    }

    public GenePool genes()
    {
        return null;
    }

    public Object instanceObject()
    {
        return null;
    }
}
