# 缺陷报告 —— Complex.multiply(Complex) 虚部符号错误

> 本文档为正式向 GitHub Issues 提交的缺陷报告草稿。
> 提交时请复制正文到 Issue，标签设为 `bug`，严重度 `Critical`，优先级 `P1`。

---

**标题**：`[BUG] Complex.multiply(Complex) 虚部计算结果符号错误，导致混合复数乘法全部失败`

## 一、缺陷摘要
`Complex#multiply(Complex factor)` 在计算虚部时，把公式中的加号误写为减号，导致**任何实部、虚部均不为零的复数相乘**结果的虚部错误。纯实数 × 纯实数、纯虚数 × 纯虚数等特殊用例恰好不受影响，容易遗漏。

## 二、环境信息
- OS: Windows 11 Pro 26200 / Ubuntu 22.04 (GitHub Actions runner)
- JDK: Temurin 11.0.x
- Maven: 3.9.x
- 项目版本: `complex-unit-test:1.0-SNAPSHOT`
- Commit: <填写触发 CI 失败的 commit SHA>

## 三、复现步骤
1. 克隆仓库：`git clone https://github.com/hdz0318/complex-unit-test.git`
2. 进入目录：`cd complex-unit-test`
3. 执行：`mvn -B test`
4. 观察 Surefire 输出（或 GitHub Actions 日志中 **Run smoke tests** 步骤）。

或者直接在 Java 代码中：

```java
Complex r = new Complex(1, 2).multiply(new Complex(3, 4));
System.out.println(r);          // 期望：-5.0 + 10.0i
                                // 实际：-5.0 + -2.0i   （虚部 1*4 − 2*3 = −2）
```

## 四、期望结果
按复数乘法定义，`(a + b·i)(c + d·i) = (ac − bd) + (ad + bc)·i`，因此：

| 输入 | 期望输出 |
| --- | --- |
| (1+2i) × (3+4i) | **-5 + 10i** |
| (2+3i) × (4+5i) | **-7 + 22i** |
| (1+i) × (1+i)   | **0 + 2i**  |

Surefire 中 `ComplexArithmeticTest$MultiplyTests` 内全部用例应通过。

## 五、实际结果
本地执行 `mvn -B test` 后 Surefire 报告总计 **7 个用例失败**，核心失败摘录如下：

```
[ERROR] ComplexArithmeticTest$MultiplyTests.multiplyMixed:168
        expected: <10.0> but was: <-2.0>
[ERROR] ComplexArithmeticTest$MultiplyTests.multiplyMixed2:176
        expected: <22.0> but was: <-2.0>
[ERROR] ComplexArithmeticTest$MultiplyTests.multiplySquare:184
        expected: <2.0>  but was: <0.0>
[ERROR] ComplexSpecialFunctionTest.reciprocalMultiply:91
        expected: <0.0>  but was: <-0.96>
[ERROR] ComplexSpecialFunctionTest.sqrtSquareRoundTrip:194
        expected: <4.0>  but was: <0.0>

[INFO] Tests run: 177, Failures: 7, Errors: 0, Skipped: 0
[INFO] BUILD FAILURE
```

可见缺陷不仅影响 `multiply` 的直接用例，还污染了 `reciprocal`、`sqrt` 等**间接依赖乘法**的高层 API，影响面超出初步估计。

## 六、缺陷定位
- **File**: `src/main/java/org/apache/commons/math3/complex/Complex.java`
- **Line**: 252
- **当前代码**：
  ```java
  return new Complex(
          real * factor.real - imaginary * factor.imaginary,
          real * factor.imaginary - imaginary * factor.real);   // ← 此处 "-" 应为 "+"
  ```
- **根本原因**：虚部公式 `ad + bc` 被错误地实现为 `ad − bc`。
- **建议修复**：
  ```java
  return new Complex(
          real * factor.real      - imaginary * factor.imaginary,
          real * factor.imaginary + imaginary * factor.real);
  ```
  方法上方的 Javadoc 注释 `(ac − bd) + (ad + bc)·i` 已正确描述规格，可作为修复依据。

## 七、严重度与优先级
- **Severity**: Critical
  理由：核心算术 API 产生**静默错误结果**（无异常、无警告），所有依赖 `multiply` 的上层功能（如 `pow`、`log` 经由 `multiply` 组合的调用链）都可能被污染。
- **Priority**: P1
  理由：影响广、定位难、修复代价极小（单行改动）。

## 八、附件
- Surefire 报告：CI 构件 `surefire-reports` → `TEST-...ComplexArithmeticTest.xml`
- Actions 运行日志链接：`https://github.com/hdz0318/complex-unit-test/actions/runs/<run_id>`
- 触发用例：`ComplexArithmeticTest$MultiplyTests#multiplyMixed / multiplyMixed2 / multiplySquare`

## 九、回归用例
修复后，`mvn test` 应 177 条全部通过。重点关注以下用例不再失败：
- `ComplexArithmeticTest$MultiplyTests#multiplyMixed`
- `ComplexArithmeticTest$MultiplyTests#multiplyMixed2`
- `ComplexArithmeticTest$MultiplyTests#multiplySquare`
- `ComplexSpecialFunctionTest#reciprocalMultiply`（间接依赖 multiply）
- `ComplexSpecialFunctionTest#sqrtSquareRoundTrip`（间接依赖 multiply）
