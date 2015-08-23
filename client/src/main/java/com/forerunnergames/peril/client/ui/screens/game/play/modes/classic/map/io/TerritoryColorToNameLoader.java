package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors.TerritoryColor;
import com.forerunnergames.peril.common.io.DataLoader;

public interface TerritoryColorToNameLoader <T extends TerritoryColor <?>> extends DataLoader <T, String>
{
}
