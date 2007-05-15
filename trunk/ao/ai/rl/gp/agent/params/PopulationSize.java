package ao.ai.rl.gp.agent.params;

/**
 *
 */
public enum PopulationSize
{
    THIRTY_TWO(32), SIXTY_FOUR(64),
    NINETY_SIX(96), ONE_TWENTY_EIGHT(128),
    TWO_FIFTY_SIX(256), THREE_EIGHTY_FOUR(384),
    FIVE_TWELVE(512), OUT_THOUSAND_TWENTY_FOUR(1024);

    private final int size;

    private PopulationSize(int populationSize)
    {
        size = populationSize;
    }

    public int size()
    {
        return size;
    }

    @Override
    public String toString()
    {
        return "popSize=" + size;
    }
}
