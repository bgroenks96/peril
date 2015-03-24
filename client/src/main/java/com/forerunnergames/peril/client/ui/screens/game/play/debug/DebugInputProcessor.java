package com.forerunnergames.peril.client.ui.screens.game.play.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;

import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.ScreenController;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.game.play.map.actors.ArmyTextActor;
import com.forerunnergames.peril.client.ui.screens.game.play.map.actors.CountryActor;
import com.forerunnergames.peril.client.ui.screens.game.play.map.actors.PlayMapActor;
import com.forerunnergames.peril.client.ui.screens.game.play.map.actors.TerritoryTextActor;
import com.forerunnergames.peril.client.ui.screens.game.play.widgets.MandatoryOccupationPopup;
import com.forerunnergames.peril.client.ui.widgets.MessageBox;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.peril.core.model.people.player.PlayerColor;
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
  private final ArmyTextActor armyTextActor;
  private final TerritoryTextActor territoryTextActor;
  private final MessageBox <StatusMessage> statusBox;
  private final MessageBox <ChatMessage> chatBox;
  private final MessageBox <Message> playerBox;
  private final MandatoryOccupationPopup mandatoryOccupationPopup;
  private final DebugEventGenerator eventGenerator;

  public DebugInputProcessor (final ScreenController screenController,
                              final PlayMapActor playMapActor,
                              final ArmyTextActor armyTextActor,
                              final TerritoryTextActor territoryTextActor,
                              final MessageBox <StatusMessage> statusBox,
                              final MessageBox <ChatMessage> chatBox,
                              final MessageBox <Message> playerBox,
                              final MandatoryOccupationPopup mandatoryOccupationPopup,
                              final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (screenController, "screenController");
    Arguments.checkIsNotNull (playMapActor, "playMapActor");
    Arguments.checkIsNotNull (armyTextActor, "armyTextActor");
    Arguments.checkIsNotNull (territoryTextActor, "territoryTextActor");
    Arguments.checkIsNotNull (statusBox, "statusBox");
    Arguments.checkIsNotNull (chatBox, "chatBox");
    Arguments.checkIsNotNull (playerBox, "playerBox");
    Arguments.checkIsNotNull (mandatoryOccupationPopup, "mandatoryOccupationPopup");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.screenController = screenController;
    this.playMapActor = playMapActor;
    this.armyTextActor = armyTextActor;
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

      return false;
    }
    case Input.Keys.ESCAPE:
    {
      Gdx.app.exit ();

      return false;
    }
    case Input.Keys.NUM_1:
    {
      playMapActor.clearCountryColors ();

      return false;
    }
    case Input.Keys.NUM_2:
    {
      playMapActor.randomizeCountryColors ();

      return false;
    }
    case Input.Keys.NUM_3:
    {
      playMapActor.randomizeCountryColorsUsingNRandomColors (Randomness.getRandomIntegerFrom (1, 10));

      return false;
    }
    case Input.Keys.NUM_4:
    {
      playMapActor.randomizeCountryColorsUsingNRandomColors (2);

      return false;
    }
    case Input.Keys.NUM_5:
    {
      playMapActor.randomizeCountryColorsUsingNRandomColors (3);

      return false;
    }
    case Input.Keys.NUM_6:
    {
      playMapActor.setClassicCountryColors ();

      return false;
    }
    case Input.Keys.NUM_7:
    {
      playMapActor.randomizeCountryColorsUsingOnly (PlayerColor.TEAL, PlayerColor.CYAN);

      return false;
    }
    case Input.Keys.NUM_8:
    {
      playMapActor.randomizeCountryColorsUsingOnly (PlayerColor.BLUE, PlayerColor.CYAN);

      return false;
    }
    case Input.Keys.NUM_9:
    {
      playMapActor.randomizeCountryColorsUsingOnly (PlayerColor.PINK, PlayerColor.PURPLE);

      return false;
    }
    case Input.Keys.NUM_0:
    {
      playMapActor.randomizeCountryColorsUsingOnly (PlayerColor.RED, PlayerColor.BROWN);

      return false;
    }
    case Input.Keys.MINUS:
    {
      playMapActor.randomizeCountryColorsUsingOnly (PlayerColor.CYAN, PlayerColor.SILVER);

      return false;
    }
    case Input.Keys.EQUALS:
    {
      playMapActor.randomizeCountryColorsUsingOnly (PlayerColor.TEAL, PlayerColor.GREEN);

      return false;
    }
    case Input.Keys.Q:
    {
      playMapActor.setCountriesTo (PlayerColor.BLUE);

      return false;
    }
    case Input.Keys.W:
    {
      playMapActor.setCountriesTo (PlayerColor.BROWN);

      return false;
    }
    case Input.Keys.E:
    {
      playMapActor.setCountriesTo (PlayerColor.CYAN);

      return false;
    }
    case Input.Keys.R:
    {
      playMapActor.setCountriesTo (PlayerColor.GOLD);

      return false;
    }
    case Input.Keys.T:
    {
      playMapActor.setCountriesTo (PlayerColor.GREEN);

      return false;
    }
    case Input.Keys.Y:
    {
      playMapActor.setCountriesTo (PlayerColor.PINK);

      return false;
    }
    case Input.Keys.U:
    {
      playMapActor.setCountriesTo (PlayerColor.PURPLE);

      return false;
    }
    case Input.Keys.I:
    {
      playMapActor.setCountriesTo (PlayerColor.RED);

      return false;
    }
    case Input.Keys.O:
    {
      playMapActor.setCountriesTo (PlayerColor.SILVER);

      return false;
    }
    case Input.Keys.LEFT_BRACKET:
    {
      playMapActor.setCountriesTo (PlayerColor.TEAL);

      return false;
    }
    case Input.Keys.F:
    {
      Assets.playScreenMapBackground.setFilter (Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
      playMapActor.setCountryTextureFiltering (Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

      return false;
    }
    case Input.Keys.G:
    {
      Assets.playScreenMapBackground.setFilter (Texture.TextureFilter.MipMapLinearNearest,
                                                Texture.TextureFilter.Nearest);
      playMapActor
          .setCountryTextureFiltering (Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Nearest);

      return false;
    }
    case Input.Keys.H:
    {
      Assets.playScreenMapBackground.setFilter (Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
      playMapActor.setCountryTextureFiltering (Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);

      return false;
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

      return false;
    }
    case 'S':
    {
      statusBox.clear ();

      return false;
    }
    case 'c':
    {
      eventGenerator.generateChatMessageSuccessEvent ();

      return false;
    }
    case 'C':
    {
      chatBox.clear ();

      return false;
    }
    case 'p':
    {
      eventGenerator.generatePlayerJoinGameSuccessEvent ();

      return false;
    }
    case 'P':
    {
      playerBox.clear ();
      eventGenerator.resetPlayers ();

      return false;
    }
    case 'a':
    {
      eventGenerator.generateCountryArmiesChangedEvent ();

      return false;
    }
    case 'A':
    {
      armyTextActor.reset ();

      return false;
    }
    case 'm':
    {
      /*
      final CountryName sourceCountryName = new CountryName ("Kamchatka");
      final CountryName destinationCountryName = new CountryName ("Northwest Territory");
      */

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

      return false;
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
    territoryTextActor.setHoveredCountryColor (playMapActor.getHoveredCountryColor ());

    return false;
  }

  @Override
  public boolean mouseMoved (final int screenX, final int screenY)
  {
    territoryTextActor.setHoveredCountryColor (playMapActor.getHoveredCountryColor ());

    return false;
  }
}
