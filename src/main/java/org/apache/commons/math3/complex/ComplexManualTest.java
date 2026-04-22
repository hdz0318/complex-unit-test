package org.apache.commons.math3.complex;

/**
 * 不依赖任何测试框架的手动单元测试。
 * 通过 main 方法驱动，使用自定义断言方法检验 Complex 类的核心功能。
 * 测试覆盖 add()、multiply()（含注入缺陷）、abs() 三个关键方法。
 */
public class ComplexManualTest {

    private static int totalTests  = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;

    // ==================== 自定义断言工具 ====================

    private static void assertDoubleEquals(String name,
                                           double expected,
                                           double actual,
                                           double eps) {
        totalTests++;
        if (Math.abs(expected - actual) < eps) {
            passedTests++;
            System.out.println("  [PASS] " + name);
        } else {
            failedTests++;
            System.out.printf("  [FAIL] %s | Expected: %f, Got: %f%n",
                              name, expected, actual);
        }
    }

    private static void assertTrue(String name, boolean condition) {
        totalTests++;
        if (condition) {
            passedTests++;
            System.out.println("  [PASS] " + name);
        } else {
            failedTests++;
            System.out.println("  [FAIL] " + name + " | Condition was false");
        }
    }

    private static void assertThrows(String name,
                                     Class<? extends Throwable> expected,
                                     Runnable block) {
        totalTests++;
        try {
            block.run();
            failedTests++;
            System.out.println("  [FAIL] " + name + " | No exception thrown");
        } catch (Throwable t) {
            if (expected.isInstance(t)) {
                passedTests++;
                System.out.println("  [PASS] " + name);
            } else {
                failedTests++;
                System.out.printf("  [FAIL] %s | Expected: %s, Got: %s%n",
                        name, expected.getSimpleName(),
                        t.getClass().getSimpleName());
            }
        }
    }

    // ==================== 测试方法 ====================

    /**
     * 测试 add()：复数加法。
     */
    private static void testAdd() {
        System.out.println("\n[Test Group] add()");

        // (1+2i) + (3+4i) = (4+6i)
        Complex r1 = new Complex(1, 2).add(new Complex(3, 4));
        assertDoubleEquals("(1+2i)+(3+4i) real=4",  4.0, r1.getReal(),      1e-10);
        assertDoubleEquals("(1+2i)+(3+4i) imag=6",  6.0, r1.getImaginary(), 1e-10);

        // (1+2i) + (-1-2i) = 0
        Complex r2 = new Complex(1, 2).add(new Complex(-1, -2));
        assertDoubleEquals("(1+2i)+(-1-2i) real=0", 0.0, r2.getReal(),      1e-10);
        assertDoubleEquals("(1+2i)+(-1-2i) imag=0", 0.0, r2.getImaginary(), 1e-10);

        // 0 + (5-3i) = (5-3i)
        Complex r3 = Complex.ZERO.add(new Complex(5, -3));
        assertDoubleEquals("0+(5-3i) real=5",  5.0,  r3.getReal(),      1e-10);
        assertDoubleEquals("0+(5-3i) imag=-3", -3.0, r3.getImaginary(), 1e-10);

        // 加法交换律: a+b == b+a
        Complex a = new Complex(2.5, -1.3);
        Complex b = new Complex(-0.7, 4.1);
        Complex ab = a.add(b);
        Complex ba = b.add(a);
        assertDoubleEquals("add commutativity real",  ab.getReal(),      ba.getReal(),      1e-10);
        assertDoubleEquals("add commutativity imag",  ab.getImaginary(), ba.getImaginary(), 1e-10);

        // null 参数应抛出 NullPointerException
        assertThrows("add(null) throws NPE", NullPointerException.class,
                () -> Complex.ONE.add((Complex) null));
    }

    /**
     * 测试 multiply()：复数乘法。
     * 本方法覆盖了注入的缺陷 —— 虚部公式的符号错误。
     */
    private static void testMultiply() {
        System.out.println("\n[Test Group] multiply()  ** covers injected defect **");

        // --- 以下测试在缺陷存在时仍能通过（纯实数 × 纯实数）---
        Complex r1 = new Complex(2, 0).multiply(new Complex(3, 0));
        assertDoubleEquals("(2)*(3) real=6",   6.0, r1.getReal(),      1e-10);
        assertDoubleEquals("(2)*(3) imag=0",   0.0, r1.getImaginary(), 1e-10);

        // --- 以下测试在缺陷存在时仍能通过（纯虚数 × 纯虚数）---
        // i * i = -1
        Complex r2 = Complex.I.multiply(Complex.I);
        assertDoubleEquals("i*i real=-1",     -1.0, r2.getReal(),      1e-10);
        assertDoubleEquals("i*i imag=0",       0.0, r2.getImaginary(), 1e-10);

        // --- 以下测试将暴露缺陷（混合复数乘法）---
        // (1+2i)(3+4i) = (1×3−2×4) + (1×4+2×3)i = −5 + 10i
        Complex r3 = new Complex(1, 2).multiply(new Complex(3, 4));
        assertDoubleEquals("(1+2i)*(3+4i) real=-5",  -5.0, r3.getReal(),      1e-10);
        assertDoubleEquals("(1+2i)*(3+4i) imag=10",  10.0, r3.getImaginary(), 1e-10);

        // (2+3i)(4+5i) = (8−15) + (10+12)i = −7 + 22i
        Complex r4 = new Complex(2, 3).multiply(new Complex(4, 5));
        assertDoubleEquals("(2+3i)*(4+5i) real=-7",  -7.0, r4.getReal(),      1e-10);
        assertDoubleEquals("(2+3i)*(4+5i) imag=22",  22.0, r4.getImaginary(), 1e-10);

        // (1+i)(1+i) = 2i
        Complex r5 = new Complex(1, 1).multiply(new Complex(1, 1));
        assertDoubleEquals("(1+i)*(1+i) real=0",  0.0, r5.getReal(),      1e-10);
        assertDoubleEquals("(1+i)*(1+i) imag=2",  2.0, r5.getImaginary(), 1e-10);

        // 乘法交换律: a*b == b*a（即使有 bug 也应满足）
        Complex a = new Complex(1, 2);
        Complex b = new Complex(3, 4);
        Complex ab = a.multiply(b);
        Complex ba = b.multiply(a);
        assertDoubleEquals("multiply commutativity real",
                ab.getReal(),      ba.getReal(),      1e-10);
        assertDoubleEquals("multiply commutativity imag",
                ab.getImaginary(), ba.getImaginary(), 1e-10);
    }

    /**
     * 测试 abs()：复数模（绝对值）。
     */
    private static void testAbs() {
        System.out.println("\n[Test Group] abs()");

        // |3+4i| = 5
        assertDoubleEquals("|3+4i|=5",   5.0, new Complex(3, 4).abs(),   1e-10);
        // |0| = 0
        assertDoubleEquals("|0|=0",      0.0, Complex.ZERO.abs(),        1e-10);
        // |1| = 1
        assertDoubleEquals("|1|=1",      1.0, Complex.ONE.abs(),         1e-10);
        // |-3-4i| = 5
        assertDoubleEquals("|-3-4i|=5",  5.0, new Complex(-3, -4).abs(), 1e-10);
        // |i| = 1
        assertDoubleEquals("|i|=1",      1.0, Complex.I.abs(),           1e-10);
        // NaN
        assertTrue("|NaN| is NaN", Double.isNaN(Complex.NaN.abs()));
    }

    // ==================== 主入口 ====================

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("       Complex 手动单元测试 (main 方法驱动)");
        System.out.println("============================================================");

        testAdd();
        testMultiply();
        testAbs();

        System.out.println("\n============================================================");
        System.out.printf("  结果: %d passed, %d failed, %d total%n",
                passedTests, failedTests, totalTests);
        System.out.println("============================================================");

        if (failedTests > 0) {
            System.out.println("\n>>> 存在失败的测试用例！ <<<");
            System.exit(1);
        } else {
            System.out.println("\n>>> 全部测试通过 <<<");
        }
    }
}
