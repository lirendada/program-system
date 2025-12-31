你现在是我的 Java 微服务架构师搭档。我们要继续开发一个基于 Spring Cloud Alibaba 的 **Online Judge (OJ) 在线判题系统**。

请读取以下**项目背景**和**当前进度**，并准备好协助我进行下一步开发：

### 1. 项目架构与基础设施
* **架构**：微服务架构 (Modules: `gateway`, `system`, `user`, `job`, `judge`）
* **核心栈**：Spring Cloud Alibaba (Nacos), Spring Boot, MyBatis-Plus, MySQL, Redis.
* **配置中心**：使用 Nacos。
    * `common.yaml`: Redis, JWT, RabbitMQ 配置。
    * `gateway-service-dev.yaml`: 网关路由与动态白名单。
* **鉴权链路**：
    1.  **Gateway (10020)**: `AuthGlobalFilter` 解析 JWT，校验通过后将 `userId` 放入 HTTP Header (`userId`) 传递给下游。
    2.  **下游服务**: 引用 `common-web` 模块，通过 `UserInterceptor` 拦截器从 Header 读取 `userId`，存入 `UserContext` (`TransmittableThreadLocal`)。

### 2. 数据库与实体现状
* **表结构**：
    * `tb_problem`: 题目主表 (包含 `submit_num`, `accepted_num` 统计字段，**不含** tags 字段)。
    * `tb_problem_tag`: 题目标签字典表。
    * `tb_problem_tag_relation`: 题目与标签关联表 (多对多)。
    * `tb_sys_user`：管理员表
    * `tb_user`：c端用户表
    * 各表到时候你直接参考 /deploy/sql 文件
* **实体类**：
    * `ProblemEntity`: 对应 `tb_problem`。
    * `ProblemVO`: 题目列表页展示对象（脱敏，含 `List<String> tags`，无答案/用例）。
    * 其它的你也参考项目代码即可

### 3. 我们刚刚完成的任务
我们正在开发 **“题目列表分页查询接口”** (`/system/problem/list/page`)，主要完成了以下重构：
1.  **基建**：在 `common-core` 封装了 `PageRequest` 分页基类和 MP 分页插件配置。
2.  **DTO/VO**：创建了 `ProblemQueryRequest` (支持 id, title, difficulty, tags 筛选) 和 `ProblemVO`。
3.  **核心业务逻辑** (`ProblemServiceImpl`)：
    * 实现了复杂的 **标签关联查询** 逻辑。
    * 逻辑：先根据 tag name 查 `tb_tag` -> 查 `tb_problem_tag` 拿到 problemId 列表 -> 再去 `tb_problem` 查分页 -> 最后批量查关联标签填充到 VO。

### 4. 待解决的问题与下一步计划
**目前状态**：代码刚刚写完，但是还在考虑tags字段的问题，并且还未进行最终测试。

