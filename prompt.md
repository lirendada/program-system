你现在是我的 Java 微服务架构师搭档。我们要继续开发一个基于 Spring Cloud Alibaba 的 **Online Judge (OJ) 在线判题系统**。
以下是项目的完整背景、架构设计、当前进度和接下来的任务清单：

### 1. 项目背景与技术栈
* **项目名称**：Liren OJ (Spring Cloud Alibaba 微服务架构)
* **核心技术**：Spring Boot 3.x, Spring Cloud Alibaba (Nacos), MyBatis-Plus, RabbitMQ, Redis, Docker (Java API), OpenFeign, Spring Cloud Gateway。
* **架构模式**：前后端分离，微服务架构。

### 2. 微服务模块划分 (已建立)
目前项目包含以下 Maven 模块：
* **`oj-system` (Root)**: 父工程。
* **`common`**: 公共模块。
  * `common-core`: 全局异常、Result 包装类、工具类、UserContext。
  * `common-web`: WebMvc 配置、拦截器、统一异常处理。
* **`api`**: **关键模块**。存放 OpenFeign 的接口定义 (`RemoteProblemService`) 和跨服务传输的 DTO (`SubmitRecordDTO`, `TestCaseDTO`, `ProblemSubmitUpdateDTO`)。
* **`gateway`**: 网关服务 (10020)。负责 JWT 鉴权 (`AuthGlobalFilter`)，白名单校验，透传 `userId` 到下游。
* **`modules` (业务服务)**:
  * **`user`(8004)**: 用户服务 (已由 System 模块暂代，UserContext 逻辑已通)。
  * **`problem` (8006)**: 题目微服务。负责题目管理、标签管理、测试用例管理、接收用户提交。
  * **`judge` (8002)**: 判题微服务。**消费者**，负责监听 MQ，调用 Docker 沙箱判题。
  * **`system` (8003)**: 
* 剩下的细节请你结合我给你的划分图片

### 3. 核心业务逻辑 (已实现)
目前最核心的 **“提交-判题-回写”** 全链路已打通，流程如下：

1.  **提交 (Producer)**: 用户调用 `POST /problem/submit` -> `oj-problem` 存入数据库 (`status=10` 待判题) -> 发送 `submitId` (Long) 到 RabbitMQ。
2.  **消费 (Consumer)**: `oj-judge` 监听 MQ -> 收到 `submitId`。
3.  **反查数据 (Feign)**: `oj-judge` 通过 `RemoteProblemService` 调用 `oj-problem`：
  * 获取提交详情 (代码、语言)。
  * 获取测试用例 (Input/Output 列表)。
4.  **沙箱执行 (Docker)**:
  * 使用 `docker-java` 客户端连接远程 Ubuntu Docker (TCP 2375)。
  * 流程：创建容器 (`openjdk:8-alpine`) -> 传输代码与输入 (TAR流) -> 编译 (`javac`) -> 运行 (`java`) -> 收集输出 -> 销毁容器。
5.  **结果比对**: 对比沙箱输出与标准输出，判定 AC/WA。
6.  **结果回写 (Feign)**: `oj-judge` 调用 `oj-problem` 更新数据库状态 (`status=30`, `result=1`, `time_cost` 等)。

### 4. 数据库设计 (关键表)
* `tb_problem`: 题目基本信息 (description, time_limit 等)。
* `tb_submit_record`: 提交记录 (submit_id, code, status, judge_result, case_result JSON)。
* `tb_test_case`: 题目的测试用例 (input, output)。
* `tb_problem_tag`, `tb_problem_tag_relation`: 题目与标签关联。

### 5. 当前代码状态 (关键细节)
* **Docker沙箱**: 实现了 `JavaDockerCodeSandbox`，支持 Java 语言的编译运行，解决了文件流传输问题。
* **Feign配置**: 解决了路径冲突问题 (`/inner/submit/{id}` 和 `/inner/test-case/{id}`)。
* **负载均衡**: 解决了 Spring Cloud LoadBalancer 缺少 Caffeine 依赖的警告。
* **鉴权**: 网关已配置 JWT 解析，Feign 内部调用不鉴权 (通过 URL 前缀区分或内网隔离，目前逻辑是通的)。

### 6. 接下来的任务 (Next Steps)
我们暂停在了 **“完善业务闭环”** 这一步。

**当前任务：实现提交记录查询接口**
* **需求**: 前端需要轮询提交结果，或者展示提交列表。
* **位置**: `oj-problem` 服务。
* **接口**: `POST /problem/submit/list` (分页查询)。
* **逻辑**: 需要支持按 `problemId`、`userId`、`status` 筛选。返回的 VO 需要脱敏 (不包含大量代码)，并包含题目名称 (需要组装数据)。

**后续规划**:
1.  **沙箱增强**: 目前沙箱资源限制是写死的，需要改为读取数据库 `tb_problem` 中的 `time_limit` 和 `memory_limit` 动态传给 Docker。
2.  **策略模式**: 重构 `JudgeReceiver`，支持多语言 (Python, C++) 判题策略。
3.  **安全增强**: 限制 Docker 容器的网络访问、最大输出大小等。

请基于以上上下文，继续辅助我完成 **“提交记录查询接口”** 的开发。