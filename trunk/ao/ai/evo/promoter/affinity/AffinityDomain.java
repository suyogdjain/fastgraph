package ao.ai.evo.promoter.affinity;

import java.util.Collection;

/**
 *
 */
public interface AffinityDomain
{
    boolean add(Affinity baseAffinity);

    Collection<Affinity> loosen(Affinity affinity);

    Collection<Affinity> tighten(Affinity affinity);
}
