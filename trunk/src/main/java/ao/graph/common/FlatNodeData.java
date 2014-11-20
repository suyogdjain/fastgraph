package ao.graph.common;

import ao.graph.user.NodeData;

/**
 * Basic implemintation
 */
public class FlatNodeData implements NodeData<FlatNodeData>
{
    private static volatile int nextId = 0;

    //-----------------------------------------------------------------------------------
//    private final Text ID;
//    private final String ID;
    private final int ID;

    /**
     */
    public FlatNodeData()
    {
        //ID = Text.valueOf( nextId++ );
//        ID = String.valueOf( nextId++ );
        ID = nextId++;
    }

    //private FlatNodeData(Text id)
//    private FlatNodeData(String id)
//    {
//        ID = id;
//    }

    public FlatNodeData mergeWith(FlatNodeData other)
    {
        return new FlatNodeData();
//        return new FlatNodeData("(" + ID + "," + other.ID + ")");

//        TextBuilder txt = new TextBuilder(ID.length() + other.ID.length() + 3);
//
//        txt.append('(');
//        txt.append(ID);
//        txt.append(',');
//        txt.append(other.ID);
//        txt.append(')');
//
//        return new FlatNodeData( txt.toText() );
    }

    //-----------------------------------------------------------------------------------
    @Override
    public boolean equals(Object obj)
    {
//        return ID.equals(((FlatNodeData) obj).ID);
        return obj == this;
    }

    @Override
    public int hashCode()
    {
//        return ID.hashCode();
        return ID;
    }

    @Override
    public String toString()
    {
        return String.valueOf( ID );
    }
}
