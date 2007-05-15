package ao.ai.evo.chromosome.config;

import ao.ai.evo.gene.Locus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * Controlls sexual recombination.
 */
@Singleton
public class CrossoverDestinationProb implements LocusChooser
{
    //--------------------------------------------------------------------
    public static final String MAX_LEAF_DISTANCE_ID = "cop.leafDistance";


    //--------------------------------------------------------------------
    private int    maxLeafDistance;
    private double weights[];


    //--------------------------------------------------------------------
    public CrossoverDestinationProb() {}


    //--------------------------------------------------------------------
    @Inject
    public void injectMaxDepth(
            @Named(MAX_LEAF_DISTANCE_ID)
                int injectedMaxLeafDistance)
    {
        maxLeafDistance = injectedMaxLeafDistance;
        populateWeights();
    }


    //--------------------------------------------------------------------
    private void populateWeights()
    {
        weights = new double[maxLeafDistance + 1];

        for (int i = 0; i < weights.length; i++)
        {
            weights[i] = weightAt(i);
        }
    }


    //--------------------------------------------------------------------
    public LocusWeight branchUseWeight(Locus branchDetails)
    {
        return (branchDetails.lociNestingDepth() > maxLeafDistance)
                ? LocusWeight.randomized(0.0)
                : LocusWeight.randomized(
                    weights[branchDetails.lociNestingDepth()]);
    }


    //--------------------------------------------------------------------
    private static double weightAt(int distanceFromLeaf)
    {
        return negativeBinomial(distanceFromLeaf, 2.0, 0.23);
    }

    private static double negativeBinomial(int k, double r, double p)
    {
        return gamma(r + k) / (factorial(k) * gamma(r))
                * Math.pow(p, r) * Math.pow(1.0 - p, k);
    }

    private static long factorial(int of)
    {
        long factorial = 1;
        for (int i = 1; i <= of; i++)
        {
            factorial *= i;
        }
        return factorial;
    }

    // @author Sundar Dorai-Raj
    private static double lnGamma(double c)
    {
        int j;
        double x,y,tmp,ser;
        double [] cof = {76.18009172947146     , -86.50532032941677 ,
                         24.01409824083091     , -1.231739572450155 ,
                          0.1208650973866179e-2, -0.5395239384953e-5};
        y = x = c;
        tmp = x + 5.5 - (x + 0.5) * Math.log(x + 5.5);
        ser = 1.000000000190015;
        for (j=0;j<=5;j++)
            ser += (cof[j] / ++y);
        return(Math.log(2.5066282746310005 * ser / x) - tmp);
    }

    private static double gamma(double c)
    {
        return Math.exp(lnGamma(c));
    }
}
