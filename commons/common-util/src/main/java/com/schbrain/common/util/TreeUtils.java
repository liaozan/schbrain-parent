package com.schbrain.common.util;

import cn.hutool.core.collection.ListUtil;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Utils to build tree
 *
 * @author panwangnan
 * @since 2023/7/29
 */
public class TreeUtils {

    public static <T, K> List<T> buildTree(List<T> nodes,
                                           Function<T, K> keyExtractor,
                                           Function<T, K> parentKeyExtractor,
                                           BiConsumer<T, List<T>> childrenSetter,
                                           @Nullable K parentId) {
        return buildTree(nodes, keyExtractor, parentKeyExtractor, Function.identity(), childrenSetter, parentId);
    }

    public static <T, K, E> List<E> buildTree(List<T> nodes,
                                              Function<T, K> keyExtractor,
                                              Function<T, K> parentKeyExtractor,
                                              Function<T, E> childMapper,
                                              BiConsumer<E, List<E>> childrenSetter,
                                              @Nullable K parentId) {
        return buildTree(nodes, keyExtractor, parentKeyExtractor, childMapper, childrenSetter, null, parentId);
    }

    public static <T, K, E> List<E> buildTree(List<T> nodes,
                                              Function<T, K> keyExtractor,
                                              Function<T, K> parentKeyExtractor,
                                              Function<T, E> childMapper,
                                              BiConsumer<E, List<E>> childrenSetter,
                                              @Nullable Comparator<E> childrenComparator,
                                              @Nullable K parentId) {
        if (CollectionUtils.isEmpty(nodes)) {
            return new ArrayList<>();
        }
        Map<K, List<T>> parentWithSubNodes = StreamUtils.groupBy(nodes, parentKeyExtractor, true);
        if (parentId == null) {
            // groupBy 不允许 key 为空，当 parentId 为空时，单独处理下
            List<T> subNodes = StreamUtils.filterToList(nodes, node -> parentKeyExtractor.apply(node) == null);
            parentWithSubNodes.put(null, subNodes);
        }
        return doBuildTree(keyExtractor, childrenSetter, childMapper, parentWithSubNodes, childrenComparator, parentId);
    }

    public static <T, E> List<E> getParents(E id, List<T> nodeList, Function<T, E> keyMapper, Function<T, E> parentMapper, boolean includeSelf) {
        // toMap 不允许 value 为空，当 parentId 为空时，单独处理下
        Map<E, E> parentMap = Maps.newHashMapWithExpectedSize(nodeList.size());
        for (T node : nodeList) {
            parentMap.put(keyMapper.apply(node), parentMapper.apply(node));
        }
        return getParents(id, parentMap, includeSelf);
    }

    public static <T> List<T> getParents(T id, Map<T, T> parentMap, boolean includeSelf) {
        List<T> parentIds = new LinkedList<>();
        if (includeSelf) {
            parentIds.add(id);
        }
        if (MapUtils.isEmpty(parentMap)) {
            return parentIds;
        }
        return getParents(id, parentMap, parentIds);
    }

    public static <T, E> List<E> buildNodeList(List<T> tree, Function<T, List<T>> childGetter, Function<T, E> mapper) {
        List<E> nodes = new ArrayList<>();
        if (CollectionUtils.isEmpty(tree)) {
            return nodes;
        }
        doBuildNodeList(tree, childGetter, mapper, nodes);
        return nodes;
    }

    private static <E, T> void doBuildNodeList(List<T> tree, Function<T, List<T>> childGetter, Function<T, E> mapper, List<E> nodes) {
        tree.forEach(node -> {
            nodes.add(mapper.apply(node));
            doBuildNodeList(childGetter.apply(node), childGetter, mapper, nodes);
        });
    }

    private static <T> List<T> getParents(T id, Map<T, T> parentMap, List<T> parentIds) {
        T parentId = parentMap.get(id);
        if (parentId == null) {
            return parentIds;
        }
        parentIds.add(0, parentId);
        return getParents(parentId, parentMap, parentIds);
    }

    private static <E, K, T> List<E> doBuildTree(Function<T, K> keyExtractor,
                                                 BiConsumer<E, List<E>> childrenSetter,
                                                 Function<T, E> childMapper,
                                                 Map<K, List<T>> parentWithSubNodes,
                                                 Comparator<E> childrenComparator,
                                                 K parentId) {
        List<T> subNodes = parentWithSubNodes.remove(parentId);
        List<E> treeList = StreamUtils.toList(subNodes, subNode -> {
            E child = childMapper.apply(subNode);
            List<E> children = doBuildTree(keyExtractor, childrenSetter, childMapper, parentWithSubNodes, childrenComparator, keyExtractor.apply(subNode));
            sort(children, childrenComparator);
            childrenSetter.accept(child, children);
            return child;
        });
        sort(treeList, childrenComparator);
        return treeList;
    }

    private static <E> void sort(List<E> dataList, Comparator<E> comparator) {
        if (comparator != null) {
            ListUtil.sort(dataList, Comparator.nullsLast(comparator));
        }
    }

}
