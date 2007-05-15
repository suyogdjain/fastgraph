package ao.ai.evo.primordial_soup;

import ao.ai.evo.genetic_material.GeneticMaterial;
import ao.ai.evo.promoter.Promoter;
import ao.ai.evo.promoter.affinity.Affinity;
import ao.util.rand.Rand;

import java.util.List;


/**
 *
 */
public class PrimordialSoup
{
    //--------------------------------------------------------------------
    private final GenePermuter  permuter;
    private final GeneDistiller distiller;
    private       GenePool      cleanGenes;


    //--------------------------------------------------------------------
    public PrimordialSoup()
    {
        permuter  = new GenePermuter();
        distiller = new GeneDistiller();
    }


    //--------------------------------------------------------------------
    public void add(GeneticMaterial material)
    {
        permuter.add( material );
        distill();
    }

    public void addAll(GenePool materials)
    {
        permuter.addAll( materials );
        distill();
    }

    public void addMethods(Class clazz)
    {
        permuter.addMethods( clazz );
        distill();
    }


    //--------------------------------------------------------------------
    private void distill()
    {
        cleanGenes = distiller.distill( permuter.rawGenes() );
    }


    //--------------------------------------------------------------------
    /**
     * Return some GeneticMaterial who's Paromoter isAffine with the
     *  given promoter.
     *
     * @param product ...
     * @return ...
     */
    public GeneticMaterial synthesize(Promoter product)
    {
        List<GeneticMaterial> matches = cleanGenes.materialLike(product);
        return matches.isEmpty()
                ? null
                : Rand.fromList( matches );
    }


    //--------------------------------------------------------------------
    /**
     * @param productAffinity look for genetic material that has
     *  a promoter whos product is affine to this given one.
     * @param needIndependent loci() == 0
     * @return matching genetic material
     */
    public GeneticMaterial
            synthesize(Affinity productAffinity,
                       boolean  needIndependent)
    {
        List<GeneticMaterial> matches =
                cleanGenes.materialReturning(
                        productAffinity, needIndependent);
        return matches.isEmpty()
                ? null
                : Rand.fromList( matches );
    }
}
