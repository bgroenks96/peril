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

package com.forerunnergames.peril.client.settings;

import com.forerunnergames.tools.common.Classes;

public final class StyleSettings
{
  // Defaults
  public static final String DEFAULT_DIALOG_WINDOW_STYLE = "dialog-modal";
  public static final String DEFAULT_DIALOG_TEXT_BUTTON_STYLE = "dialog";
  public static final String DEFAULT_DIALOG_MESSAGE_BOX_ROW_LABEL_STYLE = "dialog-message";
  public static final String DEFAULT_MESSAGE_BOX_SCROLLPANE_STYLE = "default";
  public static final String DEFAULT_TEXTFIELD_STYLE = "default";
  public static final String DEFAULT_HORIZONTAL_SLIDER_STYLE = "default-horizontal";
  public static final String DEFAULT_VERTICAL_SLIDER_STYLE = "default-vertical";
  public static final String DEFAULT_TEXT_BUTTON_STYLE = "default";
  public static final String DEFAULT_CHECK_BOX_STYLE = "default";
  public static final String DEFAULT_SELECT_BOX_STYLE = "default";
  public static final String DEFAULT_HORIZONTAL_PROGRESS_BAR_STYLE = "default-horizontal";
  public static final String DEFAULT_VERTICAL_PROGRESS_BAR_STYLE = "default-vertical";

  // Menus
  public static final String MENU_BACK_TEXT_BUTTON_STYLE = DEFAULT_TEXT_BUTTON_STYLE;
  public static final String MENU_FORWARD_TEXT_BUTTON_STYLE = DEFAULT_TEXT_BUTTON_STYLE;
  public static final String MENU_TITLE_LABEL_STYLE = "menu-title";
  public static final String MENU_SUB_TITLE_LABEL_STYLE = "menu-subtitle";
  public static final String MENU_CHOICE_IMAGE_TEXT_BUTTON_STYLE = "menu-choice";
  public static final String MENU_SETTING_SECTION_TITLE_LABEL_STYLE = "menu-settings-section-title";
  public static final String MENU_SETTING_LABEL_STYLE = "menu-settings";
  public static final String MENU_PLAYER_NAME_TEXTFIELD_STYLE = DEFAULT_TEXTFIELD_STYLE;
  public static final String MENU_CLAN_NAME_TEXTFIELD_STYLE = DEFAULT_TEXTFIELD_STYLE;
  public static final String MENU_SERVER_NAME_TEXTFIELD_STYLE = DEFAULT_TEXTFIELD_STYLE;
  public static final String MENU_CLAN_NAME_CHECK_BOX_STYLE = DEFAULT_CHECK_BOX_STYLE;
  public static final String MENU_PLAYER_LIMIT_LABEL_STYLE = "option-box";
  public static final String MENU_MAP_NAME_LABEL_STYLE = "option-box";
  public static final String MENU_CUSTOMIZE_PLAYERS_IMAGE_BUTTON_STYLE = "options";
  public static final String MENU_CUSTOMIZE_MAP_IMAGE_BUTTON_STYLE = "options";
  public static final String MENU_WIN_PERCENT_SELECT_BOX_STYLE = DEFAULT_SELECT_BOX_STYLE;
  public static final String MENU_INITIAL_COUNTRY_ASSIGNMENT_SELECT_BOX_STYLE = DEFAULT_SELECT_BOX_STYLE;
  public static final String MENU_SPECTATOR_LIMIT_SELECT_BOX_STYLE = DEFAULT_SELECT_BOX_STYLE;
  public static final String MENU_SERVER_ADDRESS_TEXTFIELD_STYLE = DEFAULT_TEXTFIELD_STYLE;

  // Loading Screens
  public static final String LOADING_SCREEN_PROGRESS_BAR_STYLE = DEFAULT_HORIZONTAL_PROGRESS_BAR_STYLE;
  public static final String LOADING_SCREEN_LOADING_TITLE_TEXT_LABEL_STYLE = "loading-title-text";
  public static final String LOADING_SCREEN_LOADING_STATUS_TEXT_LABEL_STYLE = "loading-status-text";

  // Status Box
  public static final String STATUS_BOX_SCROLLPANE_STYLE = "status-box";
  public static final String STATUS_BOX_ROW_LABEL_STYLE = "status-box-message";

  // Chat Box
  public static final String CHAT_BOX_TEXTFIELD_STYLE = DEFAULT_TEXTFIELD_STYLE;
  public static final String CHAT_BOX_SCROLLPANE_STYLE = "chat-box";
  public static final String CHAT_BOX_ROW_LABEL_STYLE = "chat-box-message";

  // Player Box
  public static final String PLAYER_BOX_SCROLLPANE_STYLE = "player-box";
  public static final String PLAYER_BOX_ROW_LABEL_STYLE = "player-box-message";

  // Play Screen Intel Box
  public static final String INTEL_BOX_TITLE_LABEL_STYLE = "side-bar-title";
  public static final String INTEL_BOX_SETTING_NAME_LABEL_STYLE = "side-bar-setting-name";
  public static final String INTEL_BOX_SETTING_TEXT_LABEL_STYLE = "side-bar-setting-text";
  public static final String INTEL_BOX_BUTTON_TEXT_LABEL_STYLE = "side-bar-button";
  public static final String INTEL_BOX_DETAILED_REPORT_IMAGE_BUTTON_STYLE = "detailed-report";

  // Play Screen Control Room Box
  public static final String CONTROL_ROOM_BOX_TITLE_LABEL_STYLE = "side-bar-title";
  public static final String CONTROL_ROOM_BOX_BUTTON_TEXT_LABEL_STYLE = "side-bar-button";
  public static final String CONTROL_ROOM_BOX_TRADE_IN_IMAGE_BUTTON_STYLE = "trade-in";
  public static final String CONTROL_ROOM_BOX_FORTIFY_IMAGE_BUTTON_STYLE = "fortify";
  public static final String CONTROL_ROOM_BOX_END_TURN_IMAGE_BUTTON_STYLE = "end-turn";
  public static final String CONTROL_ROOM_BOX_MY_SETTINGS_IMAGE_BUTTON_STYLE = "my-settings";
  public static final String CONTROL_ROOM_BOX_SURRENDER_IMAGE_BUTTON_STYLE = "surrender";

  // Play Screen Army Movement Dialogs (Occupy & Reinforce)
  public static final String ARMY_MOVEMENT_DIALOG_WINDOW_STYLE = "army-movement-dialog";
  public static final String ARMY_MOVEMENT_DIALOG_COUNTRY_NAME_LABEL_STYLE = "army-movement-dialog-country-name";
  public static final String ARMY_MOVEMENT_DIALOG_MAX_IMAGE_BUTTON_STYLE = "max";
  public static final String ARMY_MOVEMENT_DIALOG_MIN_IMAGE_BUTTON_STYLE = "min";
  public static final String ARMY_MOVEMENT_DIALOG_PLUS_IMAGE_BUTTON_STYLE = "plus";
  public static final String ARMY_MOVEMENT_DIALOG_MINUS_IMAGE_BUTTON_STYLE = "minus";
  public static final String ARMY_MOVEMENT_DIALOG_SLIDER_STYLE = DEFAULT_HORIZONTAL_SLIDER_STYLE;

  // Play Screen Battle Dialogs (Attack & Defend)
  public static final String BATTLE_DIALOG_WINDOW_STYLE = "battle-dialog";
  public static final String BATTLE_DIALOG_PLAYER_NAME_LABEL_STYLE = "battle-dialog-player-name";
  public static final String BATTLE_DIALOG_COUNTRY_NAME_LABEL_STYLE = "battle-dialog-country-name";
  public static final String BATTLE_DIALOG_BATTLING_ARROW_LABEL_STYLE = "battle-dialog-arrow";
  public static final String BATTLE_DIALOG_DIE_STYLE_PREFIX = "die-";
  public static final String BATTLE_DIALOG_DIE_ATTACK_STYLE_SEGMENT = "attack-";
  public static final String BATTLE_DIALOG_DIE_DEFEND_STYLE_SEGMENT = "defend-";
  public static final String BATTLE_DIALOG_DIE_OUTCOME_STYLE_SEGMENT = "-outcome-";

  // Play Screen Battle Result Dialogs (Victory & Defeat)
  public static final String BATTLE_RESULT_DIALOG_WINDOW_STYLE = "dialog-non-modal";

  // Player Color Icons
  public static final String HUMAN_PLAYER_COLOR_ICON_STYLE_PREFIX = "color-icon-human-player-";
  public static final String AI_PLAYER_COLOR_ICON_STYLE_PREFIX = "color-icon-ai-player-";

  private StyleSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
