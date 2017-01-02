package com.forerunnergames.peril.core.model.game.phase.turn;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.forerunnergames.peril.common.net.events.client.request.response.PlayerOccupyCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.CountryArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerWinGameEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOccupyCountryResponseSuccessEvent;
import com.forerunnergames.peril.core.model.game.CacheKey;
import com.forerunnergames.peril.core.model.game.phase.AbstractGamePhaseHandlerTest;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.model.playmap.PlayMapStateBuilder;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import org.junit.Test;

public class BattlePhaseHandlerTest extends AbstractGamePhaseHandlerTest
{
  private AttackPhaseHandler attackPhase;

  @Override
  protected void setupTest ()
  {
    initializeGameModelWith (createPlayMapModelWithTestTerritoryGraphs (defaultTestCountries));
    addMaxPlayers ();
    attackPhase = new DefaultAttackPhaseHandler (gameModelConfig);
    phaseHandlerBase = attackPhase;
  }

  // TODO Add more attack phase unit tests

  @Test
  public void testLastOccupiedCountryTriggersWinGame ()
  {
    addMaxPlayers ();
    final Id firstPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    final Id secondPlayer = playerModel.playerWith (PlayerTurnOrder.SECOND);
    final int sourceCountryArmyCount = 10;
    final int deltaArmyCount = 4;
    final int attackerDieCount = 3;
    final PlayMapStateBuilder stateBuilder = new PlayMapStateBuilder (playMapModel);
    final Id sourceCountryId = Iterables.getFirst (countryGraphModel.getCountryIds (), null);
    final Id targetCountryId = Iterables.getFirst (countryGraphModel.getAdjacentNodes (sourceCountryId), null);
    assertNotNull (sourceCountryId);
    assertNotNull (targetCountryId);
    // set owner for all countries
    stateBuilder.forCountries (countryGraphModel.getCountryIds ()).setOwner (firstPlayer);
    // add source army count for all countries except the one we are occupying
    stateBuilder.forCountries (Sets.difference (countryGraphModel.getCountryIds (), ImmutableSet.of (targetCountryId)))
            .addArmies (sourceCountryArmyCount);
    turnDataCache.put (CacheKey.OCCUPY_SOURCE_COUNTRY, countryGraphModel.countryPacketWith (sourceCountryId));
    turnDataCache.put (CacheKey.OCCUPY_TARGET_COUNTRY, countryGraphModel.countryPacketWith (targetCountryId));
    turnDataCache.put (CacheKey.OCCUPY_PREV_OWNER, playerModel.playerPacketWith (secondPlayer));
    turnDataCache.put (CacheKey.OCCUPY_MIN_ARMY_COUNT, gameRules.getMinOccupyArmyCount (attackerDieCount));
    final PlayerOccupyCountryResponseRequestEvent response = new PlayerOccupyCountryResponseRequestEvent (
            deltaArmyCount);
    assertFalse (attackPhase.verifyPlayerOccupyCountryResponseRequest (response));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerOccupyCountryResponseSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyNTimes (CountryArmiesChangedEvent.class, 2));

    // check country change events
    final Predicate <CountryArmiesChangedEvent> sourceCountryPredicate = new Predicate <CountryArmiesChangedEvent> ()
    {
      @Override
      public boolean apply (final CountryArmiesChangedEvent input)
      {
        return input.getCountry ().is (countryGraphModel.countryPacketWith (sourceCountryId))
                && input.getCountryArmyCount () == sourceCountryArmyCount - deltaArmyCount;
      }
    };
    final Predicate <CountryArmiesChangedEvent> targetCountryPredicate = new Predicate <CountryArmiesChangedEvent> ()
    {
      @Override
      public boolean apply (final CountryArmiesChangedEvent input)
      {
        return input.getCountry ().is (countryGraphModel.countryPacketWith (targetCountryId))
                && input.getCountryArmyCount () == deltaArmyCount;
      }
    };
    assertNotNull (eventHandler.lastEventWhere (CountryArmiesChangedEvent.class, sourceCountryPredicate));
    assertNotNull (eventHandler.lastEventWhere (CountryArmiesChangedEvent.class, targetCountryPredicate));

    // ensure win game events were fired
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerWinGameEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (EndGameEvent.class));
  }
}
