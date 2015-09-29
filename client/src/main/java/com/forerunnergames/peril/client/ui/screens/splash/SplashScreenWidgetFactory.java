package com.forerunnergames.peril.client.ui.screens.splash;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;

public final class SplashScreenWidgetFactory extends WidgetFactory
{
  public SplashScreenWidgetFactory (final AssetManager assetManager)
  {
    super (assetManager);
  }

  @Override
  protected Skin getSkin ()
  {
    return getAsset (AssetSettings.SPLASH_SCREEN_SKIN_ASSET_DESCRIPTOR);
  }

  public Image createBackground ()
  {
    return new Image (getSkin (), "background");
  }

  public ProgressBar createProgressBar (final float stepSize)
  {
    return createHorizontalProgressBar (0.0f, 1.0f, stepSize, "default-horizontal");
  }
}
