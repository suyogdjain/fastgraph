package ao.ai.evo.heredity;

/**
 *
 */
public interface Heritable<T>
    extends
        Replicable<T>,
        Sexual<T>,
        Asexual<T>
{
    // no additional methods
}
