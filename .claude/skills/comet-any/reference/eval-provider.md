# Eval Evidence 参考

## Eval 选择

在任何验证动作前，必须展示：

- `quick` 的预计运行次数、覆盖组件和 token 消耗。
- `full` 的预计运行次数、覆盖组件和 token 消耗。
- `skip / quick / full eval` 三个选择。

skip、eval 失败或证据 hash 过期时不得进入 ready；也不得 publish 或 distribute。

普通用户的验证路径保持单一：日常评估统一走 `comet eval`。`/comet-any` 可以在内部登记 eval evidence，但不要把内部登记步骤包装成面向普通用户的替代命令。对普通用户，解释时优先使用“验证”，不要把 `Publish readiness:` 当成第一层概念。

## 结果记录

eval evidence 必须输出结构化 JSON，至少包含：

- 当前 draft hash。
- 覆盖的 entry Skill 和 internal Node Skill。
- `workflow-protocol.json` hash。
- quick 或 full 的选择、token 消耗和结果摘要。
- Bundle 编译、安全检查和 capability evidence。

只有当前 draft hash 的 eval evidence 可以推进状态。旧 hash 证据可以保留在磁盘用于审计，但不能让当前 draft 进入 ready。

## 人工评审

eval 通过后仍必须人工批准。评审摘要至少包含：

先运行 `comet publish review <name> --platform <reference-platform> --json`，再基于其输出展示：

- Bundle 名称、版本、hash。
- 多个 entry 与 internal Skill 列表。
- `planHash`、`preferenceHash` 与 `reference/resolved-skills.json` 真实 Skill 证据，包括项目级偏好模式、required Skill、`sourceSummaries` 与“整理后的工作方式”摘要。
- 推荐调用顺序与 `preferenceIndex`。
- 偏离偏好顺序的项和原因。
- `.comet/skill-preferences.yaml` 是否在 Factory 初始化后发生漂移；`advisory` 模式给出 warning，`strict` 模式阻塞。
- 稳定组合 Skill Bundle 的 required capability set（必需能力集合）`skills/scripts/rules/hooks/references` 是否声明完整，且 `scripts/rules/hooks` 是否继续作为 required control plane。
- 是否生成 `comet/skill.yaml`、`comet/guardrails.yaml`、`comet/checks.yaml` 与 `comet/eval.yaml`。
- `hooks/*.yaml` 是否仅被当作 portable hook descriptor，等待 `comet publish distribute` 编译到目标平台。
- 能力缺口和可执行披露。
- eval 选择、token 消耗和结果摘要。
- `Validate this Skill` 与下一步提示，让用户知道 readiness 为什么可发布或被阻塞。

readiness blockers 会阻止 publish。只要存在当前 draft hash 的 eval evidence 缺失、人工 approval 缺失、required capability gap 或 executable disclosure 未确认，就必须停在评审阶段，不能继续发布。

只有用户显式批准后，才能运行 `comet publish approve` 并发布。

## 分发预览

执行真实分发前，必须先跑 preview：

```bash
comet publish distribute <name> --platform <id> --scope project --preview --json
```

preview 是强制检查，不是可选附加项。它应向用户展示：

- `Install preview`
- planned files
- unsupported capability
- executable disclosures
- `No files were written`

只有当用户确认 preview 结果后，才可以移除 `--preview` 执行真实分发。
