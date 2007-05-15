package ao.graph.impl.fast;

import ao.graph.user.NodeData;
import ao.graph.user.EdgeWeight;
import ao.graph.user.EdgeWeightDomain;
import ao.graph.Graph;
import ao.graph.struct.DataAndWeight;
import ao.graph.struct.NodeDataPair;
import ao.graph.struct.Endpoints;

import java.util.*;

/**
 *
 */
public class MatrixBuffer<D extends NodeData<D>, W extends EdgeWeight<W>>
{
    //--------------------------------------------------------------------
    private List<HexEdge/*<D,W>*/[]> hiLoIncidence =
            new ArrayList<HexEdge/*<D,W>*/[]>();

    private FastGraph<D, W> SINK;


    //--------------------------------------------------------------------
    /**
     * @param sink ...
     */
    public MatrixBuffer(FastGraph<D, W> sink)
    {
        SINK = sink;
    }
    public MatrixBuffer(EdgeWeightDomain<W> edgeWeightDomain,
                        W                   defaultNullEdge)
    {
        SINK = new FastGraph<D,W>(edgeWeightDomain, defaultNullEdge);
    }



    //--------------------------------------------------------------------
    /**
     * @param nodeData ...
     * @return ...
     */
    public int add(D nodeData)
    {
        return indexOf( nodeData );
    }

    private int indexOf(D nodeData)
    {
        int index = SINK.add(nodeData);
        assert index <= hiLoIncidence.size();  

        if (index == hiLoIncidence.size())
        {
            HexEdge[]  incidence = new HexEdge[index];
            hiLoIncidence.add(incidence);
        }

        return index;
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
        int indexA = indexOf(dataA);
        int indexB = indexOf(dataB);

        return (indexA < indexB)
                ? join(indexA, dataA, indexB, dataB, edgeWeight)
                : indexA > indexB &&
                  join(indexB, dataB, indexA, dataA, edgeWeight);
    }

    @SuppressWarnings("unchecked")
    private boolean join(
            int loDataIndex, D loData,
            int hiDataIndex, D hiData,
            W   edgeWeight)
    {
        HexEdge[]    loIncidence  = hiLoIncidence.get(hiDataIndex);
        HexEdge<D,W> existing = loIncidence[loDataIndex];
        if (existing != null)
        {
            existing.mergeWeightWith( edgeWeight );
            return false;
        }

        loIncidence[loDataIndex] =
                SINK.edgeOf(loData, hiData, edgeWeight);
        return true;
    }


    //--------------------------------------------------------------------
    /**
     * can be performed only once per MatrixBuffer object instance.
     * @return ...
     */
    @SuppressWarnings("unchecked")
    public FastGraph<D, W> flush()
    {
        for (int hiIndex = 1; hiIndex < hiLoIncidence.size(); hiIndex++)
        {
            HexEdge[] loIncidence = hiLoIncidence.get( hiIndex );
            int firstEdgeIndex = nextLoIndex(loIncidence, 0);
            if (firstEdgeIndex == -1) continue;

            NodeIncidence<D, W> hiLoNodeIncidence =
                        SINK.hiLoIncidenceOf(
                            loIncidence[ firstEdgeIndex ]
                                    .majorEndpoint(false));

            for (int loIndex = firstEdgeIndex;
                     loIndex != -1;
                     loIndex = nextLoIndex(loIncidence, loIndex + 1))
            {
                HexEdge<D, W> edge = loIncidence[ loIndex ];
                hiLoNodeIncidence.addGreatest( edge );
                SINK.indexEdgeByWeight( edge );
            }
        }

        for (int loIndex = hiLoIncidence.size() - 2;
                 loIndex >= 0;
                 loIndex--)
        {
            for (int hiIndex = nextHiIndex(loIndex,
                                           hiLoIncidence.size() - 1);
                     hiIndex != -1;
                     hiIndex = nextHiIndex(loIndex, hiIndex - 1))
            {
                HexEdge<D, W> edge = hiLoIncidence.get(hiIndex)[loIndex];
                SINK.loHiIncidenceOf(
                        edge.minorEndpoint(false)
                ).addLeast(edge);
            }
        }

        try { return SINK; }
        finally
        {
            SINK = null;
            hiLoIncidence = null;
        }
    }

    // from down to up
    private int nextHiIndex(int ofLowIndex, int startingFrom)
    {
        for (int hiIndex = startingFrom;
                 hiIndex > ofLowIndex;
                 hiIndex--)
        {
            HexEdge[] loIncidence = hiLoIncidence.get(hiIndex);
            if (loIncidence[ofLowIndex] != null)
            {
                return hiIndex;
            }
        }
        return -1;
    }

    // from left to right
    private int nextLoIndex(HexEdge[] in, int startingFrom)
    {
        for (int i = startingFrom; i < in.length; i++)
        {
            if (in[i] != null)
            {
                return i;
            }
        }
        return -1;
    }


    //--------------------------------------------------------------------
    public Graph<D, W> asGraph()
    {
        return new Graph<D, W>() {
            public int add(D nodeData) {
                return MatrixBuffer.this.add( nodeData );
            }

            public boolean join(D dataA, D dataB, W edgeWeight) {
                return MatrixBuffer.this.join(dataA, dataB, edgeWeight);
            }

            public DataAndWeight<D, W> merge(D nodeA, D nodeB) {
                throw new UnsupportedOperationException();
            }

            public DataAndWeight<D, W> merge(
                    D nodeA, D nodeB, W nullWeight) {
                throw new UnsupportedOperationException();
            }

            public NodeDataPair<D> antiEdge() {
                throw new UnsupportedOperationException();
            }

            public Endpoints<D, W> nodesIncidentHeaviestEdge() {
                throw new UnsupportedOperationException();
            }

            public Endpoints<D, W> nodesIncidentLightestEdge() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
