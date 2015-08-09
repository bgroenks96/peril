package com.forerunnergames.peril.core.model;

import com.forerunnergames.peril.core.model.map.DefaultPlayMapModel;
import com.forerunnergames.peril.core.model.map.PlayMapModel;
import com.forerunnergames.peril.core.model.map.continent.Continent;
import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.model.map.country.CountryFactory;
import com.forerunnergames.peril.core.model.people.player.DefaultPlayerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.rules.GameRules;
import com.forerunnergames.peril.core.model.turn.DefaultPlayerTurnModel;
import com.forerunnergames.peril.core.model.turn.PlayerTurnModel;
import com.forerunnergames.peril.core.shared.eventbus.EventBusFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

import net.engio.mbassy.bus.MBassador;

public class GameModelBuilder
{
  private final GameRules gameRules;
  private PlayerModel playerModel;
  private PlayMapModel playMapModel;
  private PlayerTurnModel playerTurnModel;
  private MBassador <Event> eventBus = EventBusFactory.create ();

  public GameModelBuilder (final GameRules gameRules)
  {
    Arguments.checkIsNotNull (gameRules, "gameRules");

    this.gameRules = gameRules;
    playerModel = new DefaultPlayerModel (gameRules);
    playMapModel = new DefaultPlayMapModel (generateDefaultCountries (), ImmutableSet.<Continent> of (), gameRules);
    playerTurnModel = new DefaultPlayerTurnModel (gameRules.getPlayerLimit ());
  }

  public GameModel build ()
  {
    return new GameModel (playerModel, playMapModel, playerTurnModel, gameRules, eventBus);
  }

  public GameModelBuilder playerModel (final PlayerModel playerModel)
  {
    Arguments.checkIsNotNull (playerModel, "playerModel");

    this.playerModel = playerModel;
    return this;
  }

  public GameModelBuilder playMapModel (final PlayMapModel playMapModel)
  {
    Arguments.checkIsNotNull (playMapModel, "playMapModel");

    this.playMapModel = playMapModel;
    return this;
  }

  public GameModelBuilder playerTurnModel (final PlayerTurnModel playerTurnModel)
  {
    Arguments.checkIsNotNull (playerTurnModel, "playerTurnModel");

    this.playerTurnModel = playerTurnModel;
    return this;
  }

  public GameModelBuilder eventBus (final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.eventBus = eventBus;
    return this;
  }

  public ImmutableSet <Country> generateDefaultCountries ()
  {
    final int count = gameRules.getMinTotalCountryCount ();
    final Builder <Country> countrySetBuilder = ImmutableSet.builder ();
    for (int i = 0; i < count; ++i)
    {
      final Country country = CountryFactory.create ("Country-" + i);
      countrySetBuilder.add (country);
    }
    return countrySetBuilder.build ();
  }
}
