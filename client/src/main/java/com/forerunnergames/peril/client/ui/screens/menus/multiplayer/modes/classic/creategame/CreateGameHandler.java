package com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.creategame;

import com.forerunnergames.peril.core.shared.game.GameConfiguration;

public interface CreateGameHandler
{
  void createGame (final String serverName, final GameConfiguration gameConfig, final String playerName);
}
