package com.forerunnergames.peril.client.ui.screens.game.play.modes.peril;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.ui.widgets.AbstractWidgetFactory;

public final class PerilModePlayScreenWidgetFactory extends AbstractWidgetFactory
{
  public PerilModePlayScreenWidgetFactory (final AssetManager assetManager)
  {
    super (assetManager);
  }

  @Override
  protected AssetDescriptor<Skin> getSkinAssetDescriptor ()
  {
    // TODO Create Peril Mode Skin
    return AssetSettings.CLASSIC_MODE_PLAY_SCREEN_SKIN_ASSET_DESCRIPTOR;
  }

  public TankActor2 createTankActor2 ()
  {
    return new TankActor2 (new TankBodyActor (getTextureRegion ("tankBody")),
            new TankTurretActor (getTextureRegion ("tankTurret")));
  }

  private TextureRegion getTextureRegion (final String regionName)
  {
    return getAsset (AssetSettings.PERIL_MODE_ATLAS_ASSET_DESCRIPTOR).findRegion (regionName);
  }
}
