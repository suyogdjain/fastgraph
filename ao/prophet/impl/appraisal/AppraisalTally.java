package ao.prophet.impl.appraisal;

import javolution.util.FastMap;
import ao.prophet.impl.cluster.FinalPly;
import ao.prophet.impl.ItemWeights;
import ao.prophet.ItemFilter;

import java.util.Collection;

/**
 * Holds info about the appraisals currently being concidered.
 *
 * Simple first-in-first-out for users.
 *
 * Note that objects of this class are thread safe but not concurrent.
 * That means that there will never be any errors but changes
 *  done by two users or by the same user twice and the same time
 *  might not see each other.
 * This causes some of the numebrs like appraisalCount to be
 *  off sometimes.
 * To improve the situation appraisalCount is recalculated every time
 *  it is examined.
 * Overall at best the data in a tally is close estimation.
 *
 */
public class AppraisalTally<U, I>
{
    private final FastMap<U, AppraisalHistory<I>> TALLY;

    private final int MAX_USERS;
    private final int MAX_APPRAISALS;
    private final int MAX_APPRAISALS_PER_USER;

    private int appraisalCount = 0;

    private FinalPly.Lookup<I> currFinalPly;


    public AppraisalTally(
            int maxUsers,
            int maxAppraisals,
            int maxAppraisalsPerUser)
    {
        TALLY = new FastMap<U, AppraisalHistory<I>>(maxUsers);
        TALLY.setShared( true );

        MAX_USERS               = maxUsers;
        MAX_APPRAISALS          = maxAppraisals;
        MAX_APPRAISALS_PER_USER = maxAppraisalsPerUser;
    }

    //--------------------------------------------------------------------
    public int userCount()
    {
        return TALLY.size();
    }

    public U leastRecentlyUsed()
    {
        if (TALLY.isEmpty()) return null;
        return TALLY.head().getNext().getKey();
    }


    //--------------------------------------------------------------------
    public void tally(U user, I item, Appraisal appraisal)
    {
        AppraisalHistory<I> history;
        AppraisalHistory<I> currHistory = TALLY.remove( user );

        if (currHistory == null)
        {
            if (TALLY.size() >= (MAX_USERS - 1))
            {
                FastMap.Entry<U, AppraisalHistory<I>> lruEntry =
                        TALLY.head().getNext();

                TALLY.remove( lruEntry.getKey() );
                appraisalCount -= lruEntry.getValue().size();

                lruEntry.getValue().clear();
                history = lruEntry.getValue();
            }
            else
            {
                history = new AppraisalHistory<I>(
                                MAX_APPRAISALS_PER_USER, currFinalPly);
            }
        }
        else
        {
            history = currHistory;
        }

        if ( history.size() < MAX_APPRAISALS_PER_USER )
        {
            if (appraisalCount >= MAX_APPRAISALS)
            {
                FastMap.Entry<U, AppraisalHistory<I>> lruEntry =
                        TALLY.head().getNext();

                lruEntry.getValue().removeEarliest();

                if (lruEntry.getValue().size() == 0)
                {
                    TALLY.remove( lruEntry.getKey() );
                }
            }
            else
            {
                appraisalCount++;
            }
        }

        TALLY.put(user, history);
        history.add(item, appraisal);
    }


    //--------------------------------------------------------------------
    public void removeUser(U user)
    {
        AppraisalHistory<I> history = TALLY.remove( user );

        if (history != null)
        {
            appraisalCount -= history.size();
        }
    }


    //--------------------------------------------------------------------
    public void removeItem(I item)
    {
        for (FastMap.Entry<U, AppraisalHistory<I>> e    = TALLY.tail(),
                                                   head = TALLY.head();
             (e = e.getPrevious()) != head;)
        {
            if (e.getValue().remove( item ))
            {
                appraisalCount--;

                if (e.getValue().size() == 0)
                {
                    TALLY.remove( e.getKey() );
                }
            }
        }
    }


    //--------------------------------------------------------------------
    public void examine(AppraisalHistory.Historian<I> historian)
    {
        int appraisalCountBefore = 0;
        int newAppraialCount     = 0;

        for (FastMap.Entry<U, AppraisalHistory<I>> e    = TALLY.tail(),
                                                   head = TALLY.head();
             (e = e.getPrevious()) != head;)
        {
            e.getValue().examine( historian );

            newAppraialCount += e.getValue().size();
        }

        // estimates that newAppraisals captured half the
        //  intermidiate changes correctly.
        appraisalCount = newAppraialCount +
                         ((appraisalCount - appraisalCountBefore) >> 1);
    }


    //--------------------------------------------------------------------
    public void updateClusters(FinalPly.Lookup<I> latestVersion)
    {
        ItemWeights.Translator<I> translator =
                (currFinalPly == null)
                ? null
                : currFinalPly.newWeightTranslator( latestVersion );

        currFinalPly = latestVersion;

        for (FastMap.Entry<U, AppraisalHistory<I>> e    = TALLY.tail(),
                                                   head = TALLY.head();
             (e = e.getPrevious()) != head;)
        {
            e.getValue().updateClusters( latestVersion, translator );
        }
    }


    //--------------------------------------------------------------------
    public Collection<I> mostLikely(
            U user, int howMany, ItemFilter<I> filter)
    {
        AppraisalHistory<I> history = TALLY.get( user );

        return (history == null)
                ? null
                : history.mostLikely(howMany, filter);
    }
}
