package ao.ai.rl.problem.approx;

import ao.ai.evo.coding.Coding;
import ao.ai.evo.gene.Gene;
import ao.ai.evo.genetic_material.AbstractGpMaterial;
import ao.ai.evo.genetic_material.IoGeneticMaterial;
import ao.ai.evo.primordial_soup.GenePool;
import ao.ai.evo.product.Product;
import ao.ai.evo.promoter.GpPromoter;
import ao.ai.evo.promoter.Promoter;
import ao.ai.rl.gp.TerminalReached;
import ao.ai.rl.gp.thought.factory.ActionFactorySource;

/**
 * Attempt to minimize error.
 */
public abstract class Approximator<I, O>
{
    //--------------------------------------------------------------------
    private Class<?> OUT_CLASS;


    //--------------------------------------------------------------------
    public abstract I nextInput();

    public abstract O outputFor(I input);

    public abstract double error(O expected, O recieved);


    //--------------------------------------------------------------------
    public Delta deltaFunction(I forInput)
    {
        if (OUT_CLASS == null)
        {
            OUT_CLASS = outputFor(forInput).getClass();
        }

        return new Delta<I,O>( forInput );
    }

    public double errorFor(Delta<I, O> delta)
    {
        return error(outputFor(delta.forInput),
                      delta.givenOutput);
    }
    

    //--------------------------------------------------------------------
    public class Delta<I, O> implements ActionFactorySource
    {
        //----------------------------------------------------------------
        private O givenOutput;
        private I forInput;

        private IoGeneticMaterial instance;
        private GenePool          methods;

        //----------------------------------------------------------------
        private Delta(I forInput)
        {
            this.forInput = forInput;

            instance = new IoGeneticMaterial<Object>(this);
            methods  = new GenePool();

            methods.add(new AbstractGpMaterial() {
                public Coding coding() {
                    return new Coding() {
                        @SuppressWarnings("unchecked")
                        public Product encode(Gene... inputs) {
                            ((Delta) inputs[0].express().build())
                                    .output( inputs[1].express().build() );
                            return null;
                        }
                    };
                }

                public Promoter promoter() {
                    return new GpPromoter(
                            Void.TYPE, Delta.class, OUT_CLASS);
                }
            });
        }

        //----------------------------------------------------------------
        public IoGeneticMaterial instance()
        {
            return instance;
        }

        public GenePool genes()
        {
            return methods;
        }

        public Object instanceObject()
        {
            return this;
        }


        //----------------------------------------------------------------
        public void output(O output)
        {
            givenOutput = output;
            throw new TerminalReached();
        }

        //----------------------------------------------------------------
        @Override
        public String toString()
        {
            return "Delta for input: " + forInput +
                    ", of given output: " + givenOutput;
        }
    }
}
