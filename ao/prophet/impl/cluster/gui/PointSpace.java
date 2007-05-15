package ao.prophet.impl.cluster.gui;

import ao.graph.Graph;
import ao.graph.common.RealEdgeWeight;
import ao.prophet.impl.cluster.Cluster;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class PointSpace
{
    //--------------------------------------------------------------------
    private List<Point> points;


    //--------------------------------------------------------------------
    public PointSpace()
    {
        points = new ArrayList<Point>();
    }


    //--------------------------------------------------------------------
    public void add(Point point)
    {

    }


    //--------------------------------------------------------------------
    public Graph<Cluster<Point>, RealEdgeWeight> asGraph()
    {
        return null;
    }


}
