package ao.ai.evo.chromosome;

import ao.ai.evo.gene.GpGene;
import ao.ai.evo.genetic_material.GeneticMaterial;
import ao.ai.evo.primordial_soup.PrimordialSoup;
import ao.ai.evo.chromosome.config.LocusChooser;
import ao.ai.evo.chromosome.config.MicroMutationProb;
import ao.ai.evo.gene.synthesis.WeightedLocus;
import com.google.inject.Inject;

/**
 *
 */
public class MicroMutator
{
    //--------------------------------------------------------------------
    private MicroMutationProb probs;


    //--------------------------------------------------------------------
    public MicroMutator() {}


    //--------------------------------------------------------------------
    @Inject
    public void injectMutateTreeProbs(MicroMutationProb injectedProbs)
    {
        probs = injectedProbs;
    }


    //--------------------------------------------------------------------
    public void microMutate(
            GpGene         tree,
            PrimordialSoup thoughts)
    {
        WeightedLocus choice = LocusChooser.Maximizer.maximize( tree, probs );

        GeneticMaterial mutation =
                thoughts.synthesize( choice.gene().promoter() );
        choice.gene().copyGeneticMaterial( mutation );
    }
}
