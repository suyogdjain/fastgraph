package ao.graph.impl.fast;

import ao.graph.user.EdgeWeight;
import ao.graph.user.NodeData;
import ao.graph.user.EdgeWeightDomain;
import ao.graph.struct.Endpoints;

import java.util.*;

/**
 * Sorts BidiNodes by weight.
// * @param D
// * @param W
 */
public class ByWeightIndex<D extends NodeData<D>, W extends EdgeWeight<W>>
{
    //--------------------------------------------------------------------
    private final EdgeWeightDomain<W> DOMAIN;
    private final HexEdge<D, W>       INDEX[];

    private int greatestUsedIndex;
    private int smallestUsedIndex;


    //--------------------------------------------------------------------
    /**
     * @param domain ...
     */
    @SuppressWarnings( "unchecked" )
    public ByWeightIndex(EdgeWeightDomain<W> domain)
    {
        DOMAIN = domain;
        INDEX  = new HexEdge/*<D, W>*/[ domain.size() ];

        greatestUsedIndex = -1;
        smallestUsedIndex = INDEX.length;
    }


    //--------------------------------------------------------------------
    /**
     * @param hexEdge ...
     */
    public void add(HexEdge<D, W> hexEdge)
    {
        int index = DOMAIN.labelOf(hexEdge.weight());
        updateUsedIndexExtrema( index );

        hexEdge.addToEqualWeightLabelList( INDEX[index] );
        INDEX[ index ] = hexEdge;
    }

    /**
     * @param hexEdge ...
     */
    public void remove(HexEdge<D, W> hexEdge)
    {
        int index = DOMAIN.labelOf(hexEdge.weight());
        INDEX[ index ] =
                hexEdge.removeFromEqualWeightLabelList( INDEX[index] );
    }

    /**
     * @param hexEdge ...
     * @param weight ...
     */
    public void merge(HexEdge<D, W> hexEdge, W weight)
    {
        int oldIndex = DOMAIN.labelOf( hexEdge.weight() );

        hexEdge.mergeWeightWith( weight );

        int newIndex = DOMAIN.labelOf( hexEdge.weight() );

        if (oldIndex != newIndex)
        {
            updateUsedIndexExtrema( newIndex );

            INDEX[ oldIndex ] =
                    hexEdge.removeFromEqualWeightLabelList( INDEX[oldIndex] );

            hexEdge.addToEqualWeightLabelList( INDEX[newIndex] );
            INDEX[ newIndex ] = hexEdge;
        }
    }



    /**
     * -------------------------------------------------------------------
     *
     * Uses label to return the weight with the greatest (last in
     *  ascending order) weight.
     * @return Endpoints with the greatest EdgeWeight by natural
     *              Comparable ordering.
     */
    public Endpoints<D, W> endpointsOfHeaviestEdge()
    {
        for (int i = greatestUsedIndex; i >= smallestUsedIndex; i--)
        {
            HexEdge<D, W> node = INDEX[i];
            if (node != null)
            {
                greatestUsedIndex = i;
                return node.toEndpoints();
            }
        }

        return null;
    }

    /**
     * Uses label to return the weight with the least (first in
     *  ascending order) weight.
     * @return Endpoints with the greatest EdgeWeight by natural
     *          Comparable ordering.
     */
    public Endpoints<D, W> endpointsOfLightestEdge()
    {
        for (int i = smallestUsedIndex; i <= greatestUsedIndex; i++)
        {
            HexEdge<D, W> node = INDEX[i];
            if (node != null)
            {
                smallestUsedIndex = i;
                return node.toEndpoints();
            }
        }

        return null;
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


    //--------------------------------------------------------------------
    // for testing purposes only....
    /**
     * @param allNodes ...
     * @return ...
     */
    public boolean validate(Collection<HexEdge<D, W>> allNodes)
    {
        validateByWeightLinkage();

        Map<Integer, Set<HexEdge<D, W>>> actual = actualByWeightIndex();
        Map<Integer, Set<HexEdge<D, W>>> theory =
                theoreticalByWeightIndex( allNodes );

        for (int i = 0; i < INDEX.length; i++)
        {
            Set<HexEdge<D, W>> delta =
                    setDelta(theory.get(i), actual.get(i));

            assert delta.isEmpty()
                    : "should be " + theory.get(i) +
                        " but was " + actual.get(i) +
                        " delta = " + delta;
        }

        return true;
    }

    private Set<HexEdge<D, W>> setDelta(
            Set<HexEdge<D, W>> setA,
            Set<HexEdge<D, W>> setB )
    {
        Set<HexEdge<D, W>> delta = new HashSet<HexEdge<D, W>>();

        delta.addAll( setA );
        delta.addAll( setB );

        for (HexEdge<D, W> n : setA)
        {
            if (setB.contains(n))
            {
                delta.remove( n );
            }
        }

        return delta;
    }

    private Map<Integer, Set<HexEdge<D, W>>>
            theoreticalByWeightIndex(
                Collection<HexEdge<D, W>> allEdges)
    {
        Map<Integer, Set<HexEdge<D, W>>> theoretical =
                new HashMap<Integer, Set<HexEdge<D, W>>>();

        for (int i = 0; i < INDEX.length; i++)
        {
            theoretical.put(i, new HashSet<HexEdge<D, W>>());
        }

        for (HexEdge<D, W> hexEdge : allEdges)
        {
            assert theoretical.get(
                        DOMAIN.labelOf(hexEdge.weight())
                    ).add( hexEdge );
        }

        return theoretical;
    }

    private Map<Integer, Set<HexEdge<D, W>>>
            actualByWeightIndex()
    {
        Map<Integer, Set<HexEdge<D, W>>> actual =
                new HashMap<Integer, Set<HexEdge<D, W>>>();

        for (int i = 0; i < INDEX.length; i++)
        {
            Set<HexEdge<D, W>> equalByWeightLabel =
                    new HashSet<HexEdge<D, W>>();

            actual.put(i, equalByWeightLabel);

            for (HexEdge<D, W> cursor  = INDEX[i];
                               cursor != null;
                               cursor  = cursor.byWeightPrevious())
            {
                equalByWeightLabel.add( cursor );
            }
        }

        return actual;
    }

    private boolean validateByWeightLinkage()
    {
        for (HexEdge<D, W> tail : INDEX)
        {
            validateByWeightLinkage(tail);
        }

        return true;
    }

    private boolean validateByWeightLinkage(
            HexEdge<D, W> tail)
    {
        if (tail == null) return true;

        HexEdge<D, W> next   = null;
        HexEdge<D, W> cursor = tail;

        while (cursor != null)
        {
            assert next == null || cursor == next.byWeightPrevious();
            assert cursor.byWeightNext() == next;

            next   = cursor;
            cursor = cursor.byWeightPrevious();
        }

        return true;
    }
}
