package ao.ai.evo.promoter.affinity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
*/
public class GpAffinity implements Affinity
{
    //--------------------------------------------------------------------
    private final Class clazz;


    //--------------------------------------------------------------------
    public GpAffinity(Class type)
    {
        // The reason we use Object.class to represent a void
        //  return type, instead of Void.TYPE is that in the following
        //  four possible cases:
        //  1) we want an Object but get Void
        //  2) we want an Object and get an Object
        //  3) we want a Void but get an Object
        //  4) we want a Void but and get a Void
        //  (2) contains (1), and (3) and (4) are equivilant.
        // Therefore, in all cases substituting a Void with an Object
        //  would result in valid code.  An added benefit that we get
        //  by doing this substitution is that if we return Void
        //  for void values, rather than Object, we miss out on (3).
        clazz = (type == null || type == Void.TYPE)
                ? Object.class : wrapType(type);
    }


    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public boolean isAffine(Affinity with)
    {
        return clazz.isAssignableFrom( ((GpAffinity) with).clazz );
    }


    //--------------------------------------------------------------------
    public Collection<Affinity> affineSet()
    {
        List<Affinity> hierarchy = new ArrayList<Affinity>();
        hierarchy.add( this );

        Class superclass = clazz.getSuperclass();
        while (superclass != null)
        {
            hierarchy.add(
                    new GpAffinity(superclass));

            superclass = superclass.getSuperclass();
        }

        return hierarchy;
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return clazz.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || !(o instanceof GpAffinity)) return false;

        GpAffinity that = (GpAffinity) o;
        return clazz.equals(that.clazz);
    }

    @Override
    public int hashCode()
    {
        return clazz.hashCode();
    }


    //--------------------------------------------------------------------
    private static Class wrapType(Class clazz)
    {
        return (clazz.equals( Boolean.TYPE   )) ? Boolean.class
             : (clazz.equals( Character.TYPE )) ? Character.class
             : (clazz.equals( Byte.TYPE      )) ? Byte.class
             : (clazz.equals( Short.TYPE     )) ? Short.class
             : (clazz.equals( Integer.TYPE   )) ? Integer.class
             : (clazz.equals( Long.TYPE      )) ? Long.class
             : (clazz.equals( Float.TYPE     )) ? Float.class
             : (clazz.equals( Double.TYPE    )) ? Double.class
             : clazz;
    }
}
