package com.forerunnergames.peril.core.model.people.player;

import static com.forerunnergames.peril.core.model.people.player.PlayerFluency.colorOf;
import static com.forerunnergames.peril.core.model.people.player.PlayerFluency.turnOrderOf;
import static com.forerunnergames.tools.common.assets.AssetFluency.idOf;
import static com.forerunnergames.tools.common.assets.AssetFluency.nameOf;
import static com.forerunnergames.tools.common.assets.AssetFluency.withIdOf;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.forerunnergames.peril.core.model.people.person.PersonIdentity;
import com.forerunnergames.peril.core.model.rules.GameRules;
import com.forerunnergames.peril.core.shared.net.events.denied.ChangePlayerColorDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.PlayerLeaveGameDeniedEvent;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;

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

    assertTrue (playerModel.getArmiesInHandOf (idOf (player)) == armyCount);
  }

  @Test (expected = IllegalStateException.class)
  public void testAddArmiesToHandOfPlayerFailsWhenMaxArmiesInHand ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final Player player = addSinglePlayerTo (playerModel);

    playerModel.addArmiesToHandOf (idOf (player), MAX_ARMIES_IN_PLAYER_HAND);
    playerModel.addArmiesToHandOf (idOf (player), 1);
  }

  @Test
  public void testCanAddArmiesToHandOfPlayerOneArmy ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final Player player = addSinglePlayerTo (playerModel);

    assertTrue (playerModel.canAddArmiesToHandOf (idOf (player), 1));
  }

  @Test
  public void testCanRemoveAddArmiesFromHandOfPlayerOneArmy ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final Player player = addSinglePlayerTo (playerModel);

    playerModel.addArmiesToHandOf (idOf (player), 1);

    assertTrue (playerModel.canRemoveArmiesFromHandOf (idOf (player), 1));
  }

  @Test
  public void testCannotAddArmiesToHandOfPlayerMaxArmiesInHand ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final Player player = addSinglePlayerTo (playerModel);

    playerModel.addArmiesToHandOf (idOf (player), MAX_ARMIES_IN_PLAYER_HAND);

    assertFalse (playerModel.canAddArmiesToHandOf (idOf (player), 1));
  }

  @Test
  public void testCannotRemoveArmiesFromHandOfPlayerMinArmiesInHand ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final Player player = addSinglePlayerTo (playerModel);

    playerModel.addArmiesToHandOf (idOf (player), MIN_ARMIES_IN_PLAYER_HAND);

    assertFalse (playerModel.canRemoveArmiesFromHandOf (idOf (player), MIN_ARMIES_IN_PLAYER_HAND + 1));
  }

  @Test
  public void testChangeTurnOrderOfPlayer ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (2);
    addNPlayersTo (playerModel, 2);

    final Player player1 = playerModel.playerWith (PlayerTurnOrder.FIRST);
    final Player player2 = playerModel.playerWith (PlayerTurnOrder.SECOND);

    playerModel.changeTurnOrderOfPlayer (player1.getId (), PlayerTurnOrder.SECOND);

    assertTrue (turnOrderOf (player2).is (PlayerTurnOrder.FIRST) && turnOrderOf (player1).is (PlayerTurnOrder.SECOND));
  }

  @Test
  public void testExistsPlayerWithColor ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final PlayerColor color = PlayerColor.TEAL;

    playerModel.requestToAdd (PlayerFactory.builder ("Test Player").color (color).build ());

    assertTrue (playerModel.existsPlayerWith (color));
  }

  @Test
  public void testExistsPlayerWithId ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final Player player = PlayerFactory.create ("Test Player");

    playerModel.requestToAdd (player);

    assertTrue (playerModel.existsPlayerWith (idOf (player)));
  }

  @Test
  public void testExistsPlayerWithName ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final String name = "Test Player";

    playerModel.requestToAdd (PlayerFactory.create (name));

    assertTrue (playerModel.existsPlayerWith (name));
  }

  @Test
  public void testExistsPlayerWithName2 ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final String name = "Test Player";

    playerModel.requestToAdd (PlayerFactory.create (name));

    assertTrue (playerModel.existsPlayerWithName (name));
  }

  @Test
  public void testExistsPlayerWithPersonIdentity ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final PersonIdentity identity = PersonIdentity.SELF;

    playerModel.requestToAdd (PlayerFactory.builder ("Test Player").identity (identity).build ());

    assertTrue (playerModel.existsPlayerWith (identity));
  }

  @Test
  public void testExistsPlayerWithTurnOrder ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final PlayerTurnOrder turnOrder = PlayerTurnOrder.THIRD;

    playerModel.requestToAdd (PlayerFactory.builder ("Test Player").turnOrder (turnOrder).build ());

    assertTrue (playerModel.existsPlayerWith (turnOrder));
  }

  @Test
  public void testExistsPlayerWithUnknownColor ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final PlayerColor color = PlayerColor.UNKNOWN;

    playerModel.requestToAdd (PlayerFactory.builder ("Test Player").color (color).build ());

    assertFalse (playerModel.existsPlayerWith (color));
  }

  @Test
  public void testExistsPlayerWithUnknownTurnOrder ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final PlayerTurnOrder turnOrder = PlayerTurnOrder.UNKNOWN;

    playerModel.requestToAdd (PlayerFactory.builder ("Test Player").turnOrder (turnOrder).build ());

    assertFalse (playerModel.existsPlayerWith (turnOrder));
  }

  @Test
  public void testGetArmiesInHandOf ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final Player player = addSinglePlayerTo (playerModel);

    final int armyCount = 10;
    playerModel.addArmiesToHandOf (idOf (player), armyCount);

    assertTrue (playerModel.getArmiesInHandOf (idOf (player)) == armyCount);
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
    final ImmutableSet <Player> expectedPlayers = ImmutableSet.of (PlayerFactory.create ("Test Player 1"),
                    PlayerFactory.create ("Test Player 2"), PlayerFactory.create ("Test Player 3"));

    final PlayerModel playerModel = createPlayerModelWithLimitOf (expectedPlayers.size ());

    for (final Player player : expectedPlayers)
    {
      assertTrue (playerModel.requestToAdd (player).succeeded ());
    }

    final ImmutableSet <Player> actualPlayers = playerModel.getPlayers ();

    assertTrue (actualPlayers.containsAll (expectedPlayers));
    assertTrue (expectedPlayers.containsAll (actualPlayers));
  }

  @Test
  public void testHasArmiesInHandOf ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final Player player = addSinglePlayerTo (playerModel);

    final int armyCount = 10;

    playerModel.addArmiesToHandOf (idOf (player), armyCount);

    assertTrue (playerModel.hasArmiesInHandOf (idOf (player), armyCount));
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
  public void testNonExistentPlayerWithIdThrowsException ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);

    assertTrue (playerModel.isEmpty ());

    playerModel.playerWith (new Id (5));
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

    for (final Player player1 : playerModel.getPlayers ())
    {
      assertTrue (colorOf (player1).isNot (PlayerColor.UNKNOWN));

      for (final Player player2 : playerModel.getPlayers ())
      {
        if (player2.is (player1)) continue;

        assertTrue (colorOf (player2).isNot (colorOf (player1)));
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

    for (final Player player1 : playerModel.getPlayers ())
    {
      for (final Player player2 : playerModel.getPlayers ())
      {
        if (player2.is (player1)) continue;

        assertTrue (idOf (player2).isNot (idOf (player1)));
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

    for (final Player player1 : playerModel.getPlayers ())
    {
      assertTrue (turnOrderOf (player1).isNot (PlayerTurnOrder.UNKNOWN));

      for (final Player player2 : playerModel.getPlayers ())
      {
        if (player2.is (player1)) continue;

        assertTrue (turnOrderOf (player2).isNot (turnOrderOf (player1)));
      }
    }
  }

  @Test
  public void testPlayerWithColor ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (2);
    final PlayerColor player1Color = PlayerColor.CYAN;
    final Player player1 = PlayerFactory.builder ("Test Player 1").color (player1Color).build ();
    final Player player2 = PlayerFactory.builder ("Test Player 2").color (PlayerColor.GOLD).build ();

    playerModel.requestToAdd (player1);
    playerModel.requestToAdd (player2);

    assertTrue (playerModel.playerWith (player1Color).is (player1));
  }

  @Test
  public void testPlayerWithId ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (2);
    final Id player1Id = new Id (1);
    final Player player1 = new DefaultPlayer ("Test Player 1", player1Id, PersonIdentity.UNKNOWN, PlayerColor.UNKNOWN,
                    PlayerTurnOrder.UNKNOWN);
    final Player player2 = new DefaultPlayer ("Test Player 2", new Id (2), PersonIdentity.UNKNOWN, PlayerColor.UNKNOWN,
                    PlayerTurnOrder.UNKNOWN);

    playerModel.requestToAdd (player1);
    playerModel.requestToAdd (player2);

    assertTrue (playerModel.playerWith (player1Id).is (player1));
  }

  @Test
  public void testPlayerWithTurnOrder ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (2);
    final PlayerTurnOrder player1TurnOrder = PlayerTurnOrder.NINTH;
    final Player player1 = PlayerFactory.builder ("Test Player 1").turnOrder (player1TurnOrder).build ();
    final Player player2 = PlayerFactory.builder ("Test Player 2").turnOrder (PlayerTurnOrder.SEVENTH).build ();

    playerModel.requestToAdd (player1);
    playerModel.requestToAdd (player2);

    assertTrue (playerModel.playerWith (player1TurnOrder).is (player1));
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

    playerModel.addArmiesToHandOf (idOf (player), armies);
    playerModel.removeArmiesFromHandOf (idOf (player), armies);

    assertTrue (playerModel.getArmiesInHandOf (idOf (player)) == 0);
  }

  @Test (expected = IllegalStateException.class)
  public void testRemoveArmiesFromHandOfPlayerFailsWhenMinArmiesInHand ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final Player player = addSinglePlayerTo (playerModel);

    assertTrue (playerModel.hasArmiesInHandOf (idOf (player), MIN_ARMIES_IN_PLAYER_HAND));

    playerModel.removeArmiesFromHandOf (idOf (player), 1);
  }

  @Test
  public void testRequestAddPlayerFailedWithDuplicateColor ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (2);
    final Player player1 = PlayerFactory.builder ("Test Player 1").color (PlayerColor.BLUE).build ();
    final Player player2 = PlayerFactory.builder ("Test Player 2").color (PlayerColor.BLUE).build ();

    assertTrue (playerModel.requestToAdd (player1).succeeded ());
    assertTrue (playerModel.requestToAdd (player2).failedBecauseOf (PlayerJoinGameDeniedEvent.REASON.DUPLICATE_COLOR));
    assertTrue (playerModel.playerCountIs (1));
  }

  @Test
  public void testRequestAddPlayerFailedWithDuplicateId ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (2);
    final Id duplicateId = new Id (1);
    final Player player1 = new DefaultPlayer ("Test Player 1", duplicateId, PersonIdentity.UNKNOWN,
                    PlayerColor.UNKNOWN, PlayerTurnOrder.UNKNOWN);
    final Player player2 = new DefaultPlayer ("Test Player 2", duplicateId, PersonIdentity.UNKNOWN,
                    PlayerColor.UNKNOWN, PlayerTurnOrder.UNKNOWN);

    assertTrue (playerModel.requestToAdd (player1).succeeded ());
    assertTrue (playerModel.requestToAdd (player2).failedBecauseOf (PlayerJoinGameDeniedEvent.REASON.DUPLICATE_ID));
    assertTrue (playerModel.playerCountIs (1));
  }

  @Test
  public void testRequestAddPlayerFailedWithDuplicateSelfIdentity ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (2);
    final Player player1 = PlayerFactory.builder ("Test Player 1").identity (PersonIdentity.SELF).build ();
    final Player player2 = PlayerFactory.builder ("Test Player 2").identity (PersonIdentity.SELF).build ();

    assertTrue (playerModel.requestToAdd (player1).succeeded ());
    assertTrue (playerModel.requestToAdd (player2).failedBecauseOf (
                    PlayerJoinGameDeniedEvent.REASON.DUPLICATE_SELF_IDENTITY));
    assertTrue (playerModel.playerCountIs (1));
  }

  @Test
  public void testRequestAddPlayerFailedWithDuplicateTurnOrder ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (2);
    final Player player1 = PlayerFactory.builder ("Test Player 1").turnOrder (PlayerTurnOrder.THIRD).build ();
    final Player player2 = PlayerFactory.builder ("Test Player 2").turnOrder (PlayerTurnOrder.THIRD).build ();

    assertTrue (playerModel.requestToAdd (player1).succeeded ());
    assertTrue (playerModel.requestToAdd (player2).failedBecauseOf (
                    PlayerJoinGameDeniedEvent.REASON.DUPLICATE_TURN_ORDER));
    assertTrue (playerModel.playerCountIs (1));
  }

  @Test
  public void testRequestAddPlayerFailedWithGameIsFull ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (MAX_PLAYERS);

    addNPlayersTo (playerModel, MAX_PLAYERS);

    assertTrue (playerModel.requestToAdd (PlayerFactory.create ("Test Player X")).failedBecauseOf (
                    PlayerJoinGameDeniedEvent.REASON.GAME_IS_FULL));
  }

  @Test
  public void testRequestAddPlayerFailedWitheDuplicateName ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (2);
    final String duplicateName = "Test Player";
    final Player player1 = PlayerFactory.create (duplicateName, PersonIdentity.SELF, PlayerColor.PINK,
                    PlayerTurnOrder.EIGHTH);
    final Player player2 = PlayerFactory.create (duplicateName, PersonIdentity.NON_SELF, PlayerColor.BLUE,
                    PlayerTurnOrder.THIRD);

    assertTrue (playerModel.requestToAdd (player1).succeeded ());
    assertTrue (playerModel.requestToAdd (player2).failedBecauseOf (PlayerJoinGameDeniedEvent.REASON.DUPLICATE_NAME));
    assertTrue (playerModel.playerCountIs (1));
  }

  @Test
  public void testRequestAddPlayerSucceeded ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final String name = "Test Player";
    final PlayerColor color = PlayerColor.TEAL;
    final PlayerTurnOrder turnOrder = PlayerTurnOrder.FIFTH;
    final PersonIdentity identity = PersonIdentity.SELF;
    final Player player = PlayerFactory.create (name, identity, color, turnOrder);

    assertTrue (playerModel.requestToAdd (player).succeeded ());
    assertTrue (playerModel.playerCountIs (1));
    assertTrue (playerModel.existsPlayerWith (name));
    assertTrue (playerModel.existsPlayerWith (color));
    assertTrue (playerModel.existsPlayerWith (turnOrder));
    assertTrue (playerModel.playerWith (name).has (identity));
  }

  @Test
  public void testRequestToChangeColorOfPlayerFailedWithAlreadyTaken ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (2);
    final PlayerColor player1Color = PlayerColor.PINK;
    final PlayerColor player2Color = PlayerColor.SILVER;

    final Player player1 = PlayerFactory.builder ("Test Player 1").color (player1Color).build ();
    final Player player2 = PlayerFactory.builder ("Test Player 2").color (player2Color).build ();

    playerModel.requestToAdd (player1);
    playerModel.requestToAdd (player2);

    assertTrue (playerModel.requestToChangeColorOfPlayer (withIdOf (player2), player1Color).failedBecauseOf (
                    ChangePlayerColorDeniedEvent.REASON.COLOR_ALREADY_TAKEN));
    assertTrue (playerModel.playerWith (idOf (player1)).has (player1Color));
    assertTrue (playerModel.playerWith (idOf (player2)).has (player2Color));
    assertTrue (playerModel.existsPlayerWith (player1Color));
    assertTrue (playerModel.existsPlayerWith (player2Color));
  }

  @Test
  public void testRequestToChangeColorOfPlayerSucceeded ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final PlayerColor oldColor = PlayerColor.BLUE;
    final Player player = PlayerFactory.builder ("Test Player").color (oldColor).build ();
    final PlayerColor newColor = PlayerColor.BROWN;

    playerModel.requestToAdd (player);

    assertTrue (playerModel.requestToChangeColorOfPlayer (withIdOf (player), newColor).succeeded ());
    assertTrue (playerModel.existsPlayerWith (newColor));
    assertFalse (playerModel.existsPlayerWith (oldColor));
  }

  @Test
  public void testRequestToChangeColorOfPlayerToSameColorFailedWithRequestedColorEqualsExistingColor ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final PlayerColor color = PlayerColor.PINK;
    final Player player = PlayerFactory.builder ("Test Player 1").color (color).build ();

    playerModel.requestToAdd (player);

    assertTrue (playerModel.requestToChangeColorOfPlayer (withIdOf (player), color).failedBecauseOf (
                    ChangePlayerColorDeniedEvent.REASON.REQUESTED_COLOR_EQUALS_EXISTING_COLOR));
    assertTrue (playerModel.existsPlayerWith (color));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testRequestToRemovePlayerByColorFailedWithIllegalArgumentException ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);

    addNPlayersTo (playerModel, 1);

    playerModel.requestToRemoveByColor (PlayerColor.UNKNOWN);
  }

  @Test
  public void testRequestToRemovePlayerByColorFailedWithPlayerDoesNotExist ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);

    assertTrue (playerModel.requestToRemoveByColor (PlayerColor.BROWN).failedBecauseOf (
                    PlayerLeaveGameDeniedEvent.REASON.PLAYER_DOES_NOT_EXIST));
  }

  @Test
  public void testRequestToRemovePlayerByColorSucceeded ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);

    final PlayerColor color = PlayerColor.PINK;

    playerModel.requestToAdd (PlayerFactory.builder ("Test Player").color (color).build ());

    assertTrue (playerModel.requestToRemoveByColor (color).succeeded ());
  }

  @Test
  public void testRequestToRemovePlayerByIdFailedWithPlayerDoesNotExist ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);

    assertTrue (playerModel.requestToRemoveById (new Id (1)).failedBecauseOf (
                    PlayerLeaveGameDeniedEvent.REASON.PLAYER_DOES_NOT_EXIST));
  }

  @Test
  public void testRequestToRemovePlayerByIdSucceeded ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final Player player = PlayerFactory.create ("Test Player");
    final Id id = idOf (player);

    playerModel.requestToAdd (player);

    assertTrue (playerModel.requestToRemoveById (id).succeeded ());
  }

  @Test
  public void testRequestToRemovePlayerByNameFailedWithPlayerDoesNotExist ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);

    assertTrue (playerModel.requestToRemoveByName ("Non-Existent Player").failedBecauseOf (
                    PlayerLeaveGameDeniedEvent.REASON.PLAYER_DOES_NOT_EXIST));
  }

  @Test
  public void testRequestToRemovePlayerByNameSucceeded ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final Player player = addSinglePlayerTo (playerModel);

    assertTrue (playerModel.requestToRemoveByName (nameOf (player)).succeeded ());
  }

  @Test (expected = IllegalArgumentException.class)
  public void testRequestToRemovePlayerByTurnOrderFailedWithIllegalArgumentException ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);

    addSinglePlayerTo (playerModel);

    playerModel.requestToRemoveByTurnOrder (PlayerTurnOrder.UNKNOWN);
  }

  @Test
  public void testRequestToRemovePlayerByTurnOrderFailedWithPlayerDoesNotExist ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);

    assertTrue (playerModel.requestToRemoveByTurnOrder (PlayerTurnOrder.FIFTH).failedBecauseOf (
                    PlayerLeaveGameDeniedEvent.REASON.PLAYER_DOES_NOT_EXIST));
  }

  @Test
  public void testRequestToRemovePlayerByTurnOrderSucceeded ()
  {
    final PlayerModel playerModel = createPlayerModelWithLimitOf (1);
    final PlayerTurnOrder turnOrder = PlayerTurnOrder.FIRST;

    playerModel.requestToAdd (PlayerFactory.builder ("Test Player").turnOrder (turnOrder).build ());

    assertTrue (playerModel.requestToRemoveByTurnOrder (turnOrder).succeeded ());
  }

  private ImmutableSet <Player> addNPlayersTo (final PlayerModel playerModel, final int playerCount)
  {
    assertTrue (playerModel.isEmpty ());
    assertTrue (playerCount <= playerModel.getPlayerLimit ());

    final ImmutableSet.Builder <Player> playerBuilder = ImmutableSet.builder ();

    for (int i = 1; i <= playerCount; ++i)
    {
      final Player player = PlayerFactory.create ("Test Player " + i);

      playerBuilder.add (player);

      assertTrue (playerModel.requestToAdd (player).isSuccessful ());
    }

    assertTrue (playerModel.getPlayerCount () == playerCount);

    return playerBuilder.build ();
  }

  private Player addSinglePlayerTo (final PlayerModel playerModel)
  {
    assertTrue (playerModel.isEmpty ());
    assertTrue (playerModel.getPlayerLimit () >= 1);

    final Player player = PlayerFactory.create ("Test Player");

    assertTrue (playerModel.requestToAdd (player).succeeded ());
    assertTrue (playerModel.getPlayerCount () == 1);

    return player;
  }

  private PlayerModel createPlayerModelWithLimitOf (final int playerLimit)
  {
    final GameRules gameRulesMock = mock (GameRules.class);
    when (gameRulesMock.getMinPlayers ()).thenReturn (MIN_PLAYERS);
    when (gameRulesMock.getMaxPlayers ()).thenReturn (MAX_PLAYERS);
    when (gameRulesMock.getPlayerLimit ()).thenReturn (playerLimit);
    when (gameRulesMock.getMinArmiesInHand ()).thenReturn (MIN_ARMIES_IN_PLAYER_HAND);
    when (gameRulesMock.getMaxArmiesInHand ()).thenReturn (MAX_ARMIES_IN_PLAYER_HAND);

    return new PlayerModel (gameRulesMock);
  }

  private void removeAllPlayersFrom (final PlayerModel playerModel)
  {
    for (final Player player : playerModel.getPlayers ())
    {
      assertTrue (playerModel.requestToRemove (player).succeeded ());
    }

    assertTrue (playerModel.isEmpty ());
  }
}
