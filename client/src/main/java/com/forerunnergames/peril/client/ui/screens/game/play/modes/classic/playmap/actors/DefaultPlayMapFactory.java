/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.data.CountryImageData;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.data.CountryImageDataRepository;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.data.CountryImageDataRepositoryFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.data.DefaultCountryImageDataRepositoryFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountryImagesRepository;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.DefaultCountryImagesRepository;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.input.DefaultPlayMapInputDetectionFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.input.PlayMapInputDetection;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.input.PlayMapInputDetectionFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.io.loaders.CountryAtlasesLoader;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.io.loaders.CountryImageDataLoader;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.io.loaders.CountryImagesFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.io.loaders.DefaultCountryAtlasesLoader;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.io.loaders.DefaultCountryImagesFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.io.loaders.DefaultPlayMapBackgroundImageLoader;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.io.loaders.PlayMapBackgroundImageLoader;
import com.forerunnergames.peril.common.io.BiMapDataLoader;
import com.forerunnergames.peril.common.io.ExternalStreamParserFactory;
import com.forerunnergames.peril.common.io.StreamParserFactory;
import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableMap;

public final class DefaultPlayMapFactory implements PlayMapFactory
{
  // @formatter:off
  private final AssetManager assetManager;
  private final MouseInput mouseInput;
  private final CountryAtlasesLoader countryAtlasesLoader;
  private final PlayMapInputDetectionFactory playMapInputDetectionFactory;
  private final PlayMapBackgroundImageLoader playMapBackgroundImageLoader;
  private final CountryImagesFactory countryImagesFactory = new DefaultCountryImagesFactory ();
  private final StreamParserFactory streamParserFactory = new ExternalStreamParserFactory ();
  private final BiMapDataLoader <String, CountryImageData> countryImageDataLoader = new CountryImageDataLoader (streamParserFactory);
  private final CountryImageDataRepositoryFactory countryImageDataRepositoryFactory = new DefaultCountryImageDataRepositoryFactory (countryImageDataLoader);
  // @formatter:on

  public DefaultPlayMapFactory (final AssetManager assetManager,
                                final ScreenSize screenSize,
                                final MouseInput mouseInput)
  {
    Arguments.checkIsNotNull (assetManager, "assetManager");
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");

    this.assetManager = assetManager;
    this.mouseInput = mouseInput;
    countryAtlasesLoader = new DefaultCountryAtlasesLoader (assetManager);
    playMapInputDetectionFactory = new DefaultPlayMapInputDetectionFactory (assetManager, screenSize);
    playMapBackgroundImageLoader = new DefaultPlayMapBackgroundImageLoader (assetManager);
  }

  @Override
  public void loadAssets (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");

    if (playMapMetadata.equals (PlayMapMetadata.NULL)) return;

    countryAtlasesLoader.load (playMapMetadata);
    playMapBackgroundImageLoader.load (playMapMetadata);
    playMapInputDetectionFactory.loadAssets (playMapMetadata);
  }

  @Override
  public boolean isFinishedLoadingAssets (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");

    if (playMapMetadata.equals (PlayMapMetadata.NULL)) return true;

    return countryAtlasesLoader.isFinishedLoading (playMapMetadata)
            && playMapBackgroundImageLoader.isFinishedLoading (playMapMetadata)
            && playMapInputDetectionFactory.isFinishedLoadingAssets (playMapMetadata);
  }

  @Override
  public PlayMap create (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");

    if (playMapMetadata.equals (PlayMapMetadata.NULL)) return PlayMap.NULL;

    // @formatter:off
    final BitmapFont font = new BitmapFont ();
    final CountryImageDataRepository countryImageDataRepository = countryImageDataRepositoryFactory.create (playMapMetadata);
    final Image backgroundImage = playMapBackgroundImageLoader.get (playMapMetadata);
    final Vector2 playMapReferenceSize = new Vector2 (backgroundImage.getWidth (), backgroundImage.getHeight ());
    final PlayMapInputDetection playMapInputDetection = playMapInputDetectionFactory.create (playMapMetadata, playMapReferenceSize);
    final HoveredTerritoryText hoveredTerritoryText = new HoveredTerritoryText (playMapInputDetection, mouseInput, font);
    final ImmutableMap.Builder <String, Country> countryNamesToActorsBuilder = ImmutableMap.builder ();
    countryImagesFactory.create (playMapMetadata, countryAtlasesLoader.get (playMapMetadata));
    // @formatter:on

    final CountryImagesRepository countryImagesRepository = new DefaultCountryImagesRepository (
            countryImagesFactory.getPrimary (playMapMetadata), countryImagesFactory.getSecondary (playMapMetadata));

    final CountryFactory countryFactory = new CountryFactory (countryImageDataRepository, countryImagesRepository,
            playMapReferenceSize);

    for (final String countryName : countryImageDataRepository.getCountryNames ())
    {
      countryNamesToActorsBuilder.put (countryName, countryFactory.create (countryName, font));
    }

    final PlayMap playMap = new DefaultPlayMap (countryNamesToActorsBuilder.build (), playMapInputDetection,
            hoveredTerritoryText, backgroundImage, playMapMetadata);

    hoveredTerritoryText.setPlayMap (playMap);

    return playMap;
  }

  @Override
  public void destroy (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");

    if (playMapMetadata.equals (PlayMapMetadata.NULL)) return;

    countryAtlasesLoader.unload (playMapMetadata);
    playMapBackgroundImageLoader.unload (playMapMetadata);
    playMapInputDetectionFactory.destroy (playMapMetadata);
    countryImagesFactory.destroy ();
  }

  @Override
  public float getAssetLoadingProgressPercent (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");

    return isFinishedLoadingAssets (playMapMetadata) ? 1.0f : assetManager.getProgressLoading ();
  }
}
