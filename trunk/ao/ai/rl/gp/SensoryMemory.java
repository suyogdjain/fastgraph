package ao.ai.rl.gp;

import java.util.Collection;


/**
 * A short-term memory of an Agent's sensations.
 */
public class SensoryMemory
{
    /**
     * @param facet ...
     * @param sense ...
     */
    public void glimpse(
            String facet,
            Object sense)
    {

    }

    /**
     * @return ...
     */
    public Collection<String> facets()
    {
        return null;
    }

    /**
     * @param facet ...
     * @return ...
     */
    public Object recall(
            String facet)
    {
        return recall(facet, 0);
    }

    /**
     * @param facet ...
     * @param pastOccuranceOffset ...
     * @return ...
     */
    public Object recall(
            String facet,
            int    pastOccuranceOffset)
    {
        return null;
    }
}
