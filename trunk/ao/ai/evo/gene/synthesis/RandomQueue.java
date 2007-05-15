package ao.ai.evo.gene.synthesis;


import ao.util.rand.Rand;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Queue that stores items in random order.
 */
public class RandomQueue<T>
{
    //--------------------------------------------------------------------
    private final List<T> ITEMS;


    //--------------------------------------------------------------------
    public RandomQueue()
    {
        ITEMS = new ArrayList<T>();
    }


    //--------------------------------------------------------------------
    public int size()
    {
        return ITEMS.size();
    }


    //--------------------------------------------------------------------
    public boolean isEmpty()
    {
        return ITEMS.isEmpty();
    }


    //--------------------------------------------------------------------
    public void add(T item)
    {
        ITEMS.add(item);
    }


    //--------------------------------------------------------------------
    public T removeRandom()
    {
        if (ITEMS.isEmpty())
        {
            return null;
        }

        int lastIndex = ITEMS.size() - 1;
        int randIndex = Rand.nextInt(ITEMS.size());

        Collections.swap(ITEMS, lastIndex, randIndex);

        return ITEMS.remove(lastIndex);
    }
}
