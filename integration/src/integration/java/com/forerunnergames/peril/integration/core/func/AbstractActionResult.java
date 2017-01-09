/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
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
