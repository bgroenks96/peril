package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic;

import com.forerunnergames.peril.client.events.SelectCountryEvent;
import com.forerunnergames.peril.client.events.StatusMessageEventFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountryPrimaryImageState;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerClaimCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerClaimCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerClaimCountryResponseSuccessEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ManualCountryAssignmentPhaseHandler
{
  private static final Logger log = LoggerFactory.getLogger (ManualCountryAssignmentPhaseHandler.class);
  private final MBassador <Event> eventBus;
  private PlayMap playMap;
  @Nullable
  private PlayerClaimCountryRequestEvent request = null;

  public ManualCountryAssignmentPhaseHandler (final PlayMap playMap, final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");
    Arguments.checkIsNotNull (playMap, "playMap");

    this.playMap = playMap;
    this.eventBus = eventBus;
  }

  public void setPlayMap (final PlayMap playMap)
  {
    Arguments.checkIsNotNull (playMap, "playMap");

    this.playMap = playMap;
  }

  @Handler
  void onEvent (final PlayerClaimCountryRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    request = event;

    eventBus.publish (StatusMessageEventFactory
            .create (Strings.format ("{}, claim a country.", event.getPlayerName ())));
  }

  @Handler
  void onEvent (final SelectCountryEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    final String countryName = event.getCountryName ();

    if (checkRequestExistsFor (event).failed ()) return;
    if (checkContainsUnclaimedCountry (event).failed ()) return;

    claimCountry (countryName);
    sendResponse (countryName);
    reset ();
  }

  @Handler
  void onEvent (final PlayerClaimCountryResponseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    playMap.setCountryState (event.getCountryName (),
                             CountryPrimaryImageState.fromPlayerColor (event.getPlayerColor ()));

    eventBus.publish (StatusMessageEventFactory
            .create (Strings.format ("{} claimed {} .", event.getPlayerName (), event.getCountryName ())));
  }

  private Result <String> checkRequestExistsFor (final SelectCountryEvent event)
  {
    if (request == null)
    {
      final String failureMessage = Strings.format ("Ignoring [{}] because no prior corresponding [{}] was received.",
                                                    event, PlayerClaimCountryRequestEvent.class.getSimpleName ());
      log.warn (failureMessage);
      return Result.failure (failureMessage);
    }

    return Result.success ();
  }

  private Result <String> checkContainsUnclaimedCountry (final SelectCountryEvent event)
  {
    if (isClaimedCountry (event.getCountryName ()))
    {
      final String failureMessage = Strings.format ("Ignoring local event [{}] because not a valid response to [{}].",
                                                    event, PlayerClaimCountryRequestEvent.class.getSimpleName ());
      log.warn (failureMessage);
      return Result.failure (failureMessage);
    }

    return Result.success ();
  }

  private boolean isClaimedCountry (final String countryName)
  {
    assert request != null;
    return request.isClaimedCountry (countryName);
  }

  private void claimCountry (final String countryName)
  {
    assert request != null;
    playMap.setCountryState (countryName, CountryPrimaryImageState.fromPlayerColor (request.getPlayerColor ()));
  }

  private void sendResponse (final String countryName)
  {
    eventBus.publish (new PlayerClaimCountryResponseRequestEvent (countryName));
  }

  private void reset ()
  {
    request = null;
  }
}
