/**
 * @(#)ShardPlugin.java, 10月 07, 2019.
 * <p>
 * Copyright 2019 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.skycloud.fastdao.core.plugins.shard;

import com.google.common.collect.Lists;
import com.skycloud.fastdao.core.plugins.PluggableHandler;
import com.skycloud.fastdao.core.plugins.Plugin;

import java.util.List;

/**
 * @author yuntian
 */
public class ShardPlugin implements Plugin {

    @Override
    public List<PluggableHandler> getHandlers() {
        return Lists.newArrayList(new ShardVisitorHandler());
    }
}