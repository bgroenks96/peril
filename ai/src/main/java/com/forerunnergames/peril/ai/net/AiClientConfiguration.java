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

package com.forerunnergames.peril.ai.net;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.annotations.AllowNegative;
import com.forerunnergames.tools.net.client.configuration.ClientConfiguration;

public final class AiClientConfiguration implements ClientConfiguration
{
  private final String fakeAddress;
  @AllowNegative
  private final int fakePort;

  public AiClientConfiguration (final String fakeAddress, @AllowNegative final int fakePort)
  {
    Arguments.checkIsNotNull (fakeAddress, "fakeAddress");

    this.fakeAddress = fakeAddress;
    this.fakePort = fakePort;
  }

  @Override
  public String getAddress ()
  {
    return fakeAddress;
  }

  @Override
  public int getPort ()
  {
    return fakePort;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: FakeAddress: [{}] | FakePort: [{}]", getClass ().getSimpleName (), fakeAddress,
                           fakePort);
  }
}
