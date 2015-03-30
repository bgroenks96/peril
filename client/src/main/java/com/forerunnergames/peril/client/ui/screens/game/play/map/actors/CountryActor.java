package com.forerunnergames.peril.client.ui.screens.game.play.map.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.client.settings.AssetPaths;
import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.settings.PlayMapSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.map.data.CountrySpriteColorOrder;
import com.forerunnergames.peril.client.ui.screens.game.play.map.data.CountrySpriteData;
import com.forerunnergames.peril.client.ui.screens.game.play.map.tools.CoordinateSpaces;
import com.forerunnergames.peril.core.model.people.player.PlayerColor;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.geometry.Geometry;
import com.forerunnergames.tools.common.geometry.Point2D;
import com.forerunnergames.tools.common.geometry.Size2D;

import java.util.HashMap;
import java.util.Map;

// @formatter:off
public final class CountryActor extends Actor
{
  private static final int TOTAL_SPRITE_COUNT = 13;
  private static final int DISABLED_SPRITE_INDEX = 11;
  private static final int HIGHLIGHTED_SPRITE_INDEX = 12;
  private final CountrySpriteData spriteData;
  private final CountrySpriteColorOrder colorOrder;
  private final Map <Integer, Sprite> spriteIndicesToSprites = new HashMap <> ();
  private final Texture spriteSheetTexture;
  private final float x;
  private final float y;
  private final float width;
  private final float height;
  private boolean isHovered = false;
  private boolean isTouchDown = false;
  private int currentSpriteIndex;

  public CountryActor (final CountrySpriteData spriteData, final CountrySpriteColorOrder colorOrder)
  {
    Arguments.checkIsNotNull (spriteData, "spriteData");
    Arguments.checkIsNotNull (colorOrder, "colorOrder");

    this.spriteData = spriteData;
    this.colorOrder = colorOrder;

    spriteSheetTexture = new Texture (loadSpriteSheet (), GraphicsSettings.TEXTURE_MIPMAPPING);
    spriteSheetTexture.setFilter (GraphicsSettings.TEXTURE_MINIFICATION_FILTER, GraphicsSettings.TEXTURE_MAGNIFICATION_FILTER);

    for (int spriteIndex = 0; spriteIndex < TOTAL_SPRITE_COUNT; ++spriteIndex)
    {
      final Sprite sprite = createSprite (spriteSheetTexture, spriteIndex);

      spriteIndicesToSprites.put (spriteIndex, sprite);
    }

    final Point2D destReferenceScreenSpace = CoordinateSpaces.referencePlayMapSpaceToReferenceScreenSpace (spriteData.getDestPlayMapReferenceSpace ());
    x = destReferenceScreenSpace.getX();
    y = GraphicsSettings.REFERENCE_SCREEN_HEIGHT - destReferenceScreenSpace.getY();
    final Size2D sizeActualPlayMapSpace = Geometry.scale (spriteData.getSizePlayMapReferenceSpace(), PlayMapSettings.REFERENCE_PLAY_MAP_SPACE_TO_ACTUAL_PLAY_MAP_SPACE_SCALING);
    width = sizeActualPlayMapSpace.getWidth ();
    height = sizeActualPlayMapSpace.getHeight ();

    setName (spriteData.getName ());
    setSize (width, height);
    setBounds (0, 0, width, height);
    clearColor();
  }

  @Override
  public void draw (final Batch batch, final float parentAlpha)
  {
    // TODO Production: Remove
    if (spriteData.hasName ("Antarctica")) return;
    if (currentSpriteIndex >= 0) batch.draw (getSpriteAtIndex (currentSpriteIndex), x, y, width, height);
    if (PlayMapSettings.ENABLE_HOVER_EFFECTS && isHovered) batch.draw (getSpriteAtIndex (HIGHLIGHTED_SPRITE_INDEX), x, y, width, height);
    if (PlayMapSettings.ENABLE_CLICK_EFFECTS && isTouchDown) batch.draw (getSpriteAtIndex (DISABLED_SPRITE_INDEX), x, y, width, height);
  }

  public PlayerColor getCurrentColor()
  {
    return colorOrder.getColorOf (currentSpriteIndex);
  }

  public void changeColorRandomly ()
  {
    int randomSpriteIndex;

    do
    {
      randomSpriteIndex = colorOrder.getRandomSpriteIndex ();
    }
    while (randomSpriteIndex == currentSpriteIndex);

    currentSpriteIndex = randomSpriteIndex;
  }

  public void changeColorTo (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");

    currentSpriteIndex = colorOrder.getSpriteIndexOf (color);
  }

  public void nextColor ()
  {
    final PlayerColor color = colorOrder.getColorOf (currentSpriteIndex);

    changeColorTo (color.hasNext() ? color.next() : PlayerColor.BLUE);
  }

  public void clearColor ()
  {
    changeColorTo (PlayerColor.UNKNOWN);
  }

  public void onHoverStart ()
  {
    isHovered = true;
  }

  public void onHoverEnd ()
  {
    isHovered = false;
  }

  public void onTouchDown ()
  {
    isTouchDown = true;
  }

  public void onTouchUp ()
  {
    isTouchDown = false;
  }

  public void setTextureFiltering (final Texture.TextureFilter minFilter, final Texture.TextureFilter magFilter)
  {
    Arguments.checkIsNotNull (minFilter, "minFilter");
    Arguments.checkIsNotNull (magFilter, "magFilter");

    spriteSheetTexture.setFilter (minFilter, magFilter);
  }

  public Sprite getCurrentSprite()
  {
    return getSpriteAtIndex (currentSpriteIndex);
  }

  public Point2D getDestPlayMapReferenceSpace()
  {
    return spriteData.getDestPlayMapReferenceSpace ();
  }

  public Point2D getCenterPlayMapReferenceSpace()
  {
    return spriteData.getCenterPlayMapReferenceSpace();
  }

  public String getCurrentCountrySpriteName()
  {
    return getName() + " " + getCurrentColor ().toProperCase ();
  }

  private Sprite createSprite (final Texture spriteSheetTexture, final int spriteIndex)
  {
    return new Sprite (new TextureRegion (spriteSheetTexture, Math.round (spriteData.getSrcX (spriteIndex)),
                    Math.round (spriteData.getSrcY (spriteIndex)), Math.round (spriteData.getWidth ()),
                    Math.round (spriteData.getHeight ())));
  }

  private Sprite getSpriteAtIndex (final int spriteIndex)
  {
    return spriteIndicesToSprites.get (spriteIndex);
  }

  private FileHandle loadSpriteSheet ()
  {
    return Gdx.files.internal (AssetPaths.PLAY_MAP_COUNTRY_SPRITE_IMAGES_PATH + spriteData.getNameAsFileName ("png"));
  }
}
