package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.input;

import com.badlogic.gdx.InputAdapter;

import com.forerunnergames.peril.core.shared.net.events.server.success.AttackCountrySuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.DefendCountrySuccessEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PlayMapKeyboardInput extends InputAdapter
{
  private static final Logger log = LoggerFactory.getLogger (PlayMapKeyboardInput.class);
  private final MBassador <Event> eventBus;

  public PlayMapKeyboardInput (final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.eventBus = eventBus;
  }

  @Override
  public boolean keyTyped (final char character)
  {
    switch (character)
    {
      case 'a':
      case 'A':
      {
        log.info ("Successfully attacked a country by pressing A in LibGDX.");

        eventBus.publish (new AttackCountrySuccessEvent ());

        return true;
      }
      case 'd':
      case 'D':
      {
        log.info ("Successfully defended a country by pressing D in LibGDX.");

        eventBus.publish (new DefendCountrySuccessEvent ());

        return true;
      }
      default:
      {
        return false;
      }
    }
  }
}
