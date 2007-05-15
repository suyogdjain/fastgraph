package ao.ai.evo.deme;

import ao.ai.evo.chromosome.Chromosome;
import ao.ai.evo.genome.Genome;
import ao.ai.evo.genome.UniGenome;
import ao.ai.evo.primordial_soup.PrimordialSoup;
import ao.ai.evo.fitness.Feedback;
import ao.util.rand.Rand;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 */
public abstract class AbstractDeme implements Deme
{
    //--------------------------------------------------------------------
    public static final String POP_SIZE_ID     = "ad.size";
    public static final String MACRO_WEIGHT_ID = "ad.maw";
    public static final String MICRO_WEIGHT_ID = "ad.miw";
    public static final String CROSS_WEIGHT_ID = "ad.xxx";


    //--------------------------------------------------------------------
    private List<Genome>       pop;
    private List<Genome>       nonActors;
    private LinkedList<Genome> actionStack;

    private int                popSize;
    private int                macroWeight;
    private int                microWeight;
    private int                crossWeight;
    private Chromosome.Factory cromosomeFactory;


    //--------------------------------------------------------------------
    public AbstractDeme()
    {
        pop         = new ArrayList<Genome>();
        nonActors   = new ArrayList<Genome>();
        actionStack = new LinkedList<Genome>();
    }


    //--------------------------------------------------------------------
    @Inject public void injectMaxDepth(
            @Named(POP_SIZE_ID) int size)                    {
        popSize = size;
    }

    @Inject public void injectMacroWeight(
            @Named(MACRO_WEIGHT_ID) int injectedMacroWeight) {
        macroWeight = injectedMacroWeight;
    }

    @Inject public void injectMicroWeight(
            @Named(MICRO_WEIGHT_ID) int injectedMicroWeight) {
        microWeight = injectedMicroWeight;
    }

    @Inject public void injectCrossWeight(
            @Named(CROSS_WEIGHT_ID) int injectedCrossWeight) {
        crossWeight = injectedCrossWeight;
    }

    @Inject public void injectChromosomeFactory(
            Chromosome.Factory injectedChromosomeFactory)    {
        cromosomeFactory = injectedChromosomeFactory;
    }


    //--------------------------------------------------------------------
    final public Genome nextIndividual(PrimordialSoup soup)
    {
        Genome nextIndividual;
        if (pop.size() < popSize)
        {
            nextIndividual = tabulaRasa(soup);
            pop.add( nextIndividual );
        }
        else
        {
            nextIndividual = nextIndividual(pop);
        }

        return nextIndividual;
    }
    private Genome tabulaRasa(PrimordialSoup soup)
    {
        return new UniGenome(
                    cromosomeFactory.randomInstance(soup) );
    }


    protected abstract Genome nextIndividual(List<Genome> outOf);


    //--------------------------------------------------------------------
    public void didNotAct(Genome genome)
    {
        nonActors.add( genome );
    }

    public void actionPerformed(Genome genome)
    {
        actionStack.addFirst( genome );
    }


    //--------------------------------------------------------------------
    final public void select(Feedback pressure, PrimordialSoup soup)
    {
        select(actionStack, pressure, soup);
        actionStack.clear();

        for (Genome nonActor : nonActors)
        {
            if (pop.size() < popSize / 2)
            {
                pop.remove( nonActor );
            }
            else if (nonActor.evaluationCount() < 2)
            {
                pop.remove( nonActor );
                pop.add( (Genome) nonCrappyGenome().replicate() );
            }
            else
            {
                nonActor.reduceFitness();
            }
        }
        nonActors.clear();

        afterSelection(soup);
    }

    protected void select(
            List<Genome>   actors,
            Feedback       pressure,
            PrimordialSoup soup)
    {
        int distance = 0;
        for (Genome actor : actors)
        {
            actor.cumulate( pressure.credit(distance++) );
        }
    }

    protected abstract void afterSelection( PrimordialSoup soup );


    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    private Genome nonCrappyGenome()
    {
        Genome nonCrappy = Rand.fromList( pop );
        for (int i = 0; i < 16; i++)
        {
            Genome random = Rand.fromList( pop );
            if (nonCrappy.compareTo(random) < 0)
            {
                nonCrappy = random;
            }
        }
        return nonCrappy;
    }


    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    protected void learn(PrimordialSoup soup)
    {
        if (pop.size() < 4) return;

        int[] contestants = fourConfidentDistinct();

        int[] winnerLooserA =
                winnerLooser(contestants[0], contestants[1]);
        int[] winnerLooserB =
                winnerLooser(contestants[2], contestants[3]);

        Genome looserReplacementA, looserReplacementB;

        double macroProb = Rand.nextDouble(macroWeight);
        double microProb = Rand.nextDouble(microWeight);
        double crossProb = Rand.nextDouble(crossWeight);
        if (macroProb > microProb && macroProb > crossProb)
        {
            // macro mutate
            looserReplacementA =
                    (Genome) pop.get( winnerLooserA[0] ).macroMutate(soup);
            looserReplacementB =
                    (Genome) pop.get( winnerLooserB[0] ).macroMutate(soup);
        }
        else if (microProb > macroProb && microProb > crossProb)
        {
            // micro mutate
            looserReplacementA =
                    (Genome) pop.get( winnerLooserA[0] ).microMutate(soup);
            looserReplacementB =
                    (Genome) pop.get( winnerLooserB[0] ).microMutate(soup);
        }
        else
        {
            // cross
            looserReplacementA =
                    (Genome) pop.get( winnerLooserA[0] ).recombine(
                                pop.get( winnerLooserB[0] ));
            looserReplacementB =
                    (Genome) pop.get( winnerLooserB[0] ).recombine(
                                pop.get( winnerLooserA[0] ));
        }

        pop.set(winnerLooserA[1], looserReplacementA);
        pop.set(winnerLooserB[1], looserReplacementB);
    }


    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    private int[] winnerLooser(int indexA, int indexB)
    {
        int fitnessCmp =
                pop.get(indexA).compareTo( pop.get(indexB) );

        return (fitnessCmp > 0)
                ? new int[]{ indexA, indexB }
                : (fitnessCmp < 0)
                   ? new int[]{ indexB, indexA }
                   : (Rand.nextBoolean())
                      ? new int[]{ indexA, indexB }
                      : new int[]{ indexB, indexA };
    }


    //--------------------------------------------------------------------
    private int[] twoDistinct()
    {
        int a;
        int b = Rand.nextInt(pop.size());
        do
        {
            a = Rand.nextInt(pop.size());
        }
        while (a == b);

        return new int[] { a, b };
    }

    private int[] fourDistinct()
    {
        int ab[], cd[];
        do
        {
            ab = twoDistinct();
            cd = twoDistinct();
        }
        while (ab[0] == cd[0] || ab[0] == cd[1] ||
                ab[1] == cd[0] || ab[1] == cd[1]);

        return new int[]{ ab[0], ab[1], cd[0], cd[1] };
    }

    private int[] fourConfidentDistinct()
    {
        int[] confidentFour = null;
        int   confidence    = Integer.MIN_VALUE;

        for (int i = 0; i < 16; i++)
        {
            int[] fourDistinct = fourDistinct();
            int conf = Math.min(confidence( fourDistinct[0] ),
                       Math.min(confidence( fourDistinct[1] ),
                       Math.min(confidence( fourDistinct[2] ),
                                confidence( fourDistinct[3] ))));
            if (conf > confidence)
            {
                confidence    = conf;
                confidentFour = fourDistinct;
            }
        }

        return confidentFour;
    }
    private int confidence(int ofGenomeIndexed)
    {
        return pop.get( ofGenomeIndexed ).fitnessConfidence();
    }
}
