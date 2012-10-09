package org.societies.api.internal.orchestration;

/**
 * Created with IntelliJ IDEA.
 * User: Bjørn Magnus Mathisen
 * Date: 19.09.12
 * Time: 16:06
 */
public interface ISocialGraphEdge {
    public ISocialGraphVertex getFrom();
    public ISocialGraphVertex getTo();
    public double getWeight();
}
