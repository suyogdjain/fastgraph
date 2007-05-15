package ao.ai.rl.gp.agent.params;

import ao.ai.evo.deme.ConsistentDeme;
import ao.ai.evo.deme.RandomDeme;
import ao.ai.evo.deme.Deme;

/**
 *
 */
public enum PopulationType
{
    CONSISTENT(ConsistentDeme.class),
    RANDOM(RandomDeme.class);

    private final Class<? extends Deme> value;

    private PopulationType(Class<? extends Deme> val)
    {
        value = val;
    }

    public Class<? extends Deme> type()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return "pop=" + value.getSimpleName();
    }
}
