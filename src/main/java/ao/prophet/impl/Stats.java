package ao.prophet.impl;

/**
 * Common statistic related rutines.
 */
public class Stats
{
    private Stats() {}

    /**
     * The idea here is that the larger the sample degree the more
     *  confident we are in the conclution.
     * For example if itemA has 50 positive votes for it, and
     *  itemB has 100 positive votes and
     *  neither has any negative votes then
     * `they both have a perfect score.  However itemB has a larger
     *  sample degree and therefore is more likely to better.
     *
     * The statistical error here is calculated as the square root
     * `of the population degree.
     *
     * @param quantity the quantity to be adjusted
     * @param dataPointCount population sample degree to adjust for
     * @return the adjusted (towards zero) quantity
     */
    public static double accountForStatisticalError(
            double quantity,
            int dataPointCount)
    {
        double statisticalErrorPercent =
                Math.sqrt(dataPointCount) / (dataPointCount + 1);
        double statisticalError        =
                statisticalErrorPercent * quantity;

        return quantity - statisticalError;
    }

    public static float accountForStatisticalError(
            float quantity,
            int dataPointCount)
    {
        float statisticalErrorPercent =
                (float) (Math.sqrt(dataPointCount) /
                            (dataPointCount + 1));
        float statisticalError        =
                statisticalErrorPercent * quantity;

        return quantity - statisticalError;
    }
}
