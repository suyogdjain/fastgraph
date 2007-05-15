package ao.ai.evo.genetic_material;

import ao.ai.evo.coding.Coding;
import ao.ai.evo.promoter.Promoter;

/**
 * Returns whatever is given.
 */
public class LiteralGeneticMaterial extends AbstractGpMaterial
{
    //--------------------------------------------------------------------
    private final Coding   coding;
    private final Promoter promoter;


    //--------------------------------------------------------------------
    public LiteralGeneticMaterial(
            Coding   literalCoding,
            Promoter literalPromoter)
    {
        coding   = literalCoding;
        promoter = literalPromoter;
    }


    //--------------------------------------------------------------------
    public Coding coding()
    {
        return coding;
    }

    public Promoter promoter()
    {
        return promoter;
    }
}
