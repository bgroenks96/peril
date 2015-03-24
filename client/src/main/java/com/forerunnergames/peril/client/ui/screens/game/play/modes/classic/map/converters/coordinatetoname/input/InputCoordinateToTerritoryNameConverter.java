package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.input;

import com.forerunnergames.peril.core.model.map.territory.TerritoryName;
import com.forerunnergames.tools.common.geometry.Point2D;
import com.forerunnergames.tools.common.geometry.Size2D;

public interface InputCoordinateToTerritoryNameConverter <T extends TerritoryName>
{
  T convert (final Point2D inputCoordinate, final Size2D screenSize);
}
