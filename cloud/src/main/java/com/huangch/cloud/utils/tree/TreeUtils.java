package com.huangch.cloud.utils.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author huangch
 * @since 2023-11-16
 */
@SuppressWarnings("unchecked")
public class TreeUtils {

    public static <N, TN, R, C extends List<TN>> List<TN> buildByRecursive(List<TN> treeNodes,
                                                                           N root,
                                                                           Function<TN, R> getIdColumn,
                                                                           Function<TN, R> getParentColumn,
                                                                           Function<TN, C> getChildrenColumn,
                                                                           BiConsumer<TN, C> setChildrenColumn) {
        List<TN> trees = new ArrayList<>();

        for (TN treeNode : treeNodes) {
            if (root.equals(getParentColumn.apply(treeNode))) {
                trees.add(findChildren(treeNode, treeNodes, getIdColumn, getParentColumn, getChildrenColumn, setChildrenColumn));
            }
        }
        return trees;
    }

    public static <TN, R, C extends List<TN>> TN findChildren(TN treeNode,
                                                              List<TN> treeNodes,
                                                              Function<TN, R> getIdColumn,
                                                              Function<TN, R> getParentColumn,
                                                              Function<TN, C> getChildrenColumn,
                                                              BiConsumer<TN, C> setChildrenColumn) {

        for (TN it : treeNodes) {
            if (getIdColumn.apply(treeNode).equals(getParentColumn.apply(it))) {
                if (getChildrenColumn.apply(treeNode) == null) {
                    setChildrenColumn.accept(treeNode, (C) new ArrayList<TN>());
                }

                getChildrenColumn.apply(treeNode).add(findChildren(it, treeNodes, getIdColumn, getParentColumn, getChildrenColumn, setChildrenColumn));
            }
        }
        return treeNode;
    }
}
