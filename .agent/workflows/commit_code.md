---
description: Octopus Agent 的代码格式化验证与规范提交流程 (Commit Standard)
---

# 代码提交标准工作流

当你协助人类开发者为 Octopus 框架写入新的底层代码重构或特性完成后，在向 Git 仓库提交任何代码修改时，你 **必须** 根据以下顺序完成安全校验指令与流程步骤：

// turbo-all
1. **代码格式统一验证**
   本工程继承并要求了严格的格式校验。
   请在项目根目录运行以下命令格式化全量结构：
   `mvn spotless:apply`

2. **验证编译与全量测试拦截**
   在提交至仓库前，必须跑完所有的 test（包括单元测试与端到端集成测试），确保新加入的内容对所有下属 7 个模块的依赖与功能均无破坏：
   `mvn clean test`

3. **组织 Commit Msg**
   使用符合规范的 AngularJS 提交模板结构提交变更。格式为 `<type>(<scope>): <subject>`：
   - `feat(模块名)`: 增加新的 API 或大核机制，例如 `feat(llm): implement unified ChatModel SPI`
   - `fix(模块名)`: 修复发现的 BUG 和冲突问题
   - `refactor(模块名)`: 对已有概念与类重新提取重命名而未引入特性的
   - `docs(ai_helper)`: 对辅助文档，或者 README 进行的修补

4. **安全推送代码**
   使用指令将通过两项 mvn 强阻拦规则的代码入库本地，或者视用户要求推送到 remote。
   `git add .`
   `git commit -m "feat/fix/docs(module): xxx"`
