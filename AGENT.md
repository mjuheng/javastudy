## 项目经验
- 使用 javaagent 增强 MyBatis `BaseExecutor` 打印 SQL 时，不要同时增强 4 参数 `query` 和 6 参数 `query`；4 参数方法会转调 6 参数方法，同时注入会导致同一条 SQL 打印两遍。
- Javassist `addCatch` 注入的 catch 块不能直接访问 `addLocalVariable` 新增的局部变量；需要把跨正常/异常路径共享的数据放到静态方法或 ThreadLocal 中，否则会出现 `no such field` 编译错误。
