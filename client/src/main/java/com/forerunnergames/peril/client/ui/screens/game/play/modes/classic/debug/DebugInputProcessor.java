package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.debug;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.messages.StatusMessage;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.CountryActor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.PlayMapActor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryPrimaryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.BattlePopup;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.OccupationPopup;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.PlayerBox;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.ReinforcementPopup;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBox;
import com.forerunnergames.peril.common.net.messages.ChatMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Randomness;

import net.engio.mbassy.bus.MBassador;

public final class DebugInputProcessor extends InputAdapter
{
  private final MouseInput mouseInput;
  private final MessageBox <StatusMessage> statusBox;
  private final MessageBox <ChatMessage> chatBox;
  private final PlayerBox playerBox;
  private final OccupationPopup occupationPopup;
  private final ReinforcementPopup reinforcementPopup;
  private final BattlePopup battlePopup;
  private final DebugEventGenerator eventGenerator;
  private PlayMapActor playMapActor;

  public DebugInputProcessor (final MouseInput mouseInput,
                              final PlayMapActor playMapActor,
                              final MessageBox <StatusMessage> statusBox,
                              final MessageBox <ChatMessage> chatBox,
                              final PlayerBox playerBox,
                              final OccupationPopup occupationPopup,
                              final ReinforcementPopup reinforcementPopup,
                              final BattlePopup battlePopup,
                              final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (playMapActor, "playMapActor");
    Arguments.checkIsNotNull (statusBox, "statusBox");
    Arguments.checkIsNotNull (chatBox, "chatBox");
    Arguments.checkIsNotNull (playerBox, "playerBox");
    Arguments.checkIsNotNull (occupationPopup, "occupationPopup");
    Arguments.checkIsNotNull (reinforcementPopup, "reinforcementPopup");
    Arguments.checkIsNotNull (battlePopup, "battlePopup");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.mouseInput = mouseInput;
    this.playMapActor = playMapActor;
    this.statusBox = statusBox;
    this.chatBox = chatBox;
    this.playerBox = playerBox;
    this.occupationPopup = occupationPopup;
    this.reinforcementPopup = reinforcementPopup;
    this.battlePopup = battlePopup;

    eventGenerator = new DebugEventGenerator (eventBus);
  }

  @Override
  public boolean keyDown (final int keycode)
  {
    switch (keycode)
    {
      case Input.Keys.Z:
      {
        playMapActor.disable ();

        return true;
      }
      case Input.Keys.X:
      {
        playMapActor.enable (mouseInput.position ());

        return true;
      }
      case Input.Keys.NUM_1:
      {
        playMapActor.resetCountryStates ();

        return true;
      }
      case Input.Keys.NUM_2:
      {
        playMapActor.randomizeCountryStates ();

        return true;
      }
      case Input.Keys.NUM_3:
      {
        playMapActor.randomizeCountryStatesUsingNRandomStates (Randomness.getRandomIntegerFrom (1, 10));

        return true;
      }
      case Input.Keys.NUM_4:
      {
        playMapActor.randomizeCountryStatesUsingNRandomStates (2);

        return true;
      }
      case Input.Keys.NUM_5:
      {
        playMapActor.randomizeCountryStatesUsingNRandomStates (3);

        return true;
      }
      case Input.Keys.NUM_6:
      {
        // North America
        playMapActor.setCountryState ("Alaska", CountryPrimaryImageState.GOLD);
        playMapActor.setCountryState ("Northwest Territory", CountryPrimaryImageState.GOLD);
        playMapActor.setCountryState ("Greenland", CountryPrimaryImageState.GOLD);
        playMapActor.setCountryState ("Alberta", CountryPrimaryImageState.GOLD);
        playMapActor.setCountryState ("Ontario", CountryPrimaryImageState.GOLD);
        playMapActor.setCountryState ("Quebec", CountryPrimaryImageState.GOLD);
        playMapActor.setCountryState ("Western United States", CountryPrimaryImageState.GOLD);
        playMapActor.setCountryState ("Eastern United States", CountryPrimaryImageState.GOLD);
        playMapActor.setCountryState ("Central America", CountryPrimaryImageState.GOLD);

        // South America
        playMapActor.setCountryState ("Venezuela", CountryPrimaryImageState.RED);
        playMapActor.setCountryState ("Peru", CountryPrimaryImageState.RED);
        playMapActor.setCountryState ("Brazil", CountryPrimaryImageState.RED);
        playMapActor.setCountryState ("Argentina", CountryPrimaryImageState.RED);

        // Europe
        playMapActor.setCountryState ("Iceland", CountryPrimaryImageState.BLUE);
        playMapActor.setCountryState ("Scandinavia", CountryPrimaryImageState.BLUE);
        playMapActor.setCountryState ("Great Britain", CountryPrimaryImageState.BLUE);
        playMapActor.setCountryState ("Northern Europe", CountryPrimaryImageState.BLUE);
        playMapActor.setCountryState ("Ukraine", CountryPrimaryImageState.BLUE);
        playMapActor.setCountryState ("Western Europe", CountryPrimaryImageState.BLUE);
        playMapActor.setCountryState ("Southern Europe", CountryPrimaryImageState.BLUE);

        // Asia
        playMapActor.setCountryState ("Ural", CountryPrimaryImageState.GREEN);
        playMapActor.setCountryState ("Siberia", CountryPrimaryImageState.GREEN);
        playMapActor.setCountryState ("Yakutsk", CountryPrimaryImageState.GREEN);
        playMapActor.setCountryState ("Kamchatka", CountryPrimaryImageState.GREEN);
        playMapActor.setCountryState ("Afghanistan", CountryPrimaryImageState.GREEN);
        playMapActor.setCountryState ("Irkutsk", CountryPrimaryImageState.GREEN);
        playMapActor.setCountryState ("Mongolia", CountryPrimaryImageState.GREEN);
        playMapActor.setCountryState ("Japan", CountryPrimaryImageState.GREEN);
        playMapActor.setCountryState ("Middle East", CountryPrimaryImageState.GREEN);
        playMapActor.setCountryState ("India", CountryPrimaryImageState.GREEN);
        playMapActor.setCountryState ("China", CountryPrimaryImageState.GREEN);
        playMapActor.setCountryState ("Siam", CountryPrimaryImageState.GREEN);

        // Africa
        playMapActor.setCountryState ("North Africa", CountryPrimaryImageState.BROWN);
        playMapActor.setCountryState ("Egypt", CountryPrimaryImageState.BROWN);
        playMapActor.setCountryState ("Congo", CountryPrimaryImageState.BROWN);
        playMapActor.setCountryState ("East Africa", CountryPrimaryImageState.BROWN);
        playMapActor.setCountryState ("South Africa", CountryPrimaryImageState.BROWN);
        playMapActor.setCountryState ("Madagascar", CountryPrimaryImageState.BROWN);

        // Australia
        playMapActor.setCountryState ("Indonesia", CountryPrimaryImageState.PINK);
        playMapActor.setCountryState ("New Guinea", CountryPrimaryImageState.PINK);
        playMapActor.setCountryState ("Western Australia", CountryPrimaryImageState.PINK);
        playMapActor.setCountryState ("Eastern Australia", CountryPrimaryImageState.PINK);

        // Not used in classic mode
        playMapActor.setCountryState ("Hawaii", CountryPrimaryImageState.DISABLED);
        playMapActor.setCountryState ("Caribbean Islands", CountryPrimaryImageState.DISABLED);
        playMapActor.setCountryState ("Falkland Islands", CountryPrimaryImageState.DISABLED);
        playMapActor.setCountryState ("Svalbard", CountryPrimaryImageState.DISABLED);
        playMapActor.setCountryState ("Philippines", CountryPrimaryImageState.DISABLED);
        playMapActor.setCountryState ("New Zealand", CountryPrimaryImageState.DISABLED);
        playMapActor.setCountryState ("Antarctica", CountryPrimaryImageState.DISABLED);

        return true;
      }
      case Input.Keys.NUM_7:
      {
        playMapActor.randomizeCountryStatesUsingOnly (CountryPrimaryImageState.TEAL, CountryPrimaryImageState.CYAN);

        return true;
      }
      case Input.Keys.NUM_8:
      {
        playMapActor.randomizeCountryStatesUsingOnly (CountryPrimaryImageState.BLUE, CountryPrimaryImageState.CYAN);

        return true;
      }
      case Input.Keys.NUM_9:
      {
        playMapActor.randomizeCountryStatesUsingOnly (CountryPrimaryImageState.PINK, CountryPrimaryImageState.PURPLE);

        return true;
      }
      case Input.Keys.NUM_0:
      {
        playMapActor.randomizeCountryStatesUsingOnly (CountryPrimaryImageState.RED, CountryPrimaryImageState.BROWN);

        return true;
      }
      case Input.Keys.MINUS:
      {
        playMapActor.randomizeCountryStatesUsingOnly (CountryPrimaryImageState.CYAN, CountryPrimaryImageState.SILVER);

        return true;
      }
      case Input.Keys.EQUALS:
      {
        playMapActor.randomizeCountryStatesUsingOnly (CountryPrimaryImageState.TEAL, CountryPrimaryImageState.GREEN);

        return true;
      }
      case Input.Keys.Q:
      {
        playMapActor.setCountriesTo (CountryPrimaryImageState.BLUE);

        return true;
      }
      case Input.Keys.W:
      {
        playMapActor.setCountriesTo (CountryPrimaryImageState.BROWN);

        return true;
      }
      case Input.Keys.E:
      {
        playMapActor.setCountriesTo (CountryPrimaryImageState.CYAN);

        return true;
      }
      case Input.Keys.R:
      {
        playMapActor.setCountriesTo (CountryPrimaryImageState.GOLD);

        return true;
      }
      case Input.Keys.T:
      {
        playMapActor.setCountriesTo (CountryPrimaryImageState.GREEN);

        return true;
      }
      case Input.Keys.Y:
      {
        playMapActor.setCountriesTo (CountryPrimaryImageState.PINK);

        return true;
      }
      case Input.Keys.U:
      {
        playMapActor.setCountriesTo (CountryPrimaryImageState.PURPLE);

        return true;
      }
      case Input.Keys.I:
      {
        playMapActor.setCountriesTo (CountryPrimaryImageState.RED);

        return true;
      }
      case Input.Keys.O:
      {
        playMapActor.setCountriesTo (CountryPrimaryImageState.SILVER);

        return true;
      }
      case Input.Keys.LEFT_BRACKET:
      {
        playMapActor.setCountriesTo (CountryPrimaryImageState.TEAL);

        return true;
      }
      case Input.Keys.RIGHT_BRACKET:
      {
        playMapActor.setCountriesTo (CountryPrimaryImageState.DISABLED);

        return true;
      }
      default:
      {
        return false;
      }
    }
  }

  @Override
  public boolean keyTyped (final char character)
  {
    switch (character)
    {
      case 's':
      {
        eventGenerator.generateStatusMessageEvent ();

        return true;
      }
      case 'S':
      {
        statusBox.clear ();

        return true;
      }
      case 'c':
      {
        eventGenerator.generateChatMessageSuccessEvent ();

        return true;
      }
      case 'C':
      {
        chatBox.clear ();

        return true;
      }
      case 'p':
      {
        eventGenerator.generatePlayerJoinGameSuccessEvent ();

        return true;
      }
      case 'P':
      {
        playerBox.clear ();
        eventGenerator.resetPlayers ();

        return true;
      }
      case 'a':
      {
        eventGenerator.generateCountryArmiesChangedEvent ();

        return true;
      }
      case 'A':
      {
        playMapActor.resetArmies ();

        return true;
      }
      case 'd':
      {
        eventGenerator.generatePlayerSelectCountryResponseSuccessEvent ();

        return true;
      }
      case 'm':
      {
        // final String sourceCountryName = "Brazil";
        // final String destinationCountryName = "Brazil";

        String sourceCountryName;

        do
        {
          sourceCountryName = DebugEventGenerator.getRandomCountryName ();
        }
        while (!playMapActor.existsCountryActorWithName (sourceCountryName));

        String destinationCountryName;

        do
        {
          destinationCountryName = DebugEventGenerator.getRandomCountryName ();
        }
        while (destinationCountryName.equals (sourceCountryName)
                || !playMapActor.existsCountryActorWithName (destinationCountryName));

        final CountryActor sourceCountryActor = playMapActor.getCountryActorWithName (sourceCountryName);
        final CountryActor destinationCountryActor = playMapActor.getCountryActorWithName (destinationCountryName);
        final int totalArmies = Randomness.getRandomIntegerFrom (4, 99);
        final int minArmies = Randomness.getRandomIntegerFrom (1, 3);
        final int maxArmies = totalArmies - 1;

        occupationPopup.show (minArmies, maxArmies, sourceCountryActor, destinationCountryActor, totalArmies);
        playMapActor.disable ();

        return true;
      }
      case 'n':
      {
        // final String sourceCountryName = "Brazil";
        // final String destinationCountryName = "Brazil";

        String sourceCountryName;

        do
        {
          sourceCountryName = DebugEventGenerator.getRandomCountryName ();
        }
        while (!playMapActor.existsCountryActorWithName (sourceCountryName));

        String destinationCountryName;

        do
        {
          destinationCountryName = DebugEventGenerator.getRandomCountryName ();
        }
        while (destinationCountryName.equals (sourceCountryName)
                || !playMapActor.existsCountryActorWithName (destinationCountryName));

        final CountryActor sourceCountryActor = playMapActor.getCountryActorWithName (sourceCountryName);
        final CountryActor destinationCountryActor = playMapActor.getCountryActorWithName (destinationCountryName);
        final int totalArmies = Randomness.getRandomIntegerFrom (4, 99);
        final int minArmies = Randomness.getRandomIntegerFrom (1, 3);
        final int maxArmies = totalArmies - 1;

        reinforcementPopup.show (minArmies, maxArmies, sourceCountryActor, destinationCountryActor, totalArmies);
        playMapActor.disable ();

        return true;
      }
      case 'b':
      {
        // final String attackingCountryName = "Brazil";
        // final String defendingCountryName = "Brazil";

        String attackingCountryName;

        do
        {
          attackingCountryName = DebugEventGenerator.getRandomCountryName ();
        }
        while (!playMapActor.existsCountryActorWithName (attackingCountryName));

        String defendingCountryName;

        do
        {
          defendingCountryName = DebugEventGenerator.getRandomCountryName ();
        }
        while (defendingCountryName.equals (attackingCountryName)
                || !playMapActor.existsCountryActorWithName (defendingCountryName));

        final CountryActor attackingCountryActor = playMapActor.getCountryActorWithName (attackingCountryName);
        final CountryActor defendingCountryActor = playMapActor.getCountryActorWithName (defendingCountryName);
        final int attackingCountryArmies = Randomness.getRandomIntegerFrom (1, 3);
        final int defendingCountryArmies = Randomness.getRandomIntegerFrom (1, 99);

        battlePopup.show (attackingCountryActor, defendingCountryActor, attackingCountryArmies, defendingCountryArmies);
        playMapActor.disable ();

        return true;
      }
      default:
      {
        return false;
      }
    }
  }

  public void setPlayMapActor (final PlayMapActor playMapActor)
  {
    Arguments.checkIsNotNull (playMapActor, "playMapActor");

    this.playMapActor = playMapActor;
  }

  public void reset ()
  {
    playMapActor.reset ();
    statusBox.clear ();
    chatBox.clear ();
    playerBox.clear ();
    eventGenerator.resetPlayers ();
  }
}
