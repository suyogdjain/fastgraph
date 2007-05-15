package ao.ai.rl.gp.agent.params;

/**
 *
 */
public enum TreeDepthLimit
{
    FOUR(4), FIVE(5), SIX(6), SEVEN(7),
    EIGHT(8), NINE(9), TEN(10), ELEVEN(11),
    TWELVE(12), THIRTEEN(13), FOURTEEN(14),
    FIFTEEN(15), SIXTEEN(16), SEVENTEEN(17),
    EIGHTEEN(18), NINETEEN(19), TWENTY(20);

    private final int value;

    private TreeDepthLimit(int val)
    {
        value = val;
    }

    public int depth()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return "treeDepth=" + value;
    }
}
