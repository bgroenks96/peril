package com.forerunnergames.peril.client.ui.screens.game.play.map.data;

import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.geometry.Point2D;
import com.forerunnergames.tools.common.geometry.Size2D;

public final class CountrySpriteData
{
  private final CountryName name;
  private final Point2D destPlayMap;
  private final Point2D center;
  private final Size2D size;
  private final int spritesPerRow;

  public CountrySpriteData (final CountryName name,
                            final Point2D destPlayMap,
                            final Point2D center,
                            final Size2D size,
                            final int spritesPerRow)
  {
    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNull (destPlayMap, "destPlayMap");
    Arguments.checkIsNotNull (center, "center");
    Arguments.checkIsNotNull (size, "size");
    Arguments.checkLowerExclusiveBound (spritesPerRow, 0, "spritesPerRow");

    this.name = name;
    this.destPlayMap = destPlayMap;
    this.center = center;
    this.size = size;
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

  public Point2D getDestPlayMap()
  {
    return destPlayMap;
  }

  public float getDestPlayMapX ()
  {
    return destPlayMap.getX ();
  }

  public float getDestPlayMapY ()
  {
    return destPlayMap.getY ();
  }

  public Size2D getSize()
  {
    return size;
  }

  public float getWidth ()
  {
    return size.getWidth ();
  }

  public float getHeight ()
  {
    return size.getHeight ();
  }

  public float getSrcX (final int spriteIndex)
  {
    Arguments.checkIsNotNegative (spriteIndex, "spriteIndex");
    Arguments.checkUpperExclusiveBound (spriteIndex, spritesPerRow, "spriteIndex", "spritesPerRow");

    return size.getWidth () * (spriteIndex % spritesPerRow);
  }

  public float getSrcY (final int spriteIndex)
  {
    Arguments.checkIsNotNegative (spriteIndex, "spriteIndex");
    Arguments.checkUpperExclusiveBound (spriteIndex, spritesPerRow, "spriteIndex", "spritesPerRow");

    return size.getHeight () * (spriteIndex / spritesPerRow);
  }

  @Override
  public String toString ()
  {
    return String.format (
                    "%1$s: Name: %2$s | Destination (Play Map Space): %3$s | Center (Play Map Space): %4$s | Size: %5$s | spritesPerRow: %6$s",
                    getClass ().getSimpleName (), name, destPlayMap, center, size, spritesPerRow);
  }
}
