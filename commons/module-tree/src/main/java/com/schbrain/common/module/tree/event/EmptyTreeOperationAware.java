package com.schbrain.common.module.tree.event;

import com.schbrain.common.module.tree.TreeNode;

import java.util.List;

/**
 * Created by hzchengyi on 2019/1/21.
 */
public class EmptyTreeOperationAware<NODE extends TreeNode> implements TreeOperationAware<NODE> {

    @Override
    public void before(TreeOperationEvent event, List<NODE> nodes) {

    }

    @Override
    public void after(TreeOperationEvent event, List<NODE> nodes) {

    }

}