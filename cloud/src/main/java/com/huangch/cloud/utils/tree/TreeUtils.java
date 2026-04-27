package com.huangch.cloud.utils.tree;

import cn.hutool.core.collection.CollUtil;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
    public static <TN, R, C extends List<TN>> List<TN> build(List<TN> treeNodes,
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
     * 深度遍历树数据
     *
     * @param treeNodes         树数据
     * @param consumer          每个节点的动作
     * @param getChildrenColumn 获取数据子列表列
     */
    public static <TN, C extends List<TN>> void depthFirstTraversal(List<TN> treeNodes,
                                                                   Consumer<TN> consumer,
                                                                   Function<TN, C> getChildrenColumn) {
        if (CollUtil.isEmpty(treeNodes)) {
            return;
        }
        for (TN node : treeNodes) {
            consumer.accept(node);

            List<TN> children = getChildrenColumn.apply(node);
            depthFirstTraversal(children, consumer, getChildrenColumn);
        }
    }

    /**
     * 将列表转换成树结构（递归实现）
     *
     * @param treeNodes         原始数据列表
     * @param getIdColumn       获取节点 ID
     * @param getParentColumn   获取节点父 ID
     * @param getChildrenColumn 获取子节点列表
     * @param setChildrenColumn 设置子节点列表
     * @param getSortColumn     获取排序字段（可选）
     * @return 树结构列表（根节点列表）
     */
    public static <TN, R, C extends List<TN>> List<TN> buildTree(List<TN> treeNodes,
                                                                 Function<TN, R> getIdColumn,
                                                                 Function<TN, R> getParentColumn,
                                                                 Function<TN, C> getChildrenColumn,
                                                                 BiConsumer<TN, C> setChildrenColumn,
                                                                 Function<TN, Integer> getSortColumn) {
        if (treeNodes == null || treeNodes.isEmpty()) {
            return Collections.emptyList();
        }

        // 构建 ID 映射
        Map<R, List<TN>> parentIdMap = new HashMap<>();
        for (TN node : treeNodes) {
            R parentId = getParentColumn.apply(node);
            parentIdMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(node);
        }

        // 获取所有根节点（parentId 为 null 或在数据中找不到其父节点）
        Set<R> allIds = treeNodes.stream().map(getIdColumn).collect(Collectors.toSet());
        List<TN> rootNodes = treeNodes.stream()
                .filter(node -> {
                    R parentId = getParentColumn.apply(node);
                    return parentId == null || !allIds.contains(parentId);
                })
                .collect(Collectors.toList());

        // 递归构建树
        for (TN root : rootNodes) {
            buildChildrenRecursive(root, getIdColumn, getChildrenColumn, setChildrenColumn, getSortColumn, parentIdMap);
        }

        return rootNodes;
    }

    /**
     * 递归设置子节点
     *
     * @param getIdColumn       获取节点 ID
     * @param getChildrenColumn 获取子节点列表
     * @param setChildrenColumn 设置子节点列表
     * @param getSortColumn     获取排序字段（可选）
     * @param parentIdMap       父id映射
     */
    private static <TN, R, C extends List<TN>> void buildChildrenRecursive(TN parent,
                                                                           Function<TN, R> getIdColumn,
                                                                           Function<TN, C> getChildrenColumn,
                                                                           BiConsumer<TN, C> setChildrenColumn,
                                                                           Function<TN, Integer> getSortColumn,
                                                                           Map<R, List<TN>> parentIdMap) {
        R parentId = getIdColumn.apply(parent);
        List<TN> children = parentIdMap.getOrDefault(parentId, Collections.emptyList());

        if (!children.isEmpty()) {
            // 可选排序
            if (getSortColumn != null) {
                children.sort(Comparator.comparing(getSortColumn));
            }

            C childrenList = (C) new ArrayList<>(children);
            setChildrenColumn.accept(parent, childrenList);

            for (TN child : children) {
                buildChildrenRecursive(child, getIdColumn, getChildrenColumn, setChildrenColumn, getSortColumn, parentIdMap);
            }
        }
    }

    /**
     * 过滤树结构中的节点
     *
     * @param treeNodes         树节点列表
     * @param filter            过滤条件，返回true表示保留该节点
     * @param keepParent        是否保留未命中的父节点（当子节点命中时）
     * @param getChildrenColumn 获取子节点列表的函数
     * @param setChildrenColumn 设置子节点列表的函数
     * @param <TN>              树节点类型
     * @param <C>               子节点列表类型
     * @return 过滤后的树节点列表
     */
    public static <TN, C extends List<TN>> List<TN> filter(List<TN> treeNodes,
                                                           Predicate<TN> filter,
                                                           boolean keepParent,
                                                           Function<TN, C> getChildrenColumn,
                                                           BiConsumer<TN, C> setChildrenColumn) {
        if (CollUtil.isEmpty(treeNodes)) {
            return Collections.emptyList();
        }

        List<TN> result = new ArrayList<>();
        for (TN node : treeNodes) {
            TN filteredNode = filterNode(node, filter, keepParent, getChildrenColumn, setChildrenColumn);
            if (filteredNode != null) {
                result.add(filteredNode);
            }
        }
        return result;
    }

    /**
     * 递归过滤单个节点及其子节点
     *
     * @param node              当前节点
     * @param filter            过滤条件
     * @param keepParent        是否保留未命中的父节点
     * @param getChildrenColumn 获取子节点列表的函数
     * @param setChildrenColumn 设置子节点列表的函数
     * @param <TN>              树节点类型
     * @param <C>               子节点列表类型
     * @return 过滤后的节点，如果该节点及其子节点都不满足条件则返回null
     */
    private static <TN, C extends List<TN>> TN filterNode(TN node,
                                                          Predicate<TN> filter,
                                                          boolean keepParent,
                                                          Function<TN, C> getChildrenColumn,
                                                          BiConsumer<TN, C> setChildrenColumn) {
        List<TN> children = getChildrenColumn.apply(node);
        List<TN> filteredChildren = new ArrayList<>();

        if (CollUtil.isNotEmpty(children)) {
            for (TN child : children) {
                TN filteredChild = filterNode(child, filter, keepParent, getChildrenColumn, setChildrenColumn);
                if (filteredChild != null) {
                    filteredChildren.add(filteredChild);
                }
            }
        }

        boolean selfMatch = filter.test(node);
        boolean childMatch = CollUtil.isNotEmpty(filteredChildren);

        // 情况 1：自己命中
        if (selfMatch) {
            setChildrenColumn.accept(node, (C) filteredChildren);
            return node;
        }

        // 情况 2：自己没命中，但子节点命中
        if (childMatch && keepParent) {
            setChildrenColumn.accept(node, (C) filteredChildren);
            return node;
        }

        // 情况 3：都没命中
        return null;
    }
}
