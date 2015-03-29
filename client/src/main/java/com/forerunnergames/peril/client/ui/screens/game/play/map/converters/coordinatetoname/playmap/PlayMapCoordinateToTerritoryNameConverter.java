package com.forerunnergames.peril.client.ui.screens.game.play.map.converters.coordinatetoname.playmap;

import com.forerunnergames.peril.core.model.map.territory.TerritoryName;
import com.forerunnergames.tools.common.geometry.Point2D;

public interface PlayMapCoordinateToTerritoryNameConverter <T extends TerritoryName>
{
  T convert (final Point2D playMapCoordinate);
}
