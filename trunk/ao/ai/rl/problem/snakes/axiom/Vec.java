package ao.ai.rl.problem.snakes.axiom;

import ao.ai.axiom.Num;

/**
 * Vector from one Coord to another.
 */
public class Vec
{
    //--------------------------------------------------------------------
    private static final Vec NORTH =
            new Vec(Coord.origin(),
                    new Coord(Num.zero(), Num.one()));

    private static final Vec SOUTH =
            new Vec(Coord.origin(),
                    new Coord(Num.zero(), Num.one().negate()));

    private static final Vec EAST =
            new Vec(Coord.origin(),
                    new Coord(Num.one(), Num.zero()));

    private static final Vec WEST =
            new Vec(Coord.origin(),
                    new Coord(Num.one().negate(), Num.zero()));


    //--------------------------------------------------------------------
    public static Vec north() { return NORTH; }
    public static Vec south() { return SOUTH; }
    public static Vec east()  { return EAST;  }
    public static Vec west()  { return WEST;  }


    //--------------------------------------------------------------------
    private final Coord tail;
    private final Coord head;


    //--------------------------------------------------------------------
    public Vec(Coord from, Coord to)
    {
        assert from != null &&
               to   != null;

        tail = from;
        head = to;
    }


    //--------------------------------------------------------------------
    public Coord tail()
    {
        return tail;
    }
    public Coord head()
    {
        return head;
    }

    //--------------------------------------------------------------------
    public Num length()
    {
        Num aSquared = tail.x().minus( head.x() ).pow( Num.two() );
        Num bSquared = tail.y().minus( head.y() ).pow( Num.two() );
        return aSquared.plus( bSquared ).sqrt();
    }
    public Num taxiCabLength()
    {
        Num xLength = tail.x().minus( head.x() ).absoluteValue();
        Num yLength = tail.y().minus( head.y() ).absoluteValue();
        return xLength.plus( yLength );
    }

    //--------------------------------------------------------------------
    public Num direction() // in radians
    {
        Num deltaX = head.x().minus( tail.x() );
        Num deltaY = head.y().minus( tail.y() );
        
        return deltaY.divide( deltaX ).inverseTangent();
    }

    public Coord dot(Vec with)
    {
        return head.times( with.head )
                .plus( tail.conjugate().times( with.tail.conjugate() ) );
    }

    public Vec times(Num factor)
    {
        return new Vec(tail.times( factor ),
                       head.times( factor ));
    }

    public Vec plus(Vec addend)
    {
        return new Vec(tail.plus(addend.tail),
                       head.plus(addend.head));
    }
    public Vec minus(Vec vec)
    {
        return new Vec(tail.minus(vec.tail),
                       head.minus(vec.head));
    }
    public Vec reverse()
    {
        return new Vec(head, tail);
    }

    public Num angleBetween(Vec vec)
    {
        return vec.direction().minus( direction() );
    }

    public Vec rotate(Num angle)
    {
        return new Vec(tail,
                       head.minus( tail ).rotate(angle).plus( tail ));
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return tail + " --> " + head;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vec vec = (Vec) o;
        return tail.equals(vec.tail) && head.equals(vec.head);
    }

    @Override
    public int hashCode()
    {
        int result;
        result = tail.hashCode();
        result = 31 * result + head.hashCode();
        return result;
    }
}
