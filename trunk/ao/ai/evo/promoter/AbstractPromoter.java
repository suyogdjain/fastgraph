package ao.ai.evo.promoter;

/**
 *
 */
public abstract class AbstractPromoter implements Promoter
{
    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public boolean isAffine(Promoter with)
    {
        if (! productAffinity().isAffine(
                with.productAffinity() )) return false;

        if (loci() != with.loci()) return false;
        for (int locus = 0; locus < loci(); locus++)
        {
            if (! locusAffinity(locus).isAffine(
                    with.locusAffinity(locus) ))
            {
                return false;
            }
        }
        return true;
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return "product: " + productAffinity() + ", " +
               "dependencies: " + lociAffinities();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || !(o instanceof Promoter)) return false;

        Promoter that = (Promoter) o;

        return productAffinity().equals( that.productAffinity() ) &&
               lociAffinities().equals( that.lociAffinities() );
    }

    @Override
    public int hashCode()
    {
        int result;
        result = productAffinity().hashCode();
        result = 31 * result + lociAffinities().hashCode();
        return result;
    }
}
