package ao.ai.evo.genetic_material;

import ao.ai.evo.coding.Coding;
import ao.ai.evo.coding.IoCoding;
import ao.ai.evo.promoter.Promoter;
import ao.ai.evo.promoter.GpPromoter;

/**
 *
 */
public class IoGeneticMaterial<T> extends AbstractGpMaterial
{
    //--------------------------------------------------------------------
    private IoCoding<T> CODING;
    private Promoter    PROMOTER;


    //--------------------------------------------------------------------
    public IoGeneticMaterial(T value)
    {
        CODING   = new IoCoding<T>(value);
        PROMOTER = new GpPromoter(value.getClass());
    }


    //--------------------------------------------------------------------
    public Coding coding()
    {
        return CODING;
    }

    public Promoter promoter()
    {
        return PROMOTER;
    }


    //--------------------------------------------------------------------
    public void replaceWith(T thought)
    {
        CODING.replaceWith( thought );
    }
}
