package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data;

import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.geometry.Point2D;
import com.forerunnergames.tools.common.geometry.Size2D;

public final class CountrySpriteData
{
  private final CountryName name;
  private final Point2D destPlayMapReferenceSpace;
  private final Point2D centerPlayMapReferenceSpace;
  private final Size2D sizePlayMapReferenceSpace;

  public CountrySpriteData (final CountryName name,
                            final Point2D destPlayMapReferenceSpace,
                            final Point2D centerPlayMapReferenceSpace,
                            final Size2D sizePlayMapReferenceSpace)
  {
    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNull (destPlayMapReferenceSpace, "destPlayMapReferenceSpace");
    Arguments.checkIsNotNull (centerPlayMapReferenceSpace, "centerPlayMapReferenceSpace");
    Arguments.checkIsNotNull (sizePlayMapReferenceSpace, "sizePlayMapReferenceSpace");

    this.name = name;
    this.destPlayMapReferenceSpace = destPlayMapReferenceSpace;
    this.centerPlayMapReferenceSpace = centerPlayMapReferenceSpace;
    this.sizePlayMapReferenceSpace = sizePlayMapReferenceSpace;
  }

  public String getName ()
  {
    return name.asString ();
  }

  public Point2D getDestPlayMapReferenceSpace ()
  {
    return destPlayMapReferenceSpace;
  }

  public Point2D getCenterPlayMapReferenceSpace ()
  {
    return centerPlayMapReferenceSpace;
  }

  public Size2D getSizePlayMapReferenceSpace ()
  {
    return sizePlayMapReferenceSpace;
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: Name: %2$s | Destination (Play Map Reference Space): %3$s"
                    + " | Center (Play Map Reference Space): %4$s | Size: %5$s", getClass ()
                    .getSimpleName (), name,
            destPlayMapReferenceSpace, centerPlayMapReferenceSpace, sizePlayMapReferenceSpace);
  }
}
