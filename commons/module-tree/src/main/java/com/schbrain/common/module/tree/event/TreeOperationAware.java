package com.schbrain.common.module.tree.event;

import com.schbrain.common.module.tree.TreeNode;

import java.util.List;

/**
 * Created by hzchengyi on 2019/1/21.
 */
public interface TreeOperationAware<NODE extends TreeNode> {

    /**
     * 执行操作之前调用
     */
    void before(TreeOperationEvent event, List<NODE> nodes);

    /**
     * 执行操作之后调用
     */
    void after(TreeOperationEvent event, List<NODE> nodes);

}