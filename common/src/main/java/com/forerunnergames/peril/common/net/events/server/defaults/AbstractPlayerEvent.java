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

package com.forerunnergames.peril.common.net.events.server.defaults;

import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public abstract class AbstractPlayerEvent extends AbstractPersonEvent <PlayerPacket> implements PlayerEvent
{
  protected AbstractPlayerEvent (final PlayerPacket player)
  {
    super (player);
  }

  @RequiredForNetworkSerialization
  protected AbstractPlayerEvent ()
  {
  }

  @Override
  public final PlayerColor getPlayerColor ()
  {
    return getPerson ().getColor ();
  }

  @Override
  public final int getPlayerTurnOrder ()
  {
    return getPerson ().getTurnOrder ();
  }

  @Override
  public final int getPlayerArmiesInHand ()
  {
    return getPerson ().getArmiesInHand ();
  }

  @Override
  public final int getPlayerCardsInHand ()
  {
    return getPerson ().getCardsInHand ();
  }
}
