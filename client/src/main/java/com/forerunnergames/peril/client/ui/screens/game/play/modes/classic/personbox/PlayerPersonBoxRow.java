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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.personbox;

import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBoxRowStyle;
import com.forerunnergames.peril.client.ui.widgets.personicons.PersonIconWidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.personicons.players.PlayerIcon;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.annotations.AllowNegative;

public final class PlayerPersonBoxRow extends PersonBoxRow <PlayerIcon, PlayerPacket>
{
  private int displayedArmiesInHand;

  public PlayerPersonBoxRow (final PlayerPacket player,
                             final MessageBoxRowStyle rowStyle,
                             final PersonIconWidgetFactory <PlayerIcon, PlayerPacket> widgetFactory)
  {
    super (player, rowStyle, widgetFactory);
  }

  @Override
  public void setPerson (final PlayerPacket person)
  {
    Arguments.checkIsNotNull (person, "person");

    displayedArmiesInHand = person.getArmiesInHand ();

    super.setPerson (person);
  }

  @Override
  protected String createMessageTextLeft (final PlayerPacket person)
  {
    Arguments.checkIsNotNull (person, "person");

    return createMessageTextLeft (person.getTurnOrder ());
  }

  @Override
  protected String createMessageTextRight (final PlayerPacket person)
  {
    Arguments.checkIsNotNull (person, "person");

    return createMessageTextRight (person.getName (), person.getArmiesInHand (), person.getCardsInHand ());
  }

  @Override
  protected String createMessageText (final String messageTextLeft, final String messageTextRight)
  {
    Arguments.checkIsNotNull (messageTextLeft, "messageTextLeft");
    Arguments.checkIsNotNull (messageTextRight, "messageTextRight");

    return messageTextLeft + " " + messageTextRight;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Displayed Armies In Hand: [{}]", super.toString (), displayedArmiesInHand);
  }

  public boolean playerTurnOrderDiffersFrom (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    return getPerson ().doesNotHave (player.getTurnOrder ());
  }

  public void setDisplayedArmiesInHandToDeltaFromActual (@AllowNegative final int deltaArmies)
  {
    setDisplayedPlayerArmiesInHand (getActualPlayerArmiesInHand () + deltaArmies);
  }

  public void setDisplayedPlayerArmiesInHand (final int armies)
  {
    Arguments.checkIsNotNegative (armies, "armies");

    displayedArmiesInHand = armies;
    setMessageTextRight (createMessageTextRight (getPersonName (), armies, getPerson ().getCardsInHand ()));
  }

  public void resetDisplayedArmiesInHand ()
  {
    setDisplayedPlayerArmiesInHand (getActualPlayerArmiesInHand ());
  }

  public boolean playerTurnOrderIs (final int turnOrder)
  {
    Arguments.checkIsNotNegative (turnOrder, "turnOrder");

    return getPerson ().has (turnOrder);
  }

  public int getActualPlayerArmiesInHand ()
  {
    return getPerson ().getArmiesInHand ();
  }

  public int getDisplayedArmiesInHand ()
  {
    return displayedArmiesInHand;
  }

  private static String createMessageTextLeft (final int playerTurnOrder)
  {
    return Strings.toMixedOrdinal (playerTurnOrder);
  }

  private static String createMessageTextRight (final String playerName,
                                                final int playerArmiesInHand,
                                                final int playerCardsInHand)
  {
    return playerName + ", " + Strings.pluralize (playerArmiesInHand, "army", "armies") + ", "
            + Strings.pluralizeS (playerCardsInHand, "card");
  }
}
