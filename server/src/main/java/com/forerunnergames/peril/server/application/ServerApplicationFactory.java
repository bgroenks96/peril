/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.server.application;

import com.forerunnergames.peril.common.eventbus.EventBusFactory;
import com.forerunnergames.peril.common.game.DefaultGameConfiguration;
import com.forerunnergames.peril.common.game.GameConfiguration;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.game.rules.GameRulesFactory;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.io.MapMetadataFinder;
import com.forerunnergames.peril.common.net.DefaultGameServerConfiguration;
import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.kryonet.KryonetRegistration;
import com.forerunnergames.peril.common.settings.NetworkSettings;
import com.forerunnergames.peril.core.model.GameModel;
import com.forerunnergames.peril.core.model.card.Card;
import com.forerunnergames.peril.core.model.card.CardModel;
import com.forerunnergames.peril.core.model.card.DefaultCardModel;
import com.forerunnergames.peril.core.model.card.io.CardModelDataFactoryCreator;
import com.forerunnergames.peril.core.model.map.DefaultPlayMapModelFactory;
import com.forerunnergames.peril.core.model.map.PlayMapModel;
import com.forerunnergames.peril.core.model.map.PlayMapModelFactory;
import com.forerunnergames.peril.core.model.map.continent.ContinentFactory;
import com.forerunnergames.peril.core.model.map.continent.ContinentMapGraphModel;
import com.forerunnergames.peril.core.model.map.country.CountryFactory;
import com.forerunnergames.peril.core.model.map.country.CountryMapGraphModel;
import com.forerunnergames.peril.core.model.map.country.DefaultCountryIdResolver;
import com.forerunnergames.peril.core.model.map.io.CoreMapMetadataFinderFactory;
import com.forerunnergames.peril.core.model.map.io.PlayMapModelDataFactory;
import com.forerunnergames.peril.core.model.map.io.PlayMapModelDataFactoryCreator;
import com.forerunnergames.peril.core.model.people.player.DefaultPlayerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.state.StateMachineEventHandler;
import com.forerunnergames.peril.core.model.turn.DefaultPlayerTurnModel;
import com.forerunnergames.peril.core.model.turn.PlayerTurnModel;
import com.forerunnergames.peril.server.communicators.DefaultCoreCommunicator;
import com.forerunnergames.peril.server.communicators.DefaultSpectatorCommunicator;
import com.forerunnergames.peril.server.communicators.DefaultPlayerCommunicator;
import com.forerunnergames.peril.server.controllers.EventBasedServerController;
import com.forerunnergames.peril.server.controllers.MultiplayerController;
import com.forerunnergames.peril.server.kryonet.KryonetServer;
import com.forerunnergames.peril.server.main.args.CommandLineArgs;
import com.forerunnergames.tools.common.Application;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.controllers.Controller;
import com.forerunnergames.tools.net.DefaultExternalAddressResolver;
import com.forerunnergames.tools.net.ExternalAddressResolver;
import com.forerunnergames.tools.net.server.DefaultServerConfiguration;
import com.forerunnergames.tools.net.server.Server;
import com.forerunnergames.tools.net.server.ServerConfiguration;
import com.forerunnergames.tools.net.server.ServerController;

import com.google.common.collect.ImmutableSet;

import de.matthiasmann.AsyncExecution;

import net.engio.mbassy.bus.MBassador;

public final class ServerApplicationFactory
{
  public static Application create (final CommandLineArgs args)
  {
    Arguments.checkIsNotNull (args, "args");

    final MBassador <Event> eventBus = EventBusFactory.create ();

    final Server server = new KryonetServer ();

    final AsyncExecution mainThreadExecutor = new AsyncExecution ();

    final ServerController serverController = new EventBasedServerController (server, args.serverTcpPort,
            KryonetRegistration.CLASSES, eventBus, mainThreadExecutor);

    final MapMetadataFinder mapMetadataFinder = CoreMapMetadataFinderFactory.create (args.gameMode);
    final MapMetadata mapMetadata = mapMetadataFinder.find (args.mapName);

    final PlayMapModelDataFactory dataFactory = PlayMapModelDataFactoryCreator.create (args.gameMode);

    final CountryFactory countryFactory = dataFactory.createCountries (mapMetadata);

    final ContinentFactory continentFactory = dataFactory
            .createContinents (mapMetadata, new DefaultCountryIdResolver (countryFactory));

    final CountryMapGraphModel countryMapGraphModel = dataFactory.createCountryMapGraphModel (mapMetadata,
                                                                                              countryFactory);

    final ContinentMapGraphModel continentMapGraphModel = dataFactory
            .createContinentMapGraphModel (mapMetadata, continentFactory, countryMapGraphModel);

    final ImmutableSet <Card> cards = CardModelDataFactoryCreator.create (args.gameMode).createCards (mapMetadata);

    final GameRules gameRules = GameRulesFactory.create (args.gameMode, args.playerLimit, args.winPercentage,
                                                         countryFactory.getCountryCount (),
                                                         args.initialCountryAssignment);

    final PlayMapModelFactory playMapModelFactory = new DefaultPlayMapModelFactory (gameRules);

    final PlayerModel playerModel = new DefaultPlayerModel (gameRules);
    final PlayMapModel playMapModel = playMapModelFactory.create (countryMapGraphModel, continentMapGraphModel);
    final CardModel cardModel = new DefaultCardModel (gameRules, cards);
    final PlayerTurnModel playerTurnModel = new DefaultPlayerTurnModel (args.playerLimit);
    final GameModel gameModel = GameModel.builder (gameRules).playMapModel (playMapModel).playerModel (playerModel)
            .cardModel (cardModel).playerTurnModel (playerTurnModel).eventBus (eventBus).build ();
    final StateMachineEventHandler gameStateMachine = new StateMachineEventHandler (gameModel);

    final GameConfiguration gameConfig = new DefaultGameConfiguration (args.gameMode, args.playerLimit,
            args.spectatorLimit, args.winPercentage, args.initialCountryAssignment, mapMetadata);

    final ExternalAddressResolver externalAddressResolver = new DefaultExternalAddressResolver (
            NetworkSettings.EXTERNAL_IP_RESOLVER_URL, NetworkSettings.EXTERNAL_IP_RESOLVER_BACKUP_URL);

    final ServerConfiguration serverConfig = new DefaultServerConfiguration (externalAddressResolver.resolveIp (),
            args.serverTcpPort);

    final GameServerConfiguration gameServerConfig = new DefaultGameServerConfiguration (args.gameServerName,
            args.gameServerType, gameConfig, serverConfig);

    final Controller multiplayerController = new MultiplayerController (gameServerConfig, serverController,
            new DefaultPlayerCommunicator (serverController), new DefaultSpectatorCommunicator (serverController),
            new DefaultCoreCommunicator (eventBus), eventBus);

    return new ServerApplication (gameStateMachine, eventBus, mainThreadExecutor, serverController,
            multiplayerController);
  }

  private ServerApplicationFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
