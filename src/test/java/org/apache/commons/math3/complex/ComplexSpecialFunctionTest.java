package org.apache.commons.math3.complex;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 单元测试：Complex 类的超越函数与一元运算。
 * 覆盖 exp、log、sqrt、pow、conjugate、negate、reciprocal 等方法。
 */
@DisplayName("Complex 超越函数与一元运算测试")
class ComplexSpecialFunctionTest {

    private static final double EPS = 1e-9;

    // ======================== conjugate ========================

    @Test
    @DisplayName("conjugate: conj(3+4i)=(3-4i)")
    void conjugateBasic() {
        Complex r = new Complex(3, 4).conjugate();
        assertEquals(3.0,  r.getReal(),      EPS);
        assertEquals(-4.0, r.getImaginary(), EPS);
    }

    @Test
    @DisplayName("conjugate 两次还原: conj(conj(z))=z")
    void conjugateTwice() {
        Complex z = new Complex(-2.5, 7.3);
        Complex r = z.conjugate().conjugate();
        assertEquals(z.getReal(),      r.getReal(),      EPS);
        assertEquals(z.getImaginary(), r.getImaginary(), EPS);
    }

    @Test
    @DisplayName("conjugate of NaN is NaN")
    void conjugateNaN() {
        assertTrue(Complex.NaN.conjugate().isNaN());
    }

    // ======================== negate ========================

    @Test
    @DisplayName("negate: -(2+3i)=(-2-3i)")
    void negateBasic() {
        Complex r = new Complex(2, 3).negate();
        assertEquals(-2.0, r.getReal(),      EPS);
        assertEquals(-3.0, r.getImaginary(), EPS);
    }

    @Test
    @DisplayName("negate 两次还原: -(-z)=z")
    void negateTwice() {
        Complex z = new Complex(1.5, -4.2);
        Complex r = z.negate().negate();
        assertEquals(z.getReal(),      r.getReal(),      EPS);
        assertEquals(z.getImaginary(), r.getImaginary(), EPS);
    }

    // ======================== reciprocal ========================

    @Test
    @DisplayName("reciprocal: 1/(2+0i)=(0.5+0i)")
    void reciprocalReal() {
        Complex r = new Complex(2, 0).reciprocal();
        assertEquals(0.5, r.getReal(),      EPS);
        assertEquals(0.0, r.getImaginary(), EPS);
    }

    @Test
    @DisplayName("reciprocal: 1/i=-i")
    void reciprocalI() {
        Complex r = Complex.I.reciprocal();
        assertEquals(0.0,  r.getReal(),      EPS);
        assertEquals(-1.0, r.getImaginary(), EPS);
    }

    @Test
    @DisplayName("reciprocal of zero is INF")
    void reciprocalZero() {
        assertTrue(Complex.ZERO.reciprocal().isInfinite());
    }

    @Test
    @DisplayName("z × (1/z) ≈ 1")
    void reciprocalMultiply() {
        Complex z = new Complex(3, 4);
        Complex r = z.multiply(z.reciprocal());
        assertEquals(1.0, r.getReal(),      EPS);
        assertEquals(0.0, r.getImaginary(), EPS);
    }

    // ======================== exp ========================

    @Test
    @DisplayName("exp(0)=1")
    void expZero() {
        Complex r = Complex.ZERO.exp();
        assertEquals(1.0, r.getReal(),      EPS);
        assertEquals(0.0, r.getImaginary(), EPS);
    }

    @Test
    @DisplayName("exp(1+0i)=e")
    void expOne() {
        Complex r = Complex.ONE.exp();
        assertEquals(Math.E, r.getReal(), EPS);
        assertEquals(0.0,    r.getImaginary(), EPS);
    }

    @Test
    @DisplayName("exp(iπ)=-1 (欧拉公式)")
    void expEuler() {
        Complex r = new Complex(0, Math.PI).exp();
        assertEquals(-1.0, r.getReal(),      EPS);
        assertEquals(0.0,  r.getImaginary(), EPS);
    }

    @Test
    @DisplayName("exp(NaN)=NaN")
    void expNaN() {
        assertTrue(Complex.NaN.exp().isNaN());
    }

    // ======================== log ========================

    @Test
    @DisplayName("log(1)=0")
    void logOne() {
        Complex r = Complex.ONE.log();
        assertEquals(0.0, r.getReal(),      EPS);
        assertEquals(0.0, r.getImaginary(), EPS);
    }

    @Test
    @DisplayName("log(e)=(1+0i)")
    void logE() {
        Complex r = new Complex(Math.E, 0).log();
        assertEquals(1.0, r.getReal(),      EPS);
        assertEquals(0.0, r.getImaginary(), EPS);
    }

    @Test
    @DisplayName("log(-1)=iπ")
    void logMinusOne() {
        Complex r = new Complex(-1, 0).log();
        assertEquals(0.0,     r.getReal(),      EPS);
        assertEquals(Math.PI, r.getImaginary(), EPS);
    }

    @Test
    @DisplayName("exp(log(z))≈z")
    void logExpRoundTrip() {
        Complex z = new Complex(3, 4);
        Complex r = z.log().exp();
        assertEquals(z.getReal(),      r.getReal(),      EPS);
        assertEquals(z.getImaginary(), r.getImaginary(), EPS);
    }

    // ======================== sqrt ========================

    @Test
    @DisplayName("sqrt(4+0i)=(2+0i)")
    void sqrtReal() {
        Complex r = new Complex(4, 0).sqrt();
        assertEquals(2.0, r.getReal(),      EPS);
        assertEquals(0.0, r.getImaginary(), EPS);
    }

    @Test
    @DisplayName("sqrt(-1)=i")
    void sqrtMinusOne() {
        Complex r = new Complex(-1, 0).sqrt();
        assertEquals(0.0, r.getReal(),      EPS);
        assertEquals(1.0, r.getImaginary(), EPS);
    }

    @Test
    @DisplayName("sqrt(0)=0")
    void sqrtZero() {
        Complex r = Complex.ZERO.sqrt();
        assertEquals(0.0, r.getReal(),      EPS);
        assertEquals(0.0, r.getImaginary(), EPS);
    }

    @Test
    @DisplayName("sqrt(3+4i)²≈(3+4i)")
    void sqrtSquareRoundTrip() {
        Complex z = new Complex(3, 4);
        Complex s = z.sqrt();
        Complex r = s.multiply(s);
        assertEquals(z.getReal(),      r.getReal(),      EPS);
        assertEquals(z.getImaginary(), r.getImaginary(), EPS);
    }

    // ======================== pow ========================

    @Test
    @DisplayName("z^0=1")
    void powZero() {
        Complex r = new Complex(5, 7).pow(0.0);
        assertEquals(1.0, r.getReal(),      EPS);
        assertEquals(0.0, r.getImaginary(), EPS);
    }

    @Test
    @DisplayName("z^1=z")
    void powOne() {
        Complex z = new Complex(2, 3);
        Complex r = z.pow(1.0);
        assertEquals(z.getReal(),      r.getReal(),      EPS);
        assertEquals(z.getImaginary(), r.getImaginary(), EPS);
    }

    @Test
    @DisplayName("(3+4i)^2=(-7+24i)")
    void powSquare() {
        Complex z = new Complex(3, 4);
        Complex r = z.pow(2.0);
        assertEquals(-7.0,  r.getReal(),      EPS);
        assertEquals(24.0, r.getImaginary(), EPS);
    }

    @Test
    @DisplayName("i^i = e^(-π/2)")
    void powComplexExponent() {
        // i^i = e^(i·ln(i)) = e^(i·(iπ/2)) = e^(-π/2)
        Complex r = Complex.I.pow(Complex.I);
        assertEquals(Math.exp(-Math.PI / 2), r.getReal(), EPS);
        assertEquals(0.0, r.getImaginary(), EPS);
    }

    // ======================== getArgument ========================

    @Test
    @DisplayName("arg(1+0i)=0")
    void argPositiveReal() {
        assertEquals(0.0, Complex.ONE.getArgument(), EPS);
    }

    @Test
    @DisplayName("arg(0+1i)=π/2")
    void argI() {
        assertEquals(Math.PI / 2, Complex.I.getArgument(), EPS);
    }

    @Test
    @DisplayName("arg(-1+0i)=π")
    void argNegativeReal() {
        assertEquals(Math.PI, new Complex(-1, 0).getArgument(), EPS);
    }
}
