// package com.huangch.cloud.utils.db;
//
// import cn.hutool.core.collection.CollUtil;
// import cn.hutool.core.util.ObjectUtil;
// import cn.hutool.core.util.StrUtil;
// import com.huangch.cloud.utils.db.entity.Column;
// import com.huangch.cloud.utils.db.entity.Table;
// import com.zyaud.fzhx.jdbc.model.DataSourceInfo;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.config.BeanDefinition;
//
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;
//
// /**
//  * @author huangch
//  * @since 2024-11-12
//  */
// @Slf4j
// public class Entity2TableUtils {
//
//     public void run(String ddlAuto, List<String> entityPackages) {
//         log.info("ToolEntity2TableRunner -->  ddlAuto start  ");
//
//         // 获取注释的所有实体
//         List<BeanDefinition> candidateComponents;
//         try {
//             candidateComponents = toolEntity2TableScanner.getEntityList(entityPackages);
//             // 有一个是EntityToTableBaseEntity
//             log.info("candidateComponents size --> {}", candidateComponents.size() - 1);
//         } catch (ClassNotFoundException e) {
//             throw new RuntimeException(e);
//         }
//
//         // 数据库实体转表信息
//         List<Table> entityTables = null;
//         try {
//             entityTables = toolEntity2TableScanner.convertTableColumns(dataSourceInfo, candidateComponents);
//             log.info("entityTables size --> {}", entityTables.size());
//         } catch (ClassNotFoundException e) {
//             throw new RuntimeException(e);
//         }
//
//         // 更新数据库
//         if (CollUtil.isNotEmpty(entityTables)) {
//             createAndUpdateTables(dataSourceInfo, entityTables, ddlAuto);
//         }
//
//         log.info("ToolEntity2TableRunner -->  ddlAuto end ");
//     }
//
//     private void createAndUpdateTables(DataSourceInfo dataSourceInfo, List<Table> entityTables, String ddlAuto) {
//
//         try (Database database = DatabaseHelper.connect(dataSourceInfo)) {
//             // 查询数据库已有的所有表
//             List<Table> dbTables = toolDbTableManager.getTables(dataSourceInfo);
//             Map<String, Table> existTableMap = new HashMap<>();
//             for (Table table : dbTables) {
//                 existTableMap.put(table.getName().toUpperCase(), table);
//             }
//
//             // 数据库表dbTables 与 实体 entityTables 表信息比较, 执行ddl
//             for (Table entityTable : entityTables) {
//                 if (!existTableMap.containsKey(entityTable.getName().toUpperCase())) {
//                     // create
//                     if (ddlAuto.contains("create")) {
//                         toolDbTableManager.createTable(database, entityTable);
//                     }
//                 } else {
//                     // update
//                     if (ddlAuto.contains("update")) {
//                         Table dbTable = existTableMap.get(entityTable.getName().toUpperCase());
//                         List<Column> dbColumns = dbTable.getColumns();
//                         // 逐个字段比较
//                         for (Column column : entityTable.getColumns()) {
//                             if (ObjectUtil.isNotEmpty(column.getName())) {
//                                 Optional<Column> existColumn = dbColumns.stream().filter(o -> StrUtil.equalsIgnoreCase(column.getName(), o.getName())).findFirst();
//                                 if (!existColumn.isPresent()) {
//                                     toolDbTableColumnManager.addColumn(database, column);
//                                 }
//                             }
//                         }
//                     }
//                 }
//             }
//         } catch (Exception e) {
//             log.error(e.getMessage(), e);
//         }
//     }
// }
