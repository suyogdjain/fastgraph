package ao.ai.evo.product;

/**
 * These molecules resulting from gene expression,
 *  whether RNA or protein, are known as gene products.
 */
public interface Product<T>
{
    public T build();
}
