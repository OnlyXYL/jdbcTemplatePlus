/**
 * @(#)FieldUpdateRequest.java, 10月 26, 2019.
 * <p>
 * Copyright 2019 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package io.github.skycloud.fastdao.core.ast.request;

import io.github.skycloud.fastdao.core.models.Column;

import java.util.Map;

/**
 * @author yuntian
 */
public interface FieldUpdateRequest<T extends FieldUpdateRequest> extends Request {

    T addUpdateField(Column field, Object value);

    T addUpdateField(String field, Object value);

    Map<String, Object> getUpdateFields();
}