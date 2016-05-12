/*
 * Copyright © 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic;

import com.forerunnergames.peril.client.events.StatusMessageEventFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.Country;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountryPrimaryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.dialogs.battle.defend.DefendDialog;
import com.forerunnergames.peril.client.ui.widgets.dialogs.Dialog;
import com.forerunnergames.peril.client.ui.widgets.messagebox.playerbox.PlayerBox;
import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerDefendCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerDefendCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerDefendCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerDefendCountryResponseSuccessEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;

import com.google.common.base.Optional;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefendPhaseHandler
{
  private static final Logger log = LoggerFactory.getLogger (DefendPhaseHandler.class);
  private final PlayerBox playerBox;
  private final DefendDialog defendDialog;
  private final Dialog battleResultDialog;
  private final MBassador <Event> eventBus;
  private PlayMap playMap;
  @Nullable
  private PlayerDefendCountryRequestEvent request = null;
  @Nullable
  private PlayerDefendCountryResponseRequestEvent response = null;

  public DefendPhaseHandler (final PlayMap playMap,
                             final PlayerBox playerBox,
                             final DefendDialog defendDialog,
                             final Dialog battleResultDialog,
                             final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (playMap, "playMap");
    Arguments.checkIsNotNull (playerBox, "playerBox");
    Arguments.checkIsNotNull (defendDialog, "defendDialog");
    Arguments.checkIsNotNull (battleResultDialog, "battleResultDialog");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.playMap = playMap;
    this.playerBox = playerBox;
    this.defendDialog = defendDialog;
    this.battleResultDialog = battleResultDialog;
    this.eventBus = eventBus;
  }

  public void setPlayMap (final PlayMap playMap)
  {
    Arguments.checkIsNotNull (playMap, "playMap");

    this.playMap = playMap;
  }

  public void onBattle ()
  {
    if (request == null)
    {
      log.warn ("Not sending response [{}] because no prior corresponding {} was received.",
                PlayerDefendCountryResponseRequestEvent.class.getSimpleName (),
                PlayerDefendCountryRequestEvent.class.getSimpleName ());
      eventBus.publish (StatusMessageEventFactory.create (Strings
              .format ("Whoops, it looks like you aren't authorized to defend.")));
      return;
    }

    final String attackingCountryName = defendDialog.getAttackingCountryName ();

    if (!attackingCountryName.equals (request.getAttackingCountryName ()))
    {
      log.warn ("Not sending response [{}] because specified attacking country name [{}] does not match the "
                        + "attacking country name [{}] of the original request [{}].",
                PlayerDefendCountryResponseRequestEvent.class.getSimpleName (), attackingCountryName,
                request.getAttackingCountryName (), request);
      eventBus.publish (StatusMessageEventFactory.create (Strings
              .format ("Whoops, it looks like you aren't authorized to defend against {}.", attackingCountryName)));
      return;
    }

    final String defendingCountryName = defendDialog.getDefendingCountryName ();

    if (!defendingCountryName.equals (request.getDefendingCountryName ()))
    {
      log.warn ("Not sending response [{}] because specified defending country name [{}] does not match the "
                        + "defending country name [{}] of the original request [{}].",
                PlayerDefendCountryResponseRequestEvent.class.getSimpleName (), defendingCountryName,
                request.getDefendingCountryName (), request);
      eventBus.publish (StatusMessageEventFactory.create (Strings
              .format ("Whoops, it looks like you aren't authorized to defend {}.", defendingCountryName)));
      return;
    }

    response = new PlayerDefendCountryResponseRequestEvent (defendDialog.getActiveDefenderDieCount ());

    eventBus.publish (response);
    eventBus.publish (StatusMessageEventFactory.create ("Waiting for battle result..."));
  }

  public void reset ()
  {
    request = null;
    response = null;
  }

  @Handler
  void onEvent (final PlayerDefendCountryRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    if (request != null)
    {
      log.warn ("Ignoring [{}] because another attack is still in progress [{}]", event, request);
      return;
    }

    request = event;

    if (!defendDialog.isShown ())
    {
      showDefendDialog ();
      return;
    }

    defendDialog.startBattle ();
  }

  @Handler
  void onEvent (final PlayerDefendCountryResponseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    if (response == null)
    {
      log.warn ("Ignoring [{}] because no prior corresponding {} was sent.", event,
                PlayerDefendCountryResponseRequestEvent.class.getSimpleName ());
      return;
    }

    defendDialog.showBattleResult (event.getBattleResult ());

    // @formatter:off
    eventBus.publish (StatusMessageEventFactory
            .create (Strings.format ("You defended {} against {} in {}, destroying {} & losing {}!",
                    event.getDefendingCountryName (), event.getAttackingPlayerName (), event.getAttackingCountryName (),
                    Strings.pluralize (Math.abs (event.getAttackingCountryArmyDelta ()), "army", "armies"),
                    Strings.pluralize (Math.abs (event.getDefendingCountryArmyDelta ()), "army", "armies"))));
    // @formatter:on

    reset ();
  }

  @Handler
  void onEvent (final PlayerDefendCountryResponseDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);
    log.warn ("Could not defend country. Reason: {}", event.getReason ());

    reset ();
  }

  private void showDefendDialog ()
  {
    final String attackingCountryName = request.getAttackingCountryName ();
    final String defendingCountryName = request.getDefendingCountryName ();

    if (!playMap.existsCountryWithName (attackingCountryName))
    {
      log.error ("Not showing {} for request [{}] because attacking country [{}] does not exist.",
                 DefendDialog.class.getSimpleName (), request, attackingCountryName);
      eventBus.publish (StatusMessageEventFactory.create (Strings
              .format ("Whoops, you can't defend {} because it doesn't exist on this map.", attackingCountryName)));
      return;
    }

    if (!playMap.existsCountryWithName (defendingCountryName))
    {
      log.error ("Not showing {} for request [{}] because defending country [{}] does not exist.",
                 DefendDialog.class.getSimpleName (), request, defendingCountryName);
      eventBus.publish (StatusMessageEventFactory.create (Strings
              .format ("Whoops, you can't defend {} because it doesn't exist on this map.", defendingCountryName)));
      return;
    }

    final CountryPrimaryImageState attackingCountryState = playMap.getPrimaryImageStateOf (attackingCountryName);
    final CountryPrimaryImageState defendingCountryState = playMap.getPrimaryImageStateOf (defendingCountryName);

    assert attackingCountryState != null;
    assert defendingCountryState != null;

    final PlayerColor attackerColor = PlayerColor.valueOf (attackingCountryState.name ());
    final PlayerColor defenderColor = PlayerColor.valueOf (defendingCountryState.name ());
    final Optional <String> attackingPlayerName = playerBox.getNameOfPlayerWithColor (attackerColor);
    final Optional <String> defendingPlayerName = playerBox.getNameOfPlayerWithColor (defenderColor);
    final Country attackingCountry = playMap.getCountryWithName (attackingCountryName);
    final Country defendingCountry = playMap.getCountryWithName (defendingCountryName);

    if (!attackingPlayerName.isPresent ())
    {
      log.error ("Not showing {} for request [{}] because attacking player either 1) does not exist or "
                         + "2) does not own attacking country [{}].", DefendDialog.class.getSimpleName (), request,
                 attackingCountry);
      eventBus.publish (StatusMessageEventFactory.create (Strings
              .format ("Whoops, you can't defend {} against {} "
                               + "because it looks like the attacker {} doesn't actually own {}.",
                       defendingCountryName,
                       attackingCountryName, request.getAttackingPlayerName (), attackingCountryName)));
      return;
    }

    if (!attackingPlayerName.get ().equals (request.getAttackingPlayerName ()))
    {
      log.warn ("Not showing {} for request [{}] because attacking player name [{}] as discovered by {} & {} does "
                        + "not match the attacking player name [{}] from the original request.",
                DefendDialog.class.getSimpleName (), request, attackingPlayerName.get (),
                PlayMap.class.getSimpleName (), PlayerBox.class.getSimpleName (), request.getAttackingPlayerName ());
      eventBus.publish (StatusMessageEventFactory.create (Strings
              .format ("Whoops, you can't defend {} against {} "
                               + "because it looks like the attacker {} doesn't own {}.", defendingCountryName,
                       attackingCountryName, attackingPlayerName.get (), attackingCountryName)));
      return;
    }

    if (!defendingPlayerName.isPresent ())
    {
      log.error ("Not showing {} for request [{}] because defending player either 1) does not exist or "
                         + "2) does not own defending country [{}].", DefendDialog.class.getSimpleName (), request,
                 defendingCountry);
      eventBus.publish (StatusMessageEventFactory.create (Strings
              .format ("Whoops, you can't defend {} against {} " + "because it looks like you don't actually own {}.",
                       defendingCountryName, attackingCountryName, defendingCountryName)));
      return;
    }

    if (!defendingPlayerName.get ().equals (request.getDefendingPlayerName ()))
    {
      log.warn ("Not showing {} for request [{}] because defending player name [{}] as discovered by {} & {} does "
                        + "not match the defending player name [{}] from the original request.",
                DefendDialog.class.getSimpleName (), request, defendingPlayerName.get (),
                PlayMap.class.getSimpleName (), PlayerBox.class.getSimpleName (), request.getDefendingPlayerName ());
      eventBus.publish (StatusMessageEventFactory.create (Strings
              .format ("Whoops, you can't defend {} against {} " + "because it looks like you don't actually own {}.",
                       defendingCountryName, attackingCountryName, defendingCountryName)));
      return;
    }

    // @formatter:off
    eventBus.publish (StatusMessageEventFactory.create (Strings.format ("{}, defend yourself in {} against {} in {}!",
            defendingPlayerName.get (), defendingCountryName, attackingPlayerName.get (), attackingCountryName)));
    // @formatter:on

    defendDialog.show (attackingCountry, defendingCountry, attackingPlayerName.get (), defendingPlayerName.get ());
    defendDialog.startBattle ();
  }
}
