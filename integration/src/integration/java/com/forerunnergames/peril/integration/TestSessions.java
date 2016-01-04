package com.forerunnergames.peril.integration;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Exceptions;
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

    if (!sessionMap.containsKey (name)) Exceptions.throwIllegalState ("No session with name [{}] exists.", name);
    return sessionMap.get (name);
  }

  public static synchronized boolean existsSessionWith (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    return sessionMap.containsKey (name);
  }

  public static synchronized void end (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    final TestSession session = sessionMap.get (name);
    if (!session.isShutDown ()) session.shutDown ();
    sessionMap.remove (name);
  }

  public static String createUniqueNameFrom (final String name)
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
