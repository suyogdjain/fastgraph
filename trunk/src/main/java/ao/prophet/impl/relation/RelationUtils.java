package ao.prophet.impl.relation;

/**
 * Utilities for perfoming bulk operations.
 * Primarily used for testing.
 */
public class RelationUtils
{
    private RelationUtils() {}

    public static Relation add(Relation ... relations)
    {
        Relation sum = Relation.NEUTRAL;

        for (Relation addend : relations)
        {
            sum = sum.additiveMerge( addend );
        }

        return sum;
    }

    public static Relation merge(Relation ... relations)
    {
        Relation merged = null;

        for (Relation relation : relations)
        {
            merged = (merged == null
                      ? relation
                      : merged.mergeWith( relation ));
        }

        return merged;
    }
}
