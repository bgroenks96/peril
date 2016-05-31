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

package com.forerunnergames.peril.core.model.people.player;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PersonIdentity;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.core.model.people.player.PlayerModel.PlayerJoinGameStatus;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Result.ReturnStatus;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.common.id.IdGenerator;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class PlayerModelTest
{
  private static final int MIN_PLAYERS = 2;
  private static final int MAX_PLAYERS = 10;
  private static final int MIN_ARMIES_IN_PLAYER_HAND = 0;
  private static final int MAX_ARMIES_IN_PLAYER_HAND = Integer.MAX_VALUE;

  @Test
  public void testAddArmiesToHandOfPlayer ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final Player player = addSinglePlayerTo (playerModel);

    final int armyCount = 10;
    playerModel.addArmiesToHandOf (player.getId (), armyCount);

    assertTrue (playerModel.getArmiesInHand (player.getId ()) == armyCount);
  }

  @Test (expected = IllegalStateException.class)
  public void testAddArmiesToHandOfPlayerFailsWhenMaxArmiesInHand ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final Player player = addSinglePlayerTo (playerModel);

    playerModel.addArmiesToHandOf (player.getId (), MAX_ARMIES_IN_PLAYER_HAND);
    playerModel.addArmiesToHandOf (player.getId (), 1);
  }

  @Test
  public void testCanAddArmiesToHandOfPlayerOneArmy ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final Player player = addSinglePlayerTo (playerModel);

    assertTrue (playerModel.canAddArmiesToHandOf (player.getId (), 1));
  }

  @Test
  public void testCanRemoveAddArmiesFromHandOfPlayerOneArmy ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final Player player = addSinglePlayerTo (playerModel);

    playerModel.addArmiesToHandOf (player.getId (), 1);

    assertTrue (playerModel.canRemoveArmiesFromHandOf (player.getId (), 1));
  }

  @Test
  public void testCannotAddArmiesToHandOfPlayerMaxArmiesInHand ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final Player player = addSinglePlayerTo (playerModel);

    playerModel.addArmiesToHandOf (player.getId (), MAX_ARMIES_IN_PLAYER_HAND);

    assertFalse (playerModel.canAddArmiesToHandOf (player.getId (), 1));
  }

  @Test
  public void testCannotRemoveArmiesFromHandOfPlayerMinArmiesInHand ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final Player player = addSinglePlayerTo (playerModel);

    playerModel.addArmiesToHandOf (player.getId (), MIN_ARMIES_IN_PLAYER_HAND);

    assertFalse (playerModel.canRemoveArmiesFromHandOf (player.getId (), MIN_ARMIES_IN_PLAYER_HAND + 1));
  }

  @Test
  public void testChangeTurnOrderOfPlayer ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (2);
    addNPlayersTo (playerModel, 2);

    final Id player1 = playerModel.playerWith (PlayerTurnOrder.FIRST);
    final Id player2 = playerModel.playerWith (PlayerTurnOrder.SECOND);

    playerModel.changeTurnOrderOfPlayer (player1, PlayerTurnOrder.SECOND);

    assertTrue (playerModel.turnOrderOf (player2).is (PlayerTurnOrder.FIRST)
            && playerModel.turnOrderOf (player1).is (PlayerTurnOrder.SECOND));
  }

  @Test
  public void testExistsPlayerWithColor ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final PlayerColor color = PlayerColor.TEAL;

    playerModel.requestToAdd (PlayerFactory.builder ("TestPlayer").color (color).toFactory ());

    assertTrue (playerModel.existsPlayerWith (color));
  }

  @Test
  public void testExistsPlayerWithId ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final Player player = PlayerFactory.create ("TestPlayer");

    playerModel.requestToAdd (factoryFrom (player));

    assertTrue (playerModel.existsPlayerWith (player.getId ()));
  }

  @Test
  public void testExistsPlayerWithName ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final String name = "TestPlayer";

    playerModel.requestToAdd (PlayerFactory.builder (name).toFactory ());

    assertTrue (playerModel.existsPlayerWith (name));
  }

  @Test
  public void testExistsPlayerWithName2 ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final String name = "TestPlayer";

    playerModel.requestToAdd (PlayerFactory.builder (name).toFactory ());

    assertTrue (playerModel.existsPlayerWithName (name));
  }

  @Test
  public void testExistsPlayerWithPersonIdentity ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final PersonIdentity identity = PersonIdentity.SELF;

    playerModel.requestToAdd (PlayerFactory.builder ("TestPlayer").identity (identity).toFactory ());

    assertTrue (playerModel.existsPlayerWith (identity));
  }

  @Test
  public void testExistsPlayerWithTurnOrder ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final PlayerTurnOrder turnOrder = PlayerTurnOrder.THIRD;

    playerModel.requestToAdd (PlayerFactory.builder ("TestPlayer").turnOrder (turnOrder).toFactory ());

    assertTrue (playerModel.existsPlayerWith (turnOrder));
  }

  @Test
  public void testExistsPlayerWithUnknownColor ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final PlayerColor color = PlayerColor.UNKNOWN;

    playerModel.requestToAdd (PlayerFactory.builder ("TestPlayer").color (color).toFactory ());

    assertFalse (playerModel.existsPlayerWith (color));
  }

  @Test
  public void testExistsPlayerWithUnknownTurnOrder ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final PlayerTurnOrder turnOrder = PlayerTurnOrder.UNKNOWN;

    playerModel.requestToAdd (PlayerFactory.builder ("TestPlayer").turnOrder (turnOrder).toFactory ());

    assertFalse (playerModel.existsPlayerWith (turnOrder));
  }

  @Test
  public void testGetArmiesInHandOf ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final Player player = addSinglePlayerTo (playerModel);

    final int armyCount = 10;
    playerModel.addArmiesToHandOf (player.getId (), armyCount);

    assertTrue (playerModel.getArmiesInHand (player.getId ()) == armyCount);
  }

  @Test
  public void testGetPlayerCount ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (3);

    assertThat (playerModel.getPlayerCount (), is (0));

    addNPlayersTo (playerModel, 3);

    assertThat (playerModel.getPlayerCount (), is (3));
  }

  @Test
  public void testGetPlayerLimit ()
  {
    final int limit = 5;

    final PlayerModel playerModel = createPlayerModelWithLimitOf (limit);

    assertThat (playerModel.getPlayerLimit (), is (limit));
  }

  @Test
  public void testGetPlayers ()
  {
    final ImmutableSet <Player> expectedPlayers = ImmutableSet.of (PlayerFactory.create ("TestPlayer1"),
                                                                   PlayerFactory.create ("TestPlayer2"),
                                                                   PlayerFactory.create ("TestPlayer3"));

    final PlayerModel playerModel = createPlayerModelWithLimitOf (expectedPlayers.size ());

    assertFalse (Result.anyStatusFailed (playerModel.requestToAdd (PlayerFactory.from (expectedPlayers))));

    final ImmutableSet <PlayerPacket> actualPlayers = playerModel.getPlayerPackets ();
    final ImmutableSet <PlayerPacket> expectedPlayerPackets = PlayerPackets.fromPlayers (expectedPlayers);

    assertTrue (actualPlayers.containsAll (expectedPlayerPackets));
    assertTrue (expectedPlayerPackets.containsAll (actualPlayers));
  }

  @Test
  public void testGetAllPlayersExcept ()
  {
    final ImmutableSet <Player> expectedPlayers = ImmutableSet.of (PlayerFactory.create ("TestPlayer1"),
                                                                   PlayerFactory.create ("TestPlayer2"),
                                                                   PlayerFactory.create ("TestPlayer3"));

    final PlayerModel playerModel = createPlayerModelWithLimitOf (expectedPlayers.size () + 1);

    assertFalse (Result.anyStatusFailed (playerModel.requestToAdd (PlayerFactory.from (expectedPlayers))));

    final Player unwantedPlayer = PlayerFactory.create ("TestPlayer4");

    assertFalse (Result
            .anyStatusFailed (playerModel.requestToAdd (PlayerFactory.from (ImmutableSet.of (unwantedPlayer)))));

    final ImmutableSet <PlayerPacket> actualPlayerPackets = playerModel.getAllPlayersExcept (unwantedPlayer.getId ());
    final ImmutableSet <PlayerPacket> expectedPlayerPackets = PlayerPackets.fromPlayers (expectedPlayers);

    assertTrue (actualPlayerPackets.containsAll (expectedPlayerPackets));
    assertTrue (expectedPlayerPackets.containsAll (actualPlayerPackets));
  }

  @Test
  public void testHasArmiesInHandOf ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final Player player = addSinglePlayerTo (playerModel);

    final int armyCount = 10;

    playerModel.addArmiesToHandOf (player.getId (), armyCount);

    assertTrue (playerModel.hasArmiesInHandOf (player.getId (), armyCount));
  }

  @Test
  public void testIsEmpty ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (MAX_PLAYERS);

    assertTrue (playerModel.isEmpty ());

    addNPlayersTo (playerModel, MAX_PLAYERS);

    assertFalse (playerModel.isEmpty ());

    removeAllPlayersFrom (playerModel);

    assertTrue (playerModel.isEmpty ());
  }

  @Test
  public void testIsFull ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (MAX_PLAYERS);

    assertFalse (playerModel.isFull ());

    addNPlayersTo (playerModel, MAX_PLAYERS);

    assertTrue (playerModel.isFull ());

    removeAllPlayersFrom (playerModel);

    assertFalse (playerModel.isFull ());
  }

  @Test (expected = IllegalStateException.class)
  public void testNonExistentPlayerWithColorThrowsException ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);

    assertTrue (playerModel.isEmpty ());

    playerModel.playerWith (PlayerColor.RED);
  }

  @Test (expected = IllegalStateException.class)
  public void testNonExistentPlayerPacketWithIdThrowsException ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);

    assertTrue (playerModel.isEmpty ());

    playerModel.playerPacketWith (IdGenerator.generateUniqueId ());
  }

  @Test (expected = IllegalStateException.class)
  public void testNonExistentPlayerWithTurnOrderThrowsException ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);

    assertTrue (playerModel.isEmpty ());

    playerModel.playerWith (PlayerTurnOrder.FOURTH);
  }

  @Test
  public void testPlayerColorsUniqueAndValid ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (MAX_PLAYERS);

    addNPlayersTo (playerModel, MAX_PLAYERS);

    for (final Id player1 : playerModel.getPlayerIds ())
    {
      assertTrue (playerModel.colorOf (player1).isNot (PlayerColor.UNKNOWN));

      for (final Id player2 : playerModel.getPlayerIds ())
      {
        if (player2.is (player1)) continue;

        assertTrue (playerModel.colorOf (player2).isNot (playerModel.colorOf (player1)));
      }
    }
  }

  @Test
  public void testPlayerCountIs ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (3);

    assertTrue (playerModel.playerCountIs (0));

    addNPlayersTo (playerModel, 3);

    assertTrue (playerModel.playerCountIs (3));
  }

  @Test
  public void testPlayerCountIsNot ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (3);

    assertTrue (playerModel.playerCountIsNot (1));

    addNPlayersTo (playerModel, 3);

    assertTrue (playerModel.playerCountIsNot (4));
  }

  @Test
  public void testPlayerIdsUnique ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (MAX_PLAYERS);

    for (final Id player1 : playerModel.getPlayerIds ())
    {
      for (final Id player2 : playerModel.getPlayerIds ())
      {
        if (player2.is (player1)) continue;

        assertTrue (player2.isNot (player1));
      }
    }
  }

  @Test
  public void testPlayerLimitIs ()
  {
    final int limit = 8;

    final PlayerModel playerModel = createPlayerModelWithLimitOf (limit);

    assertTrue (playerModel.playerLimitIs (limit));
  }

  @Test
  public void testPlayerTurnOrdersUniqueAndValid ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (MAX_PLAYERS);

    for (final Id player1 : playerModel.getPlayerIds ())
    {
      assertTrue (playerModel.turnOrderOf (player1).isNot (PlayerTurnOrder.UNKNOWN));

      for (final Id player2 : playerModel.getPlayerIds ())
      {
        if (player2.is (player1)) continue;

        assertTrue (playerModel.turnOrderOf (player2).isNot (playerModel.turnOrderOf (player1)));
      }
    }
  }

  @Test
  public void testPlayerWithColor ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (2);
    final PlayerColor player1Color = PlayerColor.CYAN;
    final Player player1 = PlayerFactory.builder ("TestPlayer1").color (player1Color).build ();
    final Player player2 = PlayerFactory.builder ("TestPlayer2").color (PlayerColor.GOLD).build ();

    playerModel.requestToAdd (factoryFrom (player1));
    playerModel.requestToAdd (factoryFrom (player2));

    assertTrue (playerModel.playerWith (player1Color).is (player1.getId ()));
  }

  @Test
  public void testIdOfMatchesPlayerId ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (2);
    final Id player1Id = IdGenerator.generateUniqueId ();
    final Player player1 = new DefaultPlayer ("TestPlayer1", player1Id, PersonIdentity.UNKNOWN, PlayerColor.UNKNOWN,
            PlayerTurnOrder.UNKNOWN);
    final Player player2 = new DefaultPlayer ("TestPlayer2", IdGenerator.generateUniqueId (), PersonIdentity.UNKNOWN,
            PlayerColor.UNKNOWN, PlayerTurnOrder.UNKNOWN);

    playerModel.requestToAdd (factoryFrom (player1));
    playerModel.requestToAdd (factoryFrom (player2));

    assertTrue (playerModel.idOf (player1.getName ()).is (player1.getId ()));
  }

  @Test
  public void testPlayerWithTurnOrder ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (2);
    final PlayerTurnOrder player1TurnOrder = PlayerTurnOrder.NINTH;
    final Player player1 = PlayerFactory.builder ("TestPlayer1").turnOrder (player1TurnOrder).build ();
    final Player player2 = PlayerFactory.builder ("TestPlayer2").turnOrder (PlayerTurnOrder.SEVENTH).build ();

    playerModel.requestToAdd (factoryFrom (player1));
    playerModel.requestToAdd (factoryFrom (player2));

    assertTrue (playerModel.playerWith (player1TurnOrder).is (player1.getId ()));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testPlayerWithUnknownColorThrowsException ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);

    addSinglePlayerTo (playerModel);

    playerModel.playerWith (PlayerColor.UNKNOWN);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testPlayerWithUnknownTurnOrderThrowsException ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);

    addSinglePlayerTo (playerModel);

    playerModel.playerWith (PlayerTurnOrder.UNKNOWN);
  }

  @Test
  public void testRemoveArmiesFromHandOfPlayer ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final Player player = addSinglePlayerTo (playerModel);

    final int armies = 3;

    playerModel.addArmiesToHandOf (player.getId (), armies);
    playerModel.removeArmiesFromHandOf (player.getId (), armies);

    assertTrue (playerModel.getArmiesInHand (player.getId ()) == 0);
  }

  @Test (expected = IllegalStateException.class)
  public void testRemoveArmiesFromHandOfPlayerFailsWhenMinArmiesInHand ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final Player player = addSinglePlayerTo (playerModel);

    assertTrue (playerModel.hasArmiesInHandOf (player.getId (), MIN_ARMIES_IN_PLAYER_HAND));

    playerModel.removeArmiesFromHandOf (player.getId (), 1);
  }

  @Test
  public void testRequestAddPlayerFailedWithDuplicateColor ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (2);
    final Player player1 = PlayerFactory.builder ("TestPlayer1").color (PlayerColor.BLUE).build ();
    final Player player2 = PlayerFactory.builder ("TestPlayer2").color (PlayerColor.BLUE).build ();

    assertFalse (Result.anyStatusFailed (playerModel.requestToAdd (factoryFrom (player1))));
    final ImmutableSet <PlayerJoinGameStatus> results = playerModel.requestToAdd (factoryFrom (player2));
    assertTrue (singleResultFrom (results).failedBecauseOf (PlayerJoinGameDeniedEvent.Reason.DUPLICATE_COLOR));
    assertTrue (playerModel.playerCountIs (1));
  }

  @Test
  public void testRequestAddPlayerFailedWithDuplicateTurnOrder ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (2);
    final Player player1 = PlayerFactory.builder ("TestPlayer1").turnOrder (PlayerTurnOrder.THIRD).build ();
    final Player player2 = PlayerFactory.builder ("TestPlayer2").turnOrder (PlayerTurnOrder.THIRD).build ();

    assertFalse (Result.anyStatusFailed (playerModel.requestToAdd (factoryFrom (player1))));
    final ImmutableSet <PlayerJoinGameStatus> results = playerModel.requestToAdd (factoryFrom (player2));
    assertTrue (singleResultFrom (results).failedBecauseOf (PlayerJoinGameDeniedEvent.Reason.DUPLICATE_TURN_ORDER));
    assertTrue (playerModel.playerCountIs (1));
  }

  @Test
  public void testRequestAddPlayerFailedWithGameIsFull ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (MAX_PLAYERS);

    addNPlayersTo (playerModel, MAX_PLAYERS);

    final ImmutableSet <PlayerJoinGameStatus> results = playerModel
            .requestToAdd (PlayerFactory.builder ("TestPlayerX").toFactory ());
    assertTrue (singleResultFrom (results).failedBecauseOf (PlayerJoinGameDeniedEvent.Reason.GAME_IS_FULL));
  }

  @Test
  public void testRequestAddPlayerFailedWithDuplicateName ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (2);
    final String duplicateName = "TestPlayer";
    final Player player1 = PlayerFactory.create (duplicateName, PersonIdentity.SELF, PlayerColor.PINK,
                                                 PlayerTurnOrder.EIGHTH);
    final Player player2 = PlayerFactory.create (duplicateName, PersonIdentity.NON_SELF, PlayerColor.BLUE,
                                                 PlayerTurnOrder.THIRD);

    assertFalse (Result.anyStatusFailed (playerModel.requestToAdd (factoryFrom (player1))));
    final ImmutableSet <PlayerJoinGameStatus> results = playerModel.requestToAdd (factoryFrom (player2));
    assertTrue (singleResultFrom (results).failedBecauseOf (PlayerJoinGameDeniedEvent.Reason.DUPLICATE_NAME));
    assertTrue (playerModel.playerCountIs (1));
  }

  @Test
  public void testRequestAddPlayerFailedWithInvalidNameEmpty ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final String invalidName = "";
    final Player player = PlayerFactory.create (invalidName);

    final ImmutableSet <PlayerJoinGameStatus> results = playerModel.requestToAdd (factoryFrom (player));
    assertTrue (singleResultFrom (results).failedBecauseOf (PlayerJoinGameDeniedEvent.Reason.INVALID_NAME));
    assertTrue (playerModel.isEmpty ());
  }

  @Test
  public void testRequestAddPlayerFailedWithInvalidNameSpecialCharacter ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final String invalidName = "!";
    final Player player = PlayerFactory.create (invalidName);

    final ImmutableSet <PlayerJoinGameStatus> results = playerModel.requestToAdd (factoryFrom (player));
    assertTrue (singleResultFrom (results).failedBecauseOf (PlayerJoinGameDeniedEvent.Reason.INVALID_NAME));
    assertTrue (playerModel.isEmpty ());
  }

  @Test
  public void testRequestAddPlayerFailedWithInvalidNameWhitespaceOnly ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final String invalidName = "   \r\r\t\t\n\n     \b\b";
    final Player player = PlayerFactory.create (invalidName);

    assertTrue (singleResultFrom (playerModel.requestToAdd (factoryFrom (player)))
            .failedBecauseOf (PlayerJoinGameDeniedEvent.Reason.INVALID_NAME));
    assertTrue (playerModel.isEmpty ());
  }

  @Test
  public void testRequestAddPlayerFailedWithInvalidNameSpacesOnly ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final String invalidName = "              ";
    final Player player = PlayerFactory.create (invalidName);

    assertTrue (singleResultFrom (playerModel.requestToAdd (factoryFrom (player)))
            .failedBecauseOf (PlayerJoinGameDeniedEvent.Reason.INVALID_NAME));
    assertTrue (playerModel.isEmpty ());
  }

  @Test
  public void testRequestAddPlayerFailedWithInvalidNameMultipleConsecutiveSpaces ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final String invalidName = "Test  Player";
    final Player player = PlayerFactory.create (invalidName);

    assertTrue (singleResultFrom (playerModel.requestToAdd (factoryFrom (player)))
            .failedBecauseOf (PlayerJoinGameDeniedEvent.Reason.INVALID_NAME));
    assertTrue (playerModel.isEmpty ());
  }

  @Test
  public void testRequestAddPlayerFailedWithInvalidNameBeginsWithSingleSpace ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final String invalidName = " TestPlayer";
    final Player player = PlayerFactory.create (invalidName);

    assertTrue (singleResultFrom (playerModel.requestToAdd (factoryFrom (player)))
            .failedBecauseOf (PlayerJoinGameDeniedEvent.Reason.INVALID_NAME));
    assertTrue (playerModel.isEmpty ());
  }

  @Test
  public void testRequestAddPlayerFailedWithInvalidNameEndsWithSingleSpace ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final String invalidName = "TestPlayer ";
    final Player player = PlayerFactory.create (invalidName);

    assertTrue (singleResultFrom (playerModel.requestToAdd (factoryFrom (player)))
            .failedBecauseOf (PlayerJoinGameDeniedEvent.Reason.INVALID_NAME));
    assertTrue (playerModel.isEmpty ());
  }

  @Test
  public void testRequestAddPlayerFailedWithInvalidNameTooLong ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final String invalidName = "TestPlayerTestPlayerTestPlayerTestPlayerTestPlayerTestPlayerTestPlayer"
            + "TestPlayerTestPlayerTestPlayerTestPlayerTestPlayerTestPlayerTestPlayerTestPlayer"
            + "TestPlayerTestPlayerTestPlayerTestPlayerTestPlayerTestPlayerTestPlayerTestPlayer"
            + "TestPlayerTestPlayerTestPlayerTestPlayerTestPlayerTestPlayer";
    final Player player = PlayerFactory.create (invalidName);

    assertTrue (singleResultFrom (playerModel.requestToAdd (factoryFrom (player)))
            .failedBecauseOf (PlayerJoinGameDeniedEvent.Reason.INVALID_NAME));
    assertTrue (playerModel.isEmpty ());
  }

  @Test
  public void testRequestAddPlayerSucceeded ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final String name = "TestPlayer";
    final PlayerColor color = PlayerColor.TEAL;
    final PlayerTurnOrder turnOrder = PlayerTurnOrder.FIFTH;
    final PersonIdentity identity = PersonIdentity.SELF;
    final Player player = PlayerFactory.create (name, identity, color, turnOrder);

    assertTrue (singleResultFrom (playerModel.requestToAdd (factoryFrom (player))).succeeded ());
    assertTrue (playerModel.playerCountIs (1));
    assertTrue (playerModel.existsPlayerWith (name));
    assertTrue (playerModel.existsPlayerWith (color));
    assertTrue (playerModel.existsPlayerWith (turnOrder));
    assertTrue (playerModel.identityOf (player.getId ()).equals (identity));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testRemovePlayerByUnknownColorThrowsException ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);

    addNPlayersTo (playerModel, 1);

    playerModel.removeByColor (PlayerColor.UNKNOWN);
  }

  @Test
  public void testRemoveNonExistentPlayerByColorDoesNotThrowException ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);

    playerModel.removeByColor (PlayerColor.BROWN);
  }

  @Test
  public void testRemovePlayerByColorSuccessful ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);

    final PlayerColor color = PlayerColor.PINK;

    playerModel.requestToAdd (PlayerFactory.builder ("TestPlayer").color (color).toFactory ());
    playerModel.removeByColor (color);

    assertFalse (playerModel.existsPlayerWith (color));
  }

  @Test
  public void testRemoveNonExistentPlayerByIdDoesNotThrowException ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);

    playerModel.removeById (IdGenerator.generateUniqueId ());
  }

  @Test
  public void testRemovePlayerByIdSuccessful ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final Player player = PlayerFactory.create ("TestPlayer");
    final Id id = player.getId ();

    playerModel.requestToAdd (factoryFrom (player));
    playerModel.removeById (id);

    assertFalse (playerModel.existsPlayerWith (id));
  }

  @Test
  public void testRemoveNonExistentPlayerByNameDoesNotThrowException ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);

    playerModel.removeByName ("Non-Existent Player");
  }

  @Test
  public void testRemovePlayerByNameSuccessful ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final Player player = addSinglePlayerTo (playerModel);

    playerModel.removeByName (player.getName ());

    assertFalse (playerModel.existsPlayerWithName (player.getName ()));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testRemovePlayerByUnknownTurnOrderThrowsException ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);

    addSinglePlayerTo (playerModel);

    playerModel.removeByTurnOrder (PlayerTurnOrder.UNKNOWN);
  }

  @Test
  public void testRemoveNonExistentPlayerByTurnOrderDoesNotThrowException ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    playerModel.removeByTurnOrder (PlayerTurnOrder.FIFTH);
  }

  @Test
  public void testRemovePlayerByTurnOrderSuccessful ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final PlayerTurnOrder turnOrder = PlayerTurnOrder.FIRST;

    playerModel.requestToAdd (PlayerFactory.builder ("TestPlayer").turnOrder (turnOrder).toFactory ());
    playerModel.removeByTurnOrder (turnOrder);

    assertFalse (playerModel.existsPlayerWith (turnOrder));
  }

  @Test
  public void testRemovePlayerByTurnOrderCorrectsTurnOrdersFirstPlayer ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (MAX_PLAYERS);
    addNPlayersTo (playerModel, playerModel.getPlayerLimit ());
    final Map <Id, PlayerTurnOrder> originalTurnOrders = new HashMap <> ();
    PlayerTurnOrder turnOrder = PlayerTurnOrder.FIRST;
    for (final Id player : playerModel.getPlayerIds ())
    {
      playerModel.changeTurnOrderOfPlayer (player, turnOrder);
      originalTurnOrders.put (player, turnOrder);
      if (turnOrder.hasNextValid ()) turnOrder = turnOrder.nextValid ();
    }

    final Id removedPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    playerModel.removeByTurnOrder (PlayerTurnOrder.FIRST);
    assertFalse (playerModel.existsPlayerWith (removedPlayer));

    for (final Id player : playerModel.getPlayerIds ())
    {
      final PlayerTurnOrder originalTurnOrder = originalTurnOrders.get (player);
      // assert that each player's turn order went down by one since the first player was removed
      assertEquals (originalTurnOrder.getPosition () - 1, playerModel.turnOrderOf (player).getPosition ());
    }
  }

  @Test
  public void testRemovePlayerByTurnOrderCorrectsTurnOrdersSixthPlayer ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (MAX_PLAYERS);
    addNPlayersTo (playerModel, playerModel.getPlayerLimit ());
    final Map <Id, PlayerTurnOrder> originalTurnOrders = new HashMap <> ();
    PlayerTurnOrder turnOrder = PlayerTurnOrder.FIRST;
    for (final Id player : playerModel.getPlayerIds ())
    {
      playerModel.changeTurnOrderOfPlayer (player, turnOrder);
      originalTurnOrders.put (player, turnOrder);
      if (turnOrder.hasNextValid ()) turnOrder = turnOrder.nextValid ();
    }

    final PlayerTurnOrder removedTurnOrder = PlayerTurnOrder.SIXTH;
    final Id removedPlayer = playerModel.playerWith (removedTurnOrder);
    playerModel.removeByTurnOrder (removedTurnOrder);
    assertFalse (playerModel.existsPlayerWith (removedPlayer));

    for (final Id player : playerModel.getPlayerIds ())
    {
      final PlayerTurnOrder originalTurnOrder = originalTurnOrders.get (player);
      // if the original turn order precedes the removed index, it should stay the same.
      // otherwise, if it is subsequent, it should have been decremented
      if (originalTurnOrder.compareTo (removedTurnOrder) < 0)
      {
        assertEquals (originalTurnOrder.getPosition (), playerModel.turnOrderOf (player).getPosition ());
      }
      else
      {
        assertEquals (originalTurnOrder.getPosition () - 1, playerModel.turnOrderOf (player).getPosition ());
      }
    }
  }

  @Test
  public void testRemovePlayerByTurnOrderCorrectsTurnOrdersLastPlayer ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (MAX_PLAYERS);
    addNPlayersTo (playerModel, playerModel.getPlayerLimit ());
    final Map <Id, PlayerTurnOrder> originalTurnOrders = new HashMap <> ();
    PlayerTurnOrder turnOrder = PlayerTurnOrder.FIRST;
    for (final Id player : playerModel.getPlayerIds ())
    {
      playerModel.changeTurnOrderOfPlayer (player, turnOrder);
      originalTurnOrders.put (player, turnOrder);
      if (turnOrder.hasNextValid ()) turnOrder = turnOrder.nextValid ();
    }

    final PlayerTurnOrder removedTurnOrder = PlayerTurnOrder.TENTH;
    final Id removedPlayer = playerModel.playerWith (removedTurnOrder);
    playerModel.removeByTurnOrder (removedTurnOrder);
    assertFalse (playerModel.existsPlayerWith (removedPlayer));

    for (final Id player : playerModel.getPlayerIds ())
    {
      final PlayerTurnOrder originalTurnOrder = originalTurnOrders.get (player);
      assertEquals (originalTurnOrder.getPosition (), playerModel.turnOrderOf (player).getPosition ());
    }
  }

  private static ImmutableSet <Player> addNPlayersTo (final PlayerModel playerModel, final int playerCount)
  {
    assertTrue (playerModel.isEmpty ());
    assertTrue (playerCount <= playerModel.getPlayerLimit ());

    final ImmutableSet.Builder <Player> playerBuilder = ImmutableSet.builder ();

    for (int i = 1; i <= playerCount; ++i)
    {
      final Player player = PlayerFactory.create ("TestPlayer" + i);

      playerBuilder.add (player);

      assertTrue (singleResultFrom (playerModel.requestToAdd (factoryFrom (player))).isSuccessful ());
    }

    assertTrue (playerModel.getPlayerCount () == playerCount);

    return playerBuilder.build ();
  }

  private static Player addSinglePlayerTo (final PlayerModel playerModel)
  {
    assertTrue (playerModel.isEmpty ());
    assertTrue (playerModel.getPlayerLimit () >= 1);

    final Player player = PlayerFactory.create ("TestPlayer");

    assertTrue (singleResultFrom (playerModel.requestToAdd (factoryFrom (player))).succeeded ());
    assertTrue (playerModel.getPlayerCount () == 1);

    return player;
  }

  private static PlayerModel createPlayerModelWithLimitOf (final int playerLimit)
  {
    final GameRules gameRulesMock = mock (GameRules.class);
    when (gameRulesMock.getMinPlayers ()).thenReturn (MIN_PLAYERS);
    when (gameRulesMock.getMaxPlayers ()).thenReturn (MAX_PLAYERS);
    when (gameRulesMock.getPlayerLimit ()).thenReturn (playerLimit);
    when (gameRulesMock.getMinArmiesInHand ()).thenReturn (MIN_ARMIES_IN_PLAYER_HAND);
    when (gameRulesMock.getMaxArmiesInHand ()).thenReturn (MAX_ARMIES_IN_PLAYER_HAND);

    return new DefaultPlayerModel (gameRulesMock);
  }

  private static void removeAllPlayersFrom (final PlayerModel playerModel)
  {
    for (final Id player : playerModel.getPlayerIds ())
    {
      playerModel.remove (player);
    }

    assertTrue (playerModel.isEmpty ());
  }

  private static PlayerFactory factoryFrom (final Player player)
  {
    return PlayerFactory.from (ImmutableSet.of (player));
  }

  // trying to forget about this being necessary....
  private static <U, V extends ReturnStatus <U>> Result <U> singleResultFrom (final ImmutableCollection <V> results)
  {
    return results.asList ().get (0).getResult ();
  }
}
