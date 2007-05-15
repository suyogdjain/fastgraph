package ao.ai.evo.promoter.affinity;

import java.util.Collection;

/**
 *.
 */
public interface Affinity
{
    /**
     * Note that if a.isAffine(b),
     *  then it is NOT necessary that
     *              b.isAffine(a).
     *
     * @param with ...
     * @return ...
     */
    public boolean isAffine(Affinity with);

    public Collection<Affinity> affineSet();
}
