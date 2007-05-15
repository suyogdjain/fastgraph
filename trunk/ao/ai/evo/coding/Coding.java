package ao.ai.evo.coding;

import ao.ai.evo.gene.Gene;
import ao.ai.evo.product.Product;

/**
 * Thought.
 */
public interface Coding
{
    public Product encode(Gene... dependencies);

//    /**
//     * Sometimes the arguments to encode are reshuffled.
//     * This lets us see where a dependency of the given
//     *  index is going to end up.
//     * Useful for displaying the coding.
//     *
//     * @param locus ...
//     * @return ...
//     */
//    public int dependencyDestination(int locus);
}
