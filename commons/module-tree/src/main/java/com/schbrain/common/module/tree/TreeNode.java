package com.schbrain.common.module.tree;

/**
 * @author hzchengyi
 * @since 2019/1/21
 */
public interface TreeNode {

    Long getId();

    void setId(Long id);

    Long getRelateId();

    void setRelateId(Long relateId);

    Long getParentId();

    void setParentId(Long parentId);

    Integer getDepth();

    void setDepth(Integer depth);

    Integer getLft();

    void setLft(Integer lft);

    Integer getRgt();

    void setRgt(Integer rgt);

    Integer getValidate();

    void setValidate(Integer validate);

    Long getDeleteVersion();

    void setDeleteVersion(Long deleteVersion);

    default boolean isLeaf() {
        return getRgt() == getLft() + 1;
    }

}