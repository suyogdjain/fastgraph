package ao.ai.evo.coding.invoke;

import ao.ai.evo.coding.Coding;

/**
 *
 */
public interface Message
{
    public boolean isStatic();

    public boolean[] productArgs();

    public Object dispatch(Object receiver, Object... args);

    public Coding asCoding();
}
