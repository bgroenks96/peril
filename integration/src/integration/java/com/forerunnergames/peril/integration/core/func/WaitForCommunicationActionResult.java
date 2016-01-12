package com.forerunnergames.peril.integration.core.func;

import com.forerunnergames.peril.integration.server.TestClient;

import com.google.common.collect.ImmutableSet;

public class WaitForCommunicationActionResult extends AbstractActionResult
{
  public WaitForCommunicationActionResult (final ImmutableSet <TestClient> failed, final int verified)
  {
    super (failed, verified);
  }
}
