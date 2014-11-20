package ao.graph.impl.fast;

import ao.graph.user.EdgeWeight;
import ao.graph.user.NodeData;

import java.util.*;

/**
// * @param D
// * @param W
 */
public class TreeBuffer<D extends NodeData<D>, W extends EdgeWeight<W>>
{
    //--------------------------------------------------------------------
    private final ByIndexComparator BY_INDEX =
            new ByIndexComparator();

    private List<SortedMap<HexEdge<D, W>, HexEdge<D,W>>> loHiIncidence =
            new ArrayList<SortedMap<HexEdge<D,W>, HexEdge<D,W>>>();

    private FastGraph<D, W> SINK;
    
    
    //--------------------------------------------------------------------
    /**
     * @param sink ...
     */
    public TreeBuffer(FastGraph<D, W> sink)
    {
        SINK = sink;
    }

            
    //--------------------------------------------------------------------
    /**
     * @param nodeData ...
     */
    public void add(D nodeData)
    {
        loHiIncidenceOf( nodeData );
    }
    
    private SortedMap<HexEdge<D,W>, HexEdge<D,W>>
            loHiIncidenceOf(D nodeData)
    {
        int index = SINK.add(nodeData);
        
        if (index >= loHiIncidence.size())
        {
            SortedMap<HexEdge<D,W>, HexEdge<D,W>> incidence =
                    new TreeMap<HexEdge<D,W>, HexEdge<D,W>>(BY_INDEX);
            loHiIncidence.add(incidence);
            return incidence;
        }
        else
        {
            return loHiIncidence.get(index);
        }
    }

    private class ByIndexComparator
            implements Comparator<HexEdge<D, W>>
    {
        public int compare(HexEdge<D, W> edgeA, HexEdge<D, W> edgeB)
        {
            return edgeA.majorEndpoint(false)
                    .compareTo(
                        edgeB.majorEndpoint(false));
        }
    }
    
    
    //--------------------------------------------------------------------
    /**
     * @param dataA ...
     * @param dataB ...
     * @param edgeWeight ...
     * @return  ...
     */
    public boolean join(D dataA, D dataB, W edgeWeight)
    {
        HexEdge<D, W> edge = SINK.edgeOf(dataA, dataB, edgeWeight);
        if (edge == null) return false;
        add(dataA);
        add(dataB);
        
        SortedMap<HexEdge<D,W>, HexEdge<D,W>> incidence =
                loHiIncidence.get(
                    edge.minorEndpoint(false).label());
        
        HexEdge<D,W> existing = incidence.get( edge );
        if (existing != null)
        {
            existing.mergeWeightWith( edgeWeight );
            return false;
        }
        
        incidence.put(edge, edge);
        return true;
    }
    
    
    //--------------------------------------------------------------------
    /**
     */
    public void flush()
    {
        for (SortedMap<HexEdge<D, W>, HexEdge<D,W>>
                incidence : loHiIncidence)
        {
            if (incidence.isEmpty()) continue;
            
            NodeIncidence<D, W> nodeIncidence =
                    SINK.loHiIncidenceOf(
                        incidence.firstKey().minorEndpoint(false));
            for (HexEdge<D, W> edge : incidence.keySet())
            {
                nodeIncidence.addGreatest(edge);
                SINK.indexEdgeByWeight(edge);
            }
        }
        
        for (int i = loHiIncidence.size() - 1; i >= 0; i--)
        {
            for (HexEdge<D, W> edge : loHiIncidence.get(i).keySet())
            {
                SINK.hiLoIncidenceOf(
                        edge.majorEndpoint(false)
                ).addLeast(edge);
            }
        }
        
        SINK = null;
        loHiIncidence = null;
    }
}


