package ao.ai.evo.promoter.affinity;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class TraversingDomain implements AffinityDomain
{
    //--------------------------------------------------------------------
    private Set<Affinity> baseAffinities;


    //--------------------------------------------------------------------
    public TraversingDomain()
    {
        baseAffinities = new HashSet<Affinity>();
    }


    //--------------------------------------------------------------------
    public boolean add(Affinity baseAffinity)
    {
        return baseAffinities.add( baseAffinity  );
    }


    //--------------------------------------------------------------------
    public Collection<Affinity> loosen(Affinity affinity)
    {
        return affinity.affineSet();
    }

    public Collection<Affinity> tighten(Affinity affinity)
    {
        Set<Affinity> subclasses = new HashSet<Affinity>();

        subclasses.add( affinity );
        for (Affinity anAffinity : baseAffinities)
        {
            if (affinity.isAffine(anAffinity))
            {
                subclasses.add( anAffinity );
            }
        }

        return Collections.unmodifiableCollection(subclasses);
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return baseAffinities.toString();
    }
}
