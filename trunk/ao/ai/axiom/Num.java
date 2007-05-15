package ao.ai.axiom;

import ao.util.rand.Rand;

/**
 * Magnitude axioms.
 */
public class Num implements Comparable<Num>
{
    //--------------------------------------------------------------------
    private static final Num ZERO = new Num(0, 0);
    private static final Num ONE  = new Num(1, 0);
    private static final Num TWO  = new Num(2, 0);
    private static final Num I    = new Num(0, 1);
    private static final Num PI   = new Num(Math.PI, 0);
    private static final Num E    = new Num(Math.E, 0);


    //--------------------------------------------------------------------
    public static Num zero() { return ZERO; }
    public static Num one()  { return ONE;  }
    public static Num two()  { return TWO;  }
    public static Num i()    { return I;    }
    public static Num e()    { return E;    }
    public static Num pi()   { return PI;   }
    
    public static Num random()
    {
        return new Num(Rand.nextDouble(), Rand.nextDouble());
    }


    //--------------------------------------------------------------------
    private final double real;
    private final double imaginary;


    //--------------------------------------------------------------------
    public Num(double realPart)
    {
        this( realPart, 0 );
    }
    public Num(double realPart, double imaginaryPart)
    {
        real      = (Double.isInfinite(realPart) ||
                     Double.isNaN(realPart))          ? 0 : realPart;
        imaginary = (Double.isInfinite(imaginaryPart) ||
                     Double.isNaN(imaginaryPart))     ? 0 : imaginaryPart;
    }


    //--------------------------------------------------------------------
    public double abs()
    {
        return Math.sqrt(sumOfSquares());
    }
    public double real()
    {
        return real;
    }
    public double imaginary()
    {
        return imaginary;
    }

    // Angle in radians, measured counter-clockwise from the real axis.
    public double arg()
    {
        return Math.atan2(imaginary, real);
    }

    public double signedAbs()
    {
        return real >= 0
                ?  abs()
                : -abs();
    }

    private double sumOfSquares()
    {
        return real * real + imaginary * imaginary;
    }


    //--------------------------------------------------------------------
    public Num realPart()
    {
        return new Num(real, 0);
    }
    public Num imaginaryPart()
    {
        return new Num(0, imaginary);
    }
    public Num absoluteValue() // aka. magnitude, modulus
    {
        return new Num(abs(), 0);
    }

//    public Bool isInfinite()
//    {
//        return Bool.valueOf(Double.isInfinite(real) |
//                            Double.isInfinite(imaginary));
//    }
//    public Bool isNaN()
//    {
//        return Bool.valueOf(Double.isNaN(real) |
//                            Double.isNaN(imaginary));
//    }
    

    //--------------------------------------------------------------------
    public Num plus(Num addend)
    {
        return new Num(real + addend.real,
                       imaginary + addend.imaginary);
    }
    public Num negate()
    {
        return new Num(-real, -imaginary);
    }
    public Num minus(Num substractor)
    {
        return plus( substractor.negate() );
    }
    public Num times(Num multiplier)
    {
        double realPart =
                real * multiplier.real -
                imaginary * multiplier.imaginary;
        double imaginaryPart =
                imaginary * multiplier.real +
                real * multiplier.imaginary;
        return new Num(realPart, -imaginaryPart);
    }
    public Num reciprocal()
    {
        double sumOfSuqares = sumOfSquares();
        return new Num(real / sumOfSuqares,
                       imaginary / sumOfSuqares);
    }
    public Num divide(Num denominator)
    {
        return times( denominator.reciprocal() );
    }
    public Num distance(Num to)
    {
        return minus(to).absoluteValue();
    }
    public Num conjugate()
    {
        return new Num(real, -imaginary);
    }
    public Num argument()
    {
        return new Num(arg(), 0);
    }
    public Num sqrt()
    {
        double m = Math.sqrt(abs());
        double a = arg() / 2.0;
        return new Num(m * Math.cos(a),
                       m * Math.sin(a));
    }
    // e raised to the power of this complex.
    public Num exp()
    {
        double m = Math.exp(real);
        return new Num(m * Math.cos(imaginary),
                       m * Math.sin(imaginary));
    }
    // natural logarithm (base e) of this complex.
    public Num ln()
    {
        return new Num(Math.log(abs()),
                       arg());
    }
    // this complex raised to the specified power.
    public Num pow(Num that)
    {
        double r1 = Math.log(abs());
        double i1 = arg();
        double r2 = (r1 * that.real) - (i1 * that.imaginary);
        double i2 = (r1 * that.imaginary) + (i1 * that.real);
        double m = Math.exp(r2);
        return new Num(m * Math.cos(i2),
                       m * Math.sin(i2));
    }

    
    //--------------------------------------------------------------------
    public Num cosine()
    {
        return new Num( Math.cos(real) * Math.cosh(imaginary),
                       -Math.sin(real) * Math.sinh(imaginary));
    }
    public Num sine()
    {
        return new Num(Math.sin(real) * Math.cosh(imaginary),
                       Math.cos(real) * Math.sinh(imaginary));
    }

    // acos(x) = -i * ln(x +- i*sqrt(1 - x^2))
    public Num inverseCosinePlus()
    {
        return I.negate().times(plus(I.times(trigCommon())).ln());
    }
    public Num inverseCosineMinus()
    {
        return I.negate().times(minus(I.times(trigCommon())).ln());
    }
    // acos(x) = -i * ln(i*x +- sqrt(1 - x^2))
    public Num inverseSinePlus()
    {
        return I.negate().times(times(I).plus(trigCommon()).ln());
    }
    public Num inverseSineMinus()
    {
        return I.negate().times(times(I).minus(trigCommon()).ln());
    }
    public Num inverseTangent()
    {
        return ONE.divide( TWO ).times( I )
                .times( ONE.minus(times(I)).ln().minus(
                            ONE.plus(times(I)).ln()) );
    }
    private Num trigCommon()
    {
        return ONE.minus(pow(TWO)).sqrt();
    }


    //--------------------------------------------------------------------
    public Bool lessThan(Num compareTo)
    {
        return Bool.valueOf(compareTo(compareTo) < 0);
    }
    public Bool lessThanOrEqualTo(Num compareTo)
    {
        return Bool.valueOf(compareTo(compareTo) <= 0);
    }
    public Bool equalsTo(Num compareTo)
    {
        return Bool.valueOf(compareTo(compareTo) == 0);
    }
    public Bool greaterThanOrEuqalTo(Num compareTo)
    {
        return Bool.valueOf(compareTo(compareTo) >= 0);
    }
    public Bool greaterThan(Num compareTo)
    {
        return Bool.valueOf(compareTo(compareTo) > 0);
    }

    // the real components are compared first,
    //  then if equal, the imaginary components.
    public int compareTo(Num that)
    {
        if (this.real < that.real) return -1;
        if (this.real > that.real) return 1;
        long l1 = Double.doubleToLongBits(this.real);
        long l2 = Double.doubleToLongBits(that.real);
        if (l1 < l2) return -1;
        if (l2 > l1) return 1;
        if (this.imaginary < that.imaginary) return -1;
        if (this.imaginary > that.imaginary) return 1;
        l1 = Double.doubleToLongBits(this.imaginary);
        l2 = Double.doubleToLongBits(that.imaginary);
        if (l1 < l2) return -1;
        if (l2 > l1) return 1;
        return 0;
    }

    
    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return "[" + real + "," + imaginary + "]";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || !(o instanceof Num)) return false;

        Num num = (Num) o;

        return Double.compare(num.imaginary, imaginary) == 0 &&
               Double.compare(num.real, real) == 0;
    }

    @Override
    public int hashCode()
    {
        int result;
        long temp;
        temp = real != +0.0d ? Double.doubleToLongBits(real) : 0L;
        result = (int) (temp ^ (temp >>> 32));
        temp = imaginary != +0.0d ?
               Double.doubleToLongBits(imaginary) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
