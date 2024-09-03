package com.huangch.cloud.utils.tree;

import cn.hutool.core.collection.CollUtil;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 树工具类
 *
 * @author huangch
 * @since 2023-11-16
 */
@SuppressWarnings("unchecked")
public class TreeUtils {

    /**
     * 将列表转换成树结构
     *
     * @param treeNodes         列表
     * @param root              根节点id
     * @param getIdColumn       获取数据id列
     * @param getParentColumn   获取数据父id列
     * @param getChildrenColumn 获取数据子列表列
     * @param setChildrenColumn 设置数据子列表列
     * @return 树型结构
     */
    public static <TN, R, C extends List<TN>> List<TN> buildByRecursive(List<TN> treeNodes,
                                                                        R root,
                                                                        Function<TN, R> getIdColumn,
                                                                        Function<TN, R> getParentColumn,
                                                                        Function<TN, C> getChildrenColumn,
                                                                        BiConsumer<TN, C> setChildrenColumn) {
        if (treeNodes == null || treeNodes.isEmpty()) {
            return Collections.emptyList();
        }

        Map<R, TN> idNodeMap = new HashMap<>();
        for (TN node : treeNodes) {
            idNodeMap.put(getIdColumn.apply(node), node);
        }

        List<TN> rootNodes = new ArrayList<>();
        for (TN node : treeNodes) {
            TN parentNode = idNodeMap.get(getParentColumn.apply(node));
            R id = getIdColumn.apply(node);
            if (parentNode != null && !id.equals(getParentColumn.apply(node))) {
                if (getChildrenColumn.apply(parentNode) == null) {
                    setChildrenColumn.accept(parentNode, (C) new ArrayList<TN>());
                }
                getChildrenColumn.apply(parentNode).add(node);
            } else if (root.equals(id)) {
                rootNodes.add(node);
            }

        }
        return rootNodes;

    }

    /**
     * 获取某个节点的所有子孙数据
     *
     * @param treeNode          目标节点
     * @param treeNodes         树数据
     * @param getIdColumn       获取数据id列
     * @param getParentColumn   获取数据父id列
     * @param getChildrenColumn 获取数据子列表列
     * @param setChildrenColumn 设置数据子列表列
     * @return 树型结构
     */
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

    /**
     * 遍历树数据
     *
     * @param treeNodes         树数据
     * @param consumer          每个节点的动作
     * @param getChildrenColumn 获取数据子列表列
     */
    public static <TN, C extends List<TN>> void traversalRecursive(List<TN> treeNodes,
                                                                   Consumer<TN> consumer,
                                                                   Function<TN, C> getChildrenColumn) {
        if (CollUtil.isEmpty(treeNodes)) {
            return;
        }
        for (TN node : treeNodes) {
            consumer.accept(node);

            List<TN> children = getChildrenColumn.apply(node);
            traversalRecursive(children, consumer, getChildrenColumn);
        }
    }

}
