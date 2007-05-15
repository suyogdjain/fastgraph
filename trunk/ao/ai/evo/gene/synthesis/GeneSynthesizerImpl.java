package ao.ai.evo.gene.synthesis;

import ao.ai.evo.gene.GpGene;
import ao.ai.evo.primordial_soup.PrimordialSoup;
import ao.ai.evo.promoter.affinity.Affinity;
import ao.ai.evo.chromosome.config.TreeGenParams;
import com.google.inject.Singleton;

/**
 *
 */
@Singleton
public class GeneSynthesizerImpl implements GeneSynthesizer
{
    //--------------------------------------------------------------------
    public GeneSynthesizerImpl() {}

    //--------------------------------------------------------------------
    public GpGene generate(
            TreeGenParams  params,
            Affinity       returning,
            PrimordialSoup ops)
    {
        int goalSize  = params.nextTreeSize();

        assert goalSize >= 1 : goalSize + " is too small.";
        if (goalSize == 1) return newTree(ops, returning, true);

        int                     size   = 1;
        RandomQueue<OpenBranch> toFill =
                new RandomQueue<OpenBranch>();

        GpGene root = newTree(ops, returning, false);
        OpenBranch.addArgsOf(root, toFill, 1);

        while (!(toFill.size() + size > goalSize ||
                 toFill.isEmpty()))
        {
            OpenBranch  unfilled = toFill.removeRandom();
            GpGene      filler   =
                        unfilled.fill(
                               ops,
                               unfilled.atOrBelow(params.maxLeafDistance()));

            OpenBranch.addArgsOf(filler, toFill, unfilled.nextDepth());

            size++;
        }

        while (! toFill.isEmpty())
        {
            toFill.removeRandom().fill(ops, true);
        }

        return root;
    }


    //--------------------------------------------------------------------
    private static GpGene newTree(
            PrimordialSoup ops,
            Affinity    returning,
            boolean     terminal)
    {
        return ops.synthesize(returning, terminal).construct();
    }


    //--------------------------------------------------------------------
    private static class OpenBranch
    {
        //----------------------------------------------------------------
        public static void addArgsOf(
                GpGene                  tree,
                RandomQueue<OpenBranch> to,
                int                     atDepth)
        {
            for (int i = 0; i < tree.promoter().loci(); i++)
            {
                if (tree.isAvailable(i))
                {
                    to.add( new OpenBranch(tree, i, atDepth) );
                }
            }
        }


        //----------------------------------------------------------------
        private final GpGene   TREE;
        private final int      DEPTH;
        private final int      POSITION;
        private final Affinity TYPE;


        //----------------------------------------------------------------
        private OpenBranch(GpGene of, int atPosition, int atDepth)
        {
            TYPE     = of.promoter().locusAffinity(atPosition);
            TREE     = of;
            DEPTH    = atDepth;
            POSITION = atPosition;
        }


        //----------------------------------------------------------------
        public boolean atOrBelow(int depth)
        {
            return DEPTH >= depth;
        }

        public int nextDepth()
        {
            return DEPTH + 1;
        }

        //----------------------------------------------------------------
        public boolean isAvailable()
        {
            return TREE.isAvailable( POSITION );
        }

        //----------------------------------------------------------------
        public GpGene fill(PrimordialSoup from, boolean terminal)
        {
            GpGene filler = newTree( from,
                                     TYPE,
                                     terminal );

            TREE.splice(POSITION, filler);
            return filler;
        }
    }
}

