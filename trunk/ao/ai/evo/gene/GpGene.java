package ao.ai.evo.gene;

import ao.ai.evo.coding.Coding;
import ao.ai.evo.product.Product;
import ao.ai.evo.promoter.Promoter;
import ao.ai.evo.genetic_material.GeneticMaterial;

/**
 *
 */
public class GpGene implements Gene<GpGene>
{
    //--------------------------------------------------------------------
    private static final Visitor SIZING_VISITOR = new SizingVisitor();
    private static class SizingVisitor implements Visitor
    {   public void visit(Locus details){}   }


    //--------------------------------------------------------------------
    private Coding   coding;
    private Promoter promoter;
    private GpGene   loci[];


    //--------------------------------------------------------------------
    public GpGene(Coding coding, Promoter promoter)
    {
        this.coding   = coding;
        this.promoter = promoter;
        
        loci = new GpGene[ promoter.loci() ];
    }


    //--------------------------------------------------------------------
    public void mirror(GpGene source)
    {
        assert promoter.productAffinity().isAffine(
                source.promoter.productAffinity())
                : coding + " incompatible with " + coding;

        coding   = source.coding;
        promoter = source.promoter;
        loci     = source.loci;
    }

    public void copyGeneticMaterial(GeneticMaterial material)
    {
        assert promoter.isAffine( material.promoter() )
                : promoter + " incompatible with " + material.promoter();

        coding   = material.coding();
        promoter = material.promoter();
    }


    //--------------------------------------------------------------------
    public int size()
    {
        return postOrderTraverse(SIZING_VISITOR, 0)[0];
    }


    //--------------------------------------------------------------------
    public void postOrderTraverse(Visitor visitor)
    {
        postOrderTraverse(visitor, 0);
    }

    private int[] postOrderTraverse(Visitor visitor, int depth)
    {
        int size     = 1;
        int leafDist = -1;
        for (GpGene geneAtLocus : loci)
        {
            int sizeAndDist[] =
                    geneAtLocus.postOrderTraverse(visitor, depth + 1);
            size += sizeAndDist[0];
            leafDist = Math.max(leafDist, sizeAndDist[1]);
        }

        visitor.visit(
                new Locus(this, leafDist + 1, depth, size) );
        return new int[]{size, leafDist + 1};
    }


    //--------------------------------------------------------------------
    public Promoter promoter()
    {
        return promoter;
    }


    //--------------------------------------------------------------------
    public Product express()
    {
        return coding.encode(loci);
    }


    //--------------------------------------------------------------------
    public void splice(int locus, GpGene dependency)
    {
        assert loci[locus] == null
                : "already occupied by " + loci[locus];

        loci[locus] = dependency;
    }

    public boolean isAvailable(int locus)
    {
        return loci[ locus ] == null;
    }


    //--------------------------------------------------------------------
    public GpGene replicate()
    {
        GpGene replica = new GpGene(coding, promoter);
        for (int i = 0; i < loci.length; i++)
        {
            replica.loci[i] = loci[i].replicate();
        }
        return replica;
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return toString(0);
    }

    private String toString(int atPly)
    {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < atPly; i++)
        {
            indent.append("  ");
        }

        StringBuilder ret = new StringBuilder();

        ret.append(indent).append("(");
        ret.append(coding.toString());

        for (GpGene geneAtLocus : loci)
        {
            ret.append("\n");
            ret.append(indent).append("  ");

            if (geneAtLocus == null)
            {
                ret.append(indent).append("  ...");
            }
            else
            {
                ret.append(geneAtLocus.toString(atPly + 1));
            }
        }

        ret.append(")");
        return ret.toString();
    }

    //--------------------------------------------------------------------
    public static interface Visitor
    {
        public void visit(Locus details);
    }
}

