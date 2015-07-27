package com.forerunnergames.peril.core.shared;

import static org.junit.Assert.fail;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;

import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import java.util.ArrayDeque;
import java.util.Deque;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;
import net.engio.mbassy.listener.Handler;

public final class EventBusHandler
{
  private final Deque <Event> events = new ArrayDeque <> ();

  public void clearEvents ()
  {
    events.clear ();
  }

  @SuppressWarnings ("unchecked")
  public <T> T lastEventOfType (final Class <T> type)
  {
    Arguments.checkIsNotNull (type, "type");

    for (final Event event : events)
    {
      if (type.isInstance (event)) return (T) event;
    }

    throw new IllegalStateException ("No event of type [" + type + "] was fired.");
  }

  public <T> boolean wasNeverFired (final Class <T> type)
  {
    Arguments.checkIsNotNull (type, "type");

    return Collections2.filter (events, Predicates.instanceOf (type)).isEmpty ();
  }

  public boolean wasNeverFired (final Event event)
  {
    Arguments.checkIsNotNull (event, "event");

    return Collections2.filter (events, Predicates.equalTo (event)).isEmpty ();
  }

  public <T> boolean wasFiredExactlyOnce (final Class <T> type)
  {
    Arguments.checkIsNotNull (type, "type");

    return Collections2.filter (events, Predicates.instanceOf (type)).size () == 1;
  }

  public boolean wasFiredExactlyOnce (final Event event)
  {
    Arguments.checkIsNotNull (event, "event");

    return Collections2.filter (events, Predicates.equalTo (event)).size () == 1;
  }

  public <T> boolean wasFiredExactlyNTimes (final Class <T> type, final int n)
  {
    Arguments.checkIsNotNull (type, "type");
    Arguments.checkIsNotNegative (n, "n");

    return Collections2.filter (events, Predicates.instanceOf (type)).size () == n;
  }

  public boolean wasFiredExactlyNTimes (final Event event, final int n)
  {
    Arguments.checkIsNotNull (event, "event");

    return Collections2.filter (events, Predicates.equalTo (event)).size () == n;
  }

  public <T> boolean wasFiredAtLeastNTimes (final Class <T> type, final int n)
  {
    Arguments.checkIsNotNull (type, "type");
    Arguments.checkIsNotNegative (n, "n");

    return Collections2.filter (events, Predicates.instanceOf (type)).size () >= n;
  }

  public boolean wasFiredAtLeastNTimes (final Event event, final int n)
  {
    Arguments.checkIsNotNull (event, "event");

    return Collections2.filter (events, Predicates.equalTo (event)).size () >= n;
  }

  public <T> boolean wasFiredAtMostNTimes (final Class <T> type, final int n)
  {
    Arguments.checkIsNotNull (type, "type");
    Arguments.checkIsNotNegative (n, "n");

    return Collections2.filter (events, Predicates.instanceOf (type)).size () <= n;
  }

  public boolean wasFiredAtMostNTimes (final Event event, final int n)
  {
    Arguments.checkIsNotNull (event, "event");

    return Collections2.filter (events, Predicates.equalTo (event)).size () <= n;
  }

  public <T> T lastEvent (final Class <T> type)
  {
    Arguments.checkIsNotNull (type, "type");

    return type.cast (getLastEvent ());
  }

  public Event lastEvent ()
  {
    return getLastEvent ();
  }

  public <T> boolean lastEventWasType (final Class <T> type)
  {
    Arguments.checkIsNotNull (type, "type");

    return type.isInstance (events.peekFirst ());
  }

  public Class <?> lastEventType ()
  {
    return getLastEvent ().getClass ();
  }

  public <T> T secondToLastEvent (final Class <T> type)
  {
    Arguments.checkIsNotNull (type, "type");

    return type.cast (getSecondToLastEvent ());
  }

  public Event secondToLastEvent ()
  {
    return getSecondToLastEvent ();
  }

  public <T> boolean secondToLastEventWasType (final Class <T> type)
  {
    Arguments.checkIsNotNull (type, "type");

    return type.isInstance (getSecondToLastEvent ());
  }

  public Class <?> seocndToLastEventType ()
  {
    return getSecondToLastEvent ().getClass ();
  }

  public ImmutableCollection <Event> getAllEvents ()
  {
    return ImmutableList.copyOf (events);
  }

  @Handler (priority = 10)
  public void onEvent (final Event event)
  {
    Arguments.checkIsNotNull (event, "event");

    events.addFirst (event);
  }

  public void subscribe (final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");

    eventBus.subscribe (this);
  }

  public void unsubscribe (final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");

    eventBus.unsubscribe (this);
  }

  public int countOf (final Class <?> type)
  {
    Arguments.checkIsNotNull (type, "type");

    int count = 0;

    for (final Event event : events)
    {
      if (type.isInstance (event)) ++count;
    }

    return count;
  }

  private Event getLastEvent ()
  {
    final Event event = events.peekFirst ();

    if (event == null) throw new IllegalStateException (EventBusHandler.class.getSimpleName () + " is empty.");

    return event;
  }

  private Event getSecondToLastEvent ()
  {
    final Event last = events.poll ();
    final Event prev = events.peekFirst ();

    if (prev == null) throw new IllegalStateException (EventBusHandler.class.getSimpleName () + " is empty.");

    events.push (last);

    return prev;
  }

  public static IPublicationErrorHandler createEventBusFailureHandler ()
  {
    return new FailOnEventBusError ();
  }

  private static class FailOnEventBusError implements IPublicationErrorHandler
  {
    /*
     * Unfortunately, the error message generated by this handler is really ugly and duplicates the message
     * strings multiple times due to MBassador handling the AssertionError thrown by Assert.fail. Unfortunately,
     * there is no easy way to prevent this (at the moment). Trying to cancel the error handling early (like what
     * was attempted below) doesn't work because it prevents the initial Assertion error from ultimately unwinding
     * the call stack and causing JUnit to recognize the test as a failure.
     */

    @Override
    public void handleError (final PublicationError error)
    {
      Arguments.checkIsNotNull (error, "error");

      // attempt to prevent recursive error handling... didn't work :(
      // final Optional <String> causeMessage = Optional.fromNullable (error.getCause ().getMessage ());
      // if (causeMessage.isPresent () && causeMessage.get ().contains (getClass ().getSimpleName ())) return;

      final StringBuilder errorTraceBuilder = new StringBuilder (error.toString () + "\n");
      final Deque <Throwable> causeChain = new ArrayDeque <> ();
      addCause (errorTraceBuilder, error.getCause (), causeChain);
      fail (Strings.format ("{}: Error caught in EventBus:\n{}", getClass ().getSimpleName (),
                            errorTraceBuilder.toString ()));
    }

    private void addCause (final StringBuilder errorTraceBuilder,
                           final Throwable cause,
                           final Deque <Throwable> throwableChain)
    {
      assert errorTraceBuilder != null;

      if (cause == null) return;

      errorTraceBuilder.append ("Cause: " + cause.toString () + "\n");
      throwableChain.push (cause);
      final Optional <Throwable> nextCause = Optional.fromNullable (cause.getCause ());
      if (nextCause.isPresent () && !throwableChain.contains (nextCause.get ()))
      {
        addCause (errorTraceBuilder, nextCause.get (), throwableChain);
      }
    }
  }
}
