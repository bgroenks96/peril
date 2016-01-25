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

import com.forerunnergames.peril.client.events.DefaultStatusMessageEvent;
import com.forerunnergames.peril.client.messages.DefaultStatusMessage;
import com.forerunnergames.peril.client.messages.StatusMessage;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.PlayMapActor;
import com.forerunnergames.peril.common.net.events.server.defaults.DefaultCountryArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.success.ChatMessageSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerClaimCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.messages.ChatMessage;
import com.forerunnergames.peril.common.net.messages.DefaultChatMessage;
import com.forerunnergames.peril.common.net.packets.defaults.DefaultPlayerPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Randomness;
import com.forerunnergames.tools.common.id.IdGenerator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;

import java.util.HashSet;
import java.util.Set;

import net.engio.mbassy.bus.MBassador;

public final class DebugEventGenerator
{
  private static final ImmutableSet <String> VALID_PLAYER_COLORS = ImmutableSet
          .of ("blue", "brown", "cyan", "gold", "green", "pink", "purple", "red", "silver", "teal");

  private static final ImmutableSet <Integer> VALID_SORTED_PLAYER_TURN_ORDERS = ImmutableSet.of (1, 2, 3, 4, 5, 6, 7, 8,
                                                                                                 9, 10);

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

  private final MBassador <Event> eventBus;
  private final Set <String> availablePlayerNames = new HashSet <> (RANDOM_PLAYER_NAMES);
  private UnmodifiableIterator <String> playerColorIterator = createPlayerColorIterator ();
  private UnmodifiableIterator <Integer> playerTurnOrderIterator = createPlayerTurnOrderIterator ();
  private PlayMapActor playMapActor;

  public DebugEventGenerator (final PlayMapActor playMapActor, final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (playMapActor, "playMapActor");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.playMapActor = playMapActor;
    this.eventBus = eventBus;
  }

  public String getRandomCountryName ()
  {
    return Randomness.getRandomElementFrom (playMapActor.getAllCountryNames ());
  }

  public String getRandomPlayerName ()
  {
    return Randomness.getRandomElementFrom (RANDOM_PLAYER_NAMES);
  }

  public void generateStatusMessageEvent ()
  {
    // TODO Production: Remove
    eventBus.publish (new DefaultStatusMessageEvent (createStatusMessage (), ImmutableSet.<PlayerPacket> of ()));
  }

  public void generateChatMessageSuccessEvent ()
  {
    eventBus.publish (new ChatMessageSuccessEvent (createChatMessage ()));
  }

  public void generatePlayerJoinGameSuccessEvent ()
  {
    eventBus.publish (new PlayerJoinGameSuccessEvent (createPlayer ()));
  }

  public void generateCountryArmiesChangedEvent ()
  {
    final int deltaArmyCount = getRandomCountryDeltaArmyCount ();
    final CountryPacket countryPacket = DebugPackets.from (getRandomCountryName (), deltaArmyCount);
    eventBus.publish (new DefaultCountryArmiesChangedEvent (countryPacket, deltaArmyCount));
  }

  public void generatePlayerClaimCountryResponseSuccessEvent ()
  {
    eventBus.publish (new PlayerClaimCountryResponseSuccessEvent (createPlayer (),
            DebugPackets.from (getRandomCountryName ()), 1));
  }

  public void resetPlayers ()
  {
    playerColorIterator = createPlayerColorIterator ();
    playerTurnOrderIterator = createPlayerTurnOrderIterator ();
    availablePlayerNames.clear ();
    availablePlayerNames.addAll (RANDOM_PLAYER_NAMES);
  }

  public void setPlayMapActor (final PlayMapActor playMapActor)
  {
    Arguments.checkIsNotNull (playMapActor, "playMapActor");

    this.playMapActor = playMapActor;
  }

  private static int getRandomCountryDeltaArmyCount ()
  {
    return Randomness.getRandomIntegerFrom (0, 99);
  }

  private static UnmodifiableIterator <String> createPlayerColorIterator ()
  {
    return VALID_PLAYER_COLORS.iterator ();
  }

  private static UnmodifiableIterator <Integer> createPlayerTurnOrderIterator ()
  {
    return VALID_SORTED_PLAYER_TURN_ORDERS.iterator ();
  }

  private StatusMessage createStatusMessage ()
  {
    return new DefaultStatusMessage (createMessageText ());
  }

  private ChatMessage createChatMessage ()
  {
    final Author author = new DefaultPlayerPacket (IdGenerator.generateUniqueId ().value (),
            Randomness.getRandomElementFrom (RANDOM_PLAYER_NAMES), "", 0, 0);

    return new DefaultChatMessage (author, createMessageText ());
  }

  private PlayerPacket createPlayer ()
  {
    if (shouldResetPlayers ()) resetPlayers ();

    return new DefaultPlayerPacket (IdGenerator.generateUniqueId ().value (), nextAvailablePlayerName (),
            nextAvailablePlayerColor (), nextAvailablePlayerTurnOrder (), 0);
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

  private String nextAvailablePlayerColor ()
  {
    return playerColorIterator.next ();
  }

  private int nextAvailablePlayerTurnOrder ()
  {
    return playerTurnOrderIterator.next ();
  }

  private String createMessageText ()
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
