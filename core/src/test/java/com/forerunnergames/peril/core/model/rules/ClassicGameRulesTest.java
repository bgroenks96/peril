package com.forerunnergames.peril.core.model.rules;

import static org.junit.Assert.assertTrue;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerFactory;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.settings.GameSettings;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

import org.junit.Test;

public class ClassicGameRulesTest
{
  @Test
  public void testCalculateInitialArmiesForEightPlayers ()
  {
    testCalculateInitialArmiesForNPlayers (8, 10);
  }

  @Test
  public void testCalculateInitialArmiesForMaxPlayers ()
  {
    testCalculateInitialArmiesForNPlayers (GameSettings.MAX_PLAYERS, 5);
  }

  @Test
  public void testCalculateInitialArmiesForMinPlayers ()
  {
    testCalculateInitialArmiesForNPlayers (GameSettings.MIN_PLAYERS, 40);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testCalculateInitialArmiesTooFewPlayers ()
  {
    testCalculateInitialArmiesForNPlayers (1, 0);
  }

  private PlayerModel createPlayerModel (final int playerLimit, final ImmutableSet <Player> players)
  {
    assertTrue (playerLimit >= players.size ());

    final PlayerModel playerModel = new PlayerModel (playerLimit);

    for (final Player p : players)
    {
      assertTrue (playerModel.requestToAdd (p).isSuccessful ());
    }

    return playerModel;
  }

  private ImmutableSet <Player> createPlayers (final int count)
  {
    assertTrue (count > 0);

    final Builder <Player> playerSetBuilder = ImmutableSet.builder ();
    for (int i = 0; i < count; i++)
    {
      playerSetBuilder.add (PlayerFactory.create ("Test Player-" + i));
    }

    return playerSetBuilder.build ();
  }

  private void testCalculateInitialArmiesForNPlayers (final int numPlayers, final int expectedArmies)
  {
    final PlayerModel playerModel = createPlayerModel (numPlayers, createPlayers (numPlayers));

    final GameRules strategy = new ClassicGameRules ();

    for (int i = 0; i < playerModel.getPlayerCount (); i++)
    {
      final int initialArmies = strategy.calculateInitialArmies (playerModel.getPlayerCount ());
      assertTrue (initialArmies == expectedArmies);
    }
  }
}
