package com.forerunnergames.peril.integration.core.func;

import com.forerunnergames.peril.integration.server.TestClient;

import com.google.common.collect.ImmutableSet;

public interface ActionResult
{
  /**
   * @return a set of clients that did not receive the expected event from server
   */
  ImmutableSet <TestClient> failed ();

  /**
   * @return convenience check for if the set returned by {@link #failed()} is non-empty
   */
  boolean hasAnyFailed ();

  /**
   * @return the number of clients that verified the result of the server response
   */
  int verified ();
}
