package ao.ai.evo.product;

/**
 *
 */
public class LiteralProduct<T>
        implements Product<T>
{
    //--------------------------------------------------------------------
    private final T VALUE;


    //--------------------------------------------------------------------
    public LiteralProduct(T val)
    {
        VALUE = val;
    }


    //--------------------------------------------------------------------
    public T build()
    {
        return VALUE;
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return String.valueOf( VALUE );
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || !(o instanceof LiteralProduct)) return false;

        LiteralProduct that = (LiteralProduct) o;
        return !(VALUE != null
                 ? !VALUE.equals(that.VALUE)
                 : that.VALUE != null);
    }

    @Override
    public int hashCode()
    {
        return (VALUE != null ? VALUE.hashCode() : 0);
    }
}
