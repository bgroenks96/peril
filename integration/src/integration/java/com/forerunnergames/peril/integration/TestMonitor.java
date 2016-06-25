package com.forerunnergames.peril.integration;

import static org.testng.Assert.fail;

import com.forerunnergames.tools.common.Strings;

import com.google.common.base.Optional;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.Assert;

public final class TestMonitor
{
  public static final int DEFAULT_WAIT_TIMEOUT = 10000;

  private static volatile TestMonitor current = null;

  private final BlockingQueue <MonitorEvent <?>> errorQueue = new LinkedBlockingQueue <> ();
  private final AtomicInteger checkInCount = new AtomicInteger ();

  private final int expectedCheckInCount;

  /**
   * Initializes this TestMonitor with the given expected check-in count and calls {@link #setCurrent()}.
   *
   * @param expectedCheckInCount
   */
  public TestMonitor (final int expectedCheckInCount)
  {
    this.expectedCheckInCount = expectedCheckInCount;
    setCurrent ();
  }

  public static Optional <TestMonitor> getCurrent ()
  {
    return Optional.fromNullable (current);
  }

  /**
   * Sets this TestMonitor as the default in the current static context. Calls to {@link #getCurrent()} will return a
   * reference to this monitor until {@link #setCurrent()} is called on a different TestMonitor instance.s
   */
  public void setCurrent ()
  {
    current = this;
  }

  /**
   * Increments this monitors check in count by one. This should be called by non-awaiting executors once all of their
   * checks/verifications have completed.
   */
  public void checkIn ()
  {
    final int currentCount = checkInCount.incrementAndGet ();
    if (currentCount == expectedCheckInCount)
    {
      errorQueue.add (new MonitorEvent <Throwable> (Optional. <Throwable> absent ()));
    }
  }

  public void failWith (final String message)
  {
    errorQueue.add (new MonitorEvent <> (Optional.of (new AssertionError (message))));
  }

  public void failWith (final Throwable t)
  {
    errorQueue.add (new MonitorEvent <> (Optional.of (t)));
  }

  public void assertTrue (final boolean condition)
  {
    try
    {
      Assert.assertTrue (condition);
    }
    catch (final AssertionError e)
    {
      errorQueue.add (new MonitorEvent <> (Optional.of (e)));
      throw e;
    }
  }

  public void assertFalse (final boolean condition)
  {
    try
    {
      Assert.assertFalse (condition);
    }
    catch (final AssertionError e)
    {
      errorQueue.add (new MonitorEvent <> (Optional.of (e)));
      throw e;
    }
  }

  public void assertNotNull (final Object obj)
  {
    try
    {
      Assert.assertNotNull (obj);
    }
    catch (final AssertionError e)
    {
      errorQueue.add (new MonitorEvent <> (Optional.of (e)));
      throw e;
    }
  }

  public <T> void assertEquals (final T expected, final T actual)
  {
    try
    {
      Assert.assertEquals (expected, actual);
    }
    catch (final AssertionError e)
    {
      errorQueue.add (new MonitorEvent <> (Optional.of (e)));
      throw e;
    }
  }

  public void awaitCompletion ()
  {
    awaitCompletion (DEFAULT_WAIT_TIMEOUT);
  }

  /**
   * Awaits an error to be thrown or completion reported by other threads acting on this monitor. Any errors raised by
   * this monitor on another thread will be thrown on the calling thread of this method. This method also asserts that
   * the expected verify count was reached.
   *
   * @param msTimeout
   *          timeout in milliseconds
   */
  public void awaitCompletion (final long msTimeout)
  {
    try
    {
      final MonitorEvent <?> event = errorQueue.poll (msTimeout, TimeUnit.MILLISECONDS);
      if (event == null)
      {
        throw new AssertionError (Strings.format ("Timeout reached [{} ms]", msTimeout));
      }

      if (event.error.isPresent ())
      {
        throw new AssertionError ("Monitor encountered error:", event.error.get ());
      }

      throwFirstInQueue ();

      assertEquals (expectedCheckInCount, checkInCount.get ());
    }
    catch (final InterruptedException e)
    {
      fail (e.toString ());
    }
  }

  private void throwFirstInQueue ()
  {
    while (errorQueue.size () > 0)
    {
      final MonitorEvent <?> event = errorQueue.poll ();
      if (event == null || !event.error.isPresent ()) continue;
      throw new AssertionError ("Monitor encountered error:", event.error.get ());
    }
  }

  private class MonitorEvent <T extends Throwable>
  {
    final Optional <T> error;

    MonitorEvent (final Optional <T> error)
    {
      this.error = error;
    }
  }
}
