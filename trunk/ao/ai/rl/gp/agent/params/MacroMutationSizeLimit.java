package ao.ai.rl.gp.agent.params;

/**
 *
 */
public enum MacroMutationSizeLimit
{
    FOUR(4), FIVE(5), SIX(6), SEVEN(7),
    EIGHT(8), NINE(9), TEN(10), ELEVEN(11),
    TWELVE(12), THIRTEEN(13), FOURTEEN(14),
    FIFTEEN(15), SIXTEEN(16), SEVENTEEN(17),
    EIGHTEEN(18), NINETEEN(19), TWENTY(20),
    TWENTY_ONE(21), TWENTY_TWO(22),
    TWENTY_THREE(23), TWENTY_FOUR(24);

    private final int value;

    private MacroMutationSizeLimit(int val)
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
        return "macroSize=" + value;
    }
}
