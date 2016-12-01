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

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Exceptions;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import java.util.concurrent.atomic.AtomicInteger;

public class TestSessions
{
  private static final BiMap <String, TestSession> sessionMap = Maps
          .synchronizedBiMap (HashBiMap.<String, TestSession> create ());
  private static final AtomicInteger counter = new AtomicInteger ();

  public static synchronized void start (final String name, final TestSession testSession)
  {
    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNull (testSession, "testSession");

    if (sessionMap.containsKey (name))
    {
      Exceptions.throwIllegalState ("Seesion with name {} already exists.", name);
    }
    sessionMap.put (name, testSession);
    testSession.start ();
  }

  public static synchronized TestSession get (final String name)
  {
    Arguments.checkIsNotNull (name, "name");
    Preconditions.checkIsTrue (existsSessionWith (name), "No session with name [{}] exists.", name);

    return sessionMap.get (name);
  }

  public static synchronized boolean existsSessionWith (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    return sessionMap.containsKey (name);
  }

  public static synchronized boolean exists (final TestSession session)
  {
    Arguments.checkIsNotNull (session, "session");

    return sessionMap.inverse ().containsKey (session);
  }

  public static synchronized void end (final String name)
  {
    Arguments.checkIsNotNull (name, "name");
    Preconditions.checkIsTrue (existsSessionWith (name), "No session with name [{}] exists.", name);

    final TestSession session = sessionMap.get (name);
    if (!session.isShutDown ()) session.shutDown ();
    sessionMap.remove (name);
  }

  public static synchronized void end (final TestSession session)
  {
    Arguments.checkIsNotNull (session, "session");
    Preconditions.checkIsTrue (exists (session), "Session [{}] is not registered.", session);

    if (!session.isShutDown ()) session.shutDown ();
    sessionMap.inverse ().remove (session);
  }

  public static synchronized String createUniqueNameFrom (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    return Strings.format ("session-{}-{}", counter.getAndIncrement (), name);
  }

  public interface TestSession
  {
    void start ();

    void shutDown ();

    boolean isShutDown ();

    String getName ();
  }
}
