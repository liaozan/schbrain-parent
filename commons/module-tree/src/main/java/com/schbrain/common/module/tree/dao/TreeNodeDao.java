package com.schbrain.common.module.tree.dao;

import com.github.pagehelper.Page;
import com.schbrain.common.enums.ValidateEnum;
import com.schbrain.common.module.tree.TreeNode;
import com.schbrain.common.module.tree.TreeQueryOption;
import com.schbrain.common.module.tree.constant.TreeConstant;
import com.schbrain.framework.dao.BaseDao;
import com.schbrain.framework.dao.util.SQLUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author hzchengyi
 * @since 2019/1/21
 */
public class TreeNodeDao<NODE extends TreeNode> {

    private final BaseDao<NODE> baseDao;

    public TreeNodeDao(BaseDao<NODE> baseDao) {
        this.baseDao = baseDao;
    }

    public List<NODE> listByNode(NODE node) {
        return baseDao.listByObject(node);
    }

    public NODE getById(Long id) {
        return baseDao.getOneByCondition("id = #{id} AND validate = #{validate}", id, ValidateEnum.VALID.getValue());
    }

    public List<NODE> listByIds(List<Long> ids) {
        return baseDao.listByCondition("validate = #{validate} AND " + SQLUtil.buidInClause("id", Long.class, ids), ValidateEnum.VALID.getValue());
    }

    public Integer countParent(Long relateId, NODE node, TreeQueryOption option) {
        StringBuilder condition = new StringBuilder();
        List<Object> params = new LinkedList<>();

        params.add(relateId);
        condition
                .append("relate_id = #{relateId} ")
                .append(" AND ").append(getParentLeftRange(node, option, params))
                .append(" AND ").append(getParentRightRange(node, option, params))
                .append(" AND validate = #{validate}");
        params.add(ValidateEnum.VALID.getValue());
        return baseDao.getCountByCondition(condition.toString(), params.toArray());
    }

    public Integer countChildren(Long relateId, NODE node, TreeQueryOption option) {
        StringBuilder condition = new StringBuilder();
        List<Object> params = new LinkedList<>();

        params.add(relateId);
        condition
                .append("relate_id = #{relateId}")
                .append(" AND ").append(getChildrenLeftRange(node, option, params))
                .append(" AND ").append(getChildrenRightRange(node, option, params))
                .append(" AND validate = #{validate}");
        params.add(ValidateEnum.VALID.getValue());
        if (option.getChildrenMode().equals(TreeQueryOption.TREE_QUERY_CHILDREN_DIRECT)) {
            // 只查询直接子节点
            condition.append(" AND parent_id = #{parentId} ");
            params.add(node.getId());
        }

        return baseDao.getCountByCondition(condition.toString(), params.toArray());
    }

    public Integer countByRelateId(Long relateId) {
        return baseDao.getCountByCondition("relate_id = #{relateId} AND validate = #{validate}", relateId, ValidateEnum.VALID.getValue());
    }

    public boolean addNode(Long parentId, Long preBroNodeId, NODE newNode) {
        newNode.setValidate(ValidateEnum.VALID.getValue());
        newNode.setDeleteVersion(TreeConstant.NODE_DELETE_VERSION_DEFAULT);
        NODE parent = null;
        if (null != parentId) {
            parent = getById(parentId);
        }
        NODE preBroNode = null;
        if (null != preBroNodeId) {
            preBroNode = getById(preBroNodeId);
        }

        // 如果parent和preBroNode都为null，说明是根节点
        if (null == parent && null == preBroNode) {
            newNode.setLft(1);
            newNode.setRgt(2);
            newNode.setDepth(1);
            newNode.setParentId(TreeConstant.ROOT_PARENT_ID);
            return baseDao.add(newNode);
        }
        // 如果parent不为null并且preBroNode为null，则代表该节点直接为该父节点下的第一个子节点
        if (null == preBroNode) {
            // 说明是父节点的第一个子节点
            newNode.setLft(parent.getLft() + 1);
            newNode.setRgt(newNode.getLft() + 1);
            newNode.setDepth(parent.getDepth() + 1);
            newNode.setParentId(parent.getId());
            // 增加父节点右边所有节点的left和right，留出空位
            increaseNodesLeftAndRight(newNode.getRelateId(), parent.getLft(), parent.getLft(), false, 2);
            // 添加节点
            return baseDao.add(newNode);
        }
        // 处理非唯一叶子节点的情况
        newNode.setLft(preBroNode.getRgt() + 1);
        newNode.setRgt(newNode.getLft() + 1);
        newNode.setDepth(preBroNode.getDepth());
        newNode.setParentId(preBroNode.getParentId());
        // 增加兄弟节点右边所有节点的left和right，留出空位
        increaseNodesLeftAndRight(preBroNode.getRelateId(), preBroNode.getRgt(), preBroNode.getRgt(), false, 2);
        // 添加节点
        return baseDao.add(newNode);
    }

    public int deleteSubTree(Long relateId, NODE node) {
        if (null == node) {
            return 0;
        }
        if (!relateId.equals(node.getRelateId())) {
            return 0;
        }
        Long deleteVersion = System.currentTimeMillis();

        String updateSql = "UPDATE " + baseDao.getTableName()
                + " SET validate = #{validate} , delete_version = #{deleteVersion} WHERE"
                + " relate_id = #{relateId} AND lft >= #{lft} AND rgt <= #{rgt} AND validate = #{validate}";
        int nodeCount = baseDao.updateByCompleteSql(
                updateSql, ValidateEnum.INVALID.getValue(), deleteVersion,
                node.getRelateId(), node.getLft(), node.getRgt(), ValidateEnum.VALID.getValue());
        // 更新右边节点的left和right
        return decreaseNodesLeftAndRight(node.getRelateId(), node.getRgt(), node.getRgt(), nodeCount * 2);
    }

    public List<NODE> listParent(Long relateId, NODE node, TreeQueryOption option) {
        StringBuilder condition = new StringBuilder();
        List<Object> params = new LinkedList<>();
        params.add(relateId);
        condition
                .append("relate_id = #{relateId}")
                .append(" AND ").append(getParentLeftRange(node, option, params))
                .append(" AND ").append(getParentRightRange(node, option, params))
                .append(" AND validate = #{validate} ");
        params.add(ValidateEnum.VALID.getValue());
        condition.append(getOrderBy(option));
        return baseDao.listByCondition(condition.toString(), params.toArray());
    }

    public Page<NODE> page(Long relateId, Integer pageIndex, Integer pageSize, String orderCondition) {
        String condition = "relate_id = #{relateId} AND validate = #{validate}";
        if (StringUtils.isNotBlank(orderCondition)) {
            condition += " order by " + orderCondition;
        }
        return baseDao.pageByCondition(pageIndex, pageSize, condition, relateId, ValidateEnum.VALID.getValue());
    }

    public boolean updateNodeById(NODE node) {
        return baseDao.updateById(node);
    }

    public NODE getMaxDepthSubLeaf(Long relateId, NODE node) {
        String condition = " relate_id = #{relateId} AND lft >= #{lft} AND rgt <= #{rgt} AND validate = #{validate} ORDER BY depth DESC";
        return baseDao.getOneByCondition(condition, relateId, node.getLft(), node.getRgt(), ValidateEnum.VALID.getValue());
    }

    public int updateSubTreeBySql(String updateSql, Long relateId, Long nodeId, TreeQueryOption option) {
        NODE node = getById(nodeId);
        if (node == null) {
            return 0;
        }

        List<Object> params = new LinkedList<>();
        String sql = "UPDATE " + baseDao.getTableName() + " " + updateSql
                + " WHERE " + getChildrenLeftRange(node, option, params)
                + " AND " + getChildrenRightRange(node, option, params)
                + " AND relate_id = #{relateId} AND validate = #{validate}";
        params.add(relateId);
        params.add(ValidateEnum.VALID.getValue());
        return baseDao.updateByCompleteSql(sql, params.toArray());
    }

    public NODE getFarRightNode(Long relateId, Long parentId) {
        String condition = "relate_id = #{relateId} AND parent_id = #{parentId} AND validate = #{validate} ORDER BY lft DESC limit 1";
        return baseDao.getOneByCondition(condition, relateId, parentId, ValidateEnum.VALID.getValue());
    }

    public void moveNodeToFarRight(Long nodeId, Integer nodeCount, NODE parent) {
        NODE node = getById(nodeId);
        // 1.更新节点及所有子节点的relateId
        Long tempRelateId = nodeId * -1;
        StringBuilder set = new StringBuilder();
        set.append("SET relate_id = ").append(tempRelateId);
        updateSubTreeBySql(set.toString(), parent.getRelateId(), nodeId, TreeQueryOption.instance().queryIncludeSelf());
        // 2.右边的节点更新left和right
        set.delete(0, set.length());
        set.append("UPDATE ")
                .append(baseDao.getTableName()).append(" ")
                .append("SET lft = lft - ").append(nodeCount * 2)
                .append(", rgt = rgt - ").append(nodeCount * 2)
                .append(" WHERE lft > #{lft} AND rgt < #{rgt} AND relate_id = #{relateId} AND validate = #{validate}");
        baseDao.updateByCompleteSql(set.toString(), node.getRgt(), parent.getRgt(), parent.getRelateId(), ValidateEnum.VALID.getValue());
        // 3.更新节点及所有子节点的left和right，恢复relateId
        set.delete(0, set.length());
        NODE farRightNode = getFarRightNode(parent.getRelateId(), parent.getId());
        int increment = farRightNode.getRgt() - node.getLft() + 1;
        updateLRAndDepthAndRelateIdWithRelateId(tempRelateId, parent.getRelateId(), increment, 0);
    }

    public int updateLeftWithRang(Long relateId, int diff, Integer minLeft, boolean includeMinLeft, Integer maxLeft, boolean includeMaxLeft) {
        String sql = "UPDATE " + baseDao.getTableName() + " " +
                "SET lft = lft + " + diff + " WHERE lft " +
                (includeMinLeft ? ">=" : ">") + " " + minLeft + " AND lft " +
                (includeMaxLeft ? "<=" : "<") + " " + maxLeft +
                " AND relate_id = #{relateId} AND validate = #{validate}";
        return baseDao.updateByCompleteSql(sql, relateId, ValidateEnum.VALID.getValue());
    }

    public int updateRightWithRang(Long relateId, int diff, Integer minRight, boolean includeMinRight,
                                   Integer maxRight, boolean includeMaxRight) {
        String sql = "UPDATE " + baseDao.getTableName() + " " +
                "SET rgt = rgt + " + diff + " WHERE rgt " +
                (includeMinRight ? ">=" : ">") + " " + minRight + " AND rgt " +
                (includeMaxRight ? "<=" : "<") + " " + maxRight +
                " AND relate_id = #{relateId} AND validate = #{validate}";
        return baseDao.updateByCompleteSql(sql, relateId, ValidateEnum.VALID.getValue());
    }

    public int updateLRAndDepthAndRelateIdWithRelateId(Long oldRelateId, Long newRelateId, int lrDiff, int depthDiff) {
        String sql = "UPDATE " + baseDao.getTableName() + " "
                + "SET relate_id = #{relateId}" + ", lft = lft + " + lrDiff
                + ", rgt = rgt + " + lrDiff
                + ", depth = depth + " + depthDiff
                + " WHERE relate_id = #{relateId} AND validate = #{validate}";
        return baseDao.updateByCompleteSql(sql, newRelateId, oldRelateId, ValidateEnum.VALID.getValue());
    }

    public Page<NODE> pageByParent(Long parentId, Integer pageIndex, Integer pageSize) {
        return baseDao.pageByCondition(pageIndex, pageSize, "parent_id = #{parentId} order by lft", parentId);
    }

    public int updateParentId(List<Long> nodeIds, Long parentId) {
        String sql = "UPDATE " + baseDao.getTableName() + " SET parent_id = #{parentId} WHERE " + SQLUtil.buidInClause("id", Long.class, nodeIds);
        return baseDao.updateByCompleteSql(sql, parentId);
    }

    public List<NODE> listByParent(Long parentId) {
        String condition = "parent_id = #{parentId} AND validate = #{validate} order by lft ASC";
        return baseDao.listByCondition(condition, parentId, ValidateEnum.VALID.getValue());
    }

    public List<NODE> listByParent(Long parentId, Integer levelCount) {
        NODE parent = getById(parentId);
        if (null == parent) {
            return Collections.emptyList();
        }
        int depth = parent.getDepth() + levelCount;
        String condition = "relate_id = #{relateId} AND lft > #{lft} AND rgt < #{rgt} AND depth <= #{depth} AND validate = #{validate} order by lft ASC";
        return baseDao.listByCondition(condition, parent.getRelateId(), parent.getLft(), parent.getRgt(), depth, ValidateEnum.VALID.getValue());
    }

    public int updateLRAndDepth(Integer left, Integer right, Integer depth, Long nodeId) {
        String sql = "UPDATE " + baseDao.getTableName() + " SET lft = #{lft}, rgt = #{rgt}, depth = #{depth} WHERE id = #{id}";
        return baseDao.updateByCompleteSql(sql, left, right, depth, nodeId);
    }

    public int updateNodeByIds(NODE updateNode, List<Long> nodeIds) {
        return baseDao.updateByCondition(updateNode, SQLUtil.buidInClause("id", Long.class, nodeIds));
    }

    private String getParentLeftRange(NODE node, TreeQueryOption option, List<Object> params) {
        params.add(node.getLft());
        switch (option.getSelfIncludeMode()) {
            case TreeQueryOption.TREE_QUERY_SELF_INCLUDE:
                return " lft <= #{lft} ";
            case TreeQueryOption.TREE_QUERY_SELF_EXCLUDE:
                return " lft < #{lft} ";
            default:
                // never goes here
                throw new IllegalArgumentException("param option invalid.");
        }
    }

    private String getParentRightRange(NODE node, TreeQueryOption option, List<Object> params) {
        params.add(node.getRgt());
        switch (option.getSelfIncludeMode()) {
            case TreeQueryOption.TREE_QUERY_SELF_INCLUDE:
                return " rgt >= #{rgt} ";
            case TreeQueryOption.TREE_QUERY_SELF_EXCLUDE:
                return " rgt > #{rgt} ";
            default:
                // never goes here
                throw new IllegalArgumentException("param option invalid.");
        }
    }

    private String getChildrenLeftRange(NODE node, TreeQueryOption option, List<Object> params) {
        StringBuilder condition = new StringBuilder();
        params.add(node.getLft());
        switch (option.getSelfIncludeMode()) {
            case TreeQueryOption.TREE_QUERY_SELF_INCLUDE:
                condition.append(" lft >= #{lft} ");
                break;
            case TreeQueryOption.TREE_QUERY_SELF_EXCLUDE:
                condition.append(" lft > #{lft} ");
                break;
        }

        return condition.toString();
    }

    private String getChildrenRightRange(NODE node, TreeQueryOption option, List<Object> params) {
        StringBuilder condition = new StringBuilder();
        params.add(node.getRgt());
        switch (option.getSelfIncludeMode()) {
            case TreeQueryOption.TREE_QUERY_SELF_INCLUDE:
                condition.append(" rgt <= #{rgt} ");
                break;
            case TreeQueryOption.TREE_QUERY_SELF_EXCLUDE:
                condition.append(" rgt < #{rgt} ");
                break;
        }

        return condition.toString();
    }

    /**
     * 如果是父节点的left和right，则includeRight为true，因为父节点的right值也需要更新；
     * 如果是兄弟节点的left和right，则include为false，因为系统节点的right值不需要更新。
     */
    private int increaseNodesLeftAndRight(Long relateId, Integer left, Integer right, boolean includeRight, Integer increment) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ")
                .append(baseDao.getTableName())
                .append(" SET lft = lft + ").append(increment)
                .append(" WHERE relate_id = #{relateId} AND lft > #{lft} AND validate = #{validate}");
        baseDao.updateByCompleteSql(sql.toString(), relateId, left, ValidateEnum.VALID.getValue());
        sql.delete(0, sql.length());
        sql.append("UPDATE ").append(baseDao.getTableName())
                .append(" SET rgt = rgt + ").append(increment)
                .append(" WHERE relate_id = #{relateId} AND rgt ")
                .append(includeRight ? ">=" : ">")
                .append(" #{rgt} AND validate = #{validate}");
        return baseDao.updateByCompleteSql(sql.toString(), relateId, right, ValidateEnum.VALID.getValue());
    }

    private int decreaseNodesLeftAndRight(Long relateId, Integer left, Integer right, Integer decrement) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ")
                .append(baseDao.getTableName())
                .append(" set lft = lft - ").append(decrement)
                .append(" WHERE relate_id = #{relateId} AND lft > #{lft} AND validate = #{validate}");
        baseDao.updateByCompleteSql(sql.toString(), relateId, left, ValidateEnum.VALID.getValue());
        sql.delete(0, sql.length());
        sql.append("UPDATE ").append(baseDao.getTableName())
                .append(" set rgt = rgt - ").append(decrement)
                .append(" WHERE relate_id = #{relateId} AND rgt > #{rgt} AND validate = #{validate}");
        return baseDao.updateByCompleteSql(sql.toString(), relateId, right, ValidateEnum.VALID.getValue());
    }

    private String getOrderBy(TreeQueryOption option) {
        switch (option.getDepthOrder()) {
            case TreeQueryOption.TREE_QUERY_DEPTH_ORDER_ROOT_2_LEAF:
                return " ORDER BY lft ASC";
            case TreeQueryOption.TREE_QUERY_DEPTH_ORDER_LEAF_2_ROOT:
                return " ORDER BY lft DESC";
            default:
                // never goes here
                throw new RuntimeException("查询错误");
        }
    }

}
