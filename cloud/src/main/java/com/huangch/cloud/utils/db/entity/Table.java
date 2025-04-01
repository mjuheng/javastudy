package com.huangch.cloud.utils.db.entity;

import com.zyaud.fzhx.jdbc.model.IndexKey;
import lombok.Data;

import java.util.List;

/**
 * @author huangch
 * @since 2024-11-12
 */
@Data
public class Table {

    private String catalog;
    private String schema;
    private String name;
    private String type;
    private String comment;
    private List<Column> columns;
    private IndexKey primaryKey;
    private List<IndexKey> indexKeys;
    private boolean useColumnDefaultValue;
}
