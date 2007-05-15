package ao.ai.evo.heredity;

import ao.ai.evo.primordial_soup.PrimordialSoup;

/**
 *
 */
public interface Asexual<T>
{
    public T microMutate(PrimordialSoup soup);

    public T macroMutate(PrimordialSoup soup);
}
