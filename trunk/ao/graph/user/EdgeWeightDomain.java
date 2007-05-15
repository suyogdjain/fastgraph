package ao.graph.user;

/**
 * EdgeWeights within a domain are indexed 0 .. (degree - 1)
 * archWithIndex(n - 1) <= archWithIndex(n) <= archWithIndex(n + 1)
 */
public interface EdgeWeightDomain<E extends EdgeWeight<E>>
{
    public int size();
    public int labelOf(E edge);
}
