package com.schbrain.common.module.tree;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 结构化的树节点
 *
 * @author hzchengyi
 * @since 2019/1/21
 */
@Data
public class StructureTreeNode<NODE> implements Serializable {

    private static final long serialVersionUID = -7732621737666937981L;

    private NODE node;
    private List<StructureTreeNode<NODE>> children;

}