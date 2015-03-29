package com.forerunnergames.peril.client.ui.screens.game.play.map.loaders;

import com.forerunnergames.peril.client.io.DataLoader;
import com.forerunnergames.peril.client.ui.screens.game.play.map.colors.TerritoryColor;
import com.forerunnergames.peril.core.model.map.territory.TerritoryName;

public interface TerritoryColorToNameLoader <T extends TerritoryColor <?>, U extends TerritoryName> extends
        DataLoader <T, U>
{
}
