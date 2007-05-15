package ao.prophet.test;

import ao.prophet.Prophet;
import ao.prophet.impl.ProphetImpl;

import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Benchmark test for prophet.
 */
public class Benchmark
{
    //--------------------------------------------------------------------
    private static final int INIT_APPRAISALS = 20000;

    private static final int INIT_ITEMS       = 10000;
    private static final int EDIT_ITEMS_EVERY = 4000;

    private static final int INIT_USERS       = 1000;
    private static final int EDIT_USERS_EVERY = 5000;

    private static final int APPRAISE_EVERY_1 = 25;
    private static final int APPRAISE_EVERY_2 = 10;

    private static final int PREDICT_EVERY    = 20;
    private static final int ITEMS_TO_PREDICT = 20;

    private static final double MAX_DIVIATION  = 2.8;


    //--------------------------------------------------------------------
    private final Prophet<String, String> prophet;

    private final List<String> users = new Vector<String>();
    private final List<String> items = new Vector<String>();

    private final ScheduledExecutorService scheduler
            = Executors.newScheduledThreadPool(8);

    private final Random RAND = new Random(420);


    //--------------------------------------------------------------------
    public Benchmark()
    {
        addRandomStrings( items, INIT_ITEMS );
        addRandomStrings( users, INIT_USERS );

        prophet = new ProphetImpl<String, String>();

        initAppraisals();

        scheduler.scheduleAtFixedRate(
                new Predicter(),  100, PREDICT_EVERY,    TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(
                new Appraiser(),  100, APPRAISE_EVERY_1, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(
                new Appraiser(),  100, APPRAISE_EVERY_2, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(
                new ItemEditor(), 100, EDIT_ITEMS_EVERY, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(
                new UserEditor(), 100, EDIT_USERS_EVERY, TimeUnit.MILLISECONDS);
    }

    private void initAppraisals()
    {
        Appraiser appraiser = new Appraiser();

        for (int i = 0; i < INIT_APPRAISALS; i++)
        {
            appraiser.run();
        }
    }


    //--------------------------------------------------------------------
    private class UserEditor implements Runnable
    {
        public void run()
        {
            if (RAND.nextBoolean())
            {
                addToList(users);
            }
            else
            {
                prophet.removeUser( removeFromList(users) );
            }
        }
    }

    private class ItemEditor implements Runnable
    {
        public void run()
        {
            if (RAND.nextFloat() < 0.55)
            {
                prophet.addItem( addToList(items) );
            }
            else
            {
                prophet.removeItem( removeFromList(items) );
            }
        }
    }

    private class Appraiser implements Runnable
    {
        public void run()
        {
            if (RAND.nextBoolean())
            {
                prophet.likes( pickFromList(users), pickFromList(items) );
            }
            else
            {
                prophet.dislikes( pickFromList(users), pickFromList(items) );
            }
        }
    }

    private class Predicter implements Runnable
    {
        public void run()
        {
            prophet.predict( pickFromList(users), ITEMS_TO_PREDICT );
        }
    }


    //--------------------------------------------------------------------
    private synchronized String pickFromList(List<String> choices)
    {
        double gaussian   = RAND.nextGaussian();
        double percentile =
                Math.min(
                        Math.abs(gaussian) / 2.0,
                        MAX_DIVIATION - 0.0001
                ) / MAX_DIVIATION;

        return choices.get( (int)(choices.size() * percentile) );
    }

    private synchronized String addToList(List<String> choices)
    {
        String newRandom = randomString();
        choices.add( newRandom );
        return newRandom;
    }

    private synchronized String removeFromList(List<String> choices)
    {
        return choices.remove( (int)(RAND.nextDouble() * choices.size()) );
    }

    private String randomString()
    {
        return RAND.nextInt() + " " + RAND.nextInt(100);
    }

    private void addRandomStrings(Collection<String> list, int howMany)
    {
        for (int i = 0; i < howMany; i++)
        {
            list.add( randomString() );
        }
    }


    //---------------------------------------------------------
    public static void main(String[] args)
    {
        new Benchmark();
    }
}
