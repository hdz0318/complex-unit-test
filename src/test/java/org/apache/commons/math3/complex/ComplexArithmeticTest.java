package org.apache.commons.math3.complex;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 单元测试：Complex 类的四则运算。
 * 其中 multiply 测试覆盖了注入的缺陷（虚部符号错误）。
 */
@DisplayName("Complex 四则运算测试")
class ComplexArithmeticTest {

    private static final double EPS = 1e-10;

    // ======================== add ========================

    @Nested
    @DisplayName("add() 加法测试")
    class AddTests {

        @Test
        @DisplayName("基本加法: (1+2i)+(3+4i)=(4+6i)")
        void basicAdd() {
            Complex r = new Complex(1, 2).add(new Complex(3, 4));
            assertEquals(4.0, r.getReal(),      EPS);
            assertEquals(6.0, r.getImaginary(), EPS);
        }

        @Test
        @DisplayName("加零: z+0=z")
        void addZero() {
            Complex z = new Complex(5, -3);
            Complex r = z.add(Complex.ZERO);
            assertEquals(z.getReal(),      r.getReal(),      EPS);
            assertEquals(z.getImaginary(), r.getImaginary(), EPS);
        }

        @Test
        @DisplayName("加相反数: z+(-z)=0")
        void addNegate() {
            Complex z = new Complex(2.5, -1.7);
            Complex r = z.add(z.negate());
            assertEquals(0.0, r.getReal(),      EPS);
            assertEquals(0.0, r.getImaginary(), EPS);
        }

        @Test
        @DisplayName("交换律: a+b=b+a")
        void addCommutative() {
            Complex a = new Complex(1.1, -2.2);
            Complex b = new Complex(-3.3, 4.4);
            Complex ab = a.add(b);
            Complex ba = b.add(a);
            assertEquals(ab.getReal(),      ba.getReal(),      EPS);
            assertEquals(ab.getImaginary(), ba.getImaginary(), EPS);
        }

        @Test
        @DisplayName("加实数: (2+3i)+5=(7+3i)")
        void addDouble() {
            Complex r = new Complex(2, 3).add(5.0);
            assertEquals(7.0, r.getReal(),      EPS);
            assertEquals(3.0, r.getImaginary(), EPS);
        }

        @Test
        @DisplayName("NaN 传播: z+NaN=NaN")
        void addNaN() {
            assertTrue(new Complex(1, 2).add(Complex.NaN).isNaN());
        }

        @Test
        @DisplayName("null 参数: 抛出 NullPointerException")
        void addNull() {
            assertThrows(NullPointerException.class,
                    () -> Complex.ONE.add((Complex) null));
        }

        @ParameterizedTest(name = "({0}+{1}i)+({2}+{3}i) = ({4}+{5}i)")
        @CsvSource({
                "1,  2,  3,  4,  4,  6",
                "0,  0,  5, -3,  5, -3",
                "-1, -1, 1,  1,  0,  0",
                "0.5, 0.5, 0.5, 0.5, 1.0, 1.0"
        })
        @DisplayName("参数化加法测试")
        void parameterizedAdd(double r1, double i1,
                              double r2, double i2,
                              double er, double ei) {
            Complex result = new Complex(r1, i1).add(new Complex(r2, i2));
            assertEquals(er, result.getReal(),      EPS);
            assertEquals(ei, result.getImaginary(), EPS);
        }
    }

    // ======================== subtract ========================

    @Nested
    @DisplayName("subtract() 减法测试")
    class SubtractTests {

        @Test
        @DisplayName("基本减法: (5+7i)-(2+3i)=(3+4i)")
        void basicSubtract() {
            Complex r = new Complex(5, 7).subtract(new Complex(2, 3));
            assertEquals(3.0, r.getReal(),      EPS);
            assertEquals(4.0, r.getImaginary(), EPS);
        }

        @Test
        @DisplayName("自减: z-z=0")
        void subtractSelf() {
            Complex z = new Complex(3.14, -2.72);
            Complex r = z.subtract(z);
            assertEquals(0.0, r.getReal(),      EPS);
            assertEquals(0.0, r.getImaginary(), EPS);
        }

        @Test
        @DisplayName("减零: z-0=z")
        void subtractZero() {
            Complex z = new Complex(-1, 8);
            Complex r = z.subtract(Complex.ZERO);
            assertEquals(z.getReal(),      r.getReal(),      EPS);
            assertEquals(z.getImaginary(), r.getImaginary(), EPS);
        }

        @Test
        @DisplayName("null 参数: 抛出 NullPointerException")
        void subtractNull() {
            assertThrows(NullPointerException.class,
                    () -> Complex.ONE.subtract(null));
        }
    }

    // ======================== multiply ========================

    @Nested
    @DisplayName("multiply() 乘法测试 —— 覆盖注入缺陷")
    class MultiplyTests {

        @Test
        @DisplayName("纯实数乘法: 2×3=6（缺陷不影响此用例）")
        void multiplyPureReal() {
            Complex r = new Complex(2, 0).multiply(new Complex(3, 0));
            assertEquals(6.0, r.getReal(),      EPS);
            assertEquals(0.0, r.getImaginary(), EPS);
        }

        @Test
        @DisplayName("纯虚数乘法: i×i=-1（缺陷不影响此用例）")
        void multiplyPureImaginary() {
            Complex r = Complex.I.multiply(Complex.I);
            assertEquals(-1.0, r.getReal(),      EPS);
            assertEquals(0.0,  r.getImaginary(), EPS);
        }

        @Test
        @DisplayName("混合乘法: (1+2i)(3+4i)=(-5+10i) ★ 触发缺陷")
        void multiplyMixed() {
            Complex r = new Complex(1, 2).multiply(new Complex(3, 4));
            assertEquals(-5.0,  r.getReal(),      EPS);
            assertEquals(10.0, r.getImaginary(), EPS);
        }

        @Test
        @DisplayName("混合乘法: (2+3i)(4+5i)=(-7+22i) ★ 触发缺陷")
        void multiplyMixed2() {
            Complex r = new Complex(2, 3).multiply(new Complex(4, 5));
            assertEquals(-7.0,  r.getReal(),      EPS);
            assertEquals(22.0, r.getImaginary(), EPS);
        }

        @Test
        @DisplayName("平方: (1+i)²=2i ★ 触发缺陷")
        void multiplySquare() {
            Complex r = new Complex(1, 1).multiply(new Complex(1, 1));
            assertEquals(0.0, r.getReal(),      EPS);
            assertEquals(2.0, r.getImaginary(), EPS);
        }

        @Test
        @DisplayName("乘零: z×0=0")
        void multiplyByZero() {
            Complex r = new Complex(5, 7).multiply(new Complex(0, 0));
            // 注意：实现中 multiply 对零有短路判断，这里用构造器创建零
            assertEquals(0.0, r.getReal(),      EPS);
            assertEquals(0.0, r.getImaginary(), EPS);
        }

        @Test
        @DisplayName("乘一: z×1=z")
        void multiplyByOne() {
            Complex z = new Complex(3.5, -2.1);
            Complex r = z.multiply(Complex.ONE);
            assertEquals(z.getReal(),      r.getReal(),      EPS);
            assertEquals(z.getImaginary(), r.getImaginary(), EPS);
        }

        @Test
        @DisplayName("标量乘法: (2+3i)×4=(8+12i)")
        void multiplyScalar() {
            Complex r = new Complex(2, 3).multiply(4.0);
            assertEquals(8.0,  r.getReal(),      EPS);
            assertEquals(12.0, r.getImaginary(), EPS);
        }

        @Test
        @DisplayName("交换律: a×b=b×a")
        void multiplyCommutative() {
            Complex a = new Complex(1, 2);
            Complex b = new Complex(3, 4);
            Complex ab = a.multiply(b);
            Complex ba = b.multiply(a);
            assertEquals(ab.getReal(),      ba.getReal(),      EPS);
            assertEquals(ab.getImaginary(), ba.getImaginary(), EPS);
        }

        @Test
        @DisplayName("NaN 传播: z×NaN=NaN")
        void multiplyNaN() {
            assertTrue(new Complex(1, 1).multiply(Complex.NaN).isNaN());
        }

        @Test
        @DisplayName("null 参数: 抛出 NullPointerException")
        void multiplyNull() {
            assertThrows(NullPointerException.class,
                    () -> Complex.ONE.multiply((Complex) null));
        }
    }

    // ======================== divide ========================

    @Nested
    @DisplayName("divide() 除法测试")
    class DivideTests {

        @Test
        @DisplayName("基本除法: (1+0i)/(0+1i)=(0-1i)")
        void divideBasic() {
            // 1/i = -i
            Complex r = Complex.ONE.divide(Complex.I);
            assertEquals(0.0,  r.getReal(),      EPS);
            assertEquals(-1.0, r.getImaginary(), EPS);
        }

        @Test
        @DisplayName("(2+3i)/(1+1i)=(2.5+0.5i)")
        void divideMixed() {
            // (2+3i)/(1+i) = (2+3i)(1-i)/2 = (2+3+i(-2+3))/2 = (5+i)/2
            Complex r = new Complex(2, 3).divide(new Complex(1, 1));
            assertEquals(2.5, r.getReal(),      EPS);
            assertEquals(0.5, r.getImaginary(), EPS);
        }

        @Test
        @DisplayName("除一: z/1=z")
        void divideByOne() {
            Complex z = new Complex(7, -4);
            Complex r = z.divide(Complex.ONE);
            assertEquals(z.getReal(),      r.getReal(),      EPS);
            assertEquals(z.getImaginary(), r.getImaginary(), EPS);
        }

        @Test
        @DisplayName("除以自身: z/z=1")
        void divideSelf() {
            Complex z = new Complex(3, 4);
            Complex r = z.divide(z);
            assertEquals(1.0, r.getReal(),      EPS);
            assertEquals(0.0, r.getImaginary(), EPS);
        }

        @Test
        @DisplayName("除以零: z/0=NaN")
        void divideByZero() {
            assertTrue(Complex.ONE.divide(Complex.ZERO).isNaN());
        }

        @Test
        @DisplayName("标量除法: (4+6i)/2=(2+3i)")
        void divideScalar() {
            Complex r = new Complex(4, 6).divide(2.0);
            assertEquals(2.0, r.getReal(),      EPS);
            assertEquals(3.0, r.getImaginary(), EPS);
        }

        @Test
        @DisplayName("null 参数: 抛出 NullPointerException")
        void divideNull() {
            assertThrows(NullPointerException.class,
                    () -> Complex.ONE.divide((Complex) null));
        }
    }
}
