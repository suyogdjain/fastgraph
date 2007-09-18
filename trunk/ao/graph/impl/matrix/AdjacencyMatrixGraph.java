package ao.graph.impl.matrix;

import ao.graph.Graph;
import ao.graph.impl.common.struct.EndpointsImpl;
import ao.graph.impl.common.struct.NodeDataPairImpl;
import ao.graph.impl.common.struct.DataAndWeightImpl;
import ao.graph.impl.common.EdgeWeights;
import ao.graph.impl.common.index.EndpointIndex;
import ao.graph.struct.DataAndWeight;
import ao.graph.struct.Endpoints;
import ao.graph.struct.NodeDataPair;
import ao.graph.user.EdgeWeight;
import ao.graph.user.EdgeWeightDomain;
import ao.graph.user.NodeData;
import javolution.util.FastMap;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AdjacencyMatrixGraph<D extends NodeData<D>, W extends EdgeWeight<W>>
        implements Graph<D, W>
{
    //--------------------------------------------------------------------
    private final FastMap<D, Integer> INDEX;
    private final List<D>             REVERSE_INDEX;
    private final List<EdgeWeight[]>  MATRIX;
    private final EndpointIndex<D, W> BY_EDGE_INDEX;
    private final W                   DEFAULT_NULL_EDGE;

    //--------------------------------------------------------------------
    /**
     * @param edgeWeightDomain ...
     * @param defaultNullEdge ...
     */
    public AdjacencyMatrixGraph(
            EdgeWeightDomain<W> edgeWeightDomain,
            W                   defaultNullEdge)
    {
        INDEX             = new FastMap<D, Integer>();
        REVERSE_INDEX     = new ArrayList<D>();
        MATRIX            = new ArrayList<EdgeWeight[]>();
        BY_EDGE_INDEX     = new EndpointIndex<D, W>(edgeWeightDomain);
        DEFAULT_NULL_EDGE = defaultNullEdge;
    }


    //--------------------------------------------------------------------
    public int add(D nodeData)
    {
        Integer index = INDEX.get( nodeData );
        if (index == null)
        {
            int matrixSize = MATRIX.size();
            INDEX.put(nodeData, matrixSize);
            MATRIX.add(new EdgeWeight[matrixSize]);
            REVERSE_INDEX.add(nodeData);
            return matrixSize;
        }
        return index;
    }


    //--------------------------------------------------------------------
    public boolean join(D dataA, D dataB, W edgeWeight)
    {
        int indexA = add(dataA);
        int indexB = add(dataB);

        return (indexA < indexB)
                ? join(indexA, dataA, indexB, dataB, edgeWeight)
                : indexA > indexB &&
                  join(indexB, dataB, indexA, dataA, edgeWeight);
    }

    private boolean join(
            int loDataIndex, D loData,
            int hiDataIndex, D hiData,
            W   edgeWeight)
    {
        W existingEdge = matrix(hiDataIndex, loDataIndex);
        W newEdge      = (existingEdge == null)
                         ? edgeWeight
                         : existingEdge.mergeWith(edgeWeight);
        if (existingEdge != null)
        {
            BY_EDGE_INDEX.remove(
                    EndpointsImpl.newInstance(
                            loData, hiData, existingEdge));
        }
        MATRIX.get(hiDataIndex)[loDataIndex] = newEdge; 
        BY_EDGE_INDEX.add(
                EndpointsImpl.newInstance(
                        loData, hiData, newEdge));

//        validate();
        return existingEdge == null;
    }


    //--------------------------------------------------------------------
    public DataAndWeight<D, W> merge(D nodeA, D nodeB)
    {
        return merge(nodeA, nodeB, DEFAULT_NULL_EDGE);
    }

    public DataAndWeight<D, W> merge(D nodeA, D nodeB, W nullWeight)
    {
        if (nodeA == null ||
            nodeB == null ||
            nodeA.equals( nodeB )) return null;

        int indexA = add(nodeA);
        int indexB = add(nodeB);

        return (indexA < indexB)
                ? merge(indexA, nodeA, indexB, nodeB, nullWeight)
                : merge(indexB, nodeB, indexA, nodeA, nullWeight);
    }

    private DataAndWeight<D, W> merge(
            int loDataIndex, D loData,
            int hiDataIndex, D hiData,
            W nullWeight)
    {
//        validate();

//        int startSize = INDEX.size();
        D union   = loData.mergeWith( hiData );
        W xWeight = matrix(hiDataIndex, loDataIndex);
        if (xWeight != null)
        {
            BY_EDGE_INDEX.remove(
                    EndpointsImpl.newInstance(loData, hiData, xWeight));
        }

        int unionIndex = add(union);

        horizontalOverlap(
                loDataIndex, loData,
                hiDataIndex, hiData,
                unionIndex,  union,
                nullWeight);

        horizontalHiOvershoot(
                loDataIndex,
                hiDataIndex, hiData,
                unionIndex,  union,
                nullWeight);

        verticalOverlap(
                loDataIndex, loData,
                hiDataIndex, hiData,
                unionIndex,  union,
                nullWeight);

        verticalLoOvershoot(
                loDataIndex, loData,
                hiDataIndex,
                unionIndex, union,
                nullWeight);

//        validate();
//        assert INDEX.size() == startSize - 1;
        return DataAndWeightImpl.newInstance(union, xWeight);
    }

    private void verticalLoOvershoot(
            int loDataIndex, D loData,
            int hiDataIndex,
            int unionIndex,  D union,
            W nullWeight)
    {
        W[] unionLows  = subMatrix(unionIndex);

        for (int i = loDataIndex + 1; i < hiDataIndex; i++)
        {
            W overlap = matrix(i, loDataIndex);
            if (overlap != null)
            {
                if (unionLows[i] != null)
                {
                    BY_EDGE_INDEX.remove(
                        EndpointsImpl.newInstance(
                                REVERSE_INDEX.get(i), union, unionLows[i]));
                }

                unionLows[i] =
                        EdgeWeights.merge(overlap, unionLows[i], nullWeight);

                BY_EDGE_INDEX.remove(
                    EndpointsImpl.newInstance(
                            loData, REVERSE_INDEX.get(i), overlap));
                BY_EDGE_INDEX.add(
                    EndpointsImpl.newInstance(
                            REVERSE_INDEX.get(i), union, unionLows[i]));

                subMatrix(i)[loDataIndex] = null;
            }
        }
    }

    private void verticalOverlap(
            int loDataIndex, D loData,
            int hiDataIndex, D hiData,
            int unionIndex,  D union,
            W nullWeight)
    {
        W[] unionLows  = subMatrix(unionIndex);

        for (int i = hiDataIndex + 1; i < unionIndex; i++)
        {
            W[] subMatrix = subMatrix( i );
            if (subMatrix == null ||
                    subMatrix[loDataIndex] == null &&
                    subMatrix[hiDataIndex] == null
               ) continue;

            if (subMatrix[loDataIndex] != null)
            {
                BY_EDGE_INDEX.remove(
                    EndpointsImpl.newInstance(
                            loData, REVERSE_INDEX.get(i), subMatrix[loDataIndex]));
            }
            if (subMatrix[hiDataIndex] != null)
            {
                BY_EDGE_INDEX.remove(
                    EndpointsImpl.newInstance(
                            hiData, REVERSE_INDEX.get(i), subMatrix[hiDataIndex]));
            }

            unionLows[i] =
                    EdgeWeights.merge(
                            subMatrix[loDataIndex],
                            subMatrix[hiDataIndex],
                            nullWeight);

            BY_EDGE_INDEX.add(
                EndpointsImpl.newInstance(
                        REVERSE_INDEX.get(i), union, unionLows[i]));

            subMatrix[loDataIndex] = null;
            subMatrix[hiDataIndex] = null;
        }
    }

    private void horizontalHiOvershoot(
            int loDataIndex,
            int hiDataIndex, D hiData,
            int unionIndex,  D union,
            W nullWeight)
    {
        W[] hiLows    = subMatrix(hiDataIndex);
        W[] unionLows = subMatrix(unionIndex);

        for (int i = loDataIndex + 1; i < hiDataIndex; i++)
        {
            if (hiLows[i] == null) continue;

            unionLows[i] =
                    EdgeWeights.merge(hiLows[i], nullWeight);

            BY_EDGE_INDEX.remove(
                EndpointsImpl.newInstance(
                        REVERSE_INDEX.get(i), hiData, hiLows[i]));
            BY_EDGE_INDEX.add(
                EndpointsImpl.newInstance(
                        REVERSE_INDEX.get(i), union, unionLows[i]));
        }
        clear(hiDataIndex);
    }

    private void horizontalOverlap(
            int loDataIndex, D loData,
            int hiDataIndex, D hiData,
            int unionIndex,  D union,
            W nullWeight)
    {
        W[] loLows     = subMatrix(loDataIndex);
        W[] hiLows     = subMatrix(hiDataIndex);
        W[] unionLows  = subMatrix(unionIndex);

        for (int i = 0; i < loDataIndex; i++)
        {
            if (loLows[i] == null && hiLows[i] == null) continue;

            if (loLows[i] != null)
            {
                BY_EDGE_INDEX.remove(
                    EndpointsImpl.newInstance(
                            REVERSE_INDEX.get(i), loData, loLows[i]));
            }
            if (hiLows[i] != null)
            {
                BY_EDGE_INDEX.remove(
                    EndpointsImpl.newInstance(
                            REVERSE_INDEX.get(i), hiData, hiLows[i]));
            }

            unionLows[i] =
                    EdgeWeights.merge(
                            loLows[i], hiLows[i], nullWeight);

            BY_EDGE_INDEX.add(
                EndpointsImpl.newInstance(
                        REVERSE_INDEX.get(i), union, unionLows[i]));
        }
        clear(loDataIndex);
    }

    private void clear(int index)
    {
        INDEX.remove(
                REVERSE_INDEX.get( index ));
        REVERSE_INDEX.set(index, null);
        MATRIX.set(index, null);
    }


    //--------------------------------------------------------------------
    public NodeDataPair<D> antiEdge()
    {
        for (FastMap.Entry<D, Integer> hi  = INDEX.head(),
                                       end = INDEX.tail();
             (hi = hi.getNext()) != end;)
        {
            for (FastMap.Entry<D, Integer> lo = INDEX.head();
                 (lo = lo.getNext()) != hi;)
            {
                if (MATRIX.get( hi.getValue() )
                              [ lo.getValue() ] != null)
                {
                    return NodeDataPairImpl.newInstance(
                                lo.getKey(), hi.getKey());
                }
            }
        }
        return null;
    }

    
    //--------------------------------------------------------------------
    public Endpoints<D, W> nodesIncidentHeaviestEdge()
    {
        return BY_EDGE_INDEX.nodesIncidentHeaviestEdge();
    }

    public Endpoints<D, W> nodesIncidentLightestEdge()
    {
        return BY_EDGE_INDEX.nodesIncidentLightestEdge();
    }


    //--------------------------------------------------------------------
    private W matrix(int hiIndex, int loIndex)
    {
        W[] subMatrix = subMatrix(hiIndex);
        return (subMatrix == null)
                ? null
                : subMatrix( hiIndex )[ loIndex ];
    }

    @SuppressWarnings("unchecked")
    private W[] subMatrix(int hiIndex)
    {
        return (W[]) MATRIX.get( hiIndex );
    }


    //--------------------------------------------------------------------
    public void validate()
    {
        for (int i = 0; i < REVERSE_INDEX.size(); i++)
        {
            if (REVERSE_INDEX.get(i) != null)
            {
                assert INDEX.get( REVERSE_INDEX.get(i) ) == i
                        : "index/reverse mismatch at " + i;

                assert MATRIX.get(i).length == i
                        : "wrong matrix length " +
                          MATRIX.get(i).length +
                            " != " + i;

                for (int j = 0; j < i; j++)
                {
                    if (MATRIX.get(i)[j] != null)
                    {
                        assert REVERSE_INDEX.get(j) != null
                                : "matrix/reverse mismatch at " + i + "/" + j;
                    }
                }
            }
            else
            {
                assert !INDEX.containsValue(i)
                        : "reverse/index mismatch at " + i;

                assert MATRIX.get(i) == null
                        : "reverse/matrix mismatch at " + i;
            }
        }

        NodeDataPair<D> antiEdge = antiEdge();
        if (antiEdge != null)
        {
            assert INDEX.containsKey( antiEdge.dataA() );
            assert INDEX.containsKey( antiEdge.dataB() );
        }

        for (Endpoints<D, W> byWeight : BY_EDGE_INDEX.all())
        {
            assert INDEX.containsKey( byWeight.nodeDataA() )
                    : byWeight.nodeDataA() + " missing" +
                        ", weight " + byWeight.weight();
            assert INDEX.containsKey( byWeight.nodeDataB() );

            assert matrix(INDEX.get( byWeight.nodeDataB() ),
                          INDEX.get( byWeight.nodeDataA() ))
                    .equals( byWeight.weight() )
                      : "weight mismatch for " + byWeight;
        }
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return "Matrix Graph";
    }
}

