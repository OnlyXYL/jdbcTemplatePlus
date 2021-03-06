/**
 * @(#)Columns.java, 10月 13, 2019.
 * <p>
 *
 */
package io.github.skycloud.fastdao.demo.model;

import io.github.skycloud.fastdao.core.models.Column;

/**
 * @author yuntian
 */
public class Columns {

    public static final Column ID = new Column("id");

    public static final Column NAME = new Column("name");

    public static final Column TEXT = new Column("text");

    public static final Column LONG_TIME = new Column("longTime");

    public static final Column UPDATED = new Column("updated");

    public static final Column CREATED = new Column("created");

    public static final Column DELETED = new Column("deleted");

}