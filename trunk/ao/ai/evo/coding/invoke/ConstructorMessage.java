package ao.ai.evo.coding.invoke;

import java.lang.reflect.Constructor;
import java.util.Arrays;

/**
 *
 */
public class ConstructorMessage extends AbstractMessage
{
    //--------------------------------------------------------------------
    private final Constructor deleget;


    //--------------------------------------------------------------------
    public ConstructorMessage(Constructor toInvoke)
    {
        super(toInvoke.getParameterTypes());
        deleget = toInvoke;
    }


    //--------------------------------------------------------------------
    public boolean isStatic()
    {
        return true;
    }


    //--------------------------------------------------------------------
    public Object dispatch(Object receiver, Object... args)
    {
        try
        {
            return deleget.newInstance(args);
        }
        catch (Exception e)
        {
            throw new Error(
                    deleget.toString() + " :: " +
                        Arrays.toString(args),
                    e);
        }
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return deleget.getName();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConstructorMessage that = (ConstructorMessage) o;
        return deleget.equals(that.deleget);
    }

    @Override
    public int hashCode()
    {
        return deleget.hashCode();
    }
}
