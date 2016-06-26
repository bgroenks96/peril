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

package com.forerunnergames.peril.common.net.events.server.notification;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerNotificationEvent;
import com.forerunnergames.peril.common.net.packets.card.CardPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.base.Optional;

public final class EndPlayerTurnEvent extends AbstractPlayerEvent implements PlayerNotificationEvent
{
  private final Optional <CardPacket> newCard;

  public EndPlayerTurnEvent (final PlayerPacket player, final Optional <CardPacket> newCard)
  {
    super (player);

    this.newCard = newCard;
  }

  public Optional <CardPacket> getNewCard ()
  {
    return newCard;
  }

  public boolean wasNewCardReceived ()
  {
    return newCard.isPresent ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | NewCard: {}", super.toString (), newCard);
  }

  @RequiredForNetworkSerialization
  private EndPlayerTurnEvent ()
  {
    newCard = null;
  }
}
