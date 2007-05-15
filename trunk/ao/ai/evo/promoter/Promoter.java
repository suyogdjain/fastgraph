package ao.ai.evo.promoter;

import ao.ai.evo.promoter.affinity.Affinity;

import java.util.Collection;

/**
 * 
 */
public interface Promoter
{
    //--------------------------------------------------------------------
    /**
     * @param with test against
     * @return can this Promoter be replaced with the given one
     *           without anything changing?
     */
    public boolean isAffine(Promoter with);


    //--------------------------------------------------------------------
    /**
     * The region of the chromosome at which a particular gene
     *  is located is called its locus.
     * For our purposes, a Gene's locus is its position within
     *  another Gene.  The total number of locus positions
     *  within a Gene is specified by its Promoter.
     *
     * @return The number of locus positions in the
     *          gene being promoted by this Promoter.
     */
    public int loci();
    public boolean isIndependent();

    public Affinity productAffinity();

    public Affinity locusAffinity(int locus);
    public Collection<Affinity> lociAffinities();

    public Promoter withLocus(int locus, Affinity ofType);
    public Promoter withProduct(Affinity ofType);
}
