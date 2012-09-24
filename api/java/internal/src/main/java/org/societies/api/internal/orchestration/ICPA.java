package org.societies.api.internal.orchestration;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Bjørn Magnus Mathisen
 * Date: 19.09.12
 * Time: 15:46
 */
public interface ICPA {
    public List<String> getTrends(String cisId, int n);
    public ISocialGraph getGraph(String cisId);
}
