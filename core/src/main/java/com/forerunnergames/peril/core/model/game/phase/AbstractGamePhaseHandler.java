package com.forerunnergames.peril.core.model.game.phase;

import com.forerunnergames.peril.common.game.TurnPhase;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.server.defaults.DefaultCountryArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.DefaultCountryOwnerChangedEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.DefaultPlayerCardsChangedEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.DefaultPlayerTurnOrderChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.ActivePlayerChangedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginRoundEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndRoundEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerLoseGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerWinGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.SkipPlayerTurnEvent;
import com.forerunnergames.peril.common.net.packets.card.CardPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.core.model.battle.BattleModel;
import com.forerunnergames.peril.core.model.card.Card;
import com.forerunnergames.peril.core.model.card.CardModel;
import com.forerunnergames.peril.core.model.card.CardPackets;
import com.forerunnergames.peril.core.model.game.CacheKey;
import com.forerunnergames.peril.core.model.game.GameModelConfiguration;
import com.forerunnergames.peril.core.model.game.GameStatus;
import com.forerunnergames.peril.core.model.game.InternalCommunicationHandler;
import com.forerunnergames.peril.core.model.game.PlayerTurnDataCache;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.playmap.PlayMapModel;
import com.forerunnergames.peril.core.model.playmap.continent.ContinentOwnerModel;
import com.forerunnergames.peril.core.model.playmap.country.CountryArmyModel;
import com.forerunnergames.peril.core.model.playmap.country.CountryGraphModel;
import com.forerunnergames.peril.core.model.playmap.country.CountryOwnerModel;
import com.forerunnergames.peril.core.model.state.annotations.StateEntryAction;
import com.forerunnergames.peril.core.model.state.annotations.StateExitAction;
import com.forerunnergames.peril.core.model.turn.PlayerTurnModel;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Exceptions;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractGamePhaseHandler implements GamePhaseHandler
{
  private static final Logger log = LoggerFactory.getLogger (AbstractGamePhaseHandler.class);

  protected final GameModelConfiguration gameModelConfig;
  protected final PlayerModel playerModel;
  protected final PlayMapModel playMapModel;
  protected final CountryOwnerModel countryOwnerModel;
  protected final CountryGraphModel countryGraphModel;
  protected final CountryArmyModel countryArmyModel;
  protected final ContinentOwnerModel continentOwnerModel;
  protected final CardModel cardModel;
  protected final PlayerTurnModel playerTurnModel;
  protected final BattleModel battleModel;
  protected final GameRules rules;
  protected final PlayerTurnDataCache <CacheKey> turnDataCache;
  protected final InternalCommunicationHandler internalCommHandler;
  protected final MBassador <Event> eventBus;

  private boolean active;

  protected AbstractGamePhaseHandler (final GameModelConfiguration gameModelConfig)
  {
    Arguments.checkIsNotNull (gameModelConfig, "gameModelConfig");

    this.gameModelConfig = gameModelConfig;

    // unpack game model configuration types
    playerModel = gameModelConfig.getPlayerModel ();
    playMapModel = gameModelConfig.getPlayMapModel ();
    battleModel = gameModelConfig.getBattleModel ();
    cardModel = gameModelConfig.getCardModel ();
    playerTurnModel = gameModelConfig.getPlayerTurnModel ();
    rules = gameModelConfig.getRules ();
    turnDataCache = gameModelConfig.getTurnDataCache ();
    internalCommHandler = gameModelConfig.getInternalCommunicationHandler ();
    eventBus = gameModelConfig.getEventBus ();

    // unpack play map model configuration types
    countryGraphModel = playMapModel.getCountryGraphModel ();
    countryOwnerModel = playMapModel.getCountryOwnerModel ();
    countryArmyModel = playMapModel.getCountryArmyModel ();
    continentOwnerModel = playMapModel.getContinentOwnerModel ();
  }

  @StateEntryAction
  @Override
  public void begin ()
  {
    if (active)
    {
      return;
    }

    active = true;
    onBegin ();
  }

  @StateExitAction
  @Override
  public void end ()
  {
    if (!active)
    {
      return;
    }

    active = false;
    onEnd ();
  }

  @Override
  public boolean isActive ()
  {
    return active;
  }

  @Override
  public PlayerPacket getCurrentPlayerPacket ()
  {
    return playerModel.playerPacketWith (playerTurnModel.getCurrentTurn ());
  }

  public String getCurrentPlayerName ()
  {
    return playerModel.nameOf (getCurrentPlayerId ());
  }

  public void beginPlayerTurn ()
  {
    log.info ("Turn begins for player [{}].", getCurrentPlayerName ());

    // clear state data cache
    turnDataCache.clearAll ();

    // clear inbound event cache
    internalCommHandler.clearEventCache ();

    final PlayerPacket currentPlayer = getCurrentPlayerPacket ();

    if (playerTurnModel.isFirstTurn ()) publish (new BeginRoundEvent (playerTurnModel.getRound ()));
    publish (new BeginPlayerTurnEvent (currentPlayer));
    publish (new ActivePlayerChangedEvent (currentPlayer));
  }

  public void endPlayerTurn ()
  {
    log.info ("Turn ends for player [{}].", getCurrentPlayerName ());

    // verify win/lose status of all players
    for (final Id playerId : playerModel.getPlayerIds ())
    {
      checkPlayerGameStatus (playerId);
    }

    // check if player should draw card
    final Optional <Boolean> playerOccupiedCountry = turnDataCache.checkAndGet (CacheKey.PLAYER_OCCUPIED_COUNTRY,
                                                                                Boolean.class);
    CardPacket newPlayerCard = null;
    if (playerOccupiedCountry.isPresent () && playerOccupiedCountry.get ())
    {
      // use fortify phase for rule check since card count should never exceed 6 at the end of a turn
      // TODO: Attack phase trade-ins; for the prior statement to be true, attack-phase trade-ins must be implemented
      final TurnPhase turnPhase = TurnPhase.FORTIFY;
      // TODO: Could do request/result/reason-for-denial here instead.
      if (cardModel.canGiveCard (getCurrentPlayerId (), turnPhase))
      {
        final Card card = cardModel.giveCard (getCurrentPlayerId (), TurnPhase.FORTIFY);
        log.debug ("Distributing card [{}] to player [{}]...", card, getCurrentPlayerPacket ());
        newPlayerCard = CardPackets.from (card);
      }
      else
      {
        log.warn ("Can't give card to player: [{}] for {}: [{}]. Cards in deck: [{}]. Cards in discard pile: [{}]. "
                + "Max cards allowed in hand for {}: [{}]: [{}]", getCurrentPlayerPacket (),
                  turnPhase.getClass ().getSimpleName (), turnPhase, cardModel.getDeckCount (),
                  cardModel.getDiscardCount (), turnPhase.getClass ().getSimpleName (), turnPhase,
                  rules.getMaxCardsInHand (turnPhase));
      }
    }

    publish (new EndPlayerTurnEvent (getCurrentPlayerPacket (), newPlayerCard));
    if (newPlayerCard != null) publish (new DefaultPlayerCardsChangedEvent (getCurrentPlayerPacket (), 1));
    if (playerTurnModel.isLastTurn ()) publish (new EndRoundEvent (playerTurnModel.getRound ()));

    if (turnDataCache.isSet (CacheKey.PLAYER_OCCUPIED_COUNTRY)) clearCacheValues (CacheKey.PLAYER_OCCUPIED_COUNTRY);
  }

  public void skipPlayerTurn (final SkipPlayerTurnEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.info ("Skipping turn for player [{}].", event.getPersonName ());
  }

  public void advancePlayerTurn ()
  {
    playerTurnModel.advance ();
  }

  public void resetTurn ()
  {
    playerTurnModel.resetCurrentTurn ();
  }

  @Override
  public Id getCurrentPlayerId ()
  {
    return playerModel.playerWith (playerTurnModel.getCurrentTurn ());
  }

  protected boolean isCurrentPlayer (final Id playerId)
  {
    Arguments.checkIsNotNull (playerId, "playerId");

    return getCurrentPlayerId ().is (playerId);
  }

  // checks whether or not a player has won or lost the game in the current game state
  protected void checkPlayerGameStatus (final Id playerId)
  {
    final int playerCountryCount = countryOwnerModel.countCountriesOwnedBy (playerId);
    GameStatus status = GameStatus.CONTINUE_PLAYING;

    if (playerCountryCount < rules.getMinPlayerCountryCount ()) status = playerLosesGame (playerId);

    // Player losing game causes player to be removed from game, which can trigger
    // another player winning the game if they are the sole remaining player, so we need to check GameStatus to make
    // sure we don't make the player win twice if multiple win conditions are satisfied simultaneously.
    // If the player already won from all other players losing and being removed, there is no need to
    // check if the winning country count was satisfied.
    if (status == GameStatus.GAME_OVER) return;

    if (playerCountryCount >= rules.getWinningCountryCount ()) playerWinsGame (playerId);
  }

  protected GameStatus playerWinsGame (final Id playerId)
  {
    publish (new PlayerWinGameEvent (playerModel.playerPacketWith (playerId)));
    publish (new EndGameEvent ());
    return GameStatus.GAME_OVER;
  }

  protected GameStatus playerLosesGame (final Id playerId)
  {
    publish (new PlayerLoseGameEvent (playerModel.playerPacketWith (playerId)));
    return removePlayerFromGame (playerId);
  }

  protected GameStatus removePlayerFromGame (final Id playerId)
  {
    // Ensure the player wasn't mistakenly already removed.
    assert playerModel.existsPlayerWith (playerId);

    final boolean wasCurrentPlayer = isCurrentPlayer (playerId);

    cardModel.removePlayer (playerId);
    playerTurnModel.decrementTurnCount ();

    final ImmutableSet <CountryArmyModel.CountryArmiesMutation> countryArmiesMutations = countryArmyModel
            .resetCountries (countryOwnerModel.getCountriesOwnedBy (playerId));
    final ImmutableSet <CountryOwnerModel.CountryOwnerMutation> countryOwnerMutations = countryOwnerModel
            .unassignAllCountriesOwnedBy (playerId, playerModel.playerPacketWith (playerId));
    final ImmutableSet <PlayerModel.PlayerTurnOrderMutation> turnOrderMutations = playerModel.remove (playerId);

    for (final PlayerModel.PlayerTurnOrderMutation mutation : turnOrderMutations)
    {
      publish (new DefaultPlayerTurnOrderChangedEvent (mutation.getPlayer (), mutation.getOldTurnOrder ()));
    }

    for (final CountryArmyModel.CountryArmiesMutation mutation : countryArmiesMutations)
    {
      publish (new DefaultCountryArmiesChangedEvent (mutation.getCountry (), mutation.getDeltaArmies ()));
    }

    for (final CountryOwnerModel.CountryOwnerMutation mutation : countryOwnerMutations)
    {
      publish (new DefaultCountryOwnerChangedEvent (mutation.getCountry (), mutation.getNewOwner (),
              mutation.getPreviousOwner ()));
    }

    // Removed player was the current player, so the current / active player has changed.
    if (wasCurrentPlayer) publish (new ActivePlayerChangedEvent (getCurrentPlayerPacket ()));

    return playerModel.playerCountIs (1) ? playerWinsGame (getCurrentPlayerId ()) : GameStatus.CONTINUE_PLAYING;
  }

  protected void publish (final Event event)
  {
    eventBus.publish (event);
  }

  @Nullable
  protected <T extends PlayerInputRequestEvent> T getOriginalRequestFor (final ResponseRequestEvent event,
                                                                         final Class <T> originalRequestType)
  {
    final Optional <PlayerInputRequestEvent> originalRequest = internalCommHandler.inputRequestFor (event);
    if (!originalRequest.isPresent ())
    {
      log.warn ("Unable to find request event matching response [{}].", event);
      return null;
    }

    return originalRequestType.cast (originalRequest.get ());
  }

  protected void republishRequestFor (final ResponseRequestEvent event)
  {
    final Optional <PlayerInputRequestEvent> originalRequest = internalCommHandler.inputRequestFor (event);
    if (!originalRequest.isPresent ())
    {
      log.warn ("Unable to find request event matching response [{}].", event);
      return;
    }

    publish (originalRequest.get ());
  }

  /**
   * Checks that the given keys have set values in the turn data cache. An exception is thrown if any of the given keys
   * are not set.
   */
  protected void checkCacheValues (final CacheKey... keys)
  {
    assert keys != null;

    for (final CacheKey key : keys)
    {
      if (turnDataCache.isNotSet (key))
      {
        Exceptions.throwIllegalState ("No value for {} set in turn data cache.", key);
      }
    }
  }

  protected void clearCacheValues (final CacheKey... keys)
  {
    assert keys != null;

    for (final CacheKey key : keys)
    {
      if (turnDataCache.isNotSet (key))
      {
        log.warn ("Cannot clear value for {} from turn data cache; no value currently set.", key);
        continue;
      }
      turnDataCache.clear (key);
    }
  }

  protected abstract void onBegin ();

  protected abstract void onEnd ();
}
