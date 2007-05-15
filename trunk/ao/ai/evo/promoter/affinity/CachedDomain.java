package ao.ai.evo.promoter.affinity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class CachedDomain implements AffinityDomain
{
    //--------------------------------------------------------------------
    private final TraversingDomain                    deleget;
    private final Map<Affinity, Collection<Affinity>> looseCache;
    private final Map<Affinity, Collection<Affinity>> tightCache;


    //--------------------------------------------------------------------
    public CachedDomain()
    {
        deleget    = new TraversingDomain();
        looseCache = new HashMap<Affinity, Collection<Affinity>>();
        tightCache = new HashMap<Affinity, Collection<Affinity>>();
    }


    //--------------------------------------------------------------------
    public boolean add(Affinity baseAffinity)
    {
        boolean isNew = deleget.add(baseAffinity);
//        if (isNew)
//        {
            looseCache.put( baseAffinity, deleget.loosen(baseAffinity)  );
            tightCache.put( baseAffinity, deleget.tighten(baseAffinity) );
//        }
        return isNew;
    }


    //--------------------------------------------------------------------
    public Collection<Affinity> loosen(Affinity affinity)
    {
        add( affinity );
        return looseCache.get( affinity );
    }

    public Collection<Affinity> tighten(Affinity affinity)
    {
        add( affinity );
        return tightCache.get( affinity );
    }
}
