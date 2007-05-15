package ao.prophet.impl.appraisal;

import ao.prophet.impl.Stats;

/**
 * Summarises the appraisals pertaining to some item.
 * Prefers more consistant scores.
 *
 * Note: NOT thread safe.
 */
public class CumulativeAppraisal
{
    //--------------------------------------------------------------------
    private float cumulativeValue;

    private int   numAppraisals;
    private float adjustedCumValue;


    //--------------------------------------------------------------------
    public CumulativeAppraisal() {}
    public CumulativeAppraisal(Appraisal appraisal)
    {
        add( appraisal );
    }


    //--------------------------------------------------------------------
    public void add(Appraisal appraisal)
    {
        cumulativeValue += appraisal.value();

        numAppraisals++;
        adjustedCumValue = Float.NaN;
    }

    public float value()
    {
        if (! Float.isNaN(adjustedCumValue))
        {
            return adjustedCumValue;
        }

        adjustedCumValue =
                Stats.accountForStatisticalError(
                        cumulativeValue, numAppraisals);
        return adjustedCumValue;
    }
}
