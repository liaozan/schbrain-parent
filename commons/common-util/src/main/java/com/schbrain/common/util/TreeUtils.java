package com.schbrain.common.util;

import cn.hutool.core.collection.ListUtil;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
