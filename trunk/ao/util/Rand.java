/*
 * Rand.java
 *
 * Created on March 19, 2007, 11:26 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ao.util;

import java.util.List;
import java.util.Random;

/**
 *
 * @author shalom
 */
public class Rand
{
    ///--------------------------------------------------------------------
    private static final ThreadLocal<Random> RAND = new ThreadLocal<Random>() {
        @Override
        protected synchronized Random initialValue() {
            return new MTRandom(0x92cfceb39d57d914L);
        }
    };


    //--------------------------------------------------------------------
    private Rand() {}


    //--------------------------------------------------------------------
    /**
     * 
     * @return ...
     */
    public static Random rand()
    {
        return RAND.get();
    }


    //--------------------------------------------------------------------
    /**
     * 
     * @param list ...
     * @return  ...
     */
    public static <T> T fromList(List<T> list)
    {
        if (list.isEmpty()) return null;

        return list.get(
                Rand.nextInt(list.size()) );
    }

    //--------------------------------------------------------------------
    /**
     * 
     * @return ...
     */
    public static double nextDouble()
    {
        return RAND.get().nextDouble();
    }

    /**
     * 
     * @param upTo ...
     * @return ...
     */
    public static double nextDouble(double upTo)
    {
        return upTo * RAND.get().nextDouble();
    }

    /**
     * 
     * @return ...
     */
    public static long nextLong()
    {
        return RAND.get().nextLong();
    }

    /**
     * 
     * @param n ...
     * @return ...
     */
    public static int nextInt(int n)
    {
        return (n > 0)
                ?  RAND.get().nextInt( n)
                : (n < 0)
                  ? -RAND.get().nextInt(-n)
                  : 0;
    }

    /**
     * 
     * @return ...
     */
    public static int nextInt()
    {
        return RAND.get().nextInt();
    }

    /**
     * 
     * @return  ...
     */
    public static double nextGaussian()
    {
        return RAND.get().nextGaussian();
    }

    /**
     * 
     * @return ...
     */
    public static float nextFloat()
    {
        return RAND.get().nextFloat();
    }

    /**
     * 
     * @param bytes ..
     */
    public static void nextBytes(byte[] bytes)
    {
        RAND.get().nextBytes(bytes);
    }

    /**
     * 
     * @return  ...
     */
    public static boolean nextBoolean()
    {
        return RAND.get().nextBoolean();
    }
}
