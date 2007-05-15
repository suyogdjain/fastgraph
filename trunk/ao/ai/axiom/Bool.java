package ao.ai.axiom;

import ao.ai.evo.product.Product;

/**
 * true/false
 */
public class Bool
{
    //--------------------------------------------------------------------
    private static final Bool TRUE  = new Bool(true);
    private static final Bool FALSE = new Bool(false);


    //--------------------------------------------------------------------
    public static Bool trueVal()  { return TRUE;  }
    public static Bool falseVal() { return FALSE; }

    public static Bool valueOf( boolean value )
    {
        return (value) ? TRUE : FALSE;
    }


    //--------------------------------------------------------------------
    private final boolean val;


    //--------------------------------------------------------------------
    private Bool(boolean value)
    {
        val = value;
    }


    //--------------------------------------------------------------------
    public boolean value()
    {
        return val;
    }


    //--------------------------------------------------------------------
    public Bool and(Product<Bool> x)
    {
        return valueOf(val && x.build().val);
    }
    public Bool or(Product<Bool> x)
    {
        return valueOf(val || x.build().val);
    }
    public Bool not()
    {
        return valueOf(!val);
    }
    public Bool xor(Bool x)
    {
        return valueOf(val ^ x.val);
    }
    public Bool xnor(Bool x)
    {
        return valueOf(val == x.val);
    }
    public Bool implies(Product<Bool> x)
    {
        return valueOf(!val || x.build().val);
    }
    public Bool nand(Product<Bool> x)
    {
        return valueOf(!(val && x.build().val));
    }
    public Bool nor(Product<Bool> x)
    {
        return valueOf(!(val || x.build().val));
    }
    public Bool majority(Bool x, Product<Bool> y)
    {
        int trueCount = (  val ? 1 : 0) +
                        (x.val ? 1 : 0);
        if (trueCount == 2) return Bool.TRUE;
        if (trueCount == 0) return Bool.FALSE;
        return y.build();
    }


    //--------------------------------------------------------------------
    public <T> T ternary(Product<T> ifTrue, Product<T> ifFalse)
    {
        return val ? ifTrue.build() : ifFalse.build();
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return String.valueOf(val);
    }

    @Override
    public boolean equals(Object o)
    {
        return this == o;
    }

    @Override
    public int hashCode()
    {
        return (val ? 1 : 0);
    }
}
