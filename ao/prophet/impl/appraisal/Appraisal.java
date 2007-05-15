package ao.prophet.impl.appraisal;

import ao.prophet.impl.relation.Relation;

/**
 * What a user thinks of an item.
 *
 * Note: objects of this class are immutable.
 */
public class Appraisal
{
    //--------------------------------------------------------------------
    public static final Appraisal POSITIVE = new Appraisal(true);
    public static final Appraisal NEGATIVE = new Appraisal(false);


    //--------------------------------------------------------------------
    private final boolean IS_POSITIVE;

    private Appraisal(boolean isPositive)
    {
        IS_POSITIVE = isPositive;
    }


    //--------------------------------------------------------------------
    /*
     * The higher the values the more positive the appraisal.
     * Negative values mean that the appraised item sucks.
     *
     * @return how postitive of an appraisal this is.
     */
    public float value()
    {
        return (IS_POSITIVE ? 1.0f : -1.0f);
    }


    //--------------------------------------------------------------------
    public Relation relationTo(Appraisal other)
    {
        return (isPositiveAlongWith( other )
                ? Relation.POSITIVE
                : (isNegativeAlongWith( other )
                   ? Relation.NEGATIVE
                   : Relation.MIXED ));
    }

    private boolean isPositiveAlongWith(Appraisal appraisal)
    {
        return IS_POSITIVE && appraisal.IS_POSITIVE;
    }

    private boolean isNegativeAlongWith(Appraisal appraisal)
    {
        return !(IS_POSITIVE || appraisal.IS_POSITIVE);
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return ( IS_POSITIVE ? "positive" : "negative" );
    }

    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof Appraisal)) return false;

        Appraisal appraisal = (Appraisal) obj;

        return IS_POSITIVE == appraisal.IS_POSITIVE;
    }

    public int hashCode()
    {
        return (IS_POSITIVE ? 1 : 0);
    }
}
