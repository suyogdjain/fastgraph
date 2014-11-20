package ao.graph.impl.fast;

import ao.graph.user.EdgeWeight;
import ao.graph.user.NodeData;

/**
 *
 */
public class IncidenceErrasor<D extends NodeData<D>, W extends EdgeWeight<W>>
        implements Comparable<IncidenceErrasor<D, W>>
{
    //--------------------------------------------------------------------
    private final boolean LO_HI_ORDER;

    private HexEdge<D, W> current;
    private HexEdge<D, W> end;

    private FastNode<D, W> currentMinor;


    //--------------------------------------------------------------------
    public IncidenceErrasor(
            HexEdge<D, W> from, HexEdge<D, W> to,
            boolean loHiOrder)
    {
        LO_HI_ORDER = loHiOrder;

        current = from;
        end     = to;

        if (from != null)
        {
            currentMinor = from.minorEndpoint(LO_HI_ORDER);
        }
    }


    //--------------------------------------------------------------------
    /**
     * @return ...
     */
    public HexEdge<D, W> current()
    {
        return current;
    }
    
    /**
     * @return ...
     */
    public FastNode<D, W> currentMajor()
    {
        return current.majorEndpoint( LO_HI_ORDER );
    }

    /**
     * @return ...
     */
    public FastNode<D, W> currentMinor()
    {
        //return current.minorEndpoint( LO_HI_ORDER );
        return currentMinor;
    }

    /**
     * @return ...
     */
    public W currentWeight()
    {
        return current.weight();
    }

    /**
     * @return ...
     */
    public boolean loHiOrder()
    {
        return LO_HI_ORDER;
    }


    //--------------------------------------------------------------------
    /**
     * @return ...
     */
    public boolean advance()
    {
        if (current == end)
        {
            current      = null;
            currentMinor = null;
        }
        else if (current != null)
        {
            current      = current.next ( LO_HI_ORDER );
            currentMinor = current.minorEndpoint( LO_HI_ORDER );
        }

        return (current != null);
    }

    /**
     */
    public void erraseCurrent()
    {
        if (current == null) return;

        currentMinor.incidentEdges( !LO_HI_ORDER ).remove( current );
    }

    /**
     * @param hexEdge ...
     */
    public void shiftEndIfEquals( HexEdge<D, W> hexEdge )
    {
        if (end == hexEdge && end != null)
        {
            end = end.previous( LO_HI_ORDER );

//            end = (current == end)
//                    ? null
//                    : end.previous( LO_HI_ORDER );
        }
    }


    //--------------------------------------------------------------------
    /**
     * @param hexEdge ...
     * @return  ...
     */
    public boolean minorEquals( HexEdge<D, W> hexEdge )
    {
        return currentMinor == hexEdge.minorEndpoint( LO_HI_ORDER );
    }

    public int compareTo(IncidenceErrasor<D, W> that)
    {
        return (currentMinor.label() - that.currentMinor.label());
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        if (current == null) return "()";

        StringBuilder sb = new StringBuilder();

        sb.append('(');
        sb.append(current.majorEndpoint(LO_HI_ORDER).label());
        sb.append("|..., ");

        for (HexEdge<D, W> cursor = current;
             cursor != end;
             cursor  = cursor.next(LO_HI_ORDER))
        {
            sb.append(cursor.minorEndpoint(LO_HI_ORDER).label());
            sb.append(", ");
        }

        sb.append(end.minorEndpoint(LO_HI_ORDER).label());
        sb.append(')');

        return sb.toString();
    }
}
