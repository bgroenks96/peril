/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
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

import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.peril.common.net.events.client.request.PlayerReinforceCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractCountryStateChangeDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractCountryStateChangeDeniedEvent.Reason;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerReinforceCountryDeniedEvent extends AbstractCountryStateChangeDeniedEvent
        implements PlayerResponseDeniedEvent <Reason>
{
  private final PlayerPacket player;
  private final PlayerReinforceCountryRequestEvent originalRequest;

  public PlayerReinforceCountryDeniedEvent (final PlayerPacket player,
                                            final Reason reason,
                                            final PlayerReinforceCountryRequestEvent originalRequest)
  {
    super (reason);

    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (originalRequest, "originalRequest");

    this.player = player;
    this.originalRequest = originalRequest;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return player;
  }

  @Override
  public String getPlayerName ()
  {
    return player.getName ();
  }

  @Override
  public PlayerColor getPlayerColor ()
  {
    return player.getColor ();
  }

  public PlayerReinforceCountryRequestEvent getOriginalRequest ()
  {
    return originalRequest;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Player: [{}] | OriginalRequest: [{}]", super.toString (), player, originalRequest);
  }

  @RequiredForNetworkSerialization
  private PlayerReinforceCountryDeniedEvent ()
  {
    player = null;
    originalRequest = null;
  }
}
