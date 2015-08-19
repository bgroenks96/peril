package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io;

import com.forerunnergames.peril.core.shared.io.DataLoader;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors.TerritoryColor;
import com.forerunnergames.peril.core.model.map.territory.TerritoryName;

public interface TerritoryColorToNameLoader <T extends TerritoryColor <?>, U extends TerritoryName> extends
        DataLoader <T, U>
{
}
