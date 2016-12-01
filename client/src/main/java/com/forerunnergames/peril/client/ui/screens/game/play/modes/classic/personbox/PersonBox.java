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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.personbox;

import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.messagebox.DefaultMessageBox;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBoxStyle;
import com.forerunnergames.peril.client.ui.widgets.personicons.PersonIcon;
import com.forerunnergames.peril.common.net.packets.person.PersonPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.person.SpectatorPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.annotations.AllowNegative;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PersonBox extends DefaultMessageBox <PersonBoxRow <? extends PersonIcon, ? extends PersonPacket>>
{
  private static final Logger log = LoggerFactory.getLogger (PersonBox.class);
  private final WidgetFactory widgetFactory;
  private final Collection <PlayerPacket> players = new HashSet<> ();
  private final Collection <SpectatorPacket> spectators = new LinkedHashSet<> ();
  private final Map <String, Integer> playerNamesToRowIndices = new HashMap<> ();
  @Nullable
  private PlayerPacket highlightedPlayer;

  public PersonBox (final MessageBoxStyle style, final WidgetFactory widgetFactory)
  {
    super (style, widgetFactory);

    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");

    this.widgetFactory = widgetFactory;
  }

  @Override
  public void clear ()
  {
    super.clear ();

    players.clear ();
    spectators.clear ();
    playerNamesToRowIndices.clear ();
  }

  public void setPlayers (final ImmutableSet <PlayerPacket> players)
  {
    Arguments.checkIsNotNull (players, "players");
    Arguments.checkHasNoNullElements (players, "players");

    this.players.clear ();
    playerNamesToRowIndices.clear ();

    final boolean anyWereAdded = this.players.addAll (players);
    assert anyWereAdded;

    updateMessageBox ();
  }

  public void setSpectators (final ImmutableSet <SpectatorPacket> spectators)
  {
    Arguments.checkIsNotNull (spectators, "spectators");
    Arguments.checkHasNoNullElements (spectators, "spectators");

    this.spectators.clear ();

    final boolean anyWereAdded = this.spectators.addAll (spectators);
    assert anyWereAdded;

    updateMessageBox ();
  }

  public void addPlayer (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    internalAddPlayer (player);
    updateMessageBox ();
  }

  public void addSpectator (final SpectatorPacket spectator)
  {
    Arguments.checkIsNotNull (spectator, "spectator");

    internalAddSpectator (spectator);
    updateMessageBox ();
  }

  public void removePlayer (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    internalRemovePlayer (player);
    updateMessageBox ();
    if (isHighlighted (player)) clearHighlighting ();
  }

  public void removeSpectator (final SpectatorPacket spectator)
  {
    Arguments.checkIsNotNull (spectator, "spectator");

    internalRemoveSpectator (spectator);
    updateMessageBox ();
  }

  public void updatePlayerWithNewTurnOrder (final PlayerPacket player, final int oldTurnOrder)
  {
    Arguments.checkIsNotNull (player, "player");

    assert existsRowWith (player);
    assert getRowWithPlayer (player).playerTurnOrderIs (oldTurnOrder);

    // Cannot use #updateExisting (PlayerPacket) because because it won't update the set of turn-ordered players.
    // Player must be removed and re-added.
    // Avoids unnecessarily updating the message box twice (and superfluously de-highlighting/re-highlighting player) by
    // not using the public API for add / remove.
    internalRemovePlayer (player);
    internalAddPlayer (player);
    updateMessageBox ();
  }

  public void highlightPlayer (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    if (!existsRowWith (player))
    {
      log.warn ("Cannot highlight player [{}] (row does not exist).", player);
      return;
    }

    clearHighlighting ();
    getRowWithPlayer (player).highlight ();

    highlightedPlayer = player;
  }

  public void updateExisting (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    if (!existsRowWith (player))
    {
      log.warn ("Cannot update player [{}] (not an existing player).", player);
      return;
    }

    final PlayerPersonBoxRow row = getRowWithPlayer (player);

    if (row.personIsNot (player))
    {
      log.warn ("Cannot update player [{}] (player id has changed).", player);
      return;
    }

    if (row.playerTurnOrderDiffersFrom (player))
    {
      log.warn ("Cannot update player [{}] (turn order has changed).", player);
      return;
    }

    row.setPerson (player);
  }

  public void setDisplayedArmiesInHandToDeltaFromActual (@AllowNegative final int deltaArmies, final String playerName)
  {
    Arguments.checkIsNotNull (playerName, "playerName");

    getRowWithPlayer (playerName).setDisplayedArmiesInHandToDeltaFromActual (deltaArmies);
  }

  public void resetDisplayedArmiesInHand (final String playerName)
  {
    Arguments.checkIsNotNull (playerName, "playerName");

    getRowWithPlayer (playerName).resetDisplayedArmiesInHand ();
  }

  public int getDisplayedArmiesInHand (final String playerName)
  {
    Arguments.checkIsNotNull (playerName, "playerName");

    return getRowWithPlayer (playerName).getDisplayedArmiesInHand ();
  }

  public int getActualArmiesInHand (final String playerName)
  {
    Arguments.checkIsNotNull (playerName, "playerName");

    return getRowWithPlayer (playerName).getActualPlayerArmiesInHand ();
  }

  private void internalAddPlayer (final PlayerPacket player)
  {
    if (!players.add (player))
    {
      log.warn ("Not adding player [{}] to {}. (Player already added.)", player, PersonBox.class.getSimpleName ());
    }
  }

  private void internalAddSpectator (final SpectatorPacket spectator)
  {
    if (!spectators.add (spectator))
    {
      log.warn ("Not adding spectator [{}] to {}. (Spectator already added.)", spectator,
                PersonBox.class.getSimpleName ());
    }
  }

  private void internalRemovePlayer (final PlayerPacket player)
  {
    if (!players.remove (player))
    {
      log.warn ("Not removing player [{}] from {}. (Player not found in {}.)", player, PersonBox.class.getSimpleName (),
                PersonBox.class.getSimpleName ());
    }
  }

  private void internalRemoveSpectator (final SpectatorPacket spectator)
  {
    if (!spectators.remove (spectator))
    {
      log.warn ("Not removing spectator [{}] from {}. (Spectator not found in {}.)", spectator,
                PersonBox.class.getSimpleName (), PersonBox.class.getSimpleName ());
    }
  }

  private boolean existsRowWith (final PlayerPacket player)
  {
    return hasRowWithIndex (playerNamesToRowIndices.get (player.getName ()));
  }

  private PlayerPersonBoxRow getRowWithPlayer (final PlayerPacket player)
  {
    final PlayerPersonBoxRow row = getRowWithPlayer (player.getName ());
    assert row.personIs (player);

    return row;
  }

  private PlayerPersonBoxRow getRowWithPlayer (final String playerName)
  {
    final Integer index = playerNamesToRowIndices.get (playerName);
    assert index != null;
    assert hasRowWithIndex (index);

    final PersonBoxRow <? extends PersonIcon, ? extends PersonPacket> row = getRowByIndex (index);
    assert row.personHasName (playerName);

    return (PlayerPersonBoxRow) row;
  }

  private boolean isHighlighted (final PlayerPacket player)
  {
    return Objects.equals (highlightedPlayer, player);
  }

  private void clearHighlighting ()
  {
    for (final PersonBoxRow <? extends PersonIcon, ? extends PersonPacket> row : getRows ())
    {
      row.unhighlight ();
    }
  }

  // Generally only needs to be called after adding or removing players.
  private void updateMessageBox ()
  {
    super.clear ();

    playerNamesToRowIndices.clear ();

    int rowIndex = 0;

    for (final PlayerPacket player : ImmutableSortedSet.copyOf (PlayerPacket.TURN_ORDER_COMPARATOR, players))
    {
      addRow (widgetFactory.createPlayerPersonBoxRow (player));
      playerNamesToRowIndices.put (player.getName (), rowIndex);
      if (isHighlighted (player)) highlightPlayer (player);
      rowIndex++;
    }

    for (final SpectatorPacket spectator : spectators)
    {
      addRow (widgetFactory.createSpectatorPersonBoxRow (spectator));
    }
  }
}
