package ao.ai.rl.gp.agent;

import ao.ai.rl.gp.agent.params.*;
import ao.ai.evo.deme.Deme;
import ao.util.rand.Rand;

/**
 *
 */
public class FlexConfigParams
{
    //--------------------------------------------------------------------
    private MacroMutationWeight     macroWeight;
    private MacroMutationSizeLimit  macroSize;
    private MacroMutationDepthLimit macroDepth;
    private MicroMutationWeight     microWeight;
    private RecombinationWeight     crossWeight;

    private PopulationType populationType;
    private PopulationSize populationSize;

    private TreeSizeLimit  treeSize;
    private TreeDepthLimit treeDepth;


    //--------------------------------------------------------------------
    // random instance
    public FlexConfigParams()
    {
        this( Rand.fromArray(MacroMutationSizeLimit.values()),
              Rand.fromArray(MacroMutationDepthLimit.values()),
              Rand.fromArray(MacroMutationWeight.values()),
              Rand.fromArray(MicroMutationWeight.values()),
              Rand.fromArray(RecombinationWeight.values()),
              Rand.fromArray(PopulationType.values()),
              Rand.fromArray(PopulationSize.values()),
              Rand.fromArray(TreeSizeLimit.values()),
              Rand.fromArray(TreeDepthLimit.values())
        );
    }

    public FlexConfigParams(
            MacroMutationSizeLimit  macroSize,
            MacroMutationDepthLimit macroDepth,

            MacroMutationWeight     macroWeight,
            MicroMutationWeight     microWeight,
            RecombinationWeight     crossWeight,

            PopulationType populationType,
            PopulationSize populationSize,

            TreeSizeLimit  treeSizeLimit,
            TreeDepthLimit treeDepthLimit)
    {
        this.macroWeight = macroWeight;
        this.macroSize   = macroSize;
        this.macroDepth  = macroDepth;
        this.microWeight = microWeight;
        this.crossWeight = crossWeight;

        this.populationType = populationType;
        this.populationSize = populationSize;

        this.treeSize  = treeSizeLimit;
        this.treeDepth = treeDepthLimit;
    }


    //--------------------------------------------------------------------
    public int macroWeight()  { return macroWeight.weight(); }
    public int macroSize()    { return macroSize.size();     }
    public int macroDepth()   { return macroDepth.depth();   }
    public int microWeight()  { return microWeight.weight(); }
    public int crossWeight()  { return crossWeight.weight(); }

    public Class<? extends Deme>
                populationType() { return populationType.type(); }
    public int   populationSize() { return populationSize.size(); }

    public int treeSize()  { return treeSize.size();   }
    public int treeDepth() { return treeDepth.depth(); }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return  macroSize  + "\t" +
                macroDepth + "\t" +
                macroWeight + "\t" +
                microWeight + "\t" +
                crossWeight + "\t" +
                populationType + "\t" +
                populationSize + "\t" +
                treeSize + "\t" +
                treeDepth;
    }


    //--------------------------------------------------------------------
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlexConfigParams that = (FlexConfigParams) o;

        return crossWeight == that.crossWeight &&
               macroDepth == that.macroDepth &&
               macroSize == that.macroSize &&
               macroWeight == that.macroWeight &&
               microWeight == that.microWeight &&
               populationSize == that.populationSize &&
               populationType == that.populationType &&
               treeDepth == that.treeDepth &&
               treeSize == that.treeSize;
    }

    @Override
    public int hashCode()
    {
        int result;
        result = (macroWeight != null ? macroWeight.hashCode() : 0);
        result = 31 * result + (macroSize != null ? macroSize.hashCode() : 0);
        result = 31 * result + (macroDepth != null ? macroDepth.hashCode() : 0);
        result = 31 * result + (microWeight != null ? microWeight.hashCode() : 0);
        result = 31 * result + (crossWeight != null ? crossWeight.hashCode() : 0);
        result = 31 * result + (populationType != null ? populationType.hashCode() : 0);
        result = 31 * result + (populationSize != null ? populationSize.hashCode() : 0);
        result = 31 * result + (treeSize != null ? treeSize.hashCode() : 0);
        result = 31 * result + (treeDepth != null ? treeDepth.hashCode() : 0);
        return result;
    }
}
