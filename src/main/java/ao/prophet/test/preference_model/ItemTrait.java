package ao.prophet.test.preference_model;

/**
 * A trait.
 */
public class ItemTrait
{
    //--------------------------------------------------------------------
    private final float MAGNITUDE;


    //--------------------------------------------------------------------
    public ItemTrait(float magnitude)
    {
        MAGNITUDE = magnitude;
    }


    //--------------------------------------------------------------------
    public float magnitude()
    {
        return MAGNITUDE;
    }


    //----------------------------------
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || !(o instanceof ItemTrait)) return false;

        final ItemTrait itemTrait = (ItemTrait) o;

        return Float.compare(itemTrait.MAGNITUDE, MAGNITUDE) == 0;
    }

    public int hashCode()
    {
        return MAGNITUDE != +0.0f ? Float.floatToIntBits(MAGNITUDE) : 0;
    }
}
