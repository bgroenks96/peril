package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;

import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.ScreenController;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.CountryActor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.PlayMapActor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.TerritoryTextActor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.MandatoryOccupationPopup;
import com.forerunnergames.peril.client.ui.widgets.MessageBox;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.peril.core.shared.net.messages.ChatMessage;
import com.forerunnergames.peril.core.shared.net.messages.StatusMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Message;
import com.forerunnergames.tools.common.Randomness;

import net.engio.mbassy.bus.MBassador;

public final class DebugInputProcessor extends InputAdapter
{
  private final ScreenController screenController;
  private final PlayMapActor playMapActor;
  private final TerritoryTextActor territoryTextActor;
  private final MessageBox <StatusMessage> statusBox;
  private final MessageBox <ChatMessage> chatBox;
  private final MessageBox <Message> playerBox;
  private final MandatoryOccupationPopup mandatoryOccupationPopup;
  private final DebugEventGenerator eventGenerator;

  public DebugInputProcessor (final ScreenController screenController,
                              final PlayMapActor playMapActor,
                              final TerritoryTextActor territoryTextActor,
                              final MessageBox <StatusMessage> statusBox,
                              final MessageBox <ChatMessage> chatBox,
                              final MessageBox <Message> playerBox,
                              final MandatoryOccupationPopup mandatoryOccupationPopup,
                              final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (screenController, "screenController");
    Arguments.checkIsNotNull (playMapActor, "playMapActor");
    Arguments.checkIsNotNull (territoryTextActor, "territoryTextActor");
    Arguments.checkIsNotNull (statusBox, "statusBox");
    Arguments.checkIsNotNull (chatBox, "chatBox");
    Arguments.checkIsNotNull (playerBox, "playerBox");
    Arguments.checkIsNotNull (mandatoryOccupationPopup, "mandatoryOccupationPopup");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.screenController = screenController;
    this.playMapActor = playMapActor;
    this.territoryTextActor = territoryTextActor;
    this.statusBox = statusBox;
    this.chatBox = chatBox;
    this.playerBox = playerBox;
    this.mandatoryOccupationPopup = mandatoryOccupationPopup;

    eventGenerator = new DebugEventGenerator (eventBus);
  }

  @Override
  public boolean keyDown (final int keycode)
  {
    switch (keycode)
    {
      case Input.Keys.LEFT:
      {
        screenController.toPreviousScreenOr (ScreenId.MAIN_MENU);

        return true;
      }
      case Input.Keys.RIGHT:
      {
        screenController.toScreen (ScreenId.PLAY_PERIL);

        return true;
      }
      case Input.Keys.ESCAPE:
      {
        Gdx.app.exit ();

        return true;
      }
      case Input.Keys.NUM_1:
      {
        playMapActor.setCountriesTo (CountryImageState.UNOWNED);

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
        playMapActor.setClassicCountryStates ();

        return true;
      }
      case Input.Keys.NUM_7:
      {
        playMapActor.randomizeCountryStatesUsingOnly (CountryImageState.TEAL, CountryImageState.CYAN);

        return true;
      }
      case Input.Keys.NUM_8:
      {
        playMapActor.randomizeCountryStatesUsingOnly (CountryImageState.BLUE, CountryImageState.CYAN);

        return true;
      }
      case Input.Keys.NUM_9:
      {
        playMapActor.randomizeCountryStatesUsingOnly (CountryImageState.PINK, CountryImageState.PURPLE);

        return true;
      }
      case Input.Keys.NUM_0:
      {
        playMapActor.randomizeCountryStatesUsingOnly (CountryImageState.RED, CountryImageState.BROWN);

        return true;
      }
      case Input.Keys.MINUS:
      {
        playMapActor.randomizeCountryStatesUsingOnly (CountryImageState.CYAN, CountryImageState.SILVER);

        return true;
      }
      case Input.Keys.EQUALS:
      {
        playMapActor.randomizeCountryStatesUsingOnly (CountryImageState.TEAL, CountryImageState.GREEN);

        return true;
      }
      case Input.Keys.Q:
      {
        playMapActor.setCountriesTo (CountryImageState.BLUE);

        return true;
      }
      case Input.Keys.W:
      {
        playMapActor.setCountriesTo (CountryImageState.BROWN);

        return true;
      }
      case Input.Keys.E:
      {
        playMapActor.setCountriesTo (CountryImageState.CYAN);

        return true;
      }
      case Input.Keys.R:
      {
        playMapActor.setCountriesTo (CountryImageState.GOLD);

        return true;
      }
      case Input.Keys.T:
      {
        playMapActor.setCountriesTo (CountryImageState.GREEN);

        return true;
      }
      case Input.Keys.Y:
      {
        playMapActor.setCountriesTo (CountryImageState.PINK);

        return true;
      }
      case Input.Keys.U:
      {
        playMapActor.setCountriesTo (CountryImageState.PURPLE);

        return true;
      }
      case Input.Keys.I:
      {
        playMapActor.setCountriesTo (CountryImageState.RED);

        return true;
      }
      case Input.Keys.O:
      {
        playMapActor.setCountriesTo (CountryImageState.SILVER);

        return true;
      }
      case Input.Keys.LEFT_BRACKET:
      {
        playMapActor.setCountriesTo (CountryImageState.TEAL);

        return true;
      }
      case Input.Keys.RIGHT_BRACKET:
      {
        playMapActor.setCountriesTo (CountryImageState.DISABLED);

        return true;
      }
      case Input.Keys.F:
      {
        Assets.playScreenMapBackground.setFilter (Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        return true;
      }
      case Input.Keys.G:
      {
        Assets.playScreenMapBackground.setFilter (Texture.TextureFilter.MipMapLinearNearest,
                                                  Texture.TextureFilter.Nearest);

        return true;
      }
      case Input.Keys.H:
      {
        Assets.playScreenMapBackground.setFilter (Texture.TextureFilter.MipMapLinearLinear,
                                                  Texture.TextureFilter.Linear);

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
      case 'm':
      {
//         final CountryName sourceCountryName = new CountryName ("Antarctica");
//         final CountryName destinationCountryName = new CountryName ("Kamchatka");

        final CountryName sourceCountryName = eventGenerator.getRandomCountryName ();

        CountryName destinationCountryName;

        do
        {
          destinationCountryName = eventGenerator.getRandomCountryName ();
        }
        while (destinationCountryName.equals (sourceCountryName));

        final CountryActor sourceCountryActor = playMapActor.getCountryActorWithName (sourceCountryName);
        final CountryActor destinationCountryActor = playMapActor.getCountryActorWithName (destinationCountryName);
        final int totalArmies = Randomness.getRandomIntegerFrom (4, 99);
        final int minArmies = Randomness.getRandomIntegerFrom (1, 3);
        final int maxArmies = totalArmies - 1;

        mandatoryOccupationPopup.show (minArmies, maxArmies, sourceCountryActor, destinationCountryActor, totalArmies);

        return true;
      }
      default:
      {
        return false;
      }
    }
  }

  @Override
  public boolean touchDown (final int screenX, final int screenY, final int pointer, final int button)
  {
    territoryTextActor.setHoveredCountryState (playMapActor.getHoveredCountryState ());

    return true;
  }

  @Override
  public boolean mouseMoved (final int screenX, final int screenY)
  {
    territoryTextActor.setHoveredCountryState (playMapActor.getHoveredCountryState ());

    return true;
  }
}
