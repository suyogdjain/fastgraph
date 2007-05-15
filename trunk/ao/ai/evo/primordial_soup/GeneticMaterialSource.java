package ao.ai.evo.primordial_soup;

import ao.ai.evo.coding.invoke.Message;
import ao.ai.evo.coding.invoke.MethodMessage;
import ao.ai.evo.genetic_material.LiteralGeneticMaterial;
import ao.ai.evo.product.Product;
import ao.ai.evo.promoter.GpPromoter;
import ao.ai.evo.promoter.Promoter;
import ao.ai.evo.promoter.affinity.Affinity;
import ao.ai.evo.promoter.affinity.AffinityDomain;
import ao.ai.evo.promoter.affinity.GpAffinity;

import java.lang.reflect.*;
import java.util.*;


/**
 *
 */
public class GeneticMaterialSource
{
    //--------------------------------------------------------------------
    private static final Set<String> ignoreMethods =
            new HashSet<String>(Arrays.asList(
                    "hashCode", "equals", "clone", "toString",
                    "notify", "notifyAll", "wait", "finalize",
                    "compareTo"));


    //--------------------------------------------------------------------
    public GeneticMaterialSource() {}
//    public ReflectiveGenePool(Object deriveFrom)
//    {
//        if (deriveFrom instanceof Class)
//        {
//            addMethods((Class) deriveFrom);
//        }
//        else
//        {
//            addIo(deriveFrom);
//            addMethods(deriveFrom.getClass());
//        }
//    }


    //--------------------------------------------------------------------
//    public <T> IoGeneticMaterial<T> addIo(T value)
//    {
//        IoGeneticMaterial<T> ioMaterial =
//                new IoGeneticMaterial<T>(value);
//        return ioMaterial;
//    }


    //--------------------------------------------------------------------
    public GenePool addMethods(Class clazz, AffinityDomain domain)
    {
        GenePool methods = new GenePool();

        for (Method method : clazz.getMethods())
        {
            if (! isAcceptable(method)) continue;
            addGenericMethod(methods, method, domain);
        }

        return methods;
    }

    private void addGenericMethod(
            GenePool       to,
            Method         method,
            AffinityDomain domain)
    {
        Message msg    = new MethodMessage(method);
        Class   target = method.getDeclaringClass();

        Map<TypeVariable, List<Integer>> byTypeVar = byTypeVars(method);
        assert byTypeVar.size() <= 1
                : "multiple type vars unsupported";

        Affinity       ret  = new GpAffinity( method.getReturnType() );
        List<Affinity> args = new ArrayList<Affinity>();
        for (Type arg : method.getGenericParameterTypes())
        {
            if (! isGeneric(arg))
            {
                args.add( new GpAffinity((Class) arg) );
            }
            else if (isProduct(arg))
            {
                Type typeArg =
                        ((ParameterizedType) arg)
                                .getActualTypeArguments()[0];
                if (typeArg instanceof TypeVariable)
                {
                    args.add( new GpAffinity(Object.class) );
                }
                else
                {
                    args.add( new GpAffinity((Class) typeArg) );
                }
            }
            else
            {
                args.add( new GpAffinity(Object.class) );
            }
        }

        if (byTypeVar.isEmpty())
        {
            addSpecificMethod(to, msg, target, ret, args);
            return;
        }
        for (TypeVariable type : byTypeVar.keySet())
        {
            assert type.getBounds().length == 1
                    : "multiple bounds unsupported";
            Class upperBound = (Class) type.getBounds()[0];

            Collection<Affinity> subTypes =
                    domain.tighten(new GpAffinity(upperBound));
            for (Affinity subType : subTypes)
            {
                for (Integer position : byTypeVar.get(type))
                {
                    if (position == -1)
                    {
                        ret = subType;
                    }
                    else
                    {
                        args.set(position, subType);
                    }
                }

                addSpecificMethod(
                        to, msg, target, ret, args);
            }
        }
    }

    private void addSpecificMethod(
            GenePool       to,
            Message        message,
            Class          target,
            Affinity       ret,
            List<Affinity> args)
    {
        List<Affinity> fullArgs = new ArrayList<Affinity>(args);
        if (! message.isStatic())
        {
            fullArgs.add(0, new GpAffinity(target));
        }

        Promoter promoter =
                new GpPromoter( ret,
                                fullArgs.toArray(new Affinity[0]));
        to.add( new LiteralGeneticMaterial(
                        message.asCoding(),
                        promoter) );
    }


    private Map<TypeVariable, List<Integer>> byTypeVars(Method method)
    {
        Map<TypeVariable, List<Integer>> byTypeVar =
                new HashMap<TypeVariable, List<Integer>>();

        if (method.getGenericReturnType() instanceof TypeVariable)
        {
            positionsOfType(
                    byTypeVar,
                    (TypeVariable) method.getGenericReturnType()
            ).add( -1 );
        }

        Type params[] = method.getGenericParameterTypes();
        for (int i = 0; i < params.length; i++)
        {
            Type param = params[i];
            if (param instanceof TypeVariable)
            {
                positionsOfType(
                        byTypeVar,
                        (TypeVariable) param
                ).add( i );
            }
            else if (isProduct(param))
            {
                Type typeArg =
                        ((ParameterizedType) param)
                                .getActualTypeArguments()[0];
                if (typeArg instanceof TypeVariable)
                {
                    positionsOfType(
                            byTypeVar,
                            (TypeVariable) typeArg
                    ).add( i );
                }
            }
        }

        return byTypeVar;
    }


    //--------------------------------------------------------------------
    private List<Integer> positionsOfType(
            Map<TypeVariable, List<Integer>> byType,
            TypeVariable                     type)
    {
        List<Integer> positions = byType.get(type);
        if (positions == null)
        {
            positions = new ArrayList<Integer>();
            byType.put(type, positions);
        }
        return positions;
    }


    //--------------------------------------------------------------------
    private boolean isAcceptable(Method method)
    {
        if (ignoreMethods.contains( method.getName() )) return false;
        if (! isAcceptable(method.getGenericReturnType())) return false;

        for (Type param : method.getGenericParameterTypes())
        {
            if (! isAcceptable(param)) return false;
        }
        return true;
    }

    private boolean isAcceptable(Type type)
    {
        return !isGeneric(type) ||
               type instanceof TypeVariable ||
               isProduct(type);
    }

    private boolean isGeneric(Type type)
    {
        return type instanceof GenericArrayType ||
               type instanceof ParameterizedType ||
               type instanceof TypeVariable ||
               type instanceof WildcardType;
    }

    @SuppressWarnings("unchecked")
    private boolean isProduct(Type type)
    {
        return type instanceof ParameterizedType &&
                ((Class) ((ParameterizedType) type).getRawType())
                        .isAssignableFrom(Product.class);
    }
}
