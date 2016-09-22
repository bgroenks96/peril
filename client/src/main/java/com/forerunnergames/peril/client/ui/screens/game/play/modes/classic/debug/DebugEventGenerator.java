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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.debug;

import com.forerunnergames.peril.client.messages.DefaultStatusMessage;
import com.forerunnergames.peril.client.messages.StatusMessage;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.net.events.server.defaults.DefaultCountryArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.success.ChatMessageSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerClaimCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.messages.ChatMessage;
import com.forerunnergames.peril.common.net.messages.DefaultChatMessage;
import com.forerunnergames.peril.common.net.packets.defaults.DefaultPlayerPacket;
import com.forerunnergames.peril.common.net.packets.person.PersonIdentity;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Randomness;
import com.forerunnergames.tools.common.id.IdGenerator;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DebugEventGenerator
{
  private static final Logger log = LoggerFactory.getLogger (DebugEventGenerator.class);

  // @formatter:off

  private static final ImmutableSet <Integer> VALID_SORTED_PLAYER_TURN_ORDERS =
          ImmutableSet.of (1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

  private static final ImmutableList <String> RANDOM_WORDS = ImmutableList
          .of ("Lorem", "ipsum", "dolor", "sit", "amet,", "consectetur", "adipiscing", "elit.", "Mauris", "elementum",
               "nunc", "id", "dolor", "imperdiet", "tincidunt.", "Proin", "rutrum", "leo", "orci,", "nec", "interdum",
               "mauris", "pretium", "ut.", "Suspendisse", "faucibus,", "purus", "vitae", "finibus", "euismod,",
               "libero", "urna", "fermentum", "diam,", "at", "pretium", "quam", "lacus", "vitae", "metus.",
               "Suspendisse", "ac", "tincidunt", "leo.", "Morbi", "a", "tellus", "purus.", "Aenean", "a", "arcu",
               "ante.", "Nulla", "facilisi.", "Aliquam", "pharetra", "sed", "urna", "nec", "efficitur.", "Maecenas",
               "pulvinar", "libero", "eget", "pellentesque", "sodales.", "Donec", "a", "metus", "eget", "mi", "tempus",
               "feugiat.", "Etiam", "fringilla", "ullamcorper", "justo", "ut", "mattis.", "Nam", "egestas", "elit",
               "at", "luctus", "molestie.");

  private static final ImmutableList <String> RANDOM_PLAYER_NAMES = ImmutableList
          .of ("Ben", "Bob", "Jerry", "Oscar", "Evelyn", "Josh", "Eliza", "Aaron", "Maddy", "Brittany", "Jonathan",
               "Adam", "Brian", "[FG] 3xp0n3nt", "[FG] Escendrix", "[LOLZ] nutButter", "[WWWW] WWWWWWWWWWWWWWWW",
               "[X] generalKiller");

  private static final ImmutableList <String> COUNTRY_NAMES = ImmutableList
          .of ("Alaska", "Northwest Territory", "Greenland", "Alberta", "Ontario", "Quebec", "Hawaii",
               "Western United States", "Eastern United States", "Central America", "Caribbean Islands", "Svalbard",
               "Iceland", "Scandinavia", "Great Britain", "Northern Europe", "Ukraine", "Western Europe",
               "Southern Europe", "Ural", "Siberia", "Yakutsk", "Kamchatka", "Afghanistan", "Irkutsk", "Mongolia",
               "Japan", "Middle East", "India", "China", "Siam", "Venezuela", "Peru", "Brazil", "Argentina",
               "Falkland Islands", "North Africa", "Egypt", "Congo", "East Africa", "South Africa", "Madagascar",
               "Philippines", "Indonesia", "New Guinea", "Western Australia", "Eastern Australia", "New Zealand",
               "Antarctica");

  // @formatter:on

  private final MBassador <Event> eventBus;
  private final Set <String> availablePlayerNames = new HashSet<> ();
  private final Set <PlayerColor> availablePlayerColors = new HashSet<> ();
  private final Set <Integer> availablePlayerTurnOrders = new HashSet<> ();
  private final Collection <PlayerPacket> unavailablePlayers = new ArrayList<> ();
  private Iterator <PlayerColor> playerColorIterator;
  private Iterator <Integer> playerTurnOrderIterator;
  private PlayMap playMap;

  private enum NumberSign
  {
    NEGATIVE,
    POSITIVE
  }

  public DebugEventGenerator (final PlayMap playMap, final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (playMap, "playMap");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.playMap = playMap;
    this.eventBus = eventBus;

    resetPlayers ();
  }

  static String getRandomPlayerName ()
  {
    return Randomness.getRandomElementFrom (RANDOM_PLAYER_NAMES);
  }

  static PlayerColor getRandomPlayerColor ()
  {
    return Randomness.getRandomElementFrom (PlayerColor.VALID_VALUES);
  }

  static int getRandomPlayerTurnOrder ()
  {
    return Randomness.getRandomElementFrom (VALID_SORTED_PLAYER_TURN_ORDERS);
  }

  static int getRandomCountryDeltaArmyCount ()
  {
    final int randomDelta = Randomness.getRandomIntegerFrom (0, ClassicGameRules.MAX_ARMIES_ON_COUNTRY);
    final NumberSign randomSign = Randomness.getRandomElementFrom (NumberSign.values ());

    return randomSign == NumberSign.NEGATIVE ? randomDelta * -1 : randomDelta;
  }

  public static int getRandomArmiesInHand ()
  {
    return Randomness.getRandomIntegerFrom (ClassicGameRules.MIN_ARMIES_IN_HAND, ClassicGameRules.MAX_ARMIES_IN_HAND);
  }

  public static int getRandomCardsInHand ()
  {
    return Randomness.getRandomIntegerFrom (ClassicGameRules.ABSOLUTE_MIN_CARDS_IN_HAND,
                                            ClassicGameRules.ABSOLUTE_MAX_CARDS_IN_HAND);
  }

  public void makePlayersUnavailable (final Collection <PlayerPacket> players)
  {
    Arguments.checkIsNotNull (players, "players");
    Arguments.checkHasNoNullElements (players, "players");

    for (final PlayerPacket player : players)
    {
      makePlayerUnavailable (player);
    }
  }

  public void makePlayerUnavailable (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    unavailablePlayers.add (player);

    availablePlayerNames.remove (player.getName ());
    availablePlayerColors.remove (player.getColor ());
    availablePlayerTurnOrders.remove (player.getTurnOrder ());

    resetAvailablePlayerAttributeIterators ();
  }

  public void makePlayerAvailable (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    unavailablePlayers.remove (player);

    if (RANDOM_PLAYER_NAMES.contains (player.getName ())) availablePlayerNames.add (player.getName ());
    availablePlayerColors.add (player.getColor ());
    availablePlayerTurnOrders.add (player.getTurnOrder ());

    resetAvailablePlayerAttributeIterators ();
  }

  String getRandomCountryName ()
  {
    return Randomness.getRandomElementFrom (playMap.getAllCountryNames ());
  }

  Optional <? extends PlayerPacket> createAvailablePlayer ()
  {
    if (shouldResetPlayers ()) resetPlayersKeepUnavailable ();
    if (shouldResetPlayers ()) return Optional.absent ();

    return Optional.of (new DefaultPlayerPacket (IdGenerator.generateUniqueId ().value (), nextAvailablePlayerName (),
            nextAvailablePlayerColor (), nextAvailablePlayerTurnOrder (), 0, getRandomCardsInHand ()));
  }

  void generateChatMessageSuccessEvent ()
  {
    eventBus.publish (new ChatMessageSuccessEvent (createRandomChatMessage ()));
  }

  void generatePlayerJoinGameSuccessEvent ()
  {
    final Optional <? extends PlayerPacket> player = createAvailablePlayer ();

    if (!player.isPresent ())
    {
      log.warn ("Cannot generate {}: No more available players.", PlayerJoinGameSuccessEvent.class.getSimpleName ());
      return;
    }

    eventBus.publish (new PlayerJoinGameSuccessEvent (player.get (), PersonIdentity.UNKNOWN,
            ImmutableSet.copyOf (unavailablePlayers)));
  }

  void generateCountryArmiesChangedEvent ()
  {
    final int deltaArmyCount = getRandomCountryDeltaArmyCount ();
    final CountryPacket countryPacket = DebugPackets.asCountryPacket (getRandomCountryName (), deltaArmyCount);
    eventBus.publish (new DefaultCountryArmiesChangedEvent (countryPacket, deltaArmyCount));
  }

  void generatePlayerClaimCountryResponseSuccessEvent ()
  {
    eventBus.publish (new PlayerClaimCountryResponseSuccessEvent (createRandomPlayer (),
            DebugPackets.asCountryPacket (getRandomCountryName ()), 1));
  }

  void resetPlayers ()
  {
    resetAvailablePlayerAttributes ();
    resetAvailablePlayerAttributeIterators ();

    unavailablePlayers.clear ();
  }

  void setPlayMap (final PlayMap playMap)
  {
    Arguments.checkIsNotNull (playMap, "playMap");

    this.playMap = playMap;
  }

  StatusMessage createRandomStatusMessage ()
  {
    return new DefaultStatusMessage (createRandomMessageText ());
  }

  private static PlayerPacket createRandomPlayer ()
  {
    return new DefaultPlayerPacket (IdGenerator.generateUniqueId ().value (), getRandomPlayerName (),
            getRandomPlayerColor (), getRandomPlayerTurnOrder (), 0, 0);
  }

  private void resetPlayersKeepUnavailable ()
  {
    resetAvailablePlayerAttributes ();
    removeUnavailablePlayerAttributes ();
    resetAvailablePlayerAttributeIterators ();
  }

  private void resetAvailablePlayerAttributes ()
  {
    availablePlayerNames.clear ();
    availablePlayerColors.clear ();
    availablePlayerTurnOrders.clear ();

    availablePlayerNames.addAll (RANDOM_PLAYER_NAMES);
    availablePlayerColors.addAll (PlayerColor.VALID_VALUES);
    availablePlayerTurnOrders.addAll (VALID_SORTED_PLAYER_TURN_ORDERS);
  }

  private void removeUnavailablePlayerAttributes ()
  {
    for (final PlayerPacket player : unavailablePlayers)
    {
      availablePlayerNames.remove (player.getName ());
      availablePlayerColors.remove (player.getColor ());
      availablePlayerTurnOrders.remove (player.getTurnOrder ());
    }
  }

  private void resetAvailablePlayerAttributeIterators ()
  {
    playerColorIterator = availablePlayerColors.iterator ();
    playerTurnOrderIterator = availablePlayerTurnOrders.iterator ();
  }

  private ChatMessage createRandomChatMessage ()
  {
    final Author author = new DefaultPlayerPacket (IdGenerator.generateUniqueId ().value (),
            Randomness.getRandomElementFrom (RANDOM_PLAYER_NAMES),
            Randomness.getRandomElementFrom (PlayerColor.VALID_VALUES), 0, 0, 0);

    return new DefaultChatMessage (author, createRandomMessageText ());
  }

  private boolean shouldResetPlayers ()
  {
    return !playerColorIterator.hasNext () || !playerTurnOrderIterator.hasNext () || availablePlayerNames.isEmpty ();
  }

  private String nextAvailablePlayerName ()
  {
    final String playerName = Randomness.getRandomElementFrom (availablePlayerNames);

    availablePlayerNames.remove (playerName);

    return playerName;
  }

  private PlayerColor nextAvailablePlayerColor ()
  {
    return playerColorIterator.next ();
  }

  private int nextAvailablePlayerTurnOrder ()
  {
    return playerTurnOrderIterator.next ();
  }

  private String createRandomMessageText ()
  {
    final ImmutableList <String> randomSubsetWordList = RANDOM_WORDS.subList (0,
                                                                              Randomness.getRandomIntegerFrom (1, 30));
    final StringBuilder randomSubsetWordListStringBuilder = new StringBuilder ();

    for (final String word : randomSubsetWordList)
    {
      randomSubsetWordListStringBuilder.append (word).append (" ");
    }

    randomSubsetWordListStringBuilder.deleteCharAt (randomSubsetWordListStringBuilder.lastIndexOf (" "));

    return randomSubsetWordListStringBuilder.toString ();
  }
}
