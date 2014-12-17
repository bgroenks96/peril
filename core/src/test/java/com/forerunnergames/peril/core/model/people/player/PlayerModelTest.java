package com.forerunnergames.peril.core.model.people.player;

import static com.forerunnergames.peril.core.model.people.player.PlayerFluency.*;

import static org.hamcrest.core.Is.is;

import static org.junit.Assert.*;

import com.forerunnergames.peril.core.model.people.person.PersonIdentity;
import com.forerunnergames.peril.core.model.settings.GameSettings;
import com.forerunnergames.peril.core.shared.net.events.denied.ChangePlayerColorDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.ChangePlayerLimitDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.PlayerLeaveGameDeniedEvent;
import com.forerunnergames.tools.common.Id;
import com.forerunnergames.tools.common.Result;

import com.google.common.collect.ImmutableSet;

import org.junit.Before;
import org.junit.Test;

public class PlayerModelTest
{
  private static final int INITIAL_PLAYER_LIMIT = 3;
  private PlayerModel playerModel;

  @Before
  public void setup()
  {
    playerModel = new PlayerModel (INITIAL_PLAYER_LIMIT);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testNegativeInitialPlayerLimitFails()
  {
    new PlayerModel (-1);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testInitialPlayerLimitBeyondMaxPlayersFails()
  {
    new PlayerModel (GameSettings.MAX_PLAYERS + 1);
  }

  @Test
  public void testPlayerIdsUnique()
  {
    addMaxPlayers();

    for (final Player player1 : playerModel.getPlayers())
    {
      for (final Player player2 : playerModel.getPlayers())
      {
        if (player2.is (player1)) continue;

        assertTrue (idOf(player2).isNot (idOf (player1)));
      }
    }
  }

  private void addMaxPlayers()
  {
    playerModel.requestToSetPlayerLimitTo (GameSettings.MAX_PLAYERS);

    Result <PlayerJoinGameDeniedEvent.REASON> result;
    Player player;

    for (int i = 1; i <= playerModel.getPlayerLimit(); ++i)
    {
      player = PlayerFactory.create ("Test Player " + i);

      result = playerModel.requestToAdd (player);

      if (result.isFailure())
      {
        throw new IllegalStateException ("Failed to add player: " + player + ".\nReason: " + result.getFailureReason());
      }
    }

    if (playerModel.playerCountIsNot (GameSettings.MAX_PLAYERS))
    {
      throw new IllegalStateException ("Could not add max players.\nCurrent player count: " +
              playerModel.getPlayerCount() + "\nPlayers:\n" + playerModel.getPlayers());
    }
  }

  @Test
  public void testPlayerColorsUniqueAndValid()
  {
    addMaxPlayers();

    for (final Player player1 : playerModel.getPlayers())
    {
      assertTrue (colorOf(player1).isNot (PlayerColor.UNKNOWN));

      for (final Player player2 : playerModel.getPlayers())
      {
        if (player2.is (player1)) continue;

        assertTrue (colorOf (player2).isNot (colorOf (player1)));
      }
    }
  }

  @Test
  public void testPlayerTurnOrdersUniqueAndValid()
  {
    addMaxPlayers();

    for (final Player player1 : playerModel.getPlayers())
    {
      assertTrue (turnOrderOf (player1).isNot (PlayerTurnOrder.UNKNOWN));

      for (final Player player2 : playerModel.getPlayers())
      {
        if (player2.is (player1)) continue;

        assertTrue (turnOrderOf (player2).isNot (turnOrderOf (player1)));
      }
    }
  }

  @Test
  public void testGetPlayerCount()
  {
    assertThat (playerModel.getPlayerCount(), is (0));
    assertTrue (playerModel.requestToAdd (PlayerFactory.create ("Test Player 1")).succeeded());
    assertTrue (playerModel.requestToAdd (PlayerFactory.create ("Test Player 2")).succeeded());
    assertTrue (playerModel.requestToAdd (PlayerFactory.create ("Test Player 3")).succeeded());
    assertThat (playerModel.getPlayerCount(), is (3));
  }

  @Test
  public void testPlayerCountIs()
  {
    assertTrue (playerModel.playerCountIs (0));
    assertTrue (playerModel.requestToAdd (PlayerFactory.create ("Test Player 1")).succeeded());
    assertTrue (playerModel.requestToAdd (PlayerFactory.create ("Test Player 2")).succeeded());
    assertTrue (playerModel.requestToAdd (PlayerFactory.create ("Test Player 3")).succeeded());
    assertTrue (playerModel.playerCountIs (3));
  }

  @Test
  public void testPlayerCountIsNot()
  {
    assertTrue (playerModel.playerCountIsNot (1));
    assertTrue (playerModel.requestToAdd (PlayerFactory.create ("Test Player 1")).succeeded());
    assertTrue (playerModel.requestToAdd (PlayerFactory.create ("Test Player 2")).succeeded());
    assertTrue (playerModel.requestToAdd (PlayerFactory.create ("Test Player 3")).succeeded());
    assertTrue (playerModel.playerCountIsNot (4));
  }

  @Test
  public void testGetPlayers()
  {
    final ImmutableSet <Player> expectedPlayers = ImmutableSet.of (
            PlayerFactory.create ("Test Player 1"),
            PlayerFactory.create ("Test Player 2"),
            PlayerFactory.create ("Test Player 3"));

    for (final Player player : expectedPlayers)
    {
      playerModel.requestToAdd (player);
    }

    final ImmutableSet <Player> actualPlayers = playerModel.getPlayers();

    assertTrue (actualPlayers.containsAll (expectedPlayers));
    assertTrue (expectedPlayers.containsAll (actualPlayers));
  }

  @Test
  public void testGetPlayerLimit()
  {
    final int limit = 5;

    assertTrue (playerModel.requestToSetPlayerLimitTo (limit).succeeded());
    assertThat (playerModel.getPlayerLimit(), is (limit));
  }

  @Test
  public void testPlayerLimitIs()
  {
    final int limit = 8;

    assertTrue (playerModel.requestToSetPlayerLimitTo (limit).succeeded());
    assertTrue (playerModel.playerLimitIs (limit));
  }

  @Test
  public void testIsFull()
  {
    assertFalse (playerModel.isFull());

    playerModel.requestToAdd (PlayerFactory.create ("Test Player 1"));
    playerModel.requestToAdd (PlayerFactory.create ("Test Player 2"));
    playerModel.requestToAdd (PlayerFactory.create ("Test Player 3"));

    assertTrue (playerModel.isFull());

    playerModel.requestToRemoveByName ("Test Player 1");
    playerModel.requestToRemoveByName ("Test Player 2");
    playerModel.requestToRemoveByName ("Test Player 3");

    assertFalse (playerModel.isFull());
  }

  @Test
  public void testIsEmpty()
  {
    assertTrue (playerModel.isEmpty());

    playerModel.requestToAdd (PlayerFactory.create ("Test Player 1"));
    playerModel.requestToAdd (PlayerFactory.create ("Test Player 2"));
    playerModel.requestToAdd (PlayerFactory.create ("Test Player 3"));

    assertFalse (playerModel.isEmpty());

    playerModel.requestToRemoveByName ("Test Player 1");
    playerModel.requestToRemoveByName ("Test Player 2");
    playerModel.requestToRemoveByName ("Test Player 3");

    assertTrue (playerModel.isEmpty());
  }

  @Test
  public void testExistsPlayerWithPersonIdentity()
  {
    final PersonIdentity identity = PersonIdentity.SELF;

    playerModel.requestToAdd (PlayerFactory.builder ("Test Player").withIdentity (identity).build());

    assertTrue (playerModel.existsPlayerWith (identity));
  }

  @Test
  public void testExistsPlayerWithId()
  {
    final Player player = PlayerFactory.create ("Test Player");

    playerModel.requestToAdd (player);

    assertTrue (playerModel.existsPlayerWith (idOf (player)));
  }

  @Test
  public void testExistsPlayerWithName()
  {
    final String name = "Test Player";

    playerModel.requestToAdd (PlayerFactory.create (name));

    assertTrue (playerModel.existsPlayerWith (name));
  }

  @Test
  public void testExistsPlayerWithName2()
  {
    final String name = "Test Player";

    playerModel.requestToAdd (PlayerFactory.create (name));

    assertTrue (playerModel.existsPlayerWithName (name));
  }

  @Test
  public void testExistsPlayerWithColor()
  {
    final PlayerColor color = PlayerColor.ORANGE;

    playerModel.requestToAdd (PlayerFactory.builder("Test Player").withColor(color).build());

    assertTrue (playerModel.existsPlayerWith (color));
  }

  @Test
  public void testExistsPlayerWithUnknownColor()
  {
    final PlayerColor color = PlayerColor.UNKNOWN;

    playerModel.requestToAdd (PlayerFactory.builder("Test Player").withColor(color).build());

    assertFalse (playerModel.existsPlayerWith (color));
  }

  @Test
  public void testExistsPlayerWithTurnOrder()
  {
    final PlayerTurnOrder turnOrder = PlayerTurnOrder.THIRD;

    playerModel.requestToAdd (PlayerFactory.builder("Test Player").withTurnOrder(turnOrder).build());

    assertTrue (playerModel.existsPlayerWith (turnOrder));
  }

  @Test
  public void testExistsPlayerWithUnknownTurnOrder()
  {
    final PlayerTurnOrder turnOrder = PlayerTurnOrder.UNKNOWN;

    playerModel.requestToAdd (PlayerFactory.builder("Test Player").withTurnOrder(turnOrder).build());

    assertFalse (playerModel.existsPlayerWith (turnOrder));
  }

  @Test
  public void testRequestToSetPlayerLimitToSucceeded()
  {
    final int limit = 0;

    assertTrue (playerModel.requestToSetPlayerLimitTo (limit).succeeded());
    assertTrue (playerModel.playerLimitIs (limit));
  }

  @Test
  public void testRequestToSetPlayerLimitToNegativeValueFailed()
  {
    final int oldLimit = playerModel.getPlayerLimit();
    final int newLimit = -1;

    assertTrue (playerModel.requestToSetPlayerLimitTo (newLimit).failedBecauseOf (ChangePlayerLimitDeniedEvent.REASON.CANNOT_DECREASE_BELOW_ZERO));
    assertTrue (playerModel.playerLimitIs (oldLimit));
  }

  @Test
  public void testRequestToSetPlayerLimitToInvalidPositiveValueFailed()
  {
    final int oldLimit = playerModel.getPlayerLimit();
    final int newLimit = GameSettings.MAX_PLAYERS + 1;

    assertTrue (playerModel.requestToSetPlayerLimitTo (newLimit).failedBecauseOf (ChangePlayerLimitDeniedEvent.REASON.CANNOT_INCREASE_ABOVE_MAX_PLAYERS));
    assertTrue (playerModel.playerLimitIs (oldLimit));
  }

  @Test
  public void testRequestToSetPlayerLimitToBelowCurrentPlayerCountFailed()
  {
    playerModel.requestToAdd (PlayerFactory.create ("Test Player"));

    final int oldLimit = playerModel.getPlayerLimit();
    final int newLimit = playerModel.getPlayerCount() - 1;

    assertTrue (playerModel.requestToSetPlayerLimitTo(newLimit).failedBecauseOf (ChangePlayerLimitDeniedEvent.REASON.CANNOT_DECREASE_BELOW_CURRENT_PLAYER_COUNT));
    assertTrue (playerModel.playerLimitIs (oldLimit));
  }

  @Test
  public void testRequestToSetPlayerLimitToCurrentPlayerLimitFailed()
  {
    final int currentLimit = playerModel.getPlayerLimit();

    assertTrue (playerModel.requestToSetPlayerLimitTo(currentLimit).failedBecauseOf (ChangePlayerLimitDeniedEvent.REASON.REQUESTED_LIMIT_EQUALS_EXISTING_LIMIT));
    assertTrue (playerModel.playerLimitIs (currentLimit));
  }

  @Test
  public void testRequestToSetPlayerLimitToExtremeMinimumValueFailed()
  {
    final int oldLimit = playerModel.getPlayerLimit();
    final int newLimit = Integer.MIN_VALUE;

    assertTrue (playerModel.requestToSetPlayerLimitTo(newLimit).failedBecauseOf (ChangePlayerLimitDeniedEvent.REASON.CANNOT_DECREASE_BELOW_ZERO));
    assertTrue (playerModel.playerLimitIs (oldLimit));
  }

  @Test
  public void testRequestToSetPlayerLimitToExtremeMaximumValueFailed()
  {
    final int oldLimit = playerModel.getPlayerLimit();
    final int newLimit = Integer.MAX_VALUE;

    assertTrue (playerModel.requestToSetPlayerLimitTo(newLimit).failedBecauseOf (ChangePlayerLimitDeniedEvent.REASON.CANNOT_INCREASE_ABOVE_MAX_PLAYERS));
    assertTrue (playerModel.playerLimitIs (oldLimit));
  }

  @Test
  public void testRequestToChangePlayerLimitBySucceeded()
  {
    final int oldLimit = playerModel.getPlayerLimit();
    final int delta = -2;
    final int newLimit = oldLimit + delta;

    assertTrue (playerModel.requestToChangePlayerLimitBy (delta).succeeded());
    assertTrue (playerModel.playerLimitIs (newLimit));
  }

  @Test
  public void testRequestToChangePlayerLimitByMaxValidNegativeDeltaSucceeded()
  {
    final int oldLimit = playerModel.getPlayerLimit();
    final int delta = -oldLimit;
    final int newLimit = oldLimit + delta;

    assertTrue (playerModel.requestToChangePlayerLimitBy (delta).succeeded());
    assertTrue (playerModel.playerLimitIs (newLimit));
  }

  @Test
  public void testRequestToChangePlayerLimitByMaxValidPositiveDeltaSucceeded()
  {
    final int oldLimit = playerModel.getPlayerLimit();
    final int delta = GameSettings.MAX_PLAYERS - oldLimit;
    final int newLimit = oldLimit + delta;

    assertTrue (playerModel.requestToChangePlayerLimitBy (delta).succeeded());
    assertTrue (playerModel.playerLimitIs (newLimit));
  }

  @Test
  public void testRequestToChangePlayerLimitByInvalidPositiveDeltaFailed()
  {
    final int oldLimit = playerModel.getPlayerLimit();
    final int delta = GameSettings.MAX_PLAYERS + 1;

    assertTrue (playerModel.requestToChangePlayerLimitBy(delta).failedBecauseOf (ChangePlayerLimitDeniedEvent.REASON.CANNOT_INCREASE_ABOVE_MAX_PLAYERS));
    assertTrue (playerModel.playerLimitIs (oldLimit));
  }

  @Test
  public void testRequestToChangePlayerLimitByInvalidNegativeDeltaFailed()
  {
    final int oldLimit = playerModel.getPlayerLimit();
    final int delta = -GameSettings.MAX_PLAYERS - 1;

    assertTrue (playerModel.requestToChangePlayerLimitBy(delta).failedBecauseOf (ChangePlayerLimitDeniedEvent.REASON.CANNOT_DECREASE_BELOW_ZERO));
    assertTrue (playerModel.playerLimitIs (oldLimit));
  }

  @Test
  public void testRequestToChangePlayerLimitByZeroDeltaFailed()
  {
    final int oldLimit = playerModel.getPlayerLimit();
    final int delta = 0;

    assertTrue (playerModel.requestToChangePlayerLimitBy(delta).failedBecauseOf (ChangePlayerLimitDeniedEvent.REASON.REQUESTED_LIMIT_EQUALS_EXISTING_LIMIT));
    assertTrue (playerModel.playerLimitIs (oldLimit));
  }

  @Test
  public void testRequestToChangePlayerLimitByDeltaLessThanCurrentPlayerCountFailed()
  {
    playerModel.requestToSetPlayerLimitTo (10);
    assertTrue (playerModel.playerLimitIs (10));

    playerModel.requestToAdd (PlayerFactory.create ("Test Player 1"));
    playerModel.requestToAdd (PlayerFactory.create ("Test Player 2"));
    playerModel.requestToAdd (PlayerFactory.create ("Test Player 3"));
    assertTrue (playerModel.playerCountIs (3));

    final int oldLimit = playerModel.getPlayerLimit();
    final int delta = -oldLimit + playerModel.getPlayerCount() - 1;

    assertTrue (playerModel.requestToChangePlayerLimitBy(delta).failedBecauseOf (ChangePlayerLimitDeniedEvent.REASON.CANNOT_DECREASE_BELOW_CURRENT_PLAYER_COUNT));
    assertTrue (playerModel.playerLimitIs (oldLimit));
  }

  @Test
  public void testRequestToChangePlayerLimitByExtremeMinimumDeltaFailed()
  {
    final int oldLimit = playerModel.getPlayerLimit();
    final int delta = Integer.MIN_VALUE;

    assertTrue (playerModel.requestToChangePlayerLimitBy(delta).failedBecauseOf (ChangePlayerLimitDeniedEvent.REASON.CANNOT_DECREASE_BELOW_ZERO));
    assertTrue (playerModel.playerLimitIs (oldLimit));
  }

  @Test
  public void testRequestToChangePlayerLimitByExtremeMaximumDeltaFailed()
  {
    final int oldLimit = playerModel.getPlayerLimit();
    final int delta = Integer.MAX_VALUE;

    assertTrue (playerModel.requestToChangePlayerLimitBy(delta).failedBecauseOf (ChangePlayerLimitDeniedEvent.REASON.CANNOT_INCREASE_ABOVE_MAX_PLAYERS));
    assertTrue (playerModel.playerLimitIs (oldLimit));
  }

  @Test
  public void testZeroPlayerLimitGameIsEmptyButNotFull()
  {
    playerModel.requestToSetPlayerLimitTo (0);

    assertTrue (playerModel.playerLimitIs (0));
    assertTrue (playerModel.isEmpty());
    assertFalse (playerModel.isFull());
  }

  @Test
  public void testRequestAddPlayerSucceeded()
  {
    final String name = "Test Player";
    final PlayerColor color = PlayerColor.ORANGE;
    final PlayerTurnOrder turnOrder = PlayerTurnOrder.FIFTH;
    final PersonIdentity identity = PersonIdentity.SELF;
    final Player player = PlayerFactory.create (name, identity, color, turnOrder);

    assertTrue (playerModel.requestToAdd(player).succeeded());
    assertTrue (playerModel.playerCountIs (1));
    assertTrue (playerModel.existsPlayerWith (name));
    assertTrue (playerModel.existsPlayerWith (color));
    assertTrue (playerModel.existsPlayerWith (turnOrder));
    assertTrue (playerModel.playerWith (name).has (identity));
  }

  @Test
  public void testRequestAddPlayerFailedWitheDuplicateName()
  {
    final String duplicateName = "Test Player";
    final Player player1 = PlayerFactory.create (duplicateName, PersonIdentity.SELF, PlayerColor.PINK, PlayerTurnOrder.EIGHTH);
    final Player player2 = PlayerFactory.create (duplicateName, PersonIdentity.NON_SELF, PlayerColor.BLUE, PlayerTurnOrder.THIRD);

    assertTrue (playerModel.requestToAdd(player1).succeeded());
    assertTrue (playerModel.requestToAdd(player2).failedBecauseOf (PlayerJoinGameDeniedEvent.REASON.DUPLICATE_NAME));
    assertTrue (playerModel.playerCountIs (1));
  }

  @Test
  public void testRequestAddPlayerFailedWithDuplicateSelfIdentity()
  {
    final Player player1 = PlayerFactory.builder("Test Player 1").withIdentity(PersonIdentity.SELF).build();
    final Player player2 = PlayerFactory.builder("Test Player 2").withIdentity(PersonIdentity.SELF).build();

    assertTrue (playerModel.requestToAdd(player1).succeeded());
    assertTrue (playerModel.requestToAdd(player2).failedBecauseOf (PlayerJoinGameDeniedEvent.REASON.DUPLICATE_SELF_IDENTITY));
    assertTrue (playerModel.playerCountIs (1));
  }

  @Test
  public void testRequestAddPlayerFailedWithGameIsFull()
  {
    addMaxPlayers();

    assertTrue (playerModel.requestToAdd (PlayerFactory.create ("Test Player X")).failedBecauseOf (PlayerJoinGameDeniedEvent.REASON.GAME_IS_FULL));
  }

  @Test
  public void testRequestAddPlayerFailedWithDuplicateId()
  {
    final Id duplicateId = new Id (1);
    final Player player1 = new DefaultPlayer ("Test Player 1", duplicateId, PersonIdentity.UNKNOWN, PlayerColor.UNKNOWN, PlayerTurnOrder.UNKNOWN);
    final Player player2 = new DefaultPlayer ("Test Player 2", duplicateId, PersonIdentity.UNKNOWN, PlayerColor.UNKNOWN, PlayerTurnOrder.UNKNOWN);

    assertTrue (playerModel.requestToAdd(player1).succeeded());
    assertTrue (playerModel.requestToAdd(player2).failedBecauseOf (PlayerJoinGameDeniedEvent.REASON.DUPLICATE_ID));
    assertTrue (playerModel.playerCountIs (1));
  }

  @Test
  public void testRequestAddPlayerFailedWithDuplicateColor()
  {
    final Player player1 = PlayerFactory.builder("Test Player 1").withColor(PlayerColor.BLUE).build();
    final Player player2 = PlayerFactory.builder("Test Player 2").withColor(PlayerColor.BLUE).build();

    assertTrue (playerModel.requestToAdd(player1).succeeded());
    assertTrue (playerModel.requestToAdd(player2).failedBecauseOf (PlayerJoinGameDeniedEvent.REASON.DUPLICATE_COLOR));
    assertTrue (playerModel.playerCountIs (1));
  }

  @Test
  public void testRequestAddPlayerFailedWithDuplicateTurnOrder()
  {
    final Player player1 = PlayerFactory.builder("Test Player 1").withTurnOrder (PlayerTurnOrder.THIRD).build();
    final Player player2 = PlayerFactory.builder("Test Player 2").withTurnOrder (PlayerTurnOrder.THIRD).build();

    assertTrue (playerModel.requestToAdd(player1).succeeded());
    assertTrue (playerModel.requestToAdd(player2).failedBecauseOf (PlayerJoinGameDeniedEvent.REASON.DUPLICATE_TURN_ORDER));
    assertTrue (playerModel.playerCountIs (1));
  }

  @Test
  public void testRequestToRemovePlayerByNameSucceeded()
  {
    final String name = "Test Player";

    playerModel.requestToAdd (PlayerFactory.create (name));

    assertTrue (playerModel.requestToRemoveByName(name).succeeded());
  }

  @Test
  public void testRequestToRemovePlayerByNameFailedWithPlayerDoesNotExist()
  {
    assertTrue (playerModel.requestToRemoveByName("Non-Existent Player").failedBecauseOf (PlayerLeaveGameDeniedEvent.REASON.PLAYER_DOES_NOT_EXIST));
  }

  @Test
  public void testRequestToRemovePlayerByIdSucceeded()
  {
    final Player player = PlayerFactory.create ("Test Player");
    final Id id = idOf (player);

    playerModel.requestToAdd (player);

    assertTrue (playerModel.requestToRemoveById (id).succeeded());
  }

  @Test
  public void testRequestToRemovePlayerByIdFailedWithPlayerDoesNotExist()
  {
    assertTrue (playerModel.requestToRemoveById (new Id (1)).failedBecauseOf (PlayerLeaveGameDeniedEvent.REASON.PLAYER_DOES_NOT_EXIST));
  }

  @Test
  public void testRequestToRemovePlayerByColorSucceeded()
  {
    final PlayerColor color = PlayerColor.PINK;

    playerModel.requestToAdd (PlayerFactory.builder("Test Player").withColor(color).build());

    assertTrue (playerModel.requestToRemoveByColor (color).succeeded());
  }

  @Test
  public void testRequestToRemovePlayerByColorFailedWithPlayerDoesNotExist()
  {
    assertTrue (playerModel.requestToRemoveByColor (PlayerColor.BROWN).failedBecauseOf (PlayerLeaveGameDeniedEvent.REASON.PLAYER_DOES_NOT_EXIST));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testRequestToRemovePlayerByColorFailedWithIllegalArgumentException()
  {
    playerModel.requestToRemoveByColor (PlayerColor.UNKNOWN);
  }

  @Test
  public void testRequestToRemovePlayerByTurnOrderSucceeded()
  {
    final PlayerTurnOrder turnOrder = PlayerTurnOrder.FIRST;

    playerModel.requestToAdd (PlayerFactory.builder("Test Player").withTurnOrder (turnOrder).build());

    assertTrue (playerModel.requestToRemoveByTurnOrder (turnOrder).succeeded());
  }

  @Test
  public void testRequestToRemovePlayerByTurnOrderFailedWithPlayerDoesNotExist()
  {
    assertTrue (playerModel.requestToRemoveByTurnOrder (PlayerTurnOrder.FIFTH).failedBecauseOf (PlayerLeaveGameDeniedEvent.REASON.PLAYER_DOES_NOT_EXIST));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testRequestToRemovePlayerByTurnOrderFailedWithIllegalArgumentException()
  {
    playerModel.requestToRemoveByTurnOrder (PlayerTurnOrder.UNKNOWN);
  }

  @Test
  public void testRequestToChangeColorOfPlayerSucceeded()
  {
    final PlayerColor oldColor = PlayerColor.BLUE;
    final Player player = PlayerFactory.builder("Test Player").withColor(oldColor).build();
    final PlayerColor newColor = PlayerColor.BROWN;

    playerModel.requestToAdd (player);

    assertTrue (playerModel.requestToChangeColorOfPlayer(withIdOf(player),newColor).succeeded());
    assertTrue (playerModel.existsPlayerWith (newColor));
    assertFalse (playerModel.existsPlayerWith (oldColor));
  }

  @Test
  public void testRequestToChangeColorOfPlayerFailedWithAlreadyTaken()
  {
    final PlayerColor player1Color = PlayerColor.PINK;
    final PlayerColor player2Color = PlayerColor.SILVER;

    final Player player1 = PlayerFactory.builder("Test Player 1").withColor(player1Color).build();
    final Player player2 = PlayerFactory.builder("Test Player 2").withColor(player2Color).build();

    playerModel.requestToAdd (player1);
    playerModel.requestToAdd (player2);

    assertTrue (playerModel.requestToChangeColorOfPlayer(withIdOf(player2),player1Color).failedBecauseOf (ChangePlayerColorDeniedEvent.REASON.COLOR_ALREADY_TAKEN));
    assertTrue (playerModel.playerWith(idOf(player1)).has (player1Color));
    assertTrue (playerModel.playerWith(idOf(player2)).has (player2Color));
    assertTrue (playerModel.existsPlayerWith (player1Color));
    assertTrue (playerModel.existsPlayerWith (player2Color));
  }

  @Test
  public void testRequestToChangeColorOfPlayerToSameColorFailedWithRequestedColorEqualsExistingColor()
  {
    final PlayerColor color = PlayerColor.PINK;
    final Player player = PlayerFactory.builder("Test Player 1").withColor(color).build();

    playerModel.requestToAdd (player);

    assertTrue (playerModel.requestToChangeColorOfPlayer(withIdOf(player),color).failedBecauseOf (ChangePlayerColorDeniedEvent.REASON.REQUESTED_COLOR_EQUALS_EXISTING_COLOR));
    assertTrue (playerModel.existsPlayerWith (color));
  }

  @Test
  public void testPlayerWithId()
  {
    final Id player1Id = new Id (1);
    final Player player1 = new DefaultPlayer ("Test Player 1", player1Id, PersonIdentity.UNKNOWN, PlayerColor.UNKNOWN, PlayerTurnOrder.UNKNOWN);
    final Player player2 = new DefaultPlayer ("Test Player 2", new Id (2), PersonIdentity.UNKNOWN, PlayerColor.UNKNOWN, PlayerTurnOrder.UNKNOWN);

    playerModel.requestToAdd (player1);
    playerModel.requestToAdd (player2);

    assertTrue (playerModel.playerWith (player1Id).is (player1));
  }

  @Test (expected = IllegalStateException.class)
  public void testNonExistentPlayerWithIdThrowsException()
  {
    playerModel.playerWith (new Id (5));
  }

  @Test
  public void testPlayerWithColor()
  {
    final PlayerColor player1Color = PlayerColor.CYAN;
    final Player player1 = PlayerFactory.builder("Test Player 1").withColor(player1Color).build();
    final Player player2 = PlayerFactory.builder("Test Player 2").withColor(PlayerColor.GOLD).build();

    playerModel.requestToAdd (player1);
    playerModel.requestToAdd (player2);

    assertTrue (playerModel.playerWith (player1Color).is (player1));
  }

  @Test (expected = IllegalStateException.class)
  public void testNonExistentPlayerWithColorThrowsException()
  {
    playerModel.playerWith (PlayerColor.RED);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testPlayerWithUnknownColorThrowsException()
  {
    assertTrue (playerModel.requestToAdd (PlayerFactory.create ("Test Player")).succeeded());

    playerModel.playerWith (PlayerColor.UNKNOWN);
  }

  @Test
  public void testPlayerWithTurnOrder()
  {
    final PlayerTurnOrder player1TurnOrder = PlayerTurnOrder.NINTH;
    final Player player1 = PlayerFactory.builder("Test Player 1").withTurnOrder (player1TurnOrder).build();
    final Player player2 = PlayerFactory.builder("Test Player 2").withTurnOrder (PlayerTurnOrder.SEVENTH).build();

    playerModel.requestToAdd (player1);
    playerModel.requestToAdd (player2);

    assertTrue (playerModel.playerWith (player1TurnOrder).is (player1));
  }

  @Test (expected = IllegalStateException.class)
  public void testNonExistentPlayerWithTurnOrderThrowsException()
  {
    playerModel.playerWith (PlayerTurnOrder.FOURTH);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testPlayerWithUnknownTurnOrderThrowsException()
  {
    assertTrue (playerModel.requestToAdd (PlayerFactory.create ("Test Player")).succeeded());

    playerModel.playerWith (PlayerTurnOrder.UNKNOWN);
  }
}
