package ao.ai.rl.problem.snakes;

import ao.ai.axiom.Num;
import ao.ai.axiom.Bool;
import ao.ai.rl.problem.snakes.axiom.Coord;
import ao.ai.rl.problem.snakes.axiom.Vec;
import ao.sw.engine.v2.Snake;
import ao.sw.engine.board.BoardArrangement;
import ao.sw.engine.board.BoardLocationImpl;
import ao.sw.engine.board.BoardLocation;

import java.util.List;
import java.util.ArrayList;


/**
 *
 */
public class InputObject
{
    //--------------------------------------------------------------------
    private final BoardArrangement BOARD;
    private final Snake            ME;
    private final Snake            OPPONENT;

    private final List<Vec> MY_BODY  = new ArrayList<Vec>();
    private final List<Vec> OPP_BODY = new ArrayList<Vec>();


    //--------------------------------------------------------------------
    public InputObject(BoardArrangement board,
                       Snake            me,
                       Snake            opponent)
    {
        BOARD    = board;
        ME       = me;
        OPPONENT = opponent;

        List<BoardLocation> myLocs  = ME.body();
        List<BoardLocation> oppLocs = OPPONENT.body();

        int   howMany     = 5;
        float myFraction  = (float)myLocs.size()  / (howMany + 1);
        float oppFraction = (float)oppLocs.size() / (howMany + 1);

        for (int i = 0; i < howMany; i++)
        {
            BoardLocation myFrom  = myLocs.get((int)( i      * myFraction));
            BoardLocation myTo    = myLocs.get((int)((i + 1) * myFraction));

            BoardLocation oppFrom = oppLocs.get((int)( i  *     oppFraction));
            BoardLocation oppTo   = oppLocs.get((int)((i + 1) * oppFraction));

            MY_BODY.add(  new Vec(toCoord(myFrom),  toCoord(myTo))  );
            OPP_BODY.add( new Vec(toCoord(oppFrom), toCoord(oppTo)) );
        }
    }


    //--------------------------------------------------------------------
    public Num myLength()
    {
        return new Num(ME.body().size(), 0);
    }


    //--------------------------------------------------------------------
    public Coord northEdge()
    {
        return Coord.origin().translateY(
                new Num(ME.head().getRow()) );
    }
    public Coord southEdge()
    {
        return Coord.origin().translateY(
                new Num(-(BOARD.getRowCount() - ME.head().getRow())) );
    }
    public Coord eastEdge()
    {
        return Coord.origin().translateX(
                new Num(BOARD.getColumnCount() - ME.head().getColumn()) );
    }
    public Coord westEdge()
    {
        return Coord.origin().translateX(
                new Num(-ME.head().getColumn()) );
    }


    //--------------------------------------------------------------------
    // in how many truns is the given location going to be empty.
    public Num emptyIn(Coord location)
    {
        BoardLocation loc = toLocation( location );

        if (! loc.availableIn( BOARD ))
        {
            return new Num(10000);
        }
        else
        {
            Snake occupier =
                    ME.body().contains( loc )
                    ? ME : OPPONENT;
            return new Num(occupier.occupiedFor(loc));
        }
    }

    public Bool isEmpty(Coord location)
    {
        return Bool.valueOf( toLocation( location )
                                .availableIn(BOARD) );
    }


    //    .
    //   .X.  given X, how many of its neighbours (dots) are empty.
    //    .     X itself is also counted.
    public Num emptyNeighbours(Coord of)
    {
        BoardLocation loc = toLocation(of);

        int emptyCount = 0;
        if (loc.translate( 0,  0).availableIn( BOARD )) emptyCount++;
        if (loc.translate( 0,  1).availableIn( BOARD )) emptyCount++;
        if (loc.translate( 0, -1).availableIn( BOARD )) emptyCount++;
        if (loc.translate( 1,  0).availableIn( BOARD )) emptyCount++;
        if (loc.translate(-1,  0).availableIn( BOARD )) emptyCount++;
        return new Num( emptyCount );
    }

    // first non-empty place in given angle.
    public Coord takenAt(Num angle)
    {
        return takenAt(Coord.origin(), angle);
    }

    public Coord takenAt(Coord from, Num angle)
    {
        Coord cursor = new Coord(Num.one(), Num.zero()).rotate(angle);
        while (isEmpty(from.plus(cursor)).value())
        {
            cursor = cursor.towardOrigin( new Num(-1) );
        }
        return cursor;
    }


    //--------------------------------------------------------------------
    // for sensing your, and the opponent's body.
    // tail is 1, head is 5

    public Vec myVertebrae01()
    {
        return MY_BODY.get(0);
    }
    public Vec myVertebrae12()
    {
        return MY_BODY.get(1);
    }
    public Vec myVertebrae23()
    {
        return MY_BODY.get(2);
    }
    public Vec myVertebrae34()
    {
        return MY_BODY.get(3);
    }
    public Vec myVertebrae45()
    {
        return MY_BODY.get(4);
    }

    public Vec oppVertebrae01()
    {
        return OPP_BODY.get(0);
    }
    public Vec oppVertebrae12()
    {
        return OPP_BODY.get(1);
    }
    public Vec oppVertebrae23()
    {
        return OPP_BODY.get(2);
    }
    public Vec oppVertebrae34()
    {
        return OPP_BODY.get(3);
    }
    public Vec oppVertebrae45()
    {
        return OPP_BODY.get(4);
    }


    //----------------------------------------------------------------
    private BoardLocation toLocation(Coord coord)
    {
        long deltaRows = Math.round(coord.y().signedAbs());
        long deltaCols = Math.round(coord.x().signedAbs());
        long row       = ME.head().getRow()    + deltaRows;
        long col       = ME.head().getColumn() + deltaCols;

        if (Math.abs(row) >= Integer.MAX_VALUE ||
                Math.abs(col) >= Integer.MAX_VALUE)
        {
            return new BoardLocationImpl(
                        Integer.MAX_VALUE, Integer.MAX_VALUE);
        }
        else
        {
            return new BoardLocationImpl((int) row, (int) col);
        }
    }

    private Coord toCoord(BoardLocation location)
    {
        int rowDelta = location.getRow()    - ME.head().getRow();
        int colDelta = location.getColumn() - ME.head().getColumn();

        return new Coord(new Num(colDelta), new Num(rowDelta));
    }


    //----------------------------------------------------------------
    @Override
    public String toString()
    {
        return "Snakes Environment";
    }
}
