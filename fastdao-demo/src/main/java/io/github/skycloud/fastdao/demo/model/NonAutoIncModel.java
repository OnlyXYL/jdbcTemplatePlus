/**
 * @(#)NonAutoIncModel.java, 10月 13, 2019.
 * <p>
 *
 */
package io.github.skycloud.fastdao.demo.model;

import io.github.skycloud.fastdao.core.annotation.PrimaryKey;
import io.github.skycloud.fastdao.core.annotation.Table;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author yuntian
 */
@Data
@ToString
@Table(tableName = "non_auto_inc")
public class NonAutoIncModel {

    @PrimaryKey
    private Long id;

    private String name;

    private String text;

    private Long longTime;

    private Date updated;

    private Date created;

    private Boolean deleted;

}