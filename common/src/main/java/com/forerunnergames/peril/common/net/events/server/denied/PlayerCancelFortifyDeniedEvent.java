/*
 * Copyright © 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerCancelFortifyRequestEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

public final class PlayerCancelFortifyDeniedEvent
        extends AbstractPlayerDeniedEvent <PlayerCancelFortifyRequestEvent, PlayerCancelFortifyDeniedEvent.Reason>
{
  private final CountryPacket sourceCountry;
  private final CountryPacket targetCountry;

  public enum Reason
  {
    NOT_IN_TURN
  }

  public PlayerCancelFortifyDeniedEvent (final PlayerPacket player,
                                         final CountryPacket sourceCountry,
                                         final CountryPacket targetCountry,
                                         final PlayerCancelFortifyRequestEvent deniedRequest,
                                         final Reason reason)
  {
    super (player, deniedRequest, reason);

    Arguments.checkIsNotNull (sourceCountry, "sourceCountry");
    Arguments.checkIsNotNull (targetCountry, "targetCountry");

    this.sourceCountry = sourceCountry;
    this.targetCountry = targetCountry;
  }

  public CountryPacket getSourceCountry ()
  {
    return sourceCountry;
  }

  public String getSourceCountryName ()
  {
    return sourceCountry.getName ();
  }

  public CountryPacket getTargetCountry ()
  {
    return targetCountry;
  }

  public String getTargetCountryName ()
  {
    return targetCountry.getName ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | SourceCountry: [{}] | TargetCountry: [{}]", super.toString (), sourceCountry,
                           targetCountry);
  }
}
