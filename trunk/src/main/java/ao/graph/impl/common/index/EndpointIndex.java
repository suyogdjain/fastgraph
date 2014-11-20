package ao.graph.impl.common.index;

import ao.graph.struct.Endpoints;
import ao.graph.user.EdgeWeight;
import ao.graph.user.EdgeWeightDomain;
import ao.graph.user.NodeData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Sorts Endpoints by weight.
// * @param D
// * @param W
 */
public class EndpointIndex<D extends NodeData<D>, W extends EdgeWeight<W>>
{
    private final EdgeWeightDomain<W>         DOMAIN;
    private final Collection<Endpoints<D, W>> INDEX[];

    private int greatestUsedIndex;
    private int smallestUsedIndex;

    /**
     * @param domain ...
     */
    @SuppressWarnings( "unchecked" )
    public EndpointIndex(EdgeWeightDomain<W> domain)
    {
        DOMAIN    = domain;
        INDEX     = new Collection/*<Endpoints<D, W>>*/[ domain.size() ];

        greatestUsedIndex = -1;
        smallestUsedIndex = INDEX.length;
    }

    /**
     * @param endpoints ...
     */
    public void add(Endpoints<D, W> endpoints)
    {
        int index = DOMAIN.labelOf(endpoints.weight());
        updateUsedIndexExtrema( index );

        equalEdgeList(index).add(endpoints);
    }

    /**
     * @param endpoints ...
     */
    public void remove(Endpoints<D, W> endpoints)
    {
        int index = DOMAIN.labelOf(endpoints.weight());

        Collection<Endpoints<D, W>> equalEdgeList = equalEdgeList(index);

        equalEdgeList.remove(endpoints);
        if (equalEdgeList.isEmpty())
        {
            INDEX[ index ] = null;
        }
    }

    private Collection<Endpoints<D, W>> equalEdgeList(int index)
    {
        Collection<Endpoints<D, W>> existant = INDEX[index];

        if (existant == null)
        {
            Collection<Endpoints<D, W>> addend =
                    new HashSet<Endpoints<D,W>>();
//                    new FastList<Endpoints<D, W>>();

            INDEX[index] = addend;
            return addend;
        }
        else
        {
            return existant;
        }
    }



    /**
     * -------------------------------------------------------------------
     *
     * Uses label to return the weight with the greatest
     *  (last in ascending order) asFloat.
     * @return Endpoints with the greatest EdgeWeight by
     *                      natural Comparable ordering.
     */
    public Endpoints<D, W> nodesIncidentHeaviestEdge()
    {
        for (int i = greatestUsedIndex; i >= smallestUsedIndex; i--)
        {
            Collection<Endpoints<D, W>> equalEdgeList = INDEX[i];
            if (equalEdgeList != null)
            {
                greatestUsedIndex = i;
                return equalEdgeList.iterator().next();
            }
        }

        return null;
    }

    /**
     * Uses label to return the weight with the least
     *  (first in ascending order) asFloat.
     * @return Endpoints with the greatest EdgeWeight by
     *                      natural Comparable ordering.
     */
    public Endpoints<D, W> nodesIncidentLightestEdge()
    {
        for (int i = smallestUsedIndex; i <= greatestUsedIndex; i++)
        {
            Collection<Endpoints<D, W>> equalEdgeList = INDEX[i];
            if (equalEdgeList != null)
            {
                smallestUsedIndex = i;
                return equalEdgeList.iterator().next();
            }
        }

        return null;
    }

    /**
     * @return ...
     */
    public Collection<Endpoints<D, W>> all()
    {
        Collection<Endpoints<D, W>> all =
                new ArrayList<Endpoints<D,W>>();

        for (Collection<Endpoints<D, W>> subIndex : INDEX)
        {
            if (subIndex != null)
            {
                all.addAll( subIndex );
            }
        }

        return all;
    }


    //--------------------------------------------------------------------
    private void updateUsedIndexExtrema(int newIndex)
    {
        if (newIndex > greatestUsedIndex)
        {
            greatestUsedIndex = newIndex;
        }

        if (newIndex < smallestUsedIndex)
        {
            smallestUsedIndex = newIndex;
        }
    }
}
