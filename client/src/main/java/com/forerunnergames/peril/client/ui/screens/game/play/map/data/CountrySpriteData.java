package com.forerunnergames.peril.client.ui.screens.game.play.map.data;

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
  private final int spritesPerRow;

  public CountrySpriteData (final CountryName name,
                            final Point2D destPlayMapReferenceSpace,
                            final Point2D centerPlayMapReferenceSpace,
                            final Size2D sizePlayMapReferenceSpace,
                            final int spritesPerRow)
  {
    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNull (destPlayMapReferenceSpace, "destPlayMapReferenceSpace");
    Arguments.checkIsNotNull (centerPlayMapReferenceSpace, "centerPlayMapReferenceSpace");
    Arguments.checkIsNotNull (sizePlayMapReferenceSpace, "sizePlayMapReferenceSpace");
    Arguments.checkLowerExclusiveBound (spritesPerRow, 0, "spritesPerRow");

    this.name = name;
    this.destPlayMapReferenceSpace = destPlayMapReferenceSpace;
    this.centerPlayMapReferenceSpace = centerPlayMapReferenceSpace;
    this.sizePlayMapReferenceSpace = sizePlayMapReferenceSpace;
    this.spritesPerRow = spritesPerRow;
  }

  public CountryName getName ()
  {
    return name;
  }

  public String getNameAsFileName (final String fileNameExtension)
  {
    Arguments.checkIsNotNull (fileNameExtension, "fileNameExtension");

    return name.asFileName (fileNameExtension);
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

  public float getWidth ()
  {
    return sizePlayMapReferenceSpace.getWidth ();
  }

  public float getHeight ()
  {
    return sizePlayMapReferenceSpace.getHeight ();
  }

  public float getSrcX (final int spriteIndex)
  {
    Arguments.checkIsNotNegative (spriteIndex, "spriteIndex");
    Arguments.checkUpperExclusiveBound (spriteIndex, spritesPerRow, "spriteIndex", "spritesPerRow");

    return sizePlayMapReferenceSpace.getWidth () * (spriteIndex % spritesPerRow);
  }

  public float getSrcY (final int spriteIndex)
  {
    Arguments.checkIsNotNegative (spriteIndex, "spriteIndex");
    Arguments.checkUpperExclusiveBound (spriteIndex, spritesPerRow, "spriteIndex", "spritesPerRow");

    return sizePlayMapReferenceSpace.getHeight () * (spriteIndex / spritesPerRow);
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: Name: %2$s | Destination (Play Map Reference Space): %3$s"
                                    + " | Center (Play Map Reference Space): %4$s | Size: %5$s | spritesPerRow: %6$s",
                    getClass ().getSimpleName (), name, destPlayMapReferenceSpace, centerPlayMapReferenceSpace,
                    sizePlayMapReferenceSpace, spritesPerRow);
  }
}
