package ao.graph.user;

/**
 * EdgeWeights within a domain are indexed 0 .. (degree - 1)
 * edgeWithIndex(n - 1) <= edgeWithIndex(n) <= edgeWithIndex(n + 1)
 */
public interface EdgeWeightDomain<E extends EdgeWeight<E>>
{
    public int size();
    public int labelOf(E edge);
}
