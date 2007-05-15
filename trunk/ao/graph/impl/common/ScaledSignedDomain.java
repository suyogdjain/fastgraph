package ao.graph.impl.common;

import ao.graph.user.EdgeWeight;
import ao.graph.user.EdgeWeightDomain;

/**
 * Labels weight weights with integers in range 0 .. (SIZE - 1)
 * Smaller labels correspond to smaller weights.
 *
 * Label groth rate decreases futher away from zero.
 * labelOf( x ) = floor(SIZE / 2) +
 *                  signum(x.asFloat) * scaled(abs(x.asFloat))
 *  where scaled(x) has range 0 .. (SIZE - 1).
 *
 * Note that the range of values from an odd degree is the same as
 *  the nearest lower even degree.
 * This is done so that -ve and +ve values have the same range +-(SIZE/2).
 */
public class ScaledSignedDomain<E extends EdgeWeight<E>>
        implements EdgeWeightDomain<E>
{
    //--------------------------------------------------------------------
    private final int SIZE;
    private final int HALF_SIZE;

    private final int SCALE_100_PERCENT;
    private final int SCALE_50_PERCENT;
    private final int SCALE_33_PERCENT;
    private final int SCALE_25_PERCENT;
    private final int SCALE_20_PERCENT;
    private final int SCALE_SQRT;
    private final int SCALE_LN;

    private final float WEIGHT_100_PERCENT;
    private final float WEIGHT_50_PERCENT;
    private final float WEIGHT_33_PERCENT;
    private final float WEIGHT_25_PERCENT;
    private final float WEIGHT_20_PERCENT;
    private final float WEIGHT_SQRT;
    private final float WEIGHT_LN;

    private final float SCALE_FACTOR;


    //--------------------------------------------------------------------
    public ScaledSignedDomain(int size)
    {
        this(size, 0);
    }

    /**
     * @param size total number of labels weights will be mapped into.
     * @param plusMinusRange the expected range of weight weights, zero
     *                          for whole double range.
     */
    public ScaledSignedDomain(int size, float plusMinusRange)
    {
        assert size > 0;
        assert plusMinusRange >= 0;

        SIZE      = size;
        HALF_SIZE = (size >> 1);

        SCALE_100_PERCENT = (int) (HALF_SIZE * 0.10);
        SCALE_50_PERCENT  = (int) (HALF_SIZE * 0.20);
        SCALE_33_PERCENT  = (int) (HALF_SIZE * 0.30);
        SCALE_25_PERCENT  = (int) (HALF_SIZE * 0.40);
        SCALE_20_PERCENT  = (int) (HALF_SIZE * 0.50);
        SCALE_SQRT        = (int) (HALF_SIZE * 0.80);
        SCALE_LN          = (int) (HALF_SIZE * 1.00);

        WEIGHT_100_PERCENT = SCALE_100_PERCENT;
        WEIGHT_50_PERCENT  = SCALE_50_PERCENT + WEIGHT_100_PERCENT;
        WEIGHT_33_PERCENT  = SCALE_33_PERCENT + WEIGHT_50_PERCENT;
        WEIGHT_25_PERCENT  = SCALE_25_PERCENT + WEIGHT_33_PERCENT;
        WEIGHT_20_PERCENT  = SCALE_20_PERCENT + WEIGHT_25_PERCENT;
        WEIGHT_SQRT        =
                (float) Math.pow(SCALE_SQRT - SCALE_20_PERCENT, 2) +
                            WEIGHT_20_PERCENT;
        WEIGHT_LN          =
                (float) Math.exp(SCALE_LN - SCALE_SQRT) + WEIGHT_SQRT;

        SCALE_FACTOR = (plusMinusRange == 0
                        ? 1.0f
                        : WEIGHT_LN / plusMinusRange);
    }


    //--------------------------------------------------------------------
    public int size()
    {
        return SIZE;
    }

    public int labelOf(E edge)
    {
        float weight = edge.asFloat() * SCALE_FACTOR;

        return HALF_SIZE +
                (weight >= 0 ? 1 : -1) * scaled( Math.abs(weight) );
    }

    //--------------------------------------------------------------------
    /**
     * XXX: need to optimize, log is taking a while
     *
     * @param weight to scale
     * @return asFloat scaled between 0 .. HALF_SIZE
     */
    private int scaled(float weight)
    {
        if (weight > WEIGHT_LN)
        {
            return SCALE_LN;
        }
        else if (weight > WEIGHT_SQRT)
        {
            return (int) (SCALE_SQRT +
                            Math.log(weight - WEIGHT_SQRT + 1));
        }
        else if (weight > WEIGHT_20_PERCENT)
        {
            return (int) (SCALE_20_PERCENT +
                            Math.sqrt(weight - WEIGHT_20_PERCENT + 1)
                            - 1);
        }
        else if (weight > WEIGHT_25_PERCENT)
        {
            return (int) (SCALE_25_PERCENT +
                            (weight - WEIGHT_25_PERCENT) * 0.2);
        }
        else if (weight > WEIGHT_33_PERCENT)
        {
            return (int) (SCALE_33_PERCENT +
                            (weight - WEIGHT_33_PERCENT) * 0.25);
        }
        else if (weight > WEIGHT_50_PERCENT)
        {
            return (int) (SCALE_50_PERCENT +
                            (weight - WEIGHT_50_PERCENT) * 0.33);
        }
        else if (weight > WEIGHT_100_PERCENT)
        {
            return (int) (SCALE_100_PERCENT +
                            (weight - WEIGHT_100_PERCENT) * 0.50);
        }
        else
        {
            return (int) weight;
        }
    }
}
