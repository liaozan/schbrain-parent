package com.schbrain.common.module.tree;

import com.github.pagehelper.Page;
import com.schbrain.common.module.tree.constant.TreeConstant;
import com.schbrain.common.module.tree.dao.TreeNodeDao;
import com.schbrain.common.module.tree.event.TreeOperationAware;
import com.schbrain.common.module.tree.event.TreeOperationEvent;
import com.schbrain.framework.dao.BaseDao;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 节点树处理器
 *
 * @author hzchengyi
 * @since 2019/1/21
 */
public class TreeNodeProcessor<NODE extends TreeNode> {

    private final TreeNodeDao<NODE> treeNodeDao;

    private final TreeOperationAware<NODE> operationHandler;

    public TreeNodeProcessor(BaseDao<NODE> baseDao, TreeOperationAware<NODE> operationHandler) {
        this.treeNodeDao = new TreeNodeDao<>(baseDao);
        this.operationHandler = operationHandler;
    }

    public List<NODE> listByNode(NODE node) {
        return treeNodeDao.listByNode(node);
    }

    public NODE getById(Long nodeId) {
        return treeNodeDao.getById(nodeId);
    }

    public List<NODE> listByIds(List<Long> nodeIds) {
        return treeNodeDao.listByIds(nodeIds);
    }

    public Integer getDepth(Long nodeId) {
        NODE node = treeNodeDao.getById(nodeId);
        if (node == null) {
            return null;
        }
        return node.getDepth();
    }

    public Integer countChildren(Long relateId, Long nodeId, TreeQueryOption option) {
        NODE node = treeNodeDao.getById(nodeId);
        if (node == null) {
            return null;
        }

        return treeNodeDao.countChildren(relateId, node, option);
    }

    public Map<Long, Integer> countChildren(Long relateId, List<Long> nodeIds, TreeQueryOption option) {
        Map<Long, Integer> result = new HashMap<>();
        for (Long nodeId : nodeIds) {
            result.put(nodeId, countChildren(relateId, nodeId, option));
        }
        return result;
    }

    @Transactional
    public NODE addNode(Long parentId, Long preBroNodeId, NODE newNode) {
        operationHandler.before(TreeOperationEvent.ADD, Collections.singletonList(newNode));

        treeNodeDao.addNode(parentId, preBroNodeId, newNode);

        operationHandler.after(TreeOperationEvent.ADD, Collections.singletonList(newNode));
        return newNode;
    }

    @Transactional
    public NODE createTree(NODE newNode) {
        if (treeNodeDao.countByRelateId(newNode.getRelateId()) > 0) {
            // relateId已经存在，不能创建新树
            return null;
        }

        return addNode(null, null, newNode);
    }

    public Boolean isSubNode(NODE parent, NODE child) {
        return parent.getRelateId().equals(child.getRelateId())
                && parent.getLft() < child.getLft()
                && parent.getRgt() > child.getRgt();
    }

    public Boolean isRelateTo(Long relateId, List<NODE> nodes) {
        for (NODE node : nodes) {
            if (!node.getRelateId().equals(relateId)) {
                return false;
            }
        }
        return true;
    }

    public Boolean areBros(List<NODE> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return false;
        }

        Long parentId = nodes.get(0).getParentId();
        for (NODE node : nodes) {
            if (!parentId.equals(node.getParentId())) {
                return false;
            }
        }
        return true;
    }

    @Transactional
    public Boolean moveBros(List<NODE> nodes, NODE parent) {
        operationHandler.before(TreeOperationEvent.MOVE, nodes);

        if (null == nodes || nodes.isEmpty() || null == parent) {
            return false;
        }
        if (nodes.get(0).getParentId() <= 0) {
            return false;
        }
        if (!areBros(nodes)) {
            return false;
        }
        Long relateId = parent.getRelateId();
        if (null == relateId) {
            return false;
        }
        if (!isRelateTo(relateId, nodes)) {
            return false;
        }
        NODE srcParent = treeNodeDao.getById(nodes.get(0).getParentId());
        if (null == srcParent) {
            return false;
        }
        NODE targetParent = parent;
        // 同级目录下移动，不做任何操作
        if (srcParent.getId().equals(targetParent.getId())) {
            return true;
        }
        List<Long> nodeIdList = new ArrayList<>(nodes.size());
        for (NODE node : nodes) {
            nodeIdList.add(node.getId());
            // 判断新的parent不是node的子节点以及parent不是节点本身
            if (isSubNode(node, targetParent) || node.getId().equals(targetParent.getId())) {
                return false;
            }
        }
        int totalNodes = 0;
        Map<Long, Integer> nodeCountMap = new HashMap<>(nodes.size());
        for (Long nodeId : nodeIdList) {
            Integer nodeCount = countChildren(relateId, nodeId, TreeQueryOption.instance().queryIncludeSelf());
            if (null == nodeCount) {
                nodeCountMap.put(nodeId, 0);
            } else {
                int intValue = nodeCount;
                nodeCountMap.put(nodeId, intValue);
                totalNodes += intValue;
            }
        }
        if (nodes.size() > 1) {
            // 将节点进行排序
            nodes.sort(Comparator.comparingInt(TreeNode::getLft));
        }
        NODE farLeftNode = nodes.get(0);
        TreeQueryOption queryOption = TreeQueryOption.instance().queryExcludeSelf().queryDirectChildren();
        Integer srcParentChildrenCount = treeNodeDao.countChildren(relateId, srcParent, queryOption);
        // 如果不是移动srcParent下面的所有节点
        if (srcParentChildrenCount > nodes.size()) {
            // 将所有节点按顺序移动到最右边
            for (NODE node : nodes) {
                treeNodeDao.moveNodeToFarRight(node.getId(), nodeCountMap.get(node.getId()), srcParent);
            }
            // 这个时候farLeftNode发生了变化，重新获取
            farLeftNode = treeNodeDao.getById(nodes.get(0).getId());
            // targetParent也可能发生了变化，重新获取
            targetParent = treeNodeDao.getById(targetParent.getId());
        }

        Long tempRelateId = farLeftNode.getId() * -1;
        // 将相关节点独立出来
        for (NODE node : nodes) {
            // 将节点的relateId更新成tempRelateId
            TreeQueryOption option = TreeQueryOption.instance().queryIncludeSelf();
            treeNodeDao.updateSubTreeBySql("SET relate_id = " + tempRelateId, relateId, node.getId(), option);
        }
        // 把parentId改成targetParent的id
        treeNodeDao.updateParentId(nodeIdList, targetParent.getId());
        // 执行移动
        move(relateId, tempRelateId, farLeftNode, totalNodes, srcParent, targetParent);
        operationHandler.after(TreeOperationEvent.MOVE, nodes);
        return true;
    }

    public Boolean isRoot(NODE node) {
        return node.getParentId().equals(TreeConstant.ROOT_PARENT_ID);
    }

    @Transactional
    public Boolean delete(Long relateId, List<Long> nodeIdList) {
        List<NODE> nodeList = listByIds(nodeIdList);
        if (CollectionUtils.isEmpty(nodeList)) {
            return false;
        }
        if (nodeList.size() != nodeIdList.size()) {
            return false;
        }
        for (NODE node : nodeList) {
            if (!node.getRelateId().equals(relateId)) {
                return false;
            }
        }
        operationHandler.before(TreeOperationEvent.DELETE, nodeList);

        for (NODE node : nodeList) {
            delete(relateId, node);
        }

        operationHandler.after(TreeOperationEvent.DELETE, nodeList);
        return true;
    }

    public List<NODE> listParent(Long relateId, NODE node, TreeQueryOption option) {
        return treeNodeDao.listParent(relateId, node, option);
    }

    public StructureTreeNode<NODE> convert2StructureTree(Long relateId) {
        List<NODE> nodes = batchListNode(relateId);
        Map<Long, List<NODE>> nodeSubNodeMap = getNodeSubNodeMap(nodes);
        NODE root = nodeSubNodeMap.get(TreeConstant.ROOT_PARENT_ID).get(0);

        return constructTree(root, nodeSubNodeMap);
    }

    public Boolean update(Long relateId, NODE node) {
        node.setValidate(null);
        node.setDeleteVersion(null);
        node.setLft(null);
        node.setRgt(null);
        node.setRelateId(null);
        node.setParentId(null);
        return treeNodeDao.updateNodeById(node);
    }

    public Integer getTreeHeight(Long relateId, Long nodeId) {
        NODE node = treeNodeDao.getById(nodeId);
        if (node == null) {
            return 0;
        }
        NODE maxDepthSubLeaf = treeNodeDao.getMaxDepthSubLeaf(relateId, node);
        if (maxDepthSubLeaf == null) {
            return 0;
        }

        return maxDepthSubLeaf.getDepth() - node.getDepth() + 1;
    }

    public Page<NODE> pageByParent(Long parentId, Integer pageIndex, Integer pageSize) {
        return treeNodeDao.pageByParent(parentId, pageIndex, pageSize);
    }

    public List<NODE> listByParent(Long parentId) {
        return treeNodeDao.listByParent(parentId);
    }

    public List<NODE> listByParent(Long parentId, Integer levelCount) {
        return treeNodeDao.listByParent(parentId, levelCount);
    }

    @Transactional
    public void repairLeftAndRight(Long rootId) {
        doRepairLeftAndRight(getById(rootId), null, null, null, null);
    }

    public int updateByIds(NODE updateNode, List<Long> nodeIds) {
        // 此接口不允许更新树结构相关的字段
        allTreeFiledSetNull(updateNode);
        return treeNodeDao.updateNodeByIds(updateNode, nodeIds);
    }

    public List<NODE> listNode(List<Long> nodeIds) {
        return treeNodeDao.listByIds(nodeIds);
    }

    private void move(Long relateId, Long tempRelateId, NODE farLeftNode, int nodeCount, NODE srcParent, NODE targetParent) {
        int minLeft, maxLeft, minRight, maxRight, lrDiff;
        NODE farRightOfTargetParent = treeNodeDao.getFarRightNode(targetParent.getRelateId(), targetParent.getId());
        if ((farRightOfTargetParent == null && farLeftNode.getLft() < targetParent.getLft()) ||
                (farRightOfTargetParent != null && farLeftNode.getLft() < farRightOfTargetParent.getRgt())) {
            // 往右边移
            minLeft = srcParent.getRgt();
            maxLeft = targetParent.getRgt();
            if (null != farRightOfTargetParent) {
                maxLeft = farRightOfTargetParent.getRgt();
            }
            minRight = srcParent.getRgt();
            maxRight = targetParent.getRgt();
            lrDiff = -1 * nodeCount * 2;
        } else {
            // 往左边移
            minLeft = targetParent.getRgt();
            if (null != farRightOfTargetParent) {
                minLeft = farRightOfTargetParent.getRgt();
            }
            maxLeft = srcParent.getRgt();
            minRight = targetParent.getRgt();
            maxRight = srcParent.getRgt();
            lrDiff = nodeCount * 2;
        }

        treeNodeDao.updateLeftWithRang(relateId, lrDiff, minLeft, false, maxLeft, false);
        treeNodeDao.updateRightWithRang(relateId, lrDiff, minRight, true, maxRight, false);
        // 执行上面两句操作以后，targetParent和farRightOfTargetParent的left和right都发生了变化，所以需要重新获取
        if (null == farRightOfTargetParent) {
            targetParent = treeNodeDao.getById(targetParent.getId());
            lrDiff = targetParent.getLft() - farLeftNode.getLft() + 1;
        } else {
            farRightOfTargetParent = treeNodeDao.getFarRightNode(targetParent.getRelateId(), targetParent.getId());
            lrDiff = farRightOfTargetParent.getRgt() - farLeftNode.getLft() + 1;
        }
        // 更新要移动节点的left、right、depth，并恢复relateId
        int depthDiff = targetParent.getDepth() - srcParent.getDepth();
        treeNodeDao.updateLRAndDepthAndRelateIdWithRelateId(tempRelateId, relateId, lrDiff, depthDiff);
    }

    private void delete(Long relateId, NODE node) {
        treeNodeDao.deleteSubTree(relateId, node);
    }

    private List<NODE> batchListNode(Long relateId) {
        int pageIndex = 1;
        Integer pageSize = 500;
        Page<NODE> page = treeNodeDao.page(relateId, pageIndex, pageSize, "id desc");
        List<NODE> result = new LinkedList<>(page);
        while (pageIndex < page.getPages()) {
            page = treeNodeDao.page(relateId, ++pageIndex, pageSize, "id desc");
            result.addAll(page);
        }
        return result;
    }

    private Map<Long, List<NODE>> getNodeSubNodeMap(List<NODE> nodes) {
        Map<Long, List<NODE>> map = new HashMap<>();
        for (NODE node : nodes) {
            if (map.containsKey(node.getParentId())) {
                map.get(node.getParentId()).add(node);
            } else {
                List<NODE> nodeLevel = new LinkedList<>();
                nodeLevel.add(node);
                map.put(node.getParentId(), nodeLevel);
            }
        }

        return map;
    }

    private StructureTreeNode<NODE> constructTree(NODE node, Map<Long, List<NODE>> nodeSubNodeMap) {
        StructureTreeNode<NODE> structureNode = new StructureTreeNode<>();
        structureNode.setNode(node);

        if (nodeSubNodeMap.containsKey(node.getId())) {
            List<NODE> childrenNode = nodeSubNodeMap.get(node.getId());
            List<StructureTreeNode<NODE>> children = new ArrayList<>();
            for (NODE childNode : childrenNode) {
                children.add(constructTree(childNode, nodeSubNodeMap));
            }
            children.sort(Comparator.comparingInt(e -> e.getNode().getLft()));
            structureNode.setChildren(children);
        } else {
            structureNode.setChildren(new LinkedList<>());
        }

        return structureNode;
    }

    private void doRepairLeftAndRight(NODE node, Long parentId, Integer parentLeft, Integer parentDepth, NODE preBro) {
        int left, right, depth;
        if (null == parentId) {
            left = 1;
            depth = 1;
        } else {
            left = parentLeft + 1;
            depth = parentDepth + 1;
        }
        if (null != preBro) {
            left = preBro.getRgt() + 1;
        }
        List<NODE> children = treeNodeDao.listByParent(node.getId());
        if (null == children || children.isEmpty()) {
            right = left + 1;
        } else {
            NODE preNode = null;
            for (NODE child : children) {
                doRepairLeftAndRight(child, node.getId(), left, depth, preNode);
                preNode = child;
            }
            right = preNode.getRgt() + 1;
        }
        if (left != node.getLft() || right != node.getRgt() || depth != node.getDepth()) {
            treeNodeDao.updateLRAndDepth(left, right, depth, node.getId());
            node.setLft(left);
            node.setRgt(right);
            node.setDepth(depth);
        }
    }

    private void allTreeFiledSetNull(NODE node) {
        node.setValidate(null);
        node.setRelateId(null);
        node.setParentId(null);
        node.setDepth(null);
        node.setDeleteVersion(null);
        node.setLft(null);
        node.setRgt(null);
    }

}
