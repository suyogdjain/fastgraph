package ao.ai.evo.genetic_material;

import ao.ai.evo.coding.Coding;
import ao.ai.evo.promoter.Promoter;

/**
 *
 */
public class AffineGeneticMaterial extends AbstractGpMaterial
{
    //--------------------------------------------------------------------
    private final GeneticMaterial baseMaterial;
    private final Promoter        affineWith;


    //--------------------------------------------------------------------
    public AffineGeneticMaterial(
            GeneticMaterial mapFrom,
            Promoter        affinePromoter)
    {
//        if (! mapFrom.promoter().isAffine( affinePromoter ))
//        {
//            mapFrom.promoter().isAffine( affinePromoter );
//        }
//        assert mapFrom.promoter().isAffine( affinePromoter )
//                : mapFrom + " incompatible with " + affinePromoter;

        baseMaterial = mapFrom;
        affineWith   = affinePromoter;
    }


    //--------------------------------------------------------------------
    public Coding coding()
    {
        return baseMaterial.coding();
    }

    public Promoter promoter()
    {
        return affineWith;
    }
}
