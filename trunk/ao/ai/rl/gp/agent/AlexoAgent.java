package ao.ai.rl.gp.agent;

import ao.ai.evo.deme.Deme;
import ao.ai.evo.fitness.Feedback;
import ao.ai.evo.genetic_material.IoGeneticMaterial;
import ao.ai.evo.genome.Genome;
import ao.ai.evo.primordial_soup.PrimordialSoup;
import ao.ai.rl.Agent;
import com.google.inject.Inject;

/**
 * Uses:
 *  - Cluster Analysis: to find features in environment,
 *      and break it down into nieches.
 *  - Genetic Programming: to represent thoughts.
 *  - Hierarchical Functionset Decomposition: to re-use
 *      useful thought combinations.
 *
 * Note: objects of this class are NOT threadsafe.
 */
public class AlexoAgent implements Agent
{
    //--------------------------------------------------------------------
    private PrimordialSoup thinkWith;

    private boolean initedInput = false;
    private boolean initedSense = false;
    private IoGeneticMaterial actionObject;
    private IoGeneticMaterial inputObject;

    private Object key;
    private Deme   pop;
    private Genome actor;


    //--------------------------------------------------------------------
    public AlexoAgent()
    {
        thinkWith = new PrimordialSoup();
    }

    public void setupKey(Object key)
    {
        this.key = key;
    }


    //--------------------------------------------------------------------
    @Inject
    public void injectPopulation(Deme population)
    {
        pop = population;
    }


    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public synchronized void sense(Object input)
    {
        assert !(input instanceof Class)
                : "cannot sense class objects";

        if (! initedSense)
        {
            inputObject = new IoGeneticMaterial(input);

            thinkWith.add( inputObject );
            thinkWith.addMethods( input.getClass() );

            initedSense = true;
        }
        else
        {
            inputObject.replaceWith( input );
        }
    }


    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public synchronized void act(Object on)
    {
        if (! initedInput)
        {
            actionObject = new IoGeneticMaterial(on);

            thinkWith.add( actionObject );
            thinkWith.addMethods( on.getClass() );

            initedInput = true;
        }
        else
        {
            actionObject.replaceWith( on );
        }

        if (actor != null)
        {
            pop.actionPerformed( actor );
        }
        actor = pop.nextIndividual( thinkWith );
        actor.evaluate();
    }

//    @SuppressWarnings("unchecked")
//    public void act(ActionFactorySource on)
//    {
//        if (! initedInput)
//        {
//            actionObject = on.instance();
//
//            thinkWith.add( actionObject );
//            thinkWith.addAll( on.genes() );
//
//            initedInput = true;
//        }
//        else
//        {
//            actionObject.replaceWith( on.instanceObject() );
//        }
//
//        pop.nextIndividual( thinkWith ).evaluate();
//    }


    //--------------------------------------------------------------------
    public synchronized void reinforce(Feedback feedback)
    {
        pop.select(feedback, thinkWith);
    }

    public synchronized void didNotAct()
    {
        if (actor != null)
        {
            pop.didNotAct( actor );
            actor = null;
        }
    }


    //--------------------------------------------------------------------
    public synchronized void thinkWith(Object concept)
    {
        if (concept instanceof Class)
        {
            thinkWith.addMethods( (Class) concept );
        }
        else
        {
            thinkWith.add( new IoGeneticMaterial<Object>(concept) );
            thinkWith.addMethods( concept.getClass() );
        }
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return key.toString();
    }
}
