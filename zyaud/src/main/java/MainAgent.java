import fzhx.MyBatisSqlLogAgent;
import fzhx.ZyaudFsCronAgent;

import java.lang.instrument.Instrumentation;

/**
 * Java Agent 统一入口。
 *
 * @author 36020
 */
public final class MainAgent {
    private MainAgent() {
    }

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        // 两个 Agent 在总入口中平级注册，互不嵌套。
        ZyaudFsCronAgent.register(instrumentation);
        MyBatisSqlLogAgent.register(instrumentation);
        // FzhxFlowableAgent.register(instrumentation);
        System.err.println("[main-agent] Agent 注册完成，参数：" + agentArgs);
    }
}