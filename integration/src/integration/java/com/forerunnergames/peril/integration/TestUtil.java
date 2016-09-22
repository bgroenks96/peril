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

package com.forerunnergames.peril.integration;

import com.forerunnergames.peril.integration.server.TestClient;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

import java.util.Comparator;

import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.Assert;

public class TestUtil
{
  public static final int DEFAULT_TEST_TIMEOUT = 45000;
  private static final TestClientTurnOrderComparator clientTurnOrderComparator = new TestClientTurnOrderComparator ();

  public static ImmutableSortedSet <TestClient> sortClientsByPlayerTurnOrder (final Iterable <TestClient> clients)
  {
    Arguments.checkIsNotNull (clients, "clients");
    Arguments.checkHasNoNullElements (clients, "clients");

    return ImmutableSortedSet.copyOf (clientTurnOrderComparator, clients);
  }

  public static Iterable <IPublicationErrorHandler> withDefaultHandler ()
  {
    return ImmutableSet.<IPublicationErrorHandler> of (new DefaultTestNGErrorHandler ());
  }

  public static void pause (final long millis)
  {
    Arguments.checkIsNotNegative (millis, "millis");

    try
    {
      Thread.sleep (millis);
    }
    catch (final InterruptedException e)
    {
      e.printStackTrace ();
    }
  }

  /**
   * Handles message bus errors using either the TestMonitor returned by {@link TestMonitor#getCurrent()} or by logging
   * a message and failing on the calling thread.
   */
  private static class DefaultTestNGErrorHandler implements IPublicationErrorHandler
  {
    private static final Logger log = LoggerFactory.getLogger (DefaultTestNGErrorHandler.class);

    @Override
    public void handleError (final PublicationError error)
    {
      Arguments.checkIsNotNull (error, "error");

      log.error ("Handled message bus error:", error);
      final Optional <TestMonitor> currentTestMonitor = TestMonitor.getCurrent ();
      if (currentTestMonitor.isPresent ())
      {
        currentTestMonitor.get ().failWith (new AssertionError (error.getMessage (), error.getCause ()));
      }
      else
      {
        Assert.fail (error.getMessage (), error.getCause ());
      }
    }
  }

  private static class TestClientTurnOrderComparator implements Comparator <TestClient>
  {
    @Override
    public int compare (final TestClient arg0, final TestClient arg1)
    {
      Arguments.checkIsNotNull (arg0, "arg0");
      Arguments.checkIsNotNull (arg1, "arg1");

      return arg0.getPlayer ().getTurnOrder () - arg1.getPlayer ().getTurnOrder ();
    }
  }
}
