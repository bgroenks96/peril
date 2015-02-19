package com.forerunnergames.peril.client.ui.screens.game.play.debug;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerFactory;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultStatusMessageEvent;
import com.forerunnergames.peril.core.shared.net.events.success.ChatMessageSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.core.shared.net.messages.ChatMessage;
import com.forerunnergames.peril.core.shared.net.messages.DefaultChatMessage;
import com.forerunnergames.peril.core.shared.net.messages.DefaultStatusMessage;
import com.forerunnergames.peril.core.shared.net.messages.StatusMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Randomness;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;

import java.util.HashSet;
import java.util.Set;

import net.engio.mbassy.bus.MBassador;

public final class DebugEventGenerator
{
  private final ImmutableList <String> RANDOM_WORDS = ImmutableList.of ("Lorem", "ipsum", "dolor", "sit", "amet,",
                                                                        "consectetur", "adipiscing", "elit.", "Mauris",
                                                                        "elementum", "nunc", "id", "dolor",
                                                                        "imperdiet", "tincidunt.", "Proin", "rutrum",
                                                                        "leo", "orci,", "nec", "interdum", "mauris",
                                                                        "pretium", "ut.", "Suspendisse", "faucibus,",
                                                                        "purus", "vitae", "finibus", "euismod,",
                                                                        "libero", "urna", "fermentum", "diam,", "at",
                                                                        "pretium", "quam", "lacus", "vitae", "metus.",
                                                                        "Suspendisse", "ac", "tincidunt", "leo.",
                                                                        "Morbi", "a", "tellus", "purus.", "Aenean",
                                                                        "a", "arcu", "ante.", "Nulla", "facilisi.",
                                                                        "Aliquam", "pharetra", "sed", "urna", "nec",
                                                                        "efficitur.", "Maecenas", "pulvinar", "libero",
                                                                        "eget", "pellentesque", "sodales.", "Donec",
                                                                        "a", "metus", "eget", "mi", "tempus",
                                                                        "feugiat.", "Etiam", "fringilla",
                                                                        "ullamcorper", "justo", "ut", "mattis.", "Nam",
                                                                        "egestas", "elit", "at", "luctus", "molestie.");

  private final ImmutableList <String> RANDOM_PLAYER_NAMES = ImmutableList.of ("Ben", "Bob", "Jerry", "Oscar",
                                                                               "Evelyn", "Josh", "Eliza", "Aaron",
                                                                               "Maddy", "Brittany", "Jonathan", "Adam",
                                                                               "Brian", "[FG] 3xp0nn3t",
                                                                               "[FG] Escendrix", "[LOLZ] nutButter",
                                                                               "[WWWW] WWWWWWWWWWWWWWWW",
                                                                               "[X] generalKiller");
  private final MBassador <Event> eventBus;
  private UnmodifiableIterator <PlayerTurnOrder> playerTurnOrderIterator = PlayerTurnOrder.validValues ().iterator ();
  private Set <String> availablePlayerNames = new HashSet <> (RANDOM_PLAYER_NAMES);

  public DebugEventGenerator (final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.eventBus = eventBus;
  }

  public void generateStatusMessageEvent ()
  {
    eventBus.publish (new DefaultStatusMessageEvent (createStatusMessage ()));
  }

  public void generateChatMessageSuccessEvent ()
  {
    eventBus.publish (new ChatMessageSuccessEvent (createChatMessage ()));
  }

  public void generatePlayerJoinGameSuccessEvent ()
  {
    eventBus.publish (new PlayerJoinGameSuccessEvent (createPlayer ()));
  }

  public void resetPlayers ()
  {
    playerTurnOrderIterator = createPlayerTurnOrderIterator ();
    availablePlayerNames.clear ();
    availablePlayerNames.addAll (RANDOM_PLAYER_NAMES);
  }

  private StatusMessage createStatusMessage ()
  {
    return new DefaultStatusMessage (createMessageText ());
  }

  private ChatMessage createChatMessage ()
  {
    final Author author = PlayerFactory.builder (Randomness.getRandomElementFrom (RANDOM_PLAYER_NAMES)).build();

    return new DefaultChatMessage (author, createMessageText ());
  }

  private Player createPlayer ()
  {
    if (shouldResetPlayers ()) resetPlayers ();

    return PlayerFactory.builder (createPlayerName ()).turnOrder (createPlayerTurnOrder ()).build ();
  }

  private boolean shouldResetPlayers ()
  {
    return ! playerTurnOrderIterator.hasNext () || availablePlayerNames.isEmpty ();
  }

  private String createPlayerName ()
  {
    final String playerName = Randomness.getRandomElementFrom (availablePlayerNames);

    availablePlayerNames.remove (playerName);

    return playerName;
  }

  private PlayerTurnOrder createPlayerTurnOrder ()
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

  private UnmodifiableIterator <PlayerTurnOrder> createPlayerTurnOrderIterator ()
  {
    return PlayerTurnOrder.validValues ().iterator ();
  }
}
