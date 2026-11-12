<div align="center">
  <h1>企业级大模型统一接入与治理平台（LLM Gateway）</h1>

  <p>
    <img src="https://img.shields.io/badge/license-Apache--2.0-2563eb" alt="Apache-2.0 License">
    <img src="https://img.shields.io/badge/Java-21-ef4444" alt="Java 21">
    <img src="https://img.shields.io/badge/Spring_Boot-3.3-6db33f" alt="Spring Boot 3.3">
  </p>
</div>
这是 LLM Gateway 的第一阶段可运行工程骨架（P0），用于逐步建设企业级大模型统一接入与治理平台。

企业级大模型统一接入与治理平台（LLM Gateway）
项目核心定位
在企业已有网关基础上，增加大模型专属治理能力。

1. 每个应用独立配置模型 API Key，管理困难。
2. 不同模型接口不一致，切换模型需要修改业务代码。
3. 模型调用费用无法细分到具体业务应用。
4. 缺少统一配额控制，可能出现异常调用。
5. 上游模型服务异常时，影响业务系统正常使用。
   于是设计 LLM Gateway，将模型接入、用量控制、成本统计和容灾能力统一收敛。

## 当前包含

- Java 21 + Spring Boot 3.3.5 + WebFlux
- OpenAI 风格 `POST /v1/chat/completions`
- OpenAI-compatible Provider 适配器
- 非流式调用
- SSE 流式响应基础透传
- `GET /v1/models`
- 参数校验、统一异常响应
- Actuator health/metrics 基础端点

## 当前尚未实现

租户和 API Key 管理、Redis RPM/TPM 配额、Token 预扣结算、Kafka 计量、数据库持久化、模型路由、熔断降级、管理后台和生产级审计。这些应按 README 总体方案分阶段开发，不能把当前骨架描述为完整生产平台。

## 环境要求

- JDK 21
- Maven 3.9+
- 一个 OpenAI-compatible 模型服务及其 API Key

## 启动

Linux/macOS:

```bash
export LLM_UPSTREAM_BASE_URL="https://api.openai.com/v1"
export LLM_UPSTREAM_API_KEY="你的模型服务Key"
export LLM_DEFAULT_MODEL="gpt-4o-mini"
mvn spring-boot:run
```

Windows PowerShell:

```powershell
$env:LLM_UPSTREAM_BASE_URL="https://api.openai.com/v1"
$env:LLM_UPSTREAM_API_KEY="你的模型服务Key"
$env:LLM_DEFAULT_MODEL="gpt-4o-mini"
mvn spring-boot:run
```

## 非流式调用

```bash
curl http://localhost:8080/v1/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "model": "gpt-4o-mini",
    "messages": [{"role":"user","content":"用一句话解释什么是 Redis"}],
    "stream": false
  }'
```

## 流式调用

```bash
curl -N http://localhost:8080/v1/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "model": "gpt-4o-mini",
    "messages": [{"role":"user","content":"介绍一下 Java"}],
    "stream": true
  }'
```

## 健康检查

```bash
curl http://localhost:8080/actuator/health
```

## 下一步开发顺序

1. 抽象 Provider SPI，并补充第二个 Provider 实现和 Provider 注册/选择机制。
2. 增加 requestId、统一错误码和上游超时配置。
3. 增加租户、应用、API Key 数据模型及鉴权。
4. 增加 Redis Lua RPM/TPM 配额、预扣与结算。
5. 增加 Token 用量事件、Kafka 消费、幂等入库。
6. 增加路由、熔断、重试和降级。
7. 增加监控、集成测试、故障测试和压测。

## 重要说明

此版本是 P0 工程起点，不是完整生产级实现。流式透传、上游错误映射、超时与取消传播、usage 计量、凭证安全等仍需在后续阶段完善并通过测试验证。不要将尚未实现的模块或未实测的性能指标写成已交付成果。
