package com.forerunnergames.peril.core.model.battle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerAttackCountryResponseDeniedEvent.Reason;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.model.map.PlayMapModel;
import com.forerunnergames.peril.core.model.map.PlayMapStateBuilder;
import com.forerunnergames.peril.core.model.map.country.CountryArmyModel;
import com.forerunnergames.peril.core.model.map.country.CountryMapGraphModel;
import com.forerunnergames.peril.core.model.map.country.CountryMapGraphModelTest;
import com.forerunnergames.peril.core.model.map.country.CountryOwnerModel;
import com.forerunnergames.peril.core.model.map.country.DefaultCountryArmyModel;
import com.forerunnergames.peril.core.model.map.country.DefaultCountryOwnerModel;
import com.forerunnergames.peril.core.model.people.player.DefaultPlayerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerFactory;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.tools.common.DataResult;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.graph.DefaultGraphModel;
import com.forerunnergames.tools.common.graph.GraphModel;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.common.id.IdGenerator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import org.junit.Before;
import org.junit.Test;

public class BattleModelTest
{
  private static final int TEST_COUNTRY_COUNT = 10;
  private static final ImmutableList <String> countryNames = generateCountryNameList (TEST_COUNTRY_COUNT);
  private CountryMapGraphModel countryMapGraphModel;
  private CountryOwnerModel countryOwnerModel;
  private CountryArmyModel countryArmyModel;
  private BattleModel battleModel;
  private GameRules gameRules;

  @Before
  public void prepare ()
  {
    countryMapGraphModel = createDefaultTestCountryMapGraph ();
    gameRules = new ClassicGameRules.Builder ().playerLimit (ClassicGameRules.MAX_PLAYERS)
            .totalCountryCount (countryMapGraphModel.size ()).build ();
    countryOwnerModel = new DefaultCountryOwnerModel (countryMapGraphModel, gameRules);
    countryArmyModel = new DefaultCountryArmyModel (countryMapGraphModel, gameRules);
    battleModel = new DefaultBattleModel (gameRules);
  }

  @Test
  public void testGetValidAttackTargetsForCountryWithAdjacentTargets ()
  {
    final PlayMapModel playMapModel = mockPlayMapModel ();
    final Id player0 = IdGenerator.generateUniqueId ();
    final Id player1 = IdGenerator.generateUniqueId ();
    final ImmutableList <Id> countries = getTestCountryIds ();

    // prepare owner/army state; source country is 0
    final int countryArmyCount = gameRules.getMinArmiesOnCountryForAttack ();
    final PlayMapStateBuilder playMapBuilder = new PlayMapStateBuilder (playMapModel);
    playMapBuilder.forCountries (countries.subList (0, 2)).setOwner (player0).addArmies (countryArmyCount);
    playMapBuilder.forCountries (countries.subList (2, countries.size ())).setOwner (player1)
            .addArmies (countryArmyCount);
    final ImmutableSet.Builder <CountryPacket> expected = ImmutableSet.builder ();
    for (int i = 2; i < countries.size (); i++)
    {
      expected.add (countryMapGraphModel.countryPacketWith (countries.get (i)));
    }

    final ImmutableSet <CountryPacket> actual = battleModel.getValidAttackTargetsFor (countries.get (0), playMapModel);

    assertEquals (expected.build (), actual);
  }

  @Test
  public void testGetValidAttackTargetsForCountryWithNoAdjacentTargets ()
  {
    final PlayMapModel playMapModel = mockPlayMapModel ();
    final Id player0 = IdGenerator.generateUniqueId ();
    final ImmutableList <Id> countries = getTestCountryIds ();

    // prepare owner/army state
    final int countryArmyCount = gameRules.getMinArmiesOnCountryForAttack ();
    final PlayMapStateBuilder playMapBuilder = new PlayMapStateBuilder (playMapModel);
    playMapBuilder.forCountries (countries).setOwner (player0).addArmies (countryArmyCount);

    final ImmutableSet <CountryPacket> actual = battleModel.getValidAttackTargetsFor (countries.get (0), playMapModel);

    assertTrue (actual.isEmpty ());
  }

  @Test
  public void testGetValidAttackTargetsForCountryWithTooFewArmies ()
  {
    final PlayMapModel playMapModel = mockPlayMapModel ();
    final Id player0 = IdGenerator.generateUniqueId ();
    final Id player1 = IdGenerator.generateUniqueId ();
    final ImmutableList <Id> countries = getTestCountryIds ();

    // prepare owner/army state; source country is 0
    final int countryArmyCount = gameRules.getMinArmiesOnCountryForAttack () - 1;
    final PlayMapStateBuilder playMapBuilder = new PlayMapStateBuilder (playMapModel);
    playMapBuilder.forCountries (countries.subList (0, 2)).setOwner (player0).addArmies (countryArmyCount);
    playMapBuilder.forCountries (countries.subList (2, countries.size ())).setOwner (player1);

    final ImmutableSet <CountryPacket> actual = battleModel.getValidAttackTargetsFor (countries.get (0), playMapModel);

    assertTrue (actual.isEmpty ());
  }

  @Test
  public void testNewPlayerAttackOrderWithValidAttackData ()
  {
    final PlayMapModel playMapModel = mockPlayMapModel ();
    final Id player0 = IdGenerator.generateUniqueId ();
    final Id player1 = IdGenerator.generateUniqueId ();
    final ImmutableList <Id> countries = getTestCountryIds ();

    // prepare owner/army state; source country is 0
    final int countryArmyCount = gameRules.getMinArmiesOnCountryForAttack ();
    final PlayMapStateBuilder playMapBuilder = new PlayMapStateBuilder (playMapModel);
    playMapBuilder.forCountries (countries.subList (0, 2)).setOwner (player0).addArmies (countryArmyCount);
    playMapBuilder.forCountries (countries.subList (2, countries.size ())).setOwner (player1)
            .addArmies (countryArmyCount);

    final Id sourceCountry = countries.get (0);
    final Id targetCountry = countries.get (2);
    final int dieCount = gameRules.getMaxAttackerDieCount (countryArmyCount);
    final DataResult <AttackOrder, ?> result = battleModel.newPlayerAttackOrder (player0, sourceCountry, targetCountry,
                                                                                 dieCount, playMapModel);
    assertTrue (result.succeeded ());
    final AttackOrder pendingOrder = result.getReturnValue ();
    assertEquals (player0, pendingOrder.getPlayerId ());
    assertEquals (sourceCountry, pendingOrder.getSourceCountry ());
    assertEquals (targetCountry, pendingOrder.getTargetCountry ());
    assertEquals (dieCount, pendingOrder.getDieCount ());
  }

  @Test
  public void testNewPlayerAttackOrderWithInvalidSourceCountry ()
  {
    final PlayMapModel playMapModel = mockPlayMapModel ();
    final Id player0 = IdGenerator.generateUniqueId ();
    final Id player1 = IdGenerator.generateUniqueId ();
    final ImmutableList <Id> countries = getTestCountryIds ();

    // prepare owner/army state; source country is 0
    final int countryArmyCount = gameRules.getMinArmiesOnCountryForAttack ();
    final PlayMapStateBuilder playMapBuilder = new PlayMapStateBuilder (playMapModel);
    playMapBuilder.forCountries (countries.subList (0, 2)).setOwner (player0).addArmies (countryArmyCount);
    playMapBuilder.forCountries (countries.subList (2, countries.size ())).setOwner (player1)
            .addArmies (countryArmyCount);

    final Id sourceCountry = countries.get (2);
    final Id targetCountry = countries.get (3);
    final int dieCount = gameRules.getMaxAttackerDieCount (countryArmyCount);
    final DataResult <AttackOrder, Reason> result;
    result = battleModel.newPlayerAttackOrder (player0, sourceCountry, targetCountry, dieCount, playMapModel);
    assertTrue (result.failed ());
    assertTrue (result.failedBecauseOf (Reason.NOT_OWNER_OF_SOURCE_COUNTRY));
  }

  @Test
  public void testNewPlayerAttackOrderWithInvalidTargetCountry ()
  {
    final PlayMapModel playMapModel = mockPlayMapModel ();
    final Id player0 = IdGenerator.generateUniqueId ();
    final Id player1 = IdGenerator.generateUniqueId ();
    final ImmutableList <Id> countries = getTestCountryIds ();

    // prepare owner/army state; source country is 0
    final int countryArmyCount = gameRules.getMinArmiesOnCountryForAttack ();
    final PlayMapStateBuilder playMapBuilder = new PlayMapStateBuilder (playMapModel);
    playMapBuilder.forCountries (countries.subList (0, 2)).setOwner (player0).addArmies (countryArmyCount);
    playMapBuilder.forCountries (countries.subList (2, countries.size ())).setOwner (player1)
            .addArmies (countryArmyCount);

    final Id sourceCountry = countries.get (0);
    final Id targetCountry = countries.get (1);
    final int dieCount = gameRules.getMaxAttackerDieCount (countryArmyCount);
    final DataResult <AttackOrder, Reason> result;
    result = battleModel.newPlayerAttackOrder (player0, sourceCountry, targetCountry, dieCount, playMapModel);
    assertTrue (result.failed ());
    assertTrue (result.failedBecauseOf (Reason.ALREADY_OWNER_OF_TARGET_COUNTRY));
  }

  @Test
  public void testNewPlayerAttackOrderWithInsufficientArmyCount ()
  {
    final PlayMapModel playMapModel = mockPlayMapModel ();
    final Id player0 = IdGenerator.generateUniqueId ();
    final Id player1 = IdGenerator.generateUniqueId ();
    final ImmutableList <Id> countries = getTestCountryIds ();

    // prepare owner/army state; source country is 0
    final int countryArmyCount = gameRules.getMinArmiesOnCountryForAttack () - 1;
    final PlayMapStateBuilder playMapBuilder = new PlayMapStateBuilder (playMapModel);
    playMapBuilder.forCountries (countries.subList (0, 2)).setOwner (player0).addArmies (countryArmyCount);
    playMapBuilder.forCountries (countries.subList (2, countries.size ())).setOwner (player1)
            .addArmies (countryArmyCount);

    final Id sourceCountry = countries.get (0);
    final Id targetCountry = countries.get (2);
    final int dieCount = gameRules.getMaxAttackerDieCount (countryArmyCount);
    final DataResult <AttackOrder, Reason> result;
    result = battleModel.newPlayerAttackOrder (player0, sourceCountry, targetCountry, dieCount, playMapModel);
    assertTrue (result.failed ());
    assertTrue (result.failedBecauseOf (Reason.INSUFFICIENT_ARMY_COUNT));
  }

  @Test
  public void testGenerateResultForBattle ()
  {
    final PlayMapModel playMapModel = mockPlayMapModel ();
    final PlayerModel playerModel = new DefaultPlayerModel (gameRules);
    final PlayerFactory factory = new PlayerFactory ();
    factory.newPlayerWith ("TestPlayer0");
    factory.newPlayerWith ("TestPlayer1");
    assertFalse (Result.anyStatusFailed (playerModel.requestToAdd (factory)));
    final Id player0 = playerModel.playerWith ("TestPlayer0");
    final Id player1 = playerModel.playerWith ("TestPlayer1");
    final ImmutableList <Id> countries = getTestCountryIds ();

    // prepare owner/army state; source country is 0
    final int countryArmyCount = gameRules.getMinArmiesOnCountryForAttack ();
    final PlayMapStateBuilder playMapBuilder = new PlayMapStateBuilder (playMapModel);
    playMapBuilder.forCountries (countries.subList (0, 2)).setOwner (player0).addArmies (countryArmyCount);
    playMapBuilder.forCountries (countries.subList (2, countries.size ())).setOwner (player1)
            .addArmies (countryArmyCount);

    final Id sourceCountry = countries.get (0);
    final Id targetCountry = countries.get (2);
    final int attackerDieCount = gameRules.getMaxAttackerDieCount (countryArmyCount);
    final int defenderDieCount = gameRules.getMaxDefenderDieCount (countryArmyCount);
    final AttackOrder mockOrder = battleModel
            .newPlayerAttackOrder (player0, sourceCountry, targetCountry, attackerDieCount, playMapModel)
            .getReturnValue ();

    final BattleResult battleResult = battleModel.generateResultFor (mockOrder, defenderDieCount, playerModel,
                                                                     playMapModel);
    assertNotNull (battleResult);
    assertEquals (player0, battleResult.getAttacker ().getPlayerId ());
    assertEquals (sourceCountry, battleResult.getAttacker ().getCountryId ());
    assertEquals (player1, battleResult.getDefender ().getPlayerId ());
    assertEquals (targetCountry, battleResult.getDefender ().getCountryId ());
  }

  @Test
  public void testGenerateResultForBattleWithMoreArmies ()
  {
    final PlayMapModel playMapModel = mockPlayMapModel ();
    final PlayerModel playerModel = new DefaultPlayerModel (gameRules);
    final PlayerFactory factory = new PlayerFactory ();
    factory.newPlayerWith ("TestPlayer0");
    factory.newPlayerWith ("TestPlayer1");
    assertFalse (Result.anyStatusFailed (playerModel.requestToAdd (factory)));
    final Id player0 = playerModel.playerWith ("TestPlayer0");
    final Id player1 = playerModel.playerWith ("TestPlayer1");
    final ImmutableList <Id> countries = getTestCountryIds ();

    // prepare owner/army state; source country is 0
    final int attackingCountryArmyCount = 5 * gameRules.getMinArmiesOnCountryForAttack ();
    final int defendingCountryArmyCount = 3 * gameRules.getMinArmiesOnCountryForAttack ();
    final PlayMapStateBuilder playMapBuilder = new PlayMapStateBuilder (playMapModel);
    playMapBuilder.forCountries (countries.subList (0, 2)).setOwner (player0).addArmies (attackingCountryArmyCount);
    playMapBuilder.forCountries (countries.subList (2, countries.size ())).setOwner (player1)
            .addArmies (defendingCountryArmyCount);

    final Id sourceCountry = countries.get (0);
    final Id targetCountry = countries.get (2);
    final int attackerDieCount = gameRules.getMaxAttackerDieCount (attackingCountryArmyCount);
    final int defenderDieCount = gameRules.getMaxDefenderDieCount (attackingCountryArmyCount);
    final AttackOrder mockOrder = battleModel
            .newPlayerAttackOrder (player0, sourceCountry, targetCountry, attackerDieCount, playMapModel)
            .getReturnValue ();

    final BattleResult battleResult = battleModel.generateResultFor (mockOrder, defenderDieCount, playerModel,
                                                                     playMapModel);
    assertNotNull (battleResult);
    assertEquals (player0, battleResult.getAttacker ().getPlayerId ());
    assertEquals (sourceCountry, battleResult.getAttacker ().getCountryId ());
    assertEquals (player1, battleResult.getDefender ().getPlayerId ());
    assertEquals (targetCountry, battleResult.getDefender ().getCountryId ());
  }

  private PlayMapModel mockPlayMapModel ()
  {
    final PlayMapModel playMapModelMock = mock (PlayMapModel.class);
    when (playMapModelMock.getCountryMapGraphModel ()).thenReturn (countryMapGraphModel);
    when (playMapModelMock.getCountryOwnerModel ()).thenReturn (countryOwnerModel);
    when (playMapModelMock.getCountryArmyModel ()).thenReturn (countryArmyModel);
    return playMapModelMock;
  }

  // get country ids in order that they are declared in name list
  private ImmutableList <Id> getTestCountryIds ()
  {
    final ImmutableList.Builder <Id> builder = ImmutableList.builder ();
    for (final String name : countryNames)
    {
      builder.add (countryMapGraphModel.countryWith (name));
    }
    return builder.build ();
  }

  private static CountryMapGraphModel createDefaultTestCountryMapGraph ()
  {
    final DefaultGraphModel.Builder <String> countryNameGraphBuilder = DefaultGraphModel.builder ();
    // set every node adjacent to country 0
    for (int i = 1; i < countryNames.size (); i++)
    {
      countryNameGraphBuilder.setAdjacent (countryNames.get (0), countryNames.get (i));
    }
    // set each country 1-4 adjacent to its sequential neighbors
    for (int i = 2; i < countryNames.size (); i++)
    {
      countryNameGraphBuilder.setAdjacent (countryNames.get (i - 1), countryNames.get (i));
    }
    // complete the cycle by setting country 1 adjacent to last country
    countryNameGraphBuilder.setAdjacent (countryNames.get (countryNames.size () - 1), countryNames.get (1));
    final GraphModel <String> countryNameGraph = countryNameGraphBuilder.build ();
    return CountryMapGraphModelTest.createCountryMapGraphModelFrom (countryNameGraph);
  }

  private static final ImmutableList <String> generateCountryNameList (final int count)
  {
    final ImmutableList.Builder <String> builder = ImmutableList.builder ();
    for (int i = 0; i < count; i++)
    {
      builder.add ("Country-" + i);
    }
    return builder.build ();
  }
}
