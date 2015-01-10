package com.forerunnergames.peril.core.model.strategy;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.forerunnergames.peril.core.model.armies.Army;
import com.forerunnergames.peril.core.model.armies.ArmyFactory;
import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerFactory;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.settings.GameSettings;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

import org.junit.Test;

public class DefaultGameStrategyTest
{
  @Test
  public void testComputeInitialArmiesForMinPlayers ()
  {
    testComputeInitialArmiesForNPlayers (GameSettings.MIN_PLAYERS, 40);
  }

  @Test
  public void testComputeInitialArmiesForEightPlayers ()
  {
    testComputeInitialArmiesForNPlayers (8, 10);
  }

  @Test
  public void testComputeInitialArmiesForMaxPlayers ()
  {
    testComputeInitialArmiesForNPlayers (GameSettings.MAX_PLAYERS, 5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testComputeInitialArmiesTooFewPlayers ()
  {
    testComputeInitialArmiesForNPlayers (1, 0);
  }

  private void testComputeInitialArmiesForNPlayers (final int numPlayers, final int expectedArmyCount)
  {
    final PlayerModel playerModel = createPlayerModel (numPlayers, createPlayers (numPlayers));

    final GameStrategy strategy = new DefaultGameStrategy ();
    ImmutableSet <Army> initialArmySet;

    for (int i = 0; i < playerModel.getPlayerCount (); i++)
    {
      final int initialArmyCount = strategy.computeInitialArmyCount (playerModel.getPlayerCount ());
      initialArmySet = ArmyFactory.create (initialArmyCount);
      assertNotNull (initialArmySet);
      assertTrue (initialArmySet.size () == expectedArmyCount);
    }
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
}
