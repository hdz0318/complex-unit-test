/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math3.complex;

import java.io.Serializable;

/**
 * Representation of a complex number, i.e. a number which has both a real
 * and imaginary part.
 * <p>
 * Based on {@code org.apache.commons.math3.complex.Complex} from
 * Apache Commons Math 3.6.1. This is a standalone version retaining
 * the core arithmetic, transcendental, and comparison functionality.
 * </p>
 * <p>
 * Implementations of arithmetic operations handle {@code NaN} and
 * infinite values according to the rules for {@link Double} arithmetic,
 * applying the basic real-number rules to the respective real and
 * imaginary parts.
 * </p>
 *
 * @see <a href="https://commons.apache.org/proper/commons-math/">
 *      Apache Commons Math</a>
 */
public class Complex implements Serializable {

    private static final long serialVersionUID = -6195664516687396620L;

    /** The square root of -1, i.e. the imaginary unit {@code i}. */
    public static final Complex I = new Complex(0.0, 1.0);

    /** A complex number representing {@code NaN + NaN·i}. */
    public static final Complex NaN = new Complex(Double.NaN, Double.NaN);

    /** A complex number representing {@code +INF + INF·i}. */
    public static final Complex INF =
            new Complex(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

    /** A complex number representing zero. */
    public static final Complex ZERO = new Complex(0.0, 0.0);

    /** A complex number representing one. */
    public static final Complex ONE = new Complex(1.0, 0.0);

    /** The real part. */
    private final double real;

    /** The imaginary part. */
    private final double imaginary;

    /** Record whether this complex number is NaN. */
    private final transient boolean isNaN;

    /** Record whether this complex number is infinite. */
    private final transient boolean isInfinite;

    /**
     * Create a complex number given only the real part.
     *
     * @param real Real part.
     */
    public Complex(double real) {
        this(real, 0.0);
    }

    /**
     * Create a complex number given the real and imaginary parts.
     *
     * @param real      Real part.
     * @param imaginary Imaginary part.
     */
    public Complex(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;

        isNaN = Double.isNaN(real) || Double.isNaN(imaginary);
        isInfinite = !isNaN &&
                (Double.isInfinite(real) || Double.isInfinite(imaginary));
    }

    // ----------------------------------------------------------------
    //  Accessors
    // ----------------------------------------------------------------

    /**
     * Access the real part.
     *
     * @return the real part.
     */
    public double getReal() {
        return real;
    }

    /**
     * Access the imaginary part.
     *
     * @return the imaginary part.
     */
    public double getImaginary() {
        return imaginary;
    }

    /**
     * Checks whether either the real or imaginary part of this complex
     * number is {@code NaN}.
     *
     * @return true if either part is NaN.
     */
    public boolean isNaN() {
        return isNaN;
    }

    /**
     * Checks whether either the real or imaginary part of this complex
     * number takes an infinite value while the other part is not NaN.
     *
     * @return true if either part is infinite and neither part is NaN.
     */
    public boolean isInfinite() {
        return isInfinite;
    }

    // ----------------------------------------------------------------
    //  Modulus & Argument
    // ----------------------------------------------------------------

    /**
     * Return the absolute value (modulus) of this complex number.
     * <p>
     * Returns {@code sqrt(real² + imaginary²)}, using
     * {@link Math#hypot(double, double)} to avoid intermediate overflow
     * or underflow.
     * </p>
     *
     * @return the absolute value.
     */
    public double abs() {
        if (isNaN) {
            return Double.NaN;
        }
        if (isInfinite) {
            return Double.POSITIVE_INFINITY;
        }
        return Math.hypot(real, imaginary);
    }

    /**
     * Compute the argument of this complex number.
     *
     * @return the argument (phase angle) in radians, range (-π, π].
     */
    public double getArgument() {
        return Math.atan2(imaginary, real);
    }

    // ----------------------------------------------------------------
    //  Arithmetic operations
    // ----------------------------------------------------------------

    /**
     * Returns a {@code Complex} whose value is {@code (this + addend)}.
     *
     * @param addend Value to be added to this complex number.
     * @return {@code this + addend}.
     * @throws NullPointerException if {@code addend} is null.
     */
    public Complex add(Complex addend) {
        if (addend == null) {
            throw new NullPointerException("addend must not be null");
        }
        if (isNaN || addend.isNaN) {
            return NaN;
        }
        return new Complex(real + addend.real,
                           imaginary + addend.imaginary);
    }

    /**
     * Returns a {@code Complex} whose value is {@code (this + addend)},
     * with {@code addend} interpreted as a real number.
     *
     * @param addend Value to be added to this complex number.
     * @return {@code this + addend}.
     */
    public Complex add(double addend) {
        if (isNaN || Double.isNaN(addend)) {
            return NaN;
        }
        return new Complex(real + addend, imaginary);
    }

    /**
     * Returns a {@code Complex} whose value is
     * {@code (this - subtrahend)}.
     *
     * @param subtrahend Value to be subtracted from this complex number.
     * @return {@code this - subtrahend}.
     * @throws NullPointerException if {@code subtrahend} is null.
     */
    public Complex subtract(Complex subtrahend) {
        if (subtrahend == null) {
            throw new NullPointerException("subtrahend must not be null");
        }
        if (isNaN || subtrahend.isNaN) {
            return NaN;
        }
        return new Complex(real - subtrahend.real,
                           imaginary - subtrahend.imaginary);
    }

    /**
     * Returns a {@code Complex} whose value is
     * {@code (this × factor)}.
     * <p>
     * The multiplication formula for complex numbers is:
     * <pre>
     *   (a + b·i)(c + d·i) = (ac − bd) + (ad + bc)·i
     * </pre>
     * </p>
     *
     * @param factor Value to be multiplied by this complex number.
     * @return {@code this × factor}.
     * @throws NullPointerException if {@code factor} is null.
     */
    public Complex multiply(Complex factor) {
        if (factor == null) {
            throw new NullPointerException("factor must not be null");
        }
        if (isNaN || factor.isNaN) {
            return NaN;
        }
        if (Double.isInfinite(real) || Double.isInfinite(imaginary) ||
            Double.isInfinite(factor.real) || Double.isInfinite(factor.imaginary)) {
            return INF;
        }
        return new Complex(
                real * factor.real - imaginary * factor.imaginary,
                real * factor.imaginary + imaginary * factor.real);
    }

    /**
     * Returns a {@code Complex} whose value is {@code this × factor},
     * with {@code factor} interpreted as a real number.
     *
     * @param factor Value to be multiplied by this complex number.
     * @return {@code this × factor}.
     */
    public Complex multiply(double factor) {
        if (isNaN || Double.isNaN(factor)) {
            return NaN;
        }
        if (Double.isInfinite(real) || Double.isInfinite(imaginary) ||
            Double.isInfinite(factor)) {
            return INF;
        }
        return new Complex(real * factor, imaginary * factor);
    }

    /**
     * Returns a {@code Complex} whose value is
     * {@code (this / divisor)}.
     * <p>
     * The division formula is:
     * <pre>
     *   (a + bi) / (c + di) = ((ac + bd) + (bc − ad)·i) / (c² + d²)
     * </pre>
     * </p>
     *
     * @param divisor Value by which this complex number is to be divided.
     * @return {@code this / divisor}.
     * @throws NullPointerException if {@code divisor} is null.
     */
    public Complex divide(Complex divisor) {
        if (divisor == null) {
            throw new NullPointerException("divisor must not be null");
        }
        if (isNaN || divisor.isNaN) {
            return NaN;
        }
        double c = divisor.real;
        double d = divisor.imaginary;
        if (c == 0.0 && d == 0.0) {
            return NaN;
        }
        if (divisor.isInfinite && !isInfinite) {
            return ZERO;
        }
        double denominator = c * c + d * d;
        return new Complex(
                (real * c + imaginary * d) / denominator,
                (imaginary * c - real * d) / denominator);
    }

    /**
     * Returns a {@code Complex} whose value is {@code (this / divisor)},
     * with {@code divisor} interpreted as a real number.
     *
     * @param divisor Value by which this complex number is to be divided.
     * @return {@code this / divisor}.
     */
    public Complex divide(double divisor) {
        if (isNaN || Double.isNaN(divisor)) {
            return NaN;
        }
        if (divisor == 0.0) {
            return NaN;
        }
        if (Double.isInfinite(divisor)) {
            return isInfinite ? NaN : ZERO;
        }
        return new Complex(real / divisor, imaginary / divisor);
    }

    // ----------------------------------------------------------------
    //  Unary operations
    // ----------------------------------------------------------------

    /**
     * Returns the conjugate of this complex number.
     * The conjugate of {@code a + b·i} is {@code a − b·i}.
     *
     * @return the conjugate.
     */
    public Complex conjugate() {
        if (isNaN) {
            return NaN;
        }
        return new Complex(real, -imaginary);
    }

    /**
     * Returns the negation of this complex number.
     * The negation of {@code a + b·i} is {@code −a − b·i}.
     *
     * @return the negation.
     */
    public Complex negate() {
        if (isNaN) {
            return NaN;
        }
        return new Complex(-real, -imaginary);
    }

    /**
     * Returns the multiplicative inverse ({@code 1 / this}).
     *
     * @return the reciprocal.
     */
    public Complex reciprocal() {
        if (isNaN) {
            return NaN;
        }
        if (real == 0.0 && imaginary == 0.0) {
            return INF;
        }
        if (isInfinite) {
            return ZERO;
        }
        double denom = real * real + imaginary * imaginary;
        return new Complex(real / denom, -imaginary / denom);
    }

    // ----------------------------------------------------------------
    //  Transcendental functions
    // ----------------------------------------------------------------

    /**
     * Compute the
     * <a href="http://mathworld.wolfram.com/ExponentialFunction.html">
     * exponential function</a> of this complex number.
     * <p>
     * Implements the formula:
     * <pre>
     *   exp(a + b·i) = exp(a) · (cos(b) + sin(b)·i)
     * </pre>
     * </p>
     *
     * @return <code>e<sup>this</sup></code>.
     */
    public Complex exp() {
        if (isNaN) {
            return NaN;
        }
        double expReal = Math.exp(real);
        return new Complex(expReal * Math.cos(imaginary),
                           expReal * Math.sin(imaginary));
    }

    /**
     * Compute the
     * <a href="http://mathworld.wolfram.com/NaturalLogarithm.html">
     * natural logarithm</a> of this complex number.
     * <p>
     * Implements the formula:
     * <pre>
     *   log(a + b·i) = ln|a + b·i| + arg(a + b·i)·i
     * </pre>
     * </p>
     *
     * @return ln(this).
     */
    public Complex log() {
        if (isNaN) {
            return NaN;
        }
        return new Complex(Math.log(abs()), Math.atan2(imaginary, real));
    }

    /**
     * Compute the
     * <a href="http://mathworld.wolfram.com/SquareRoot.html">
     * square root</a> of this complex number.
     *
     * @return the square root of this complex number.
     */
    public Complex sqrt() {
        if (isNaN) {
            return NaN;
        }
        if (real == 0.0 && imaginary == 0.0) {
            return ZERO;
        }
        double absVal = abs();
        double t = Math.sqrt((Math.abs(real) + absVal) / 2.0);
        if (real >= 0.0) {
            return new Complex(t, imaginary / (2.0 * t));
        } else {
            return new Complex(
                    Math.abs(imaginary) / (2.0 * t),
                    Math.copySign(1.0, imaginary) * t);
        }
    }

    /**
     * Returns of value of this complex number raised to the power of
     * {@code x}.
     *
     * @param x exponent to which this complex number is to be raised.
     * @return <code>this<sup>x</sup></code>.
     */
    public Complex pow(double x) {
        return this.log().multiply(x).exp();
    }

    /**
     * Returns of value of this complex number raised to the power of
     * {@code x}, where {@code x} is a complex number.
     *
     * @param x exponent to which this complex number is to be raised.
     * @return <code>this<sup>x</sup></code>.
     * @throws NullPointerException if x is null.
     */
    public Complex pow(Complex x) {
        if (x == null) {
            throw new NullPointerException("x must not be null");
        }
        return this.log().multiply(x).exp();
    }

    // ----------------------------------------------------------------
    //  Object overrides
    // ----------------------------------------------------------------

    /**
     * Test for equality with another object.  If both the real and
     * imaginary parts of two complex numbers are exactly the same,
     * they are considered equal.  {@code NaN} values are considered
     * equal to themselves.
     *
     * @param other Object to test for equality with this instance.
     * @return {@code true} if the objects are equal, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Complex)) {
            return false;
        }
        Complex c = (Complex) other;
        if (c.isNaN) {
            return isNaN;
        }
        return Double.compare(real, c.real) == 0 &&
               Double.compare(imaginary, c.imaginary) == 0;
    }

    /**
     * Get a hashCode for the complex number.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        if (isNaN) {
            return 7;
        }
        long realBits = Double.doubleToLongBits(real);
        long imagBits = Double.doubleToLongBits(imaginary);
        return 31 * (int) (realBits ^ (realBits >>> 32)) +
               (int) (imagBits ^ (imagBits >>> 32));
    }

    /**
     * Returns a string representation of this complex number in the
     * form {@code (real, imaginary)}.
     *
     * @return a string representation.
     */
    @Override
    public String toString() {
        return "(" + real + ", " + imaginary + ")";
    }
}
