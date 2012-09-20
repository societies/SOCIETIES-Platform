package org.societies.api.internal.orchestration;

import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: Bjørn Magnus Mathisen
 * Date: 18.09.12
 * Time: 17:44
 */
public interface ICisDataCollectorManager {
    Future<ICisDataCollector> getDataCollector(String cisId);
}
