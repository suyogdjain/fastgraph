package ao.graph.impl.common;

import ao.graph.user.EdgeWeight;
import ao.graph.user.EdgeWeightDomain;

/**
 * Uses the absolute value of asFloat.
 */
public class SimpleAbsDomain<E extends EdgeWeight<E>>
        implements EdgeWeightDomain<E>
{
    //--------------------------------------------------------------------
    private final int   SIZE;
    private final float MAX_WEIGHT;


    //--------------------------------------------------------------------
    public SimpleAbsDomain(int size)
    {
        this(size, 1.0f);
    }

    /**
     * @param size total number of labels weights will be mapped into.
     * @param absValRange the expected range of weight weights, zero for
     *                      whole float range.
     */
    public SimpleAbsDomain(int size, float absValRange)
    {
        assert size > 0;
        assert absValRange >= 0;

        SIZE       = size;
        MAX_WEIGHT = absValRange;
    }


    //--------------------------------------------------------------------
    public int size()
    {
        return SIZE;
    }

    public int labelOf(E edge)
    {
        float percentile = Math.abs(edge.asFloat()) / MAX_WEIGHT;
        
        return (percentile >= 1.0)
                ? SIZE - 1
                : (int) (percentile * SIZE);
    }


//  //--------------------------------------------------------------------
//    private final static ScaleOp op100  = new LinearScale(1.00f);
//    private final static ScaleOp op50   = new LinearScale(0.50f);
//    private final static ScaleOp op33   = new LinearScale(0.33f);
//    private final static ScaleOp op25   = new LinearScale(0.25f);
//    private final static ScaleOp op20   = new LinearScale(0.20f);
//    private final static ScaleOp opSqrt = new SqrtScale();
//    private final static ScaleOp opLn   = new LnScale();
//  //--------------------------------------------------------------------
//    private static class ScaleSpan
//    {
//        private final int offset;
//        private final int degree;
//
//        private final float weightOffset;
//        private final float weightRange;
//
//        private final ScaleOp op;
//
//        public ScaleSpan(
//                int offset, int degree,
//                float weightOffset, float weightRange,
//                ScaleOp op)
//        {
//            this.offset = offset;
//            this.degree   = degree;
//
//            this.weightOffset = weightOffset;
//            this.weightRange  = weightRange;
//
//            this.op = op;
//        }
//
//        public boolean startsAtOrBefore(float asFloat)
//        {
//            return weightOffset <= asFloat;
//        }
//
//        public int scale(float asFloat)
//        {
//            float toUnscale = asFloat - weightOffset;
//            float unscaled  = op.unscale( toUnscale );
//
//
//            return 0;
//        }
//    }
//
//    //------------------------------------------------------------------
//    private static interface ScaleOp
//    {
//        public float scale(float value);
//        public float unscale(float value);
//    }
//
//    private static class LinearScale implements ScaleOp
//    {
//        private final float factor;
//
//        public LinearScale(float factor)
//        {
//            this.factor = factor;
//        }
//
//        public float scale(float value)
//        {
//            return factor * value;
//        }
//
//        public float unscale(float value)
//        {
//            return value / factor;
//        }
//    }
//
//    private static class SqrtScale implements ScaleOp
//    {
//        public float scale(float value)
//        {
//            return (float) Math.sqrt( value );
//        }
//
//        public float unscale(float value)
//        {
//            return value * value;
//        }
//    }
//
//    private static class LnScale implements ScaleOp
//    {
//        public float scale(float value)
//        {
//            return (float) Math.log( value );
//        }
//
//        public float unscale(float value)
//        {
//            return (float) Math.exp( value );
//        }
//    }
}
