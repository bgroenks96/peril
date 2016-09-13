/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.common.eventbus;

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
import java.util.Iterator;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;
import net.engio.mbassy.listener.Handler;

public final class EventBusHandler
{
  private final Deque <Event> events = new ArrayDeque<> ();

  public static IPublicationErrorHandler createEventBusFailureHandler ()
  {
    return new FailOnEventBusError ();
  }

  public void clearEvents ()
  {
    events.clear ();
  }

  /**
   * @throws IllegalStateException
   *           if no event of the given type was published
   */
  public <T> T lastEventOfType (final Class <T> type)
  {
    Arguments.checkIsNotNull (type, "type");

    for (final Event event : events)
    {
      if (type.isInstance (event)) return type.cast (event);
    }

    throw new IllegalStateException ("No event of type [" + type + "] was fired.");
  }

  /**
   * @return an ImmutableList of events of the given type, in the order that they were published
   */
  public <T> ImmutableList <T> allEventsOfType (final Class <T> type)
  {
    Arguments.checkIsNotNull (type, "type");

    final ImmutableList.Builder <T> listBuilder = ImmutableList.builder ();

    for (final Event event : events)
    {
      if (type.isInstance (event)) listBuilder.add (type.cast (event));
    }

    return listBuilder.build ();
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

  public Class <?> secondToLastEventType ()
  {
    return getSecondToLastEvent ().getClass ();
  }

  public <T> T thirdToLastEvent (final Class <T> type)
  {
    Arguments.checkIsNotNull (type, "type");

    return type.cast (getThirdToLastEvent ());
  }

  public Event thirdToLastEvent ()
  {
    return getThirdToLastEvent ();
  }

  public <T> boolean thirdToLastEventWasType (final Class <T> type)
  {
    Arguments.checkIsNotNull (type, "type");

    return type.isInstance (getThirdToLastEvent ());
  }

  public Class <?> thirdToLastEventType ()
  {
    return getThirdToLastEvent ().getClass ();
  }

  public <T> T nthToLastEvent (final Class <T> type, final int n)
  {
    Arguments.checkIsNotNull (type, "type");
    Arguments.checkLowerInclusiveBound (n, 1, "n");

    return type.cast (getNthToLastEvent (n));
  }

  public Event nthToLastEvent (final int n)
  {
    Arguments.checkLowerInclusiveBound (n, 1, "n");

    return getNthToLastEvent (n);
  }

  public <T> boolean nthToLastEventWasType (final Class <T> type, final int n)
  {
    Arguments.checkIsNotNull (type, "type");
    Arguments.checkLowerInclusiveBound (n, 1, "n");

    return type.isInstance (getNthToLastEvent (n));
  }

  public Class <?> nthToLastEventType (final int n)
  {
    Arguments.checkLowerInclusiveBound (n, 1, "n");

    return getNthToLastEvent (n).getClass ();
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
    return getNthToLastEvent (1);
  }

  private Event getSecondToLastEvent ()
  {
    return getNthToLastEvent (2);
  }

  private Event getThirdToLastEvent ()
  {
    return getNthToLastEvent (3);
  }

  private Event getNthToLastEvent (final int n)
  {
    if (n < 1) throw new IllegalStateException ("n must be >= 1");

    Event nthToLastEvent = null;

    final Iterator <Event> eventIterator = events.iterator ();

    for (int i = 0; i < n; ++i)
    {
      if (!eventIterator.hasNext ())
      {
        throw new IllegalStateException (
                Strings.format ("{} does not have enough events to get the {}-to-last event.",
                                EventBusHandler.class.getSimpleName (), Strings.toMixedOrdinal (n)));
      }

      nthToLastEvent = eventIterator.next ();
    }

    assert nthToLastEvent != null;

    return nthToLastEvent;
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

      final StringBuilder errorTraceBuilder = new StringBuilder (error + "\n");
      final Deque <Throwable> causeChain = new ArrayDeque<> ();
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

      errorTraceBuilder.append ("Cause: " + cause + "\n");
      throwableChain.push (cause);
      final Optional <Throwable> nextCause = Optional.fromNullable (cause.getCause ());
      if (nextCause.isPresent () && !throwableChain.contains (nextCause.get ()))
      {
        addCause (errorTraceBuilder, nextCause.get (), throwableChain);
      }
    }
  }
}
