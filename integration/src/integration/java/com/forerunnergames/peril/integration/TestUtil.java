package com.forerunnergames.peril.integration;

import com.google.common.collect.ImmutableSet;

import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;

import org.testng.Assert;

public class TestUtil
{
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
}
