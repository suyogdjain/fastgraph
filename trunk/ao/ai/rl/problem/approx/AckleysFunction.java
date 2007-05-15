package ao.ai.rl.problem.approx;

import ao.util.rand.Rand;

/**
 * Ackley's Function.
 * See
 */
public class AckleysFunction extends Approximator<Double, Double>
{
    //--------------------------------------------------------------------
    public Double nextInput()
    {
        return Rand.nextDouble(64) - 32;
    }

    //--------------------------------------------------------------------
    public Double outputFor(Double input)
    {
        return valueAt( input );
    }


    //--------------------------------------------------------------------
    public double error(Double expected, Double recieved)
    {
        if (recieved == null) return -10000.0;
        return -(expected - recieved)*(expected - recieved);
    }


    //--------------------------------------------------------------------
    private double valueAt(double x)
    {
        return -20.0 *
               Math.exp(-0.2 * Math.sqrt(
                                 1.0/30 * sumOfSquares(x, 30)))
                * -Math.exp(1.0/30 * sumOfTrig(x, 30))
                + 20 + Math.E;
    }

    private double sumOfTrig(double x, int nTimes)
    {
        double sum = 0;
        for (int i = 0; i < nTimes; i++)
        {
            sum += Math.cos(2.0 * Math.PI * x);
        }
        return sum;
    }

    private double sumOfSquares(double x, int nTimes)
    {
        double sum = 0;
        for (int i = 0; i < nTimes; i++)
        {
            sum += x * x;
        }
        return sum;
    }

    
    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return "Ackley's Delta";
    }
}
