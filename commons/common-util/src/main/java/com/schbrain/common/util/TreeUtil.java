package com.schbrain.common.util;

import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 树工具类
 */
public class TreeUtil {

    /**
     * 通过列表构建一个树状结构
     *
     * @param nodes 节点列表
     * @param idGetMethod 获取当前节点ID的方法
     * @param parentIdGetMethod 获取父节点ID的方法
     * @param setParentMethod 设置子节点列表的方法
     * @param parentId 父节点ID，可以为null
     */
    public static <T, K> List<T> buildTree(List<T> nodes,
                                           Function<T, K> idGetMethod,
                                           Function<T, K> parentIdGetMethod,
                                           BiConsumer<T, List<T>> setParentMethod,
                                           K parentId) {
        if (CollectionUtils.isEmpty(nodes)) {
            return nodes;
        }
        Map<K, List<T>> parentGroupMap = new HashMap<>();
        for (T node : nodes) {
            K key = parentIdGetMethod.apply(node);
            List<T> subNodes = parentGroupMap.getOrDefault(key, new ArrayList<>());
            subNodes.add(node);
            parentGroupMap.put(key, subNodes);
        }
        return buildTree(idGetMethod, setParentMethod, parentId, parentGroupMap);
    }

    /**
     * 递归通过列表构建一个树状结构
     *
     * @param idGetMethod 获取当前节点ID的方法
     * @param setParentMethod 设置子节点列表的方法
     * @param parentId 父节点ID，可以为null
     * @param parentGroupMap 存放节点下层节点列表的map
     */
    private static <T, K> List<T> buildTree(Function<T, K> idGetMethod,
                                            BiConsumer<T, List<T>> setParentMethod,
                                            K parentId, Map<K, List<T>> parentGroupMap) {
        List<T> nodes = parentGroupMap.get(parentId);
        if (CollectionUtils.isEmpty(nodes)) {
            return Collections.emptyList();
        }
        nodes.forEach(item -> setParentMethod.accept(item, buildTree(idGetMethod, setParentMethod, idGetMethod.apply(item), parentGroupMap)));
        return nodes;
    }

}





