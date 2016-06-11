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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.attack.AttackDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.client.ui.widgets.messagebox.playerbox.PlayerBox;
import com.forerunnergames.peril.common.net.events.server.request.PlayerBeginAttackRequestEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;

public final class AttackPhaseHandler extends AbstractBattlePhaseHandler
{
  private final CountrySelectionHandler countrySelectionHandler;
  @Nullable
  private PlayerBeginAttackRequestEvent request;
  @Nullable
  private String attackerName;
  @Nullable
  private String defenderName;
  @Nullable
  private String attackingCountryName;
  @Nullable
  private String defendingCountryName;

  public AttackPhaseHandler (final PlayMap playMap,
                             final PlayerBox playerBox,
                             final AttackDialog attackDialog,
                             // final BattleResetState resetState,
                             final MBassador <Event> eventBus)
  {
    super (playMap, playerBox, attackDialog, eventBus);

    // TODO: this is broken
    countrySelectionHandler = new AttackPhaseCountrySelectionHandler (attackerName, eventBus);
  }

  // @Override
  // public void onRetreat ()
  // {
  // status ("You stopped attacking {} in {} from {}.", defenderName, defendingCountryName, attackingCountryName);
  // }

  @Override
  public void reset ()
  {
    super.reset ();

    request = null;
    attackerName = null;
    defenderName = null;
    attackingCountryName = null;
    defendingCountryName = null;

    countrySelectionHandler.reset ();
    unsubscribe (countrySelectionHandler);
  }

  @Override
  public void softReset ()
  {
    super.softReset ();

    defenderName = null;
    attackingCountryName = null;
    defendingCountryName = null;

    countrySelectionHandler.reset ();
    // countrySelectionHandler.start ();
  }

  @Override
  protected synchronized int getBattlingDieCount (final int attackerDieCount, final int defenderDieCount)
  {
    return attackerDieCount;
  }

  @Override
  protected String attackOrDefend ()
  {
    return "attack";
  }

  // @Override
  // protected String getBattleRequestClassName ()
  // {
  // return PlayerAttackCountryRequestEvent.class.getSimpleName ();
  // }
  //
  // @Override
  // protected String getBattleResponseRequestClassName ()
  // {
  // return PlayerAttackCountryResponseRequestEvent.class.getSimpleName ();
  // }
  //
  // @Override
  // protected synchronized ResponseRequestEvent createResponse (final String attackingCountry,
  // final String defendingCountry,
  // final int dieCount)
  // {
  // return new PlayerAttackCountryResponseRequestEvent (attackingCountry, defendingCountry, dieCount);
  // }

  @Override
  protected void onNewBattleRequest ()
  {
    softReset ();
    subscribe (countrySelectionHandler);
  }

  // @Override
  // protected void onBattleStart (final String attackerName,
  // final String defenderName,
  // final String attackingCountryName,
  // final String defendingCountryName)
  // {
  // status ("{}, prepare to attack {} in {} from {}!", attackerName, defenderName, defendingCountryName,
  // attackingCountryName);
  //
  // this.defenderName = defenderName;
  // this.attackingCountryName = attackingCountryName;
  // this.defendingCountryName = defendingCountryName;
  // }

  // @Handler
  // void onEvent (final PlayerAttackCountryRequestEvent event)
  // {
  // Arguments.checkIsNotNull (event, "event");
  //
  // log.debug ("Event received [{}].", event);
  //
  // request = event;
  // attackerName = request.getPlayerName ();
  //
  // onBattleRequestEvent (event);
  // }
  //
  // @Handler
  // void onEvent (final PlayerAttackCountryResponseSuccessEvent event)
  // {
  // Arguments.checkIsNotNull (event, "event");
  //
  // log.debug ("Event received [{}].", event);
  //
  // onBattleResponseSuccessEvent (event);
  // }
  //
  // @Handler
  // void onEvent (final PlayerAttackCountryResponseDeniedEvent event)
  // {
  // status ("Whoops, it looks like you aren't authorized to attack {} from {}. Reason: {}", defendingCountryName,
  // attackingCountryName,
  // Strings.toCase (event.getReason ().toString ().replaceAll ("_", " "), LetterCase.LOWER));
  //
  // onBattleResponseDeniedEvent (event);
  // }

  // @Handler
  // void onEvent (final PlayerOccupyCountryRequestEvent event)
  // {
  // Arguments.checkIsNotNull (event, "event");
  //
  // log.debug ("Event received [{}].", event);
  //
  // hideBattleDialog ();
  // }

  private class AttackPhaseCountrySelectionHandler extends AbstractCountrySelectionHandler
  {
    // @Override
    // public void onStart ()
    // {
    // Preconditions.checkIsTrue (request != null,
    // "Cannot start attack phase country selection because no prior [{}] was received.",
    // getBattleRequestClassName ());
    //
    // status ("{}, choose a country to attack from.", attackerName);
    // }
    //
    // @Override
    // public void onSelectValidSourceCountry (final String countryName)
    // {
    // Arguments.checkIsNotNull (countryName, "countryName");
    // Preconditions.checkIsTrue (request != null,
    // "Cannot select \'attack from\' country because no prior [{}] was received.",
    // getBattleRequestClassName ());
    //
    // status ("{}, choose a country to attack to.", attackerName);
    // }

    AttackPhaseCountrySelectionHandler (final String phaseAsVerb, final MBassador <Event> eventBus)
    {
      super (phaseAsVerb, eventBus);
      // TODO Auto-generated "just to make it compile" DELETE LATER
    }

    @Override
    public void onEnd (final String sourceCountryName, final String destCountryName)
    {
      Arguments.checkIsNotNull (sourceCountryName, "sourceCountryName");
      Arguments.checkIsNotNull (destCountryName, "destCountryName");
      Preconditions.checkIsTrue (request != null,
                                 "Cannot complete attack phase country selection because no prior [{}] was received.",
                                 getBattleRequestClassName ());

      showBattleDialog (sourceCountryName, destCountryName);
    }

    @Override
    public boolean isValidSourceCountry (final String countryName)
    {
      Arguments.checkIsNotNull (countryName, "countryName");
      Preconditions.checkIsTrue (request != null,
                                 "Cannot validate \'attack from\' country because no prior [{}] was received.",
                                 getBattleRequestClassName ());

      return request.isValidAttackFromCountry (countryName);
    }

    @Override
    public boolean isValidDestCountry (final String sourceCountryName, final String destCountryName)
    {
      Arguments.checkIsNotNull (sourceCountryName, "sourceCountryName");
      Arguments.checkIsNotNull (destCountryName, "destCountryName");
      Preconditions.checkIsTrue (request != null,
                                 "Cannot validate \'attack to\' country because no prior [{}] was received.",
                                 getBattleRequestClassName ());

      return request.isValidAttackVector (sourceCountryName, destCountryName);
    }
  }

  // --------------------- TEMPORARY ----------------------- //
  // TODO Auto-generated "just to make it compile" DELETE LATER

  @Override
  protected String getBattleRequestClassName ()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected String getBattleResponseRequestClassName ()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected ResponseRequestEvent createResponse (final String attackingCountry,
                                                 final String defendingCountry,
                                                 final int dieCount)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected void onBattleStart ()
  {
    // TODO Auto-generated method stub

  }
}
