package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.peril.TankActor2;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.peril.TankBodyActor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.peril.TankTurretActor;
import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;

public final class PerilModePlayScreenWidgetFactory extends WidgetFactory
{
  public PerilModePlayScreenWidgetFactory (final AssetManager assetManager)
  {
    super (assetManager);
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
