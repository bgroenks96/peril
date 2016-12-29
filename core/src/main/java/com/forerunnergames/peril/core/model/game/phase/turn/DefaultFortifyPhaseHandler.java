package com.forerunnergames.peril.core.model.game.phase.turn;

import com.forerunnergames.peril.common.game.GamePhase;
import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerCancelFortifyRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerFortifyCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerSelectFortifyVectorRequestEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.DefaultCountryArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerCancelFortifyDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerFortifyCountryDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerSelectFortifyVectorDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.inform.PlayerEndTurnAvailableEvent;
import com.forerunnergames.peril.common.net.events.server.inform.PlayerFortifyCountryEvent;
import com.forerunnergames.peril.common.net.events.server.inform.PlayerSelectFortifyVectorEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginFortifyPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndFortifyPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.SkipFortifyPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerBeginFortificationWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerIssueFortifyOrderWaitEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerCancelFortifySuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerFortifyCountrySuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerSelectFortifyVectorSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.model.game.CacheKey;
import com.forerunnergames.peril.core.model.game.GameModelConfiguration;
import com.forerunnergames.peril.core.model.game.phase.AbstractGamePhaseHandler;
import com.forerunnergames.peril.core.model.state.annotations.StateEntryAction;
import com.forerunnergames.peril.core.model.state.annotations.StateExitAction;
import com.forerunnergames.peril.core.model.state.annotations.StateTransitionAction;
import com.forerunnergames.peril.core.model.state.annotations.StateTransitionCondition;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Exceptions;
import com.forerunnergames.tools.common.MutatorResult;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;

public final class DefaultFortifyPhaseHandler extends AbstractGamePhaseHandler implements FortifyPhaseHandler
{
  public DefaultFortifyPhaseHandler (final GameModelConfiguration gameModelConfig)
  {
    super (gameModelConfig);
  }

  @Override
  @StateEntryAction
  protected void onBegin ()
  {
    final PlayerPacket currentPlayer = getCurrentPlayerPacket ();

    final Id currentPlayerId = getCurrentPlayerId ();
    final ImmutableSet <CountryPacket> ownedCountries = countryOwnerModel.getCountryPacketsOwnedBy (currentPlayerId);
    final ImmutableMultimap.Builder <CountryPacket, CountryPacket> validFortifyVectorBuilder = ImmutableSetMultimap
            .builder ();
    for (final CountryPacket country : ownedCountries)
    {
      if (!country.hasAtLeastNArmies (rules.getMinArmiesOnSourceCountryForFortify ())) continue;
      final Id countryId = countryGraphModel.countryWith (country.getName ());
      final ImmutableSet <Id> adjCountries = countryGraphModel.getAdjacentNodes (countryId);
      for (final Id adjCountry : adjCountries)
      {
        if (!countryOwnerModel.isCountryOwnedBy (adjCountry, currentPlayerId)) continue;
        validFortifyVectorBuilder.put (country, countryGraphModel.countryPacketWith (adjCountry));
      }
    }

    final ImmutableMultimap <CountryPacket, CountryPacket> validFortifyVectors = validFortifyVectorBuilder.build ();
    if (validFortifyVectors.isEmpty ())
    {
      publish (new SkipFortifyPhaseEvent (getCurrentPlayerPacket ()));
      return;
    }

    turnDataCache.put (CacheKey.FORTIFY_VALID_VECTORS, validFortifyVectors);

    log.info ("Begin fortify phase for player [{}].", currentPlayer);

    changeGamePhaseTo (GamePhase.FORTIFY);

    publish (new BeginFortifyPhaseEvent (currentPlayer));
  }

  @Override
  @StateExitAction
  protected void onEnd ()
  {
    final PlayerPacket player = getCurrentPlayerPacket ();

    log.info ("End fortify phase for player [{}].", player);

    publish (new EndFortifyPhaseEvent (player));
  }

  @Override
  @StateEntryAction
  @SuppressWarnings ("unchecked")
  public void waitForPlayerToSelectFortifyVector ()
  {
    // Fixes PERIL-842 ("SkipFortifyPhaseEvent Crashes Server")
    // We're in the middle of skipping fortification phase, so do nothing.
    if (turnDataCache.isNotSet (CacheKey.FORTIFY_VALID_VECTORS)) return;

    final PlayerPacket player = getCurrentPlayerPacket ();

    log.info ("Waiting for player [{}] to select a fortification vector...", player);

    final ImmutableMultimap <CountryPacket, CountryPacket> validFortifyVectors = turnDataCache
            .get (CacheKey.FORTIFY_VALID_VECTORS, ImmutableMultimap.class);

    publish (new PlayerSelectFortifyVectorEvent (player, validFortifyVectors));
    publish (new PlayerBeginFortificationWaitEvent (player));
    publish (new PlayerEndTurnAvailableEvent (player));
  }

  @Override
  @StateTransitionCondition
  public boolean verifyPlayerFortifyVectorSelection (final PlayerSelectFortifyVectorRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final Id currentPlayerId = getCurrentPlayerId ();
    final PlayerPacket currentPlayer = getCurrentPlayerPacket ();

    if (internalCommHandler.isNotSenderOf (event, currentPlayer))
    {
      publish (new PlayerSelectFortifyVectorDeniedEvent (currentPlayer,
              PlayerSelectFortifyVectorDeniedEvent.Reason.PLAYER_NOT_IN_TURN));
      return false;
    }

    if (!countryGraphModel.existsCountryWith (event.getSourceCountry ()))
    {
      publish (new PlayerSelectFortifyVectorDeniedEvent (currentPlayer,
              PlayerSelectFortifyVectorDeniedEvent.Reason.SOURCE_COUNTRY_DOES_NOT_EXIST));
      return false;
    }

    if (!countryGraphModel.existsCountryWith (event.getTargetCountry ()))
    {
      publish (new PlayerSelectFortifyVectorDeniedEvent (currentPlayer,
              PlayerSelectFortifyVectorDeniedEvent.Reason.TARGET_COUNTRY_DOES_NOT_EXIST));
      return false;
    }

    final Id sourceCountryId = countryGraphModel.countryWith (event.getSourceCountry ());
    final Id targetCountryId = countryGraphModel.countryWith (event.getTargetCountry ());

    if (!countryOwnerModel.isCountryOwnedBy (sourceCountryId, currentPlayerId))
    {
      publish (new PlayerSelectFortifyVectorDeniedEvent (currentPlayer,
              PlayerSelectFortifyVectorDeniedEvent.Reason.NOT_OWNER_OF_SOURCE_COUNTRY));
      return false;
    }

    if (!countryOwnerModel.isCountryOwnedBy (targetCountryId, currentPlayerId))
    {
      publish (new PlayerSelectFortifyVectorDeniedEvent (currentPlayer,
              PlayerSelectFortifyVectorDeniedEvent.Reason.NOT_OWNER_OF_TARGET_COUNTRY));
      return false;
    }

    if (!countryGraphModel.areAdjacent (sourceCountryId, targetCountryId))
    {
      publish (new PlayerSelectFortifyVectorDeniedEvent (currentPlayer,
              PlayerSelectFortifyVectorDeniedEvent.Reason.COUNTRIES_NOT_ADJACENT));
      return false;
    }

    final int sourceCountryArmyCount = countryArmyModel.getArmyCountFor (sourceCountryId);
    final int targetCountryArmyCount = countryArmyModel.getArmyCountFor (targetCountryId);

    if (sourceCountryArmyCount < rules.getMinArmiesOnSourceCountryForFortify ())
    {
      publish (new PlayerSelectFortifyVectorDeniedEvent (currentPlayer,
              PlayerSelectFortifyVectorDeniedEvent.Reason.SOURCE_COUNTRY_ARMY_UNDERFLOW));
      return false;
    }

    if (targetCountryArmyCount > rules.getMaxArmiesOnTargetCountryForFortify ())
    {
      publish (new PlayerSelectFortifyVectorDeniedEvent (currentPlayer,
              PlayerSelectFortifyVectorDeniedEvent.Reason.TARGET_COUNTRY_ARMY_OVERFLOW));
      return false;
    }

    final CountryPacket sourceCountryPacket = countryGraphModel.countryPacketWith (sourceCountryId);
    final CountryPacket targetCountryPacket = countryGraphModel.countryPacketWith (targetCountryId);
    final PlayerPacket playerPacket = getCurrentPlayerPacket ();
    final int minDeltaArmyCount = rules.getMinFortifyDeltaArmyCount (sourceCountryArmyCount, targetCountryArmyCount);
    final int maxDeltaArmyCount = rules.getMaxFortifyDeltaArmyCount (sourceCountryArmyCount, targetCountryArmyCount);

    publish (new PlayerSelectFortifyVectorSuccessEvent (playerPacket, sourceCountryPacket, targetCountryPacket));
    publish (new PlayerFortifyCountryEvent (playerPacket, sourceCountryPacket, targetCountryPacket, minDeltaArmyCount,
            maxDeltaArmyCount));
    publish (new PlayerIssueFortifyOrderWaitEvent (playerPacket, sourceCountryPacket, targetCountryPacket,
            minDeltaArmyCount, maxDeltaArmyCount));

    turnDataCache.put (CacheKey.FORTIFY_SOURCE_COUNTRY_ID, sourceCountryId);
    turnDataCache.put (CacheKey.FORTIFY_TARGET_COUNTRY_ID, targetCountryId);

    return true;
  }

  @Override
  @StateTransitionCondition
  public boolean verifyPlayerFortifyOrder (final PlayerFortifyCountryRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    checkCacheValues (CacheKey.FORTIFY_SOURCE_COUNTRY_ID, CacheKey.FORTIFY_TARGET_COUNTRY_ID);

    final PlayerPacket currentPlayer = getCurrentPlayerPacket ();

    if (internalCommHandler.isNotSenderOf (event, currentPlayer))
    {
      publish (new PlayerFortifyCountryDeniedEvent (currentPlayer,
              PlayerFortifyCountryDeniedEvent.Reason.PLAYER_NOT_IN_TURN));
      return false;
    }

    final Id sourceCountry = turnDataCache.get (CacheKey.FORTIFY_SOURCE_COUNTRY_ID, Id.class);
    final Id targetCountry = turnDataCache.get (CacheKey.FORTIFY_TARGET_COUNTRY_ID, Id.class);
    final int deltaArmyCount = event.getDeltaArmyCount ();
    final int sourceCountryArmyCount = countryArmyModel.getArmyCountFor (sourceCountry);
    final int targetCountryArmyCount = countryArmyModel.getArmyCountFor (targetCountry);
    final int minDeltaArmyCount = rules.getMinFortifyDeltaArmyCount (sourceCountryArmyCount, targetCountryArmyCount);
    final int maxDeltaArmyCount = rules.getMaxFortifyDeltaArmyCount (sourceCountryArmyCount, targetCountryArmyCount);

    if (deltaArmyCount < minDeltaArmyCount)
    {
      publish (new PlayerFortifyCountryDeniedEvent (currentPlayer,
              PlayerFortifyCountryDeniedEvent.Reason.FORTIFY_DELTA_ARMY_COUNT_UNDERFLOW));
      return false;
    }

    if (deltaArmyCount > maxDeltaArmyCount)
    {
      publish (new PlayerFortifyCountryDeniedEvent (currentPlayer,
              PlayerFortifyCountryDeniedEvent.Reason.FORTIFY_DELTA_ARMY_COUNT_OVERFLOW));
      return false;
    }

    final MutatorResult <?> res1, res2;
    res1 = countryArmyModel.requestToRemoveArmiesFromCountry (sourceCountry, deltaArmyCount);
    res2 = countryArmyModel.requestToAddArmiesToCountry (targetCountry, deltaArmyCount);

    final Optional <MutatorResult <?>> failed = Result.firstGenericFailedFrom (res1, res2);
    if (failed.isPresent ())
    {
      // failure result from model class suggests some kind of serious state inconsistency
      Exceptions.throwIllegalState ("Failed to change country army states [Reason: {}].",
                                    failed.get ().getFailureReason ());
    }

    MutatorResult.commitAllSuccessful (res1, res2);

    final CountryPacket sourceCountryPacket = countryGraphModel.countryPacketWith (sourceCountry);
    final CountryPacket targetCountryPacket = countryGraphModel.countryPacketWith (targetCountry);
    publish (new DefaultCountryArmiesChangedEvent (sourceCountryPacket, -deltaArmyCount));
    publish (new DefaultCountryArmiesChangedEvent (targetCountryPacket, deltaArmyCount));
    publish (new PlayerFortifyCountrySuccessEvent (currentPlayer, sourceCountryPacket, targetCountryPacket,
            deltaArmyCount));

    clearCacheValues (CacheKey.FORTIFY_SOURCE_COUNTRY_ID, CacheKey.FORTIFY_TARGET_COUNTRY_ID);

    return true;
  }

  @Override
  @StateTransitionAction
  public boolean verifyPlayerCancelFortifyVector (final PlayerCancelFortifyRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    final Id sourceCountry = turnDataCache.get (CacheKey.FORTIFY_SOURCE_COUNTRY_ID, Id.class);
    final Id targetCountry = turnDataCache.get (CacheKey.FORTIFY_TARGET_COUNTRY_ID, Id.class);
    final CountryPacket sourceCountryPacket = countryGraphModel.countryPacketWith (sourceCountry);
    final CountryPacket targetCountryPacket = countryGraphModel.countryPacketWith (targetCountry);

    final PlayerPacket player = getCurrentPlayerPacket ();
    final Optional <PlayerPacket> sender = internalCommHandler.senderOf (event);
    if (!sender.isPresent () || player.isNot (sender.get ()))
    {
      publish (new PlayerCancelFortifyDeniedEvent (player, sourceCountryPacket, targetCountryPacket,
              PlayerCancelFortifyDeniedEvent.Reason.NOT_IN_TURN));
      return false;
    }

    publish (new PlayerCancelFortifySuccessEvent (player, sourceCountryPacket, targetCountryPacket));

    clearCacheValues (CacheKey.FORTIFY_SOURCE_COUNTRY_ID, CacheKey.FORTIFY_TARGET_COUNTRY_ID);

    return true;
  }
}
