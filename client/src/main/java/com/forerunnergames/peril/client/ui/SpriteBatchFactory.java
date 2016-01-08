package com.forerunnergames.peril.client.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

public final class SpriteBatchFactory
{
  public static SpriteBatch create (final AssetManager assetManager)
  {
    Arguments.checkIsNotNull (assetManager, "assetManager");

    if (GraphicsSettings.USE_OPENGL_CORE_PROFILE)
    {
      return new SpriteBatch (GraphicsSettings.SPRITES_IN_BATCH,
              assetManager.get (AssetSettings.SPRITE_BATCH_SHADER_PROGRAM_ASSET_DESCRIPTOR));
    }
    else
    {
      return new SpriteBatch (GraphicsSettings.SPRITES_IN_BATCH);
    }
  }

  private SpriteBatchFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
