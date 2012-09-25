package org.societies.api.internal.orchestration;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Bjørn Magnus Mathisen
 * Date: 19.09.12
 * Time: 16:17
 */
public interface ISocialGraphVertex {
    public List<ISocialGraphEdge> getEdges();
    public String getName();
}
