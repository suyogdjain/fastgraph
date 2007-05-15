package ao.ai.evo.primordial_soup;

import ao.ai.evo.genetic_material.GeneticMaterial;
import ao.ai.evo.promoter.affinity.Affinity;
import ao.ai.evo.promoter.Promoter;

import java.util.*;

/**
 * threadsafe
 */
public class GenePool
{
    //--------------------------------------------------------------------
    // return type => thoughts
    private final Set<GeneticMaterial>                 allGeneticMaterial;
    private final Map<Affinity, List<GeneticMaterial>> byAffinityIndie;
    private final Map<Affinity, List<GeneticMaterial>> byAffinityDepnd;
    private final Map<Promoter, List<GeneticMaterial>> byPromoter;


    //--------------------------------------------------------------------
    public GenePool()
    {
        byAffinityIndie = new HashMap<Affinity, List<GeneticMaterial>>();
        byAffinityDepnd = new HashMap<Affinity, List<GeneticMaterial>>();
        byPromoter      = new HashMap<Promoter, List<GeneticMaterial>>();
        allGeneticMaterial = new HashSet<GeneticMaterial>();
    }

//    public GenePool(
//            GenePool copyUniqueGeneticMaterial)
//    {
//        this();
//        addAll( copyUniqueGeneticMaterial.material() );
//    }


    //--------------------------------------------------------------------
    public synchronized Collection<GeneticMaterial> material()
    {
        return Collections.unmodifiableCollection( allGeneticMaterial );
    }

    public synchronized List<GeneticMaterial>
            materialReturning(
                    Affinity productAffinity,
                    boolean  needIndependent )
    {
        return Collections.unmodifiableList(
                listOf(productAffinity, needIndependent) );
    }

    public synchronized List<GeneticMaterial>
            materialLike( Promoter promoter )
    {
        return Collections.unmodifiableList( listOf(promoter) );
    }


    //--------------------------------------------------------------------
    public synchronized void add(GeneticMaterial geneticMaterial)
    {
        if (allGeneticMaterial.contains( geneticMaterial )) return;
        allGeneticMaterial.add( geneticMaterial );

        Promoter promoter = geneticMaterial.promoter();
        boolean  indie    = promoter.isIndependent();

        listOf(promoter.productAffinity(), indie).add( geneticMaterial );
        listOf(promoter                         ).add( geneticMaterial );
    }


    //--------------------------------------------------------------------
    public synchronized void addAll(
            Collection<GeneticMaterial> geneticMaterial)
    {
        for (GeneticMaterial addend : geneticMaterial)
        {
            add( addend );
        }
    }


    //--------------------------------------------------------------------
    private List<GeneticMaterial>
            listOf(Affinity affinity, boolean indie)
    {
        Map<Affinity, List<GeneticMaterial>>
                byAffinity = (indie
                              ? byAffinityIndie
                              : byAffinityDepnd );

        List<GeneticMaterial> list = byAffinity.get( affinity );
        if (list == null)
        {
            list = new ArrayList<GeneticMaterial>();
            byAffinity.put(affinity, list);
        }
        return list;
    }
    private List<GeneticMaterial>
            listOf(Promoter promoter)
    {
        List<GeneticMaterial> list = byPromoter.get( promoter );
        if (list == null)
        {
            list = new ArrayList<GeneticMaterial>();
            byPromoter.put(promoter, list);
        }
        return list;
    }


    //--------------------------------------------------------------------
    public synchronized GenePool copy()
    {
        GenePool copy = new GenePool();
        copy.addAll( allGeneticMaterial );
        return copy;
    }
}