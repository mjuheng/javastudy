package com.huangch.cloud.utils.db.entity;

import lombok.Data;

/**
 * @author huangch
 * @since 2024-11-12
 */
@Data
public class Column {
    private String catalog;
    private String schema;
    private String table;
    private String name;
    private int type;
    private String typeName;
    private int size = 0;
    private int decimalDigits = 0;
    private boolean nullable;
    private String comment;
    private String defaultValue;
    private boolean autoIncrement;
    private boolean sortable;
    private int position = 1;
}
