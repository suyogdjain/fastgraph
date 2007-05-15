package ao.ai.rl.gp.agent.params;

/**
 *
 */
public enum TreeSizeLimit
{
    SIXTEEN(16), THIRTY_TWO(32),
    SIXTY_FOUR(64), NINETY_SIX(96),
    ONE_TWENTY_EIGHT(128), TWO_FIFTY_SIX(256),
    THREE_EIGHTY_FOUR(384), FIVE_TWELVE(512);

    private final int value;

    private TreeSizeLimit(int val)
    {
        value = val;
    }

    public int size()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return "treeSize=" + value;
    }
}
