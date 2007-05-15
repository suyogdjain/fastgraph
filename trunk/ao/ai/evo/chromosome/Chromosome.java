package ao.ai.evo.chromosome;

import ao.ai.evo.gene.GpGene;
import ao.ai.evo.gene.synthesis.RandomGeneSynthesizer;
import ao.ai.evo.heredity.Heritable;
import ao.ai.evo.primordial_soup.PrimordialSoup;
import ao.ai.rl.gp.TerminalReached;
import com.google.inject.Inject;
import com.google.inject.Provider;


/**
 * 
 */
public class Chromosome implements Heritable<Chromosome>
{
    //--------------------------------------------------------------------
    private GpGene       root;

    private Factory      factory;
    private Recombiner   recombiner;
    private MicroMutator microMutator;
    private MacroMutator macroMutator;


    //--------------------------------------------------------------------
    public Chromosome() {}

    public static class Factory
    {
        private RandomGeneSynthesizer thoughtTreeFactory;
        private Provider<Chromosome> branchProvider;

        public Chromosome randomInstance(PrimordialSoup thoughts)
        {
            Chromosome branch = branchProvider.get();
            branch.root = thoughtTreeFactory.randomInstance(thoughts);
            return branch;
        }

        public Chromosome copy(Chromosome branch)
        {
            Chromosome copyBranch = branchProvider.get();
            copyBranch.root        = branch.root.replicate();
            return copyBranch;
        }

        @Inject
        public void injectThoughtTreeFactory(
                RandomGeneSynthesizer injectedThoughtTreeFactory)
        {
            thoughtTreeFactory = injectedThoughtTreeFactory;
        }

        @Inject
        public void injectBranchProvider(
                Provider<Chromosome> injectedBranchProvider)
        {
            branchProvider = injectedBranchProvider;
        }
    }

    
    //--------------------------------------------------------------------
    @Inject
    public void injectBranchFactory(Factory injectedFactory)
    {
        factory = injectedFactory;
    }

    @Inject
    public void injectRecombiner(Recombiner injectedRecombiner)
    {
        recombiner = injectedRecombiner;
    }

    @Inject
    public void injectMicroMutator(MicroMutator injectedMicroMutator)
    {
        microMutator = injectedMicroMutator;
    }

    @Inject
    public void injectMacroMutator(MacroMutator injectedMacroMutator)
    {
        macroMutator = injectedMacroMutator;
    }


    //--------------------------------------------------------------------
    public int size()
    {
        return root.size();
    }


    //--------------------------------------------------------------------
    public Chromosome replicate()
    {
        return factory.copy(this);
    }


    //--------------------------------------------------------------------
    public Chromosome microMutate(PrimordialSoup thoughts)
    {
        Chromosome copy = replicate();
        microMutator.microMutate(copy.root, thoughts);
        return copy;
    }


    //--------------------------------------------------------------------
    public Chromosome macroMutate(PrimordialSoup thoughts)
    {
        Chromosome copy = replicate();
        macroMutator.macroMutate(copy.root, thoughts);
        return copy;
    }


    //--------------------------------------------------------------------
    public Chromosome recombine(Chromosome partner)
    {
        Chromosome copy = replicate();
        recombiner.copyGene(partner.root, copy.root);
        return copy;
    }


    //--------------------------------------------------------------------
    public void execute()
    {
        try
        {
            try
            {
                root.express().build();
            }
            catch (Throwable error)
            {
                Throwable root = error;
                while (root.getCause() != null)
                {
                    root = root.getCause();
                }
                if (root instanceof TerminalReached)
                {
                    throw ((TerminalReached) root);
                }

                error.printStackTrace();
            }
        }
        catch (TerminalReached normalEnd)
        {
            // program returned normally
        }
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
//        return String.valueOf( root.size() );
        return root.toString();
    }
}
