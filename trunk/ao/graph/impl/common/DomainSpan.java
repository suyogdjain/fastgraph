package ao.graph.impl.common;

/**
 * Represents a portion of a weight asFloat domain.
 * Not used in currect implimentation.
 */
public interface DomainSpan
{
    /**
     * An weight domain is constructed out of one or more DomainSpans.
     *
     * @return proportion of a domain that this DomainSpan occupies.
     */
    public float relativeSize();

    /**
     * For example if you want this DomainSpan to scale with the square
     * root of the input then:
     *  scale(x) = x^2
     *
     * @param in
     * @return the inverse of the scale function.
     */
    public float scale(float in);

}
