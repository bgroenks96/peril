package com.forerunnergames.peril.common.eventbus;

import com.forerunnergames.tools.common.Exceptions;

import com.google.common.collect.Sets;

import java.util.Set;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

/**
 * Represents a "pipeline" between two event buses.
 *
 * @param <S>
 *          the generic type of the source bus
 * @param <T>
 *          the generic type of the second bus
 */
public final class EventBusPipe <S, T>
{
  private final Set <S> targetOutboundEvents = Sets.newConcurrentHashSet ();
  private final Set <T> sourceOutboundEvents = Sets.newConcurrentHashSet ();
  private final SourceSubscriber sourceSubscriber = new SourceSubscriber ();
  private final TargetSubscriber targetSubscriber = new TargetSubscriber ();
  private final MBassador <S> sourceEventBus;
  private MBassador <T> targetEventBus;

  public EventBusPipe (final MBassador <S> sourceEventBus)
  {
    this.sourceEventBus = sourceEventBus;
    sourceEventBus.subscribe (sourceSubscriber);
  }

  public void pipeTo (final MBassador <T> targetEventBus)
  {
    if (this.targetEventBus != null) Exceptions.throwIllegalState ("Target already set.");
    this.targetEventBus = targetEventBus;
    targetEventBus.subscribe (targetSubscriber);
  }

  private class SourceSubscriber
  {
    @Handler
    void sourceToTarget (final T sourceEvent)
    {
      if (targetOutboundEvents.remove (sourceEvent)) return;
      sourceOutboundEvents.add (sourceEvent);
      targetEventBus.publish (sourceEvent);
    }
  }

  private class TargetSubscriber
  {
    @Handler
    void targetToSource (final S targetEvent)
    {
      if (sourceOutboundEvents.remove (targetEvent)) return;
      targetOutboundEvents.add (targetEvent);
      sourceEventBus.publish (targetEvent);
    }
  }
}
