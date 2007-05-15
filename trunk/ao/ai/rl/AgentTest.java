package ao.ai.rl;

import ao.ai.rl.problem.snakes.AgentPlayer;
import ao.ai.rl.tourney.Tournament;
import ao.ai.sample.RandomAi;
import ao.sw.control.SnakesRunner;
import ao.util.serial.Stringer;
import com.google.inject.Guice;
import com.google.inject.Injector;


/**
 *
 */
public class AgentTest
{
    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        Injector injector =
                Guice.createInjector(
                        new Config());

        Tournament problem =
                injector.getInstance(Tournament.class);

        problem.init();
        for (int i = 1; i < Integer.MAX_VALUE; i++)
        {
            problem.runOnce();
            if (i % 10000 == 0)
            {
                snakesTest( problem.bestOfBreed() );
            }
        }
    }


    //----------------------------------------------------------
    public static void snakesTest(Agent vs)
    {
        System.out.println(Stringer.toString( vs ));

        SnakesRunner.asynchDemoMatch(
                15, 15,
                new RandomAi(),
                new AgentPlayer( vs ));
    }


    //--------------------------------------------------------------------
    private static void errorReducerTest(Agent agent)
    {
//        ErrorReducer reducer =
//                new ErrorReducer(
//                        agent,
//                        new AckleysFunction());
//
//        for (int i = 0; i < 100000000; i++)
//        {
//            double error = reducer.attemptReduce();
//
//            if (i % 1000 == 0)
//            {
//                System.out.println(-error);
//            }
//        }
    }
}


