package org.apache.commons.math3.complex;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * JUnit 5 测试套件：将所有 Complex 单元测试打包为一个测试集，
 * 实现批量运行。
 *
 * 运行方式：
 *   mvn test -Dtest="org.apache.commons.math3.complex.ComplexAllTests"
 *   或在 IDE 中右键运行此类。
 */
@Suite
@SuiteDisplayName("Complex 完整测试套件")
@SelectClasses({
        ComplexArithmeticTest.class,
        ComplexSpecialFunctionTest.class,
        ComplexEdgeCaseTest.class
})
public class ComplexAllTests {
}
