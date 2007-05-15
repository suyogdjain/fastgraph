package ao.ai.rl.gp.agent;

import ao.ai.evo.chromosome.config.CrossoverDestinationProb;
import ao.ai.evo.chromosome.config.MacroMutationProb;
import ao.ai.evo.chromosome.config.TreeGenParams;
import ao.ai.evo.deme.AbstractDeme;
import ao.ai.evo.deme.Deme;
import ao.ai.evo.gene.synthesis.GeneSynthesizer;
import com.google.inject.AbstractModule;
import static com.google.inject.name.Names.named;

/**
 * Flexible configuration for an agent.
 */
public class FlexConfig extends AbstractModule
{
    //--------------------------------------------------------------------
    private FlexConfigParams params;


    //--------------------------------------------------------------------
    public FlexConfig(FlexConfigParams configParams)
    {
        params = configParams;
    }
    public FlexConfig()
    {
        this( new FlexConfigParams() );
    }


    //--------------------------------------------------------------------
    protected void configure()
    {
//        bind(Agent.class)
//                .to(AlexoAgent.class);
        bind(AlexoAgent.class);

        bind(Deme.class).to(params.populationType());

        bindConstant().annotatedWith(
                named(AbstractDeme.POP_SIZE_ID)
        ).to(params.populationSize());

        bindConstant().annotatedWith(
                named(AbstractDeme.CROSS_WEIGHT_ID)
        ).to(params.crossWeight());
        bindConstant().annotatedWith(
                named(AbstractDeme.MACRO_WEIGHT_ID)
        ).to(params.macroWeight());
        bindConstant().annotatedWith(
                named(AbstractDeme.MICRO_WEIGHT_ID)
        ).to(params.microWeight());

        // tree formation parameters
        bindConstant().annotatedWith(
                named(CrossoverDestinationProb.MAX_LEAF_DISTANCE_ID)
        ).to(99);

        bindConstant().annotatedWith(
                named(MacroMutationProb.MAX_LEAF_DISTANCE_ID)
        ).to(params.macroDepth());
        bindConstant().annotatedWith(
                named(MacroMutationProb.MAX_MUTATION_SIZE_ID)
        ).to(params.macroSize());

        bindConstant().annotatedWith(
                named(TreeGenParams.MAX_DIST_ID)
        ).to(params.treeDepth());
        bindConstant().annotatedWith(
                named(TreeGenParams.MAX_SIZE_ID)
        ).to(params.treeSize());

        bind(GeneSynthesizer.class);
    }
}
