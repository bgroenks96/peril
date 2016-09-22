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

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.armymovement.fortification.FortificationDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.armymovement.occupation.OccupationDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.attack.AttackDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.defend.DefendDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.Country;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountryPrimaryImageState;
import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBox;
import com.forerunnergames.peril.client.ui.widgets.messagebox.chatbox.ChatBoxRow;
import com.forerunnergames.peril.client.ui.widgets.messagebox.playerbox.PlayerBox;
import com.forerunnergames.peril.client.ui.widgets.messagebox.statusbox.StatusBoxRow;
import com.forerunnergames.peril.common.net.packets.battle.PendingBattleActorPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Randomness;

import net.engio.mbassy.bus.MBassador;

public final class DebugInputProcessor extends InputAdapter
{
  private final DebugEventGenerator eventGenerator;
  private final WidgetFactory widgetFactory;
  private final MouseInput mouseInput;
  private final MessageBox <StatusBoxRow> statusBox;
  private final MessageBox <ChatBoxRow> chatBox;
  private final PlayerBox playerBox;
  private final OccupationDialog occupationDialog;
  private final FortificationDialog fortificationDialog;
  private final AttackDialog attackDialog;
  private final DefendDialog defendDialog;
  private PlayMap playMap;

  public DebugInputProcessor (final DebugEventGenerator eventGenerator,
                              final WidgetFactory widgetFactory,
                              final MouseInput mouseInput,
                              final PlayMap playMap,
                              final MessageBox <StatusBoxRow> statusBox,
                              final MessageBox <ChatBoxRow> chatBox,
                              final PlayerBox playerBox,
                              final OccupationDialog occupationDialog,
                              final FortificationDialog fortificationDialog,
                              final AttackDialog attackDialog,
                              final DefendDialog defendDialog,
                              final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (eventGenerator, "eventGenerator");
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (playMap, "playMap");
    Arguments.checkIsNotNull (statusBox, "statusBox");
    Arguments.checkIsNotNull (chatBox, "chatBox");
    Arguments.checkIsNotNull (playerBox, "playerBox");
    Arguments.checkIsNotNull (occupationDialog, "occupationDialog");
    Arguments.checkIsNotNull (fortificationDialog, "fortificationDialog");
    Arguments.checkIsNotNull (attackDialog, "attackDialog");
    Arguments.checkIsNotNull (defendDialog, "defendDialog");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.eventGenerator = eventGenerator;
    this.widgetFactory = widgetFactory;
    this.mouseInput = mouseInput;
    this.playMap = playMap;
    this.statusBox = statusBox;
    this.chatBox = chatBox;
    this.playerBox = playerBox;
    this.occupationDialog = occupationDialog;
    this.fortificationDialog = fortificationDialog;
    this.attackDialog = attackDialog;
    this.defendDialog = defendDialog;
  }

  @Override
  public boolean keyDown (final int keycode)
  {
    switch (keycode)
    {
      case Input.Keys.Z:
      {
        playMap.disable ();

        return true;
      }
      case Input.Keys.X:
      {
        playMap.enable (mouseInput.position ());

        return true;
      }
      case Input.Keys.NUM_1:
      {
        playMap.resetCountryStates ();

        return true;
      }
      case Input.Keys.NUM_2:
      {
        playMap.randomizeCountryStates ();

        return true;
      }
      case Input.Keys.NUM_3:
      {
        playMap.randomizeCountryStatesUsingNRandomStates (Randomness.getRandomIntegerFrom (1, 10));

        return true;
      }
      case Input.Keys.NUM_4:
      {
        playMap.randomizeCountryStatesUsingNRandomStates (2);

        return true;
      }
      case Input.Keys.NUM_5:
      {
        playMap.randomizeCountryStatesUsingNRandomStates (3);

        return true;
      }
      case Input.Keys.NUM_6:
      {
        // North America
        playMap.setCountryState ("Alaska", CountryPrimaryImageState.GOLD);
        playMap.setCountryState ("Northwest Territory", CountryPrimaryImageState.GOLD);
        playMap.setCountryState ("Greenland", CountryPrimaryImageState.GOLD);
        playMap.setCountryState ("Alberta", CountryPrimaryImageState.GOLD);
        playMap.setCountryState ("Ontario", CountryPrimaryImageState.GOLD);
        playMap.setCountryState ("Quebec", CountryPrimaryImageState.GOLD);
        playMap.setCountryState ("Western United States", CountryPrimaryImageState.GOLD);
        playMap.setCountryState ("Eastern United States", CountryPrimaryImageState.GOLD);
        playMap.setCountryState ("Central America", CountryPrimaryImageState.GOLD);

        // South America
        playMap.setCountryState ("Venezuela", CountryPrimaryImageState.RED);
        playMap.setCountryState ("Peru", CountryPrimaryImageState.RED);
        playMap.setCountryState ("Brazil", CountryPrimaryImageState.RED);
        playMap.setCountryState ("Argentina", CountryPrimaryImageState.RED);

        // Europe
        playMap.setCountryState ("Iceland", CountryPrimaryImageState.BLUE);
        playMap.setCountryState ("Scandinavia", CountryPrimaryImageState.BLUE);
        playMap.setCountryState ("Great Britain", CountryPrimaryImageState.BLUE);
        playMap.setCountryState ("Northern Europe", CountryPrimaryImageState.BLUE);
        playMap.setCountryState ("Ukraine", CountryPrimaryImageState.BLUE);
        playMap.setCountryState ("Western Europe", CountryPrimaryImageState.BLUE);
        playMap.setCountryState ("Southern Europe", CountryPrimaryImageState.BLUE);

        // Asia
        playMap.setCountryState ("Ural", CountryPrimaryImageState.GREEN);
        playMap.setCountryState ("Siberia", CountryPrimaryImageState.GREEN);
        playMap.setCountryState ("Yakutsk", CountryPrimaryImageState.GREEN);
        playMap.setCountryState ("Kamchatka", CountryPrimaryImageState.GREEN);
        playMap.setCountryState ("Afghanistan", CountryPrimaryImageState.GREEN);
        playMap.setCountryState ("Irkutsk", CountryPrimaryImageState.GREEN);
        playMap.setCountryState ("Mongolia", CountryPrimaryImageState.GREEN);
        playMap.setCountryState ("Japan", CountryPrimaryImageState.GREEN);
        playMap.setCountryState ("Middle East", CountryPrimaryImageState.GREEN);
        playMap.setCountryState ("India", CountryPrimaryImageState.GREEN);
        playMap.setCountryState ("China", CountryPrimaryImageState.GREEN);
        playMap.setCountryState ("Siam", CountryPrimaryImageState.GREEN);

        // Africa
        playMap.setCountryState ("North Africa", CountryPrimaryImageState.BROWN);
        playMap.setCountryState ("Egypt", CountryPrimaryImageState.BROWN);
        playMap.setCountryState ("Congo", CountryPrimaryImageState.BROWN);
        playMap.setCountryState ("East Africa", CountryPrimaryImageState.BROWN);
        playMap.setCountryState ("South Africa", CountryPrimaryImageState.BROWN);
        playMap.setCountryState ("Madagascar", CountryPrimaryImageState.BROWN);

        // Australia
        playMap.setCountryState ("Indonesia", CountryPrimaryImageState.PINK);
        playMap.setCountryState ("New Guinea", CountryPrimaryImageState.PINK);
        playMap.setCountryState ("Western Australia", CountryPrimaryImageState.PINK);
        playMap.setCountryState ("Eastern Australia", CountryPrimaryImageState.PINK);

        // Not used in classic mode
        playMap.setCountryState ("Hawaii", CountryPrimaryImageState.DISABLED);
        playMap.setCountryState ("Caribbean Islands", CountryPrimaryImageState.DISABLED);
        playMap.setCountryState ("Falkland Islands", CountryPrimaryImageState.DISABLED);
        playMap.setCountryState ("Svalbard", CountryPrimaryImageState.DISABLED);
        playMap.setCountryState ("Philippines", CountryPrimaryImageState.DISABLED);
        playMap.setCountryState ("New Zealand", CountryPrimaryImageState.DISABLED);
        playMap.setCountryState ("Antarctica", CountryPrimaryImageState.DISABLED);

        return true;
      }
      case Input.Keys.NUM_7:
      {
        playMap.randomizeCountryStatesUsingOnly (CountryPrimaryImageState.TEAL, CountryPrimaryImageState.CYAN);

        return true;
      }
      case Input.Keys.NUM_8:
      {
        playMap.randomizeCountryStatesUsingOnly (CountryPrimaryImageState.BLUE, CountryPrimaryImageState.CYAN);

        return true;
      }
      case Input.Keys.NUM_9:
      {
        playMap.randomizeCountryStatesUsingOnly (CountryPrimaryImageState.PINK, CountryPrimaryImageState.PURPLE);

        return true;
      }
      case Input.Keys.NUM_0:
      {
        playMap.randomizeCountryStatesUsingOnly (CountryPrimaryImageState.RED, CountryPrimaryImageState.BROWN);

        return true;
      }
      case Input.Keys.MINUS:
      {
        playMap.randomizeCountryStatesUsingOnly (CountryPrimaryImageState.CYAN, CountryPrimaryImageState.SILVER);

        return true;
      }
      case Input.Keys.EQUALS:
      {
        playMap.randomizeCountryStatesUsingOnly (CountryPrimaryImageState.TEAL, CountryPrimaryImageState.GREEN);

        return true;
      }
      case Input.Keys.Q:
      {
        playMap.setCountriesTo (CountryPrimaryImageState.BLUE);

        return true;
      }
      case Input.Keys.W:
      {
        playMap.setCountriesTo (CountryPrimaryImageState.BROWN);

        return true;
      }
      case Input.Keys.E:
      {
        playMap.setCountriesTo (CountryPrimaryImageState.CYAN);

        return true;
      }
      case Input.Keys.R:
      {
        playMap.setCountriesTo (CountryPrimaryImageState.GOLD);

        return true;
      }
      case Input.Keys.T:
      {
        playMap.setCountriesTo (CountryPrimaryImageState.GREEN);

        return true;
      }
      case Input.Keys.Y:
      {
        playMap.setCountriesTo (CountryPrimaryImageState.PINK);

        return true;
      }
      case Input.Keys.U:
      {
        playMap.setCountriesTo (CountryPrimaryImageState.PURPLE);

        return true;
      }
      case Input.Keys.I:
      {
        playMap.setCountriesTo (CountryPrimaryImageState.RED);

        return true;
      }
      case Input.Keys.O:
      {
        playMap.setCountriesTo (CountryPrimaryImageState.SILVER);

        return true;
      }
      case Input.Keys.LEFT_BRACKET:
      {
        playMap.setCountriesTo (CountryPrimaryImageState.TEAL);

        return true;
      }
      case Input.Keys.RIGHT_BRACKET:
      {
        playMap.setCountriesTo (CountryPrimaryImageState.DISABLED);

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
        statusBox.addRow (widgetFactory.createStatusMessageBoxRow (eventGenerator.createRandomStatusMessage ()));

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
        playMap.resetArmies ();

        return true;
      }
      case 'd':
      {
        eventGenerator.generatePlayerClaimCountryResponseSuccessEvent ();

        return true;
      }
      case 'm':
      {
        // final String sourceCountryName = "Brazil";
        // final String targetCountryName = "Brazil";

        String sourceCountryName;

        do
        {
          sourceCountryName = eventGenerator.getRandomCountryName ();
        }
        while (!playMap.existsCountryWithName (sourceCountryName));

        String targetCountryName;

        do
        {
          targetCountryName = eventGenerator.getRandomCountryName ();
        }
        while (targetCountryName.equals (sourceCountryName) || !playMap.existsCountryWithName (targetCountryName));

        final Country sourceCountry = playMap.getCountryWithName (sourceCountryName);
        final Country targetCountry = playMap.getCountryWithName (targetCountryName);
        final int totalArmies = Randomness.getRandomIntegerFrom (4, 99);
        final int minArmies = Randomness.getRandomIntegerFrom (1, 3);
        final int maxArmies = totalArmies - 1;

        occupationDialog.show (minArmies, targetCountry.getArmies (), maxArmies, totalArmies, sourceCountry,
                               targetCountry);
        playMap.disable ();

        return true;
      }
      case 'n':
      {
        // final String sourceCountryName = "Brazil";
        // final String targetCountryName = "Brazil";

        String sourceCountryName;

        do
        {
          sourceCountryName = eventGenerator.getRandomCountryName ();
        }
        while (!playMap.existsCountryWithName (sourceCountryName));

        String targetCountryName;

        do
        {
          targetCountryName = eventGenerator.getRandomCountryName ();
        }
        while (targetCountryName.equals (sourceCountryName) || !playMap.existsCountryWithName (targetCountryName));

        final Country sourceCountry = playMap.getCountryWithName (sourceCountryName);
        final Country targetCountry = playMap.getCountryWithName (targetCountryName);
        final int totalArmies = Randomness.getRandomIntegerFrom (4, 99);
        final int minArmies = Randomness.getRandomIntegerFrom (1, 3);
        final int maxArmies = totalArmies - 1;

        fortificationDialog.show (minArmies, targetCountry.getArmies (), maxArmies, totalArmies, sourceCountry,
                                  targetCountry);
        playMap.disable ();

        return true;
      }
      case 'b':
      {
        // final String attackingCountryName = "Brazil";
        // final String defendingCountryName = "Brazil";

        String attackingCountryName;

        do
        {
          attackingCountryName = eventGenerator.getRandomCountryName ();
        }
        while (!playMap.existsCountryWithName (attackingCountryName));

        String defendingCountryName;

        do
        {
          defendingCountryName = eventGenerator.getRandomCountryName ();
        }
        while (defendingCountryName.equals (attackingCountryName)
                || !playMap.existsCountryWithName (defendingCountryName));

        final String attackingPlayerName = DebugEventGenerator.getRandomPlayerName ();
        String defendingPlayerName;

        do
        {
          defendingPlayerName = DebugEventGenerator.getRandomPlayerName ();
        }
        while (defendingPlayerName.equals (attackingPlayerName));

        final Country attackingCountry = playMap.getCountryWithName (attackingCountryName);
        final Country defendingCountry = playMap.getCountryWithName (defendingCountryName);

        attackingCountry.setArmies (Randomness.getRandomIntegerFrom (10, 10));
        defendingCountry.setArmies (Randomness.getRandomIntegerFrom (10, 10));

        final PendingBattleActorPacket attacker = DebugPackets
                .asAttackerPendingBattleActorPacket (attackingPlayerName, attackingCountryName,
                                                     attackingCountry.getArmies ());

        final PendingBattleActorPacket defender = DebugPackets
                .asDefenderPendingBattleActorPacket (defendingPlayerName, defendingCountryName,
                                                     defendingCountry.getArmies ());

        attackDialog.startBattle (attacker, defender, attackingCountry, defendingCountry);

        playMap.disable ();

        return true;
      }
      case 'v':
      {
        // final String attackingCountryName = "Brazil";
        // final String defendingCountryName = "Brazil";

        String attackingCountryName;

        do
        {
          attackingCountryName = eventGenerator.getRandomCountryName ();
        }
        while (!playMap.existsCountryWithName (attackingCountryName));

        String defendingCountryName;

        do
        {
          defendingCountryName = eventGenerator.getRandomCountryName ();
        }
        while (defendingCountryName.equals (attackingCountryName)
                || !playMap.existsCountryWithName (defendingCountryName));

        final String attackingPlayerName = DebugEventGenerator.getRandomPlayerName ();
        String defendingPlayerName;

        do
        {
          defendingPlayerName = DebugEventGenerator.getRandomPlayerName ();
        }
        while (defendingPlayerName.equals (attackingPlayerName));

        final Country attackingCountry = playMap.getCountryWithName (attackingCountryName);
        final Country defendingCountry = playMap.getCountryWithName (defendingCountryName);

        attackingCountry.setArmies (Randomness.getRandomIntegerFrom (10, 10));
        defendingCountry.setArmies (Randomness.getRandomIntegerFrom (10, 10));

        final PendingBattleActorPacket attacker = DebugPackets
                .asAttackerPendingBattleActorPacket (attackingPlayerName, attackingCountryName,
                                                     attackingCountry.getArmies ());

        final PendingBattleActorPacket defender = DebugPackets
                .asDefenderPendingBattleActorPacket (defendingPlayerName, defendingCountryName,
                                                     defendingCountry.getArmies ());

        defendDialog.startBattle (attacker, defender, attackingCountry, defendingCountry);

        playMap.disable ();

        return true;
      }
      default:
      {
        return false;
      }
    }
  }

  public void setPlayMap (final PlayMap playMap)
  {
    Arguments.checkIsNotNull (playMap, "playMap");

    this.playMap = playMap;

    eventGenerator.setPlayMap (playMap);
  }

  public void reset ()
  {
    playMap.reset ();
    statusBox.clear ();
    chatBox.clear ();
    playerBox.clear ();
    eventGenerator.resetPlayers ();
  }
}
