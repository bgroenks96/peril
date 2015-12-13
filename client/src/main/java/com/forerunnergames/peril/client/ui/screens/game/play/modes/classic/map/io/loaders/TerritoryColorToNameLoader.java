package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.loaders;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors.TerritoryColor;
import com.forerunnergames.peril.common.io.BiMapDataLoader;

public interface TerritoryColorToNameLoader <T extends TerritoryColor <?>> extends BiMapDataLoader <T, String>
{
}
