package ao.ai.rl.tourney;

/**
 * Tracks fitness.
 * Uses the Elo elgorithm with the logistic curve, as described at:
 *  http://en.wikipedia.org/wiki/Chess_rating#Mathematical_details
 */
public class EloRating implements Comparable<EloRating>
{
    //--------------------------------------------------------------------
    private static final double BASE_RATING = 1000.0;
//    private static final double K           = BASE_RATING / 100;
//    private static final double REDUCTION   = K;


    //--------------------------------------------------------------------
    private double rating;


    //--------------------------------------------------------------------
    public EloRating()
    {
        this( BASE_RATING );
    }

    private EloRating(double initRating)
    {
        rating = initRating;
    }


    //--------------------------------------------------------------------
    public void updateRating(EloRating opponent, int outcome)
    {
        double thisDelta =
                (outcome == 0)
                ? 0.5
                : (outcome > 0)
                  ? 1.0 : 0.0;
        double thatDelta = 1.0 - thisDelta;

        double thisExpectedDelta =
                1.0 / (1 + Math.pow(10, (opponent.rating - rating)/400));
        double thatExpectedDelta =
                1.0 / (1 + Math.pow(10, (rating - opponent.rating)/400));

        double thisK = BASE_RATING / Math.sqrt(rating);
        double thatK = BASE_RATING / Math.sqrt(opponent.rating);

        rating          += thisK * (thisDelta - thisExpectedDelta);
        opponent.rating += thatK * (thatDelta - thatExpectedDelta);
    }


    //--------------------------------------------------------------------
    public double rating()
    {
        return rating;
    }


    //--------------------------------------------------------------------
    public EloRating average(EloRating with)
    {
        return new EloRating(
                    Math.abs(rating - with.rating)/2);
    }


    //--------------------------------------------------------------------
    public double normalDeviation()
    {
        return (rating - BASE_RATING) / 200.0;
    }


    //--------------------------------------------------------------------
    public EloRating copy()
    {
        return new EloRating(rating);
    }


    //--------------------------------------------------------------------
//    public EloRating setBack()
//    {
//        return new EloRating(rating - REDUCTION);
//    }


    //--------------------------------------------------------------------
    public String toString()
    {
//        return "[" + ((int) rating()) + " -> " +
//                     ((double)((int) (normalDeviation() * 100))) / 100.0
//                + "]";

        return String.format("%4d", (int) rating());
    }

    public int compareTo(EloRating o)
    {
        return Double.compare(rating, o.rating);
    }
}
