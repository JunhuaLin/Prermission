package cn.junhua.android.permission.core;

/**
 * 用于继续执行权限申请操作
 *
 * @author junhua.lin@jinfuzi.com<br/>
 * CREATED 2018/12/6 17:20
 */
public interface AgentExecutor {
    /**
     * 处理后续权限请求
     */
    void execute();
}
