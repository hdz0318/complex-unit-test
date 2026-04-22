# 测试管理实验 —— Complex 单元测试的 CI/CD 与缺陷管理

> 软件测试课程 · 测试管理作业
> 学号 / 姓名：202330218 何东泽
> GitHub：[@hdz0318](https://github.com/hdz0318)

[![CI - Build & Smoke Test](https://github.com/hdz0318/complex-unit-test/actions/workflows/ci.yml/badge.svg)](https://github.com/hdz0318/complex-unit-test/actions/workflows/ci.yml)

## 一、实验目标

在《单元测试》实验的基础上，引入 **软件配置管理 / 持续集成 / 缺陷管理** 工具链，完成一次完整的测试管理实践：

| 模块 | 工具 |
| --- | --- |
| Git 代码仓库 | GitHub |
| 持续集成 & 冒烟测试 | GitHub Actions |
| Issue tracking / 缺陷管理 | GitHub Issues |
| 自动化构建与测试 | Maven（+ Ant wrapper） |
| 单元测试框架 | JUnit 5 (Jupiter) |

## 二、项目结构

```
.
├── .github/
│   ├── workflows/ci.yml          # GitHub Actions 持续集成配置
│   └── ISSUE_TEMPLATE/           # 缺陷报告模板
├── src/
│   ├── main/java/.../Complex.java            # 被测类（含注入缺陷）
│   └── test/java/.../Complex*Test.java       # JUnit 5 测试用例
├── pom.xml                       # Maven 项目配置
├── build.xml                     # Ant 顶层 build/test 目标
├── BUG_REPORT.md                 # 正式缺陷报告（用于提交到 Issues）
└── README.md
```

## 三、构建与测试

### 使用 Maven（推荐）

```bash
# test 目标：运行全部 JUnit 5 单元测试
mvn test

# build 目标：导出完整 jar 包到 target/*.jar
mvn package
```

### 使用 Ant（作业要求的 build / test 两个目标）

```bash
ant test     # 等价于 mvn test
ant build    # 等价于 mvn package
ant all      # clean + test + build
```

构建产物：`target/complex-unit-test-1.0-SNAPSHOT.jar`
测试报告：`target/surefire-reports/`

## 四、持续集成（冒烟测试）

每次 `push` / `pull_request` 都会触发 GitHub Actions：

1. 使用 Temurin JDK 11，启用 Maven 依赖缓存
2. 执行 `mvn -B -ntp test`（冒烟测试，日志中可见每个测试用例的执行情况）
3. 执行 `mvn -B -ntp package` 产出 jar 包
4. 上传 Surefire 报告和 jar 包为 Artifact，便于追溯

查看结果：仓库 → **Actions** 标签页 → 选择最近一次运行 → 展开 `Run smoke tests` 步骤。

## 五、已知缺陷

在 `Complex.multiply(Complex)` 中注入了一个实现缺陷（虚部符号错误），触发后共 **3 个测试用例失败**，详见 [`BUG_REPORT.md`](./BUG_REPORT.md) 以及 Issues 中对应条目。

该缺陷的存在用于验证：
- 持续集成能够在代码出现回归时阻断流水线；
- 缺陷报告流程可以把"CI 失败"转化为"可跟踪、可修复"的 Issue。

## 六、参考

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Apache Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)
- [GitHub Actions — Building and testing Java with Maven](https://docs.github.com/en/actions/automating-builds-and-tests/building-and-testing-java-with-maven)
