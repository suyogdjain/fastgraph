package ao.ai.evo.chromosome;

import ao.ai.evo.gene.GpGene;
import ao.ai.evo.gene.synthesis.WeightedLocus;
import ao.ai.evo.chromosome.config.LocusChooser;
import ao.ai.evo.chromosome.config.CrossoverDestinationProb;
import ao.ai.evo.chromosome.config.CrossoverSourceProb;
import ao.ai.evo.chromosome.config.TreeGenParams;
import com.google.inject.Inject;

/**
 * sexual recombination
 */
public class Recombiner
{
    //--------------------------------------------------------------------
    private CrossoverDestinationProb destProbs;
    private TreeGenParams            params;


    //--------------------------------------------------------------------
    public Recombiner() {}


    //--------------------------------------------------------------------
    @Inject
    public void injectCrossoverProbs(
            CrossoverDestinationProb injectedProbs)
    {
        destProbs = injectedProbs;
    }

    @Inject
    public void injectTreeGenProbs(TreeGenParams injectedTreeGenParams)
    {
        params = injectedTreeGenParams;
    }


    //--------------------------------------------------------------------
    public void copyGene(
            GpGene sourceTree,
            GpGene destinationTree)
    {
        for (int tryCount = 0; tryCount < 10; tryCount++)
        {
            WeightedLocus destinationLocus =
                    LocusChooser.Maximizer.maximize(
                            destinationTree, destProbs);

            int targetTreeSize = params.nextTreeSize();
            int maxSourceSize   =
                    Math.max((targetTreeSize - destinationTree.size())
                                + destinationLocus.size(), 1);

            WeightedLocus source =
                    LocusChooser.Maximizer.maximize(
                            destinationTree,
                            new CrossoverSourceProb(
                                    maxSourceSize,
                                    destinationLocus.gene()));
            if (source != null &&
                source.weight().isUnusable())
            {
//                if ((destinationLocus.size() - source.size()) > 8)
//                {
//                    System.out.println("!!");
//                }

                destinationLocus.gene().mirror(
                        source.gene().replicate());
                break;
            }
        }
    }
}
