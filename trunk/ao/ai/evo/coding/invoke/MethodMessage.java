package ao.ai.evo.coding.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 *
 */
public class MethodMessage extends AbstractMessage
{
    //--------------------------------------------------------------------
    private final Method deleget;


    //--------------------------------------------------------------------
    public MethodMessage(Method toInvoke)
    {
        super(toInvoke.getParameterTypes());
        deleget = toInvoke;
    }


    //--------------------------------------------------------------------
    public boolean isStatic()
    {
        return Modifier.isStatic(deleget.getModifiers());
    }


    //--------------------------------------------------------------------
    public Object dispatch(Object receiver, Object... args)
    {
        try
        {
            return deleget.invoke(receiver, args);
        }
        catch (Exception e)
        {
            throw new Error(
                    deleget.toString() + " :: " +
                        String.valueOf(receiver) + " :: " +
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

        MethodMessage that = (MethodMessage) o;
        return deleget.equals(that.deleget);
    }

    @Override
    public int hashCode()
    {
        return deleget.hashCode();
    }
}
