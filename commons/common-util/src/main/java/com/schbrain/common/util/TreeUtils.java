package com.schbrain.common.util;

import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
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
        if (CollectionUtils.isEmpty(nodes)) {
            return new ArrayList<>();
        }
        Map<K, List<T>> parentWithSubNodes = StreamUtils.groupBy(nodes, parentKeyExtractor, true);
        if (parentId == null) {
            // groupBy 不允许 key 为空，当 parentId 为空时，单独处理下
            List<T> subNodes = StreamUtils.filterToList(nodes, node -> parentKeyExtractor.apply(node) == null);
            parentWithSubNodes.put(null, subNodes);
        }
        return buildTree(keyExtractor, childrenSetter, childMapper, parentWithSubNodes, parentId);
    }

    private static <E, K, T> List<E> buildTree(Function<T, K> keyExtractor,
                                               BiConsumer<E, List<E>> childrenSetter,
                                               Function<T, E> childMapper,
                                               Map<K, List<T>> parentWithSubNodes,
                                               K parentId) {
        List<T> subNodes = parentWithSubNodes.get(parentId);
        return StreamUtils.toList(subNodes, subNode -> {
            E convertedSubNode = childMapper.apply(subNode);
            List<E> children = buildTree(keyExtractor, childrenSetter, childMapper, parentWithSubNodes, keyExtractor.apply(subNode));
            childrenSetter.accept(convertedSubNode, children);
            return convertedSubNode;
        });
    }

}
