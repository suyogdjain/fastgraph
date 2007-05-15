package ao.ai.evo.promoter;

import ao.ai.evo.promoter.affinity.Affinity;
import ao.ai.evo.promoter.affinity.GpAffinity;

import java.util.Arrays;
import java.util.Collection;

/**
 *
 */
public class GpPromoter extends AbstractPromoter
{
    //--------------------------------------------------------------------
    private static final Affinity[] NO_ARGS = new Affinity[0];


    //--------------------------------------------------------------------
    private final Affinity   TYPE;
    private final Affinity[] ARGS;


    //--------------------------------------------------------------------
    public GpPromoter(
            Affinity    type,
            Affinity... args)
    {
        TYPE = type;
        ARGS = args;
    }

    public GpPromoter(
            Class    type,
            Class... args)
    {
        this( new GpAffinity(type), fromClasses(args) );
    }

    private static Affinity[] fromClasses(Class[] classes)
    {
        if (classes == null || classes.length == 0)
        {
            return NO_ARGS;
        }
        else
        {
            GpAffinity affinities[] = new GpAffinity[ classes.length ];
            for (int i = 0; i < affinities.length; i++)
            {
                affinities[i] = new GpAffinity( classes[i] );
            }
            return affinities;
        }
    }


    //--------------------------------------------------------------------
    public Promoter withLocus(int locus, Affinity ofType)
    {
        assert ARGS[ locus ].isAffine( ofType );

        Affinity copyArgs[] = Arrays.copyOf(ARGS, ARGS.length);
        copyArgs[locus] = ofType;
        return new GpPromoter( TYPE, copyArgs );
    }

    public Promoter withProduct(Affinity ofType)
    {
        assert ofType.isAffine( TYPE ) :
                ofType + " not affine with " + TYPE;
        return new GpPromoter( ofType, ARGS );
    }


    //--------------------------------------------------------------------
    public int loci()
    {
        return ARGS.length;
    }

    public boolean isIndependent()
    {
        return loci() == 0;
    }

    public Affinity productAffinity()
    {
        return TYPE;
    }

    public Affinity locusAffinity(int loci)
    {
        return ARGS[ loci ];
    }

    public Collection<Affinity> lociAffinities()
    {
        return Arrays.asList(ARGS);
    }
}


