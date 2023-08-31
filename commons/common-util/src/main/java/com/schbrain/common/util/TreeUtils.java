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
 * Utils for tree
 *
 * @author panwangnan
 * @since 2023/7/29
 */
public class TreeUtils {

    public static <T, K, C extends Collection<T>> List<T> buildTree(C nodes,
                                                                    Function<T, K> keyExtractor,
                                                                    Function<T, K> parentKeyExtractor,
                                                                    BiConsumer<T, List<T>> childrenSetter,
                                                                    @Nullable K parentId) {
        return buildTree(nodes, keyExtractor, parentKeyExtractor, Function.identity(), childrenSetter, parentId);
    }

    public static <T, K, E, C extends Collection<T>> List<E> buildTree(C nodes,
                                                                       Function<T, K> keyExtractor,
                                                                       Function<T, K> parentKeyExtractor,
                                                                       Function<T, E> childMapper,
                                                                       BiConsumer<E, List<E>> childrenSetter,
                                                                       @Nullable K parentId) {
        return buildTree(nodes, keyExtractor, parentKeyExtractor, childMapper, childrenSetter, null, parentId);
    }

    public static <T, K, E, C extends Collection<T>> List<E> buildTree(C nodes,
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

    public static <T, E> List<E> getParents(E self, Collection<T> nodes, Function<T, E> keyMapper, Function<T, E> parentMapper, boolean includeSelf) {
        // toMap 不允许 value 为空，当 parentId 为空时，单独处理下
        Map<E, E> parentMap = Maps.newHashMapWithExpectedSize(nodes.size());
        for (T node : nodes) {
            parentMap.put(keyMapper.apply(node), parentMapper.apply(node));
        }
        return getParents(self, parentMap, includeSelf);
    }

    public static <T> List<T> getParents(T self, Map<T, T> parentMap, boolean includeSelf) {
        List<T> parentIds = new LinkedList<>();
        if (includeSelf) {
            parentIds.add(self);
        }
        if (MapUtils.isEmpty(parentMap)) {
            return parentIds;
        }
        return getParents(self, parentMap, parentIds);
    }

    public static <T, E> List<E> buildNodeList(Collection<T> tree, Function<T, Collection<T>> childGetter, Function<T, E> mapper) {
        List<E> nodes = new ArrayList<>();
        doBuildNodeList(tree, childGetter, mapper, nodes);
        return nodes;
    }

    private static <E, T> void doBuildNodeList(Collection<T> tree, Function<T, Collection<T>> childGetter, Function<T, E> mapper, List<E> nodes) {
        if (CollectionUtils.isEmpty(tree)) {
            return;
        }
        tree.forEach(node -> {
            nodes.add(mapper.apply(node));
            doBuildNodeList(childGetter.apply(node), childGetter, mapper, nodes);
        });
    }

    private static <T> List<T> getParents(T self, Map<T, T> parentMap, List<T> parentIds) {
        T parentId = parentMap.get(self);
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

    private static <E> void sort(List<E> data, Comparator<E> comparator) {
        if (comparator != null && CollectionUtils.isNotEmpty(data)) {
            ListUtil.sort(data, comparator);
        }
    }

}
