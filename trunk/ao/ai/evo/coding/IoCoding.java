package ao.ai.evo.coding;

import ao.ai.evo.gene.Gene;
import ao.ai.evo.product.LiteralProduct;
import ao.ai.evo.product.Product;

/**
 * Coding that can be modified in-place.
 */
public class IoCoding<T> implements Coding
{
    //--------------------------------------------------------------------
    private T VALUE;


    //--------------------------------------------------------------------
    public IoCoding(T value)
    {
        VALUE = value;
    }


    //--------------------------------------------------------------------
    public Product<T> encode(Gene... dependencies)
    {
        return new LiteralProduct<T>( VALUE );
    }


    //--------------------------------------------------------------------
    public void replaceWith(T value)
    {
        VALUE = value;
    }


    //--------------------------------------------------------------------
    public Coding replicate()
    {
        return this;
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return VALUE.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || !(o instanceof IoCoding)) return false;

        IoCoding ioCoding = (IoCoding) o;
        return !(VALUE != null
                 ? !VALUE.equals(ioCoding.VALUE)
                 : ioCoding.VALUE != null);
    }

    @Override
    public int hashCode()
    {
        return (VALUE != null ? VALUE.hashCode() : 0);
    }
}
