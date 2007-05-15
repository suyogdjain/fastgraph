package ao.ai.rl.problem.approx;

import ao.ai.rl.Agent;
import ao.ai.evo.fitness.Feedback;

import java.util.LinkedList;

/**
 *
 */
public class ErrorReducer
{
    //--------------------------------------------------------------------
    private final Agent AGENT;
    private final Approximator APROX;

    private LinkedList<Double> errors = new LinkedList<Double>();
    private double             errorSum;

    //--------------------------------------------------------------------
    public ErrorReducer(Agent agent, Approximator aprox)
    {
        AGENT = agent;
        APROX = aprox;
    }


    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public double attemptReduce()
    {
        Object input = APROX.nextInput();
        Approximator.Delta delta = APROX.deltaFunction(input);

        AGENT.sense( input );
        AGENT.act( delta );

        double error = APROX.errorFor( delta );
        AGENT.reinforce( new Feedback(-Math.abs(error)) );

        return runningAverage( error );
//        return error;
    }

    private double runningAverage(double error)
    {
        if (errors.size() >= 128)
        {
            errorSum -= errors.removeLast();
        }
        errors.addFirst( error );
        errorSum += error;

        return errorSum / errors.size();
    }
}
