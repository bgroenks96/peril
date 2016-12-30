package com.forerunnergames.peril.core.model.game;

import com.forerunnergames.peril.common.eventbus.EventBusFactory;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.core.events.DefaultEventRegistry;
import com.forerunnergames.peril.core.events.EventRegistry;
import com.forerunnergames.peril.core.model.battle.BattleModel;
import com.forerunnergames.peril.core.model.battle.DefaultBattleModel;
import com.forerunnergames.peril.core.model.card.Card;
import com.forerunnergames.peril.core.model.card.CardModel;
import com.forerunnergames.peril.core.model.card.DefaultCardModel;
import com.forerunnergames.peril.core.model.people.player.DefaultPlayerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.playmap.DefaultPlayMapModelFactory;
import com.forerunnergames.peril.core.model.playmap.PlayMapModel;
import com.forerunnergames.peril.core.model.playmap.continent.ContinentFactory;
import com.forerunnergames.peril.core.model.playmap.continent.ContinentGraphModel;
import com.forerunnergames.peril.core.model.playmap.country.CountryFactory;
import com.forerunnergames.peril.core.model.playmap.country.CountryGraphModel;
import com.forerunnergames.peril.core.model.turn.DefaultPlayerTurnModel;
import com.forerunnergames.peril.core.model.turn.PlayerTurnModel;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import com.google.common.collect.ImmutableSet;

import net.engio.mbassy.bus.MBassador;

public final class GameModelConfiguration
{
  private final PlayerModel playerModel;
  private final PlayMapModel playMapModel;
  private final CardModel cardModel;
  private final PlayerTurnModel playerTurnModel;
  private final BattleModel battleModel;
  private final GameRules rules;
  private final EventRegistry eventRegistry;
  private final PlayerTurnDataCache <CacheKey> turnDataCache;
  private final InternalCommunicationHandler internalCommHandler;
  private final MBassador <Event> eventBus;

  public GameModelConfiguration (final PlayerModel playerModel,
                                 final PlayMapModel playMapModel,
                                 final CardModel cardModel,
                                 final PlayerTurnModel playerTurnModel,
                                 final BattleModel battleModel,
                                 final GameRules rules,
                                 final EventRegistry eventRegistry,
                                 final PlayerTurnDataCache <CacheKey> turnDataCache,
                                 final InternalCommunicationHandler internalCommHandler,
                                 final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (playerModel, "playerModel");
    Arguments.checkIsNotNull (playMapModel, "playMapModel");
    Arguments.checkIsNotNull (cardModel, "cardModel");
    Arguments.checkIsNotNull (playerTurnModel, "playerTurnModel");
    Arguments.checkIsNotNull (battleModel, "battleModel");
    Arguments.checkIsNotNull (rules, "rules");
    Arguments.checkIsNotNull (eventRegistry, "eventRegistry");
    Arguments.checkIsNotNull (turnDataCache, "turnDataCache");
    Arguments.checkIsNotNull (internalCommHandler, "internalCommHandler");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.playerModel = playerModel;
    this.playMapModel = playMapModel;
    this.cardModel = cardModel;
    this.playerTurnModel = playerTurnModel;
    this.battleModel = battleModel;
    this.rules = rules;
    this.eventRegistry = eventRegistry;
    this.turnDataCache = turnDataCache;
    this.internalCommHandler = internalCommHandler;
    this.eventBus = eventBus;
  }

  public static Builder builder (final GameRules gameRules)
  {
    return new Builder (gameRules);
  }

  public PlayerModel getPlayerModel ()
  {
    return playerModel;
  }

  public PlayMapModel getPlayMapModel ()
  {
    return playMapModel;
  }

  public CardModel getCardModel ()
  {
    return cardModel;
  }

  public PlayerTurnModel getPlayerTurnModel ()
  {
    return playerTurnModel;
  }

  public BattleModel getBattleModel ()
  {
    return battleModel;
  }

  public GameRules getRules ()
  {
    return rules;
  }

  public EventRegistry getEventRegistry ()
  {
    return eventRegistry;
  }

  public PlayerTurnDataCache <CacheKey> getTurnDataCache ()
  {
    return turnDataCache;
  }

  public InternalCommunicationHandler getInternalCommunicationHandler ()
  {
    return internalCommHandler;
  }

  public MBassador <Event> getEventBus ()
  {
    return eventBus;
  }

  public static class Builder
  {
    private final GameRules gameRules;
    private PlayMapModel playMapModel;
    private PlayerModel playerModel;
    private CardModel cardModel;
    private PlayerTurnModel playerTurnModel;
    private BattleModel battleModel;
    private EventRegistry eventRegistry;
    private PlayerTurnDataCache <CacheKey> turnDataCache;
    private InternalCommunicationHandler internalCommHandler;
    private MBassador <Event> eventBus = EventBusFactory.create ();

    public GameModelConfiguration build ()
    {
      if (internalCommHandler == null)
      {
        internalCommHandler = new InternalCommunicationHandler (eventRegistry, eventBus);
      }

      return new GameModelConfiguration (playerModel, playMapModel, cardModel, playerTurnModel, battleModel, gameRules,
              eventRegistry, turnDataCache, internalCommHandler, eventBus);
    }

    public Builder playMapModel (final PlayMapModel playMapModel)
    {
      Arguments.checkIsNotNull (playMapModel, "playMapModel");

      this.playMapModel = playMapModel;
      return this;
    }

    public Builder playerModel (final PlayerModel playerModel)
    {
      Arguments.checkIsNotNull (playerModel, "playerModel");

      this.playerModel = playerModel;
      return this;
    }

    public Builder cardModel (final CardModel cardModel)
    {
      Arguments.checkIsNotNull (cardModel, "cardModel");

      this.cardModel = cardModel;
      return this;
    }

    public Builder playerTurnModel (final PlayerTurnModel playerTurnModel)
    {
      Arguments.checkIsNotNull (playerTurnModel, "playerTurnModel");

      this.playerTurnModel = playerTurnModel;
      return this;
    }

    public Builder battleModel (final BattleModel battleModel)
    {
      Arguments.checkIsNotNull (battleModel, "battleModel");

      this.battleModel = battleModel;
      return this;
    }

    public Builder turnDataCache (final PlayerTurnDataCache <CacheKey> turnDataCache)
    {
      Arguments.checkIsNotNull (turnDataCache, "turnDataCache");

      this.turnDataCache = turnDataCache;
      return this;
    }

    public Builder internalComms (final InternalCommunicationHandler internalCommHandler)
    {
      Arguments.checkIsNotNull (internalCommHandler, "internalCommHandler");

      this.internalCommHandler = internalCommHandler;
      return this;
    }

    public Builder eventRegistry (final EventRegistry eventRegistry)
    {
      Arguments.checkIsNotNull (eventRegistry, "eventRegistry");

      this.eventRegistry = eventRegistry;
      return this;
    }

    public Builder eventBus (final MBassador <Event> eventBus)
    {
      Arguments.checkIsNotNull (eventBus, "eventBus");

      this.eventBus = eventBus;
      return this;
    }

    private Builder (final GameRules gameRules)
    {
      Arguments.checkIsNotNull (gameRules, "gameRules");

      eventRegistry = new DefaultEventRegistry (eventBus);
      this.gameRules = gameRules;
      final CountryFactory defaultCountryFactory = CountryFactory
              .generateDefaultCountries (gameRules.getTotalCountryCount ());
      final ContinentFactory emptyContinentFactory = new ContinentFactory ();
      final CountryGraphModel disjointCountryGraph = CountryGraphModel.disjointCountryGraphFrom (defaultCountryFactory);
      playMapModel = new DefaultPlayMapModelFactory (gameRules)
              .create (disjointCountryGraph,
                       ContinentGraphModel.disjointContinentGraphFrom (emptyContinentFactory, disjointCountryGraph));
      playerModel = new DefaultPlayerModel (gameRules);
      cardModel = new DefaultCardModel (gameRules, playerModel, ImmutableSet. <Card>of ());
      playerTurnModel = new DefaultPlayerTurnModel (gameRules);
      battleModel = new DefaultBattleModel (playMapModel);
      turnDataCache = new PlayerTurnDataCache <> ();
    }
  }
}
