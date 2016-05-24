package com.forerunnergames.peril.integration.core.func;

import com.forerunnergames.peril.integration.server.TestClient;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableSet;

public abstract class AbstractActionResult implements ActionResult
{
  private final ImmutableSet <TestClient> failed;
  private final int verified;

  protected AbstractActionResult (final ImmutableSet <TestClient> failed, final int verified)
  {
    Arguments.checkIsNotNull (failed, "failed");
    Arguments.checkIsNotNegative (verified, "verified");

    this.failed = failed;
    this.verified = verified;
  }

  @Override
  public ImmutableSet <TestClient> failed ()
  {
    return failed;
  }

  @Override
  public boolean hasAnyFailed ()
  {
    return !failed.isEmpty ();
  }

  @Override
  public int verified ()
  {
    return verified;
  }
}
