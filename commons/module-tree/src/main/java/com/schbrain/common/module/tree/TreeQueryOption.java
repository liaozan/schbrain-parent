package com.schbrain.common.module.tree;

/**
 * @author hzchengyi
 * @since 2019/1/21
 */
public class TreeQueryOption {

    /**
     * 包括节点自身
     */
    public static final int TREE_QUERY_SELF_INCLUDE = 0;
    /**
     * 不包括节点自身
     */
    public static final int TREE_QUERY_SELF_EXCLUDE = 1;
    /**
     * 只包含直接子节点
     */
    public static final int TREE_QUERY_CHILDREN_DIRECT = 0;
    /**
     * 包含所有子节点
     */
    public static final int TREE_QUERY_CHILDREN_ALL = 1;
    /**
     * 深度排序-从根到叶子节点
     */
    public static final int TREE_QUERY_DEPTH_ORDER_ROOT_2_LEAF = 0;
    /**
     * 深度排序-从叶子节点到根
     */
    public static final int TREE_QUERY_DEPTH_ORDER_LEAF_2_ROOT = 1;

    private int selfIncludeMode;
    private int childrenMode;
    private int depthOrder;

    private TreeQueryOption() {
    }

    public static TreeQueryOption instance() {
        TreeQueryOption option = new TreeQueryOption();
        option.selfIncludeMode = TREE_QUERY_SELF_EXCLUDE;
        option.childrenMode = TREE_QUERY_CHILDREN_ALL;
        option.depthOrder = TREE_QUERY_DEPTH_ORDER_ROOT_2_LEAF;
        return option;
    }

    public Integer getSelfIncludeMode() {
        return this.selfIncludeMode;
    }

    public Integer getChildrenMode() {
        return this.childrenMode;
    }

    public Integer getDepthOrder() {
        return this.depthOrder;
    }

    public TreeQueryOption queryIncludeSelf() {
        this.selfIncludeMode = TREE_QUERY_SELF_INCLUDE;
        return this;
    }

    public TreeQueryOption queryExcludeSelf() {
        this.selfIncludeMode = TREE_QUERY_SELF_EXCLUDE;
        return this;
    }

    public TreeQueryOption queryDirectChildren() {
        this.childrenMode = TREE_QUERY_CHILDREN_DIRECT;
        return this;
    }

    public TreeQueryOption queryAllChildren() {
        this.childrenMode = TREE_QUERY_CHILDREN_ALL;
        return this;
    }

    public TreeQueryOption depthOrderRoot2Leaf() {
        this.depthOrder = TREE_QUERY_DEPTH_ORDER_ROOT_2_LEAF;
        return this;
    }

    public TreeQueryOption depthOrderLeaf2Root() {
        this.depthOrder = TREE_QUERY_DEPTH_ORDER_LEAF_2_ROOT;
        return this;
    }

}