package cn.junhua.android.permission.core;

/**
 * 向用户解释说明
 *
 * @author junhua.lin@jinfuzi.com<br/>
 * CREATED 2018/12/7 14:51
 */
public interface Rationale {
    /**
     * 当需要向用户解释说明时候回调
     *
     * @param permissions 需要解释的权限列表
     * @param executor    继续申请
     */
    void onShowRationale(String[] permissions, AgentExecutor executor);
}
