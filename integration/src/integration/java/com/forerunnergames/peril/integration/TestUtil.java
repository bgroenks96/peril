package com.forerunnergames.peril.integration;

import com.forerunnergames.peril.integration.server.TestClient;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

import java.util.Comparator;

import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;

import org.testng.Assert;

public class TestUtil
{
  private static final TestClientTurnOrderComparator clientTurnOrderComparator = new TestClientTurnOrderComparator ();
  public static final int DEFAULT_TEST_TIMEOUT = 45000;

  public static ImmutableSortedSet <TestClient> sortClientsByPlayerTurnOrder (final Iterable <TestClient> clients)
  {
    return ImmutableSortedSet.copyOf (clientTurnOrderComparator, clients);
  }

  public static Iterable <IPublicationErrorHandler> withDefaultHandler ()
  {
    return ImmutableSet.<IPublicationErrorHandler> of (new DefaultTestNGErrorHandler ());
  }

  private static class DefaultTestNGErrorHandler implements IPublicationErrorHandler
  {
    @Override
    public void handleError (final PublicationError error)
    {
      Assert.fail (error.getMessage (), error.getCause ());
    }
  }

  private static class TestClientTurnOrderComparator implements Comparator <TestClient>
  {
    @Override
    public int compare (final TestClient arg0, final TestClient arg1)
    {
      return arg0.getPlayer ().getTurnOrder () - arg1.getPlayer ().getTurnOrder ();
    }
  }
}
