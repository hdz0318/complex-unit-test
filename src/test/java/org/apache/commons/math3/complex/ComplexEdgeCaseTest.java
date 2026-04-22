package org.apache.commons.math3.complex;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 单元测试：Complex 类的边界条件与对象方法。
 * 覆盖 NaN / Infinity 传播、equals、hashCode、toString、abs 等。
 */
@DisplayName("Complex 边界条件与对象方法测试")
class ComplexEdgeCaseTest {

    private static final double EPS = 1e-10;

    // ======================== 构造与访问 ========================

    @Nested
    @DisplayName("构造器与访问器")
    class ConstructorTests {

        @Test
        @DisplayName("双参构造: Complex(3,4)")
        void twoArgConstructor() {
            Complex c = new Complex(3.0, 4.0);
            assertEquals(3.0, c.getReal(),      EPS);
            assertEquals(4.0, c.getImaginary(), EPS);
        }

        @Test
        @DisplayName("单参构造: Complex(5) 虚部为0")
        void oneArgConstructor() {
            Complex c = new Complex(5.0);
            assertEquals(5.0, c.getReal(),      EPS);
            assertEquals(0.0, c.getImaginary(), EPS);
        }

        @Test
        @DisplayName("静态常量 ZERO")
        void constantZero() {
            assertEquals(0.0, Complex.ZERO.getReal(),      EPS);
            assertEquals(0.0, Complex.ZERO.getImaginary(), EPS);
        }

        @Test
        @DisplayName("静态常量 ONE")
        void constantOne() {
            assertEquals(1.0, Complex.ONE.getReal(),      EPS);
            assertEquals(0.0, Complex.ONE.getImaginary(), EPS);
        }

        @Test
        @DisplayName("静态常量 I")
        void constantI() {
            assertEquals(0.0, Complex.I.getReal(),      EPS);
            assertEquals(1.0, Complex.I.getImaginary(), EPS);
        }
    }

    // ======================== NaN ========================

    @Nested
    @DisplayName("NaN 行为")
    class NaNTests {

        @Test
        @DisplayName("NaN(real) 构造")
        void nanReal() {
            Complex c = new Complex(Double.NaN, 0);
            assertTrue(c.isNaN());
            assertFalse(c.isInfinite());
        }

        @Test
        @DisplayName("NaN(imag) 构造")
        void nanImag() {
            Complex c = new Complex(0, Double.NaN);
            assertTrue(c.isNaN());
        }

        @Test
        @DisplayName("NaN 加法传播")
        void nanAddPropagation() {
            assertTrue(Complex.NaN.add(Complex.ONE).isNaN());
            assertTrue(Complex.ONE.add(Complex.NaN).isNaN());
        }

        @Test
        @DisplayName("NaN 乘法传播")
        void nanMultiplyPropagation() {
            assertTrue(Complex.NaN.multiply(Complex.ONE).isNaN());
        }

        @Test
        @DisplayName("NaN 除法传播")
        void nanDividePropagation() {
            assertTrue(Complex.NaN.divide(Complex.ONE).isNaN());
        }
    }

    // ======================== Infinity ========================

    @Nested
    @DisplayName("Infinity 行为")
    class InfinityTests {

        @Test
        @DisplayName("Infinity 构造")
        void infiniteConstruction() {
            Complex c = new Complex(Double.POSITIVE_INFINITY, 0);
            assertTrue(c.isInfinite());
            assertFalse(c.isNaN());
        }

        @Test
        @DisplayName("NaN 优先于 Infinity")
        void nanOverridesInfinite() {
            Complex c = new Complex(Double.POSITIVE_INFINITY, Double.NaN);
            assertTrue(c.isNaN());
            assertFalse(c.isInfinite());
        }

        @Test
        @DisplayName("Infinite × finite = INF")
        void infiniteMultiply() {
            assertTrue(Complex.INF.multiply(Complex.ONE).isInfinite());
        }

        @Test
        @DisplayName("finite / INF = ZERO")
        void finiteDivideInfinite() {
            Complex r = Complex.ONE.divide(Complex.INF);
            assertEquals(0.0, r.getReal(),      EPS);
            assertEquals(0.0, r.getImaginary(), EPS);
        }
    }

    // ======================== abs ========================

    @Nested
    @DisplayName("abs() 测试")
    class AbsTests {

        @Test
        @DisplayName("|3+4i|=5")
        void abs345() {
            assertEquals(5.0, new Complex(3, 4).abs(), EPS);
        }

        @Test
        @DisplayName("|0|=0")
        void absZero() {
            assertEquals(0.0, Complex.ZERO.abs(), EPS);
        }

        @Test
        @DisplayName("|-3-4i|=5")
        void absNegative() {
            assertEquals(5.0, new Complex(-3, -4).abs(), EPS);
        }

        @Test
        @DisplayName("|NaN|=NaN")
        void absNaN() {
            assertTrue(Double.isNaN(Complex.NaN.abs()));
        }

        @Test
        @DisplayName("|INF|=+∞")
        void absInfinite() {
            assertEquals(Double.POSITIVE_INFINITY, Complex.INF.abs());
        }
    }

    // ======================== equals / hashCode ========================

    @Nested
    @DisplayName("equals() 与 hashCode()")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("相同值相等")
        void equalsSameValue() {
            assertEquals(new Complex(1, 2), new Complex(1, 2));
        }

        @Test
        @DisplayName("自反性: z.equals(z)")
        void equalsReflexive() {
            Complex z = new Complex(3, 4);
            assertEquals(z, z);
        }

        @Test
        @DisplayName("对称性")
        void equalsSymmetric() {
            Complex a = new Complex(1, 2);
            Complex b = new Complex(1, 2);
            assertEquals(a, b);
            assertEquals(b, a);
        }

        @Test
        @DisplayName("不同值不等: (1,2)≠(1,3)")
        void notEqualsDifferentImag() {
            assertNotEquals(new Complex(1, 2), new Complex(1, 3));
        }

        @Test
        @DisplayName("不同值不等: (1,2)≠(3,2)")
        void notEqualsDifferentReal() {
            assertNotEquals(new Complex(1, 2), new Complex(3, 2));
        }

        @Test
        @DisplayName("与 null 不等")
        void notEqualsNull() {
            assertNotEquals(null, new Complex(1, 2));
        }

        @Test
        @DisplayName("与其他类型不等")
        void notEqualsDifferentType() {
            assertNotEquals("(1, 2)", new Complex(1, 2));
        }

        @Test
        @DisplayName("NaN equals NaN")
        void nanEqualsNan() {
            assertEquals(Complex.NaN, new Complex(Double.NaN, 0));
        }

        @Test
        @DisplayName("hashCode 一致性: equal objects → equal hashCode")
        void hashCodeConsistency() {
            Complex a = new Complex(5, -3);
            Complex b = new Complex(5, -3);
            assertEquals(a.hashCode(), b.hashCode());
        }
    }

    // ======================== toString ========================

    @Nested
    @DisplayName("toString()")
    class ToStringTests {

        @Test
        @DisplayName("格式: (real, imaginary)")
        void toStringFormat() {
            assertEquals("(3.0, 4.0)", new Complex(3, 4).toString());
        }

        @Test
        @DisplayName("零: (0.0, 0.0)")
        void toStringZero() {
            assertEquals("(0.0, 0.0)", Complex.ZERO.toString());
        }

        @Test
        @DisplayName("负数: (-1.0, -2.0)")
        void toStringNegative() {
            assertEquals("(-1.0, -2.0)", new Complex(-1, -2).toString());
        }
    }
}
