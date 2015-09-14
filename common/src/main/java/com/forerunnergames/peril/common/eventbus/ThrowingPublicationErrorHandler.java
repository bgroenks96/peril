package com.forerunnergames.peril.common.eventbus;

import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;

public class ThrowingPublicationErrorHandler implements IPublicationErrorHandler
{
  @Override
  public void handleError (final PublicationError error)
  {
    throw new RuntimeException (error.getCause ());
  }
}
