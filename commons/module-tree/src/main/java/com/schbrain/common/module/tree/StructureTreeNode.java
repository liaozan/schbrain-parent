package com.schbrain.common.module.tree;

import lombok.Data;

import java.util.List;

/**
 * 结构化的树节点
 *
 * @author hzchengyi
 * @since 2019/1/21
 */
@Data
public class StructureTreeNode<NODE> {

    private NODE node;

    private List<StructureTreeNode<NODE>> children;

}
