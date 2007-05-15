package ao.ai.evo.primordial_soup;

import ao.ai.evo.coding.diversity.ByTypeDiversifier;
import ao.ai.evo.genetic_material.GeneticMaterial;
import ao.ai.evo.promoter.affinity.AffinityDomain;
import ao.ai.evo.promoter.affinity.CachedDomain;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 */
public class GenePermuter
{
    //--------------------------------------------------------------------
    private final ByTypeDiversifier     diversifier;
    private final GenePool              geneticMaterial;
    private final AffinityDomain        affinityDomain;
    private final GeneticMaterialSource materialSource;

    private final Collection<Class> classPool;


    //--------------------------------------------------------------------
    public GenePermuter()
    {
        diversifier     = new ByTypeDiversifier();
        affinityDomain  = new CachedDomain();
        geneticMaterial = new GenePool();
        materialSource  = new GeneticMaterialSource();

        classPool       = new ArrayList<Class>();
    }


    //--------------------------------------------------------------------
    public void add(GeneticMaterial material)
    {
        doAdd( material );
        reAddMethods();
    }

    private void doAdd(GeneticMaterial material)
    {
        affinityDomain.add( material.promoter().productAffinity() );
        geneticMaterial.addAll(
                diversifier.diversify( material, affinityDomain ));
    }


    //--------------------------------------------------------------------
    public void addAll(GenePool materials)
    {
        doAddAll(materials);
        reAddMethods();
    }

    private void doAddAll(GenePool materials)
    {
        for (GeneticMaterial material : materials.material())
        {
            doAdd( material );
        }
    }


    //--------------------------------------------------------------------
    public void addMethods(Class clazz)
    {
        classPool.add( clazz );
        doAddAll( materialSource.addMethods(clazz, affinityDomain) );
        reAddMethods();
    }

    private void reAddMethods()
    {
        // this is done to recalculate the affinity sets
        //  of existing functions.
        for (Class addedClass : classPool)
        {
            doAddAll( materialSource.addMethods(
                            addedClass, affinityDomain) );
        }
    }


    //--------------------------------------------------------------------
    public GenePool rawGenes()
    {
        return geneticMaterial;
    }
}
