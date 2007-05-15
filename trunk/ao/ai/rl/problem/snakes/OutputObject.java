package ao.ai.rl.problem.snakes;

import ao.ai.axiom.Num;
import ao.sw.engine.board.Direction;
import ao.sw.engine.player.MoveSpecifier;

/**
 *
 */
public class OutputObject
{
    //--------------------------------------------------------------------
    private static final double TWO_PI      = 2.0   * Math.PI;

    private static final double NORTH_UPPER = 3.0/4 * Math.PI;
    private static final double NORTH_LOWER = 1.0/4 * Math.PI;

    private static final double WEST_UPPER = 5.0/4 * Math.PI;
    private static final double WEST_LOWER = NORTH_UPPER;

    private static final double SOUTH_UPPER = 7.0/4 * Math.PI;
    private static final double SOUTH_LOWER = WEST_UPPER;


    //--------------------------------------------------------------------
    private final MoveSpecifier deleget;


    //--------------------------------------------------------------------
    public OutputObject(MoveSpecifier moveSpecifier)
    {
        deleget = moveSpecifier;
    }


    //--------------------------------------------------------------------
    public void setDirection(Num angle)
    {
        double rads = (angle.abs() * Math.E) % TWO_PI;

        Direction dir =
                (NORTH_UPPER >= rads && rads > NORTH_LOWER)
                ? Direction.NORTH
                : (WEST_UPPER >= rads && rads > WEST_LOWER)
                  ? Direction.WEST
                  : (SOUTH_UPPER >= rads && rads > SOUTH_LOWER)
                    ? Direction.SOUTH
                    : Direction.EAST;

//        Num prevAngleChoice = prevAngleChoice();
        deleget.setDirection(dir);
//        throw new TerminalReached();
//        return prevAngleChoice;
    }

//    private Num prevAngleChoice()
//    {
//        Direction last = deleget.latestDirection();
//        return new Num((last == null || last.equals( Direction.EAST ))
//                       ? 0
//                       : (last.equals( Direction.NORTH ))
//                         ? Math.PI / 2
//                         : (last.equals( Direction.WEST ))
//                           ?     Math.PI
//                           : 3.0*Math.PI / 2);
//    }

    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return "Snakes Output";
    }
}
