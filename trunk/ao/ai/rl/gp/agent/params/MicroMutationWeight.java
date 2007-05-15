package ao.ai.rl.gp.agent.params;

/**
 *
 */
public enum MicroMutationWeight
{
    ONE(1), FIVE(5), TEN(10), TWENTY(20),
    THIRTY(30), FOURTY(40), FIFTY(50), SIXTY(60),
    SEVENTY(70), EIGHTY(80), NINETY(90), HUNDRED(100);

    private final int value;

    private MicroMutationWeight(int val)
    {
        value = val;
    }

    public int weight()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return "microWeight=" + value;
    }
}
