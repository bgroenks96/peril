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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageData;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageDataRepository;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageDataRepositoryFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.DefaultCountryImageDataRepositoryFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryImagesRepository;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.DefaultCountryImagesRepository;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.input.DefaultPlayMapInputDetectionFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.input.PlayMapInputDetection;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.input.PlayMapInputDetectionFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.loaders.CountryAtlasesLoader;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.loaders.CountryImageDataLoader;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.loaders.CountryImagesFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.loaders.DefaultCountryAtlasesLoader;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.loaders.DefaultCountryImagesFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.loaders.DefaultPlayMapBackgroundImageLoader;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.loaders.PlayMapBackgroundImageLoader;
import com.forerunnergames.peril.common.io.BiMapDataLoader;
import com.forerunnergames.peril.common.io.ExternalStreamParserFactory;
import com.forerunnergames.peril.common.io.StreamParserFactory;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import com.google.common.collect.ImmutableMap;

import net.engio.mbassy.bus.MBassador;

public final class DefaultPlayMapFactory implements PlayMapFactory
{
  // @formatter:off
  private final AssetManager assetManager;
  private final MouseInput mouseInput;
  private final MBassador <Event> eventBus;
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
                                final MouseInput mouseInput,
                                final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (assetManager, "assetManager");
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.assetManager = assetManager;
    this.mouseInput = mouseInput;
    this.eventBus = eventBus;
    countryAtlasesLoader = new DefaultCountryAtlasesLoader (assetManager);
    playMapInputDetectionFactory = new DefaultPlayMapInputDetectionFactory (assetManager, screenSize);
    playMapBackgroundImageLoader = new DefaultPlayMapBackgroundImageLoader (assetManager);
  }

  @Override
  public void loadAssets (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    if (mapMetadata.equals (MapMetadata.NULL_MAP_METADATA)) return;

    countryAtlasesLoader.load (mapMetadata);
    playMapBackgroundImageLoader.load (mapMetadata);
    playMapInputDetectionFactory.loadAssets (mapMetadata);
  }

  @Override
  public boolean isFinishedLoadingAssets (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    if (mapMetadata.equals (MapMetadata.NULL_MAP_METADATA)) return true;

    return countryAtlasesLoader.isFinishedLoading (mapMetadata)
            && playMapBackgroundImageLoader.isFinishedLoading (mapMetadata)
            && playMapInputDetectionFactory.isFinishedLoadingAssets (mapMetadata);
  }

  @Override
  public PlayMap create (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    if (mapMetadata.equals (MapMetadata.NULL_MAP_METADATA)) return PlayMap.NULL_PLAY_MAP;

    // @formatter:off
    final BitmapFont font = new BitmapFont ();
    final CountryImageDataRepository countryImageDataRepository = countryImageDataRepositoryFactory.create (mapMetadata);
    final Image backgroundImage = playMapBackgroundImageLoader.get (mapMetadata);
    final Vector2 playMapReferenceSize = new Vector2 (backgroundImage.getWidth (), backgroundImage.getHeight ());
    final PlayMapInputDetection playMapInputDetection = playMapInputDetectionFactory.create (mapMetadata, playMapReferenceSize);
    final HoveredTerritoryText hoveredTerritoryText = new HoveredTerritoryText (playMapInputDetection, mouseInput, font);
    final ImmutableMap.Builder <String, Country> countryNamesToActorsBuilder = ImmutableMap.builder ();
    countryImagesFactory.create (mapMetadata, countryAtlasesLoader.get (mapMetadata));
    // @formatter:on

    final CountryImagesRepository countryImagesRepository = new DefaultCountryImagesRepository (
            countryImagesFactory.getPrimary (mapMetadata), countryImagesFactory.getSecondary (mapMetadata));

    final CountryFactory countryFactory = new CountryFactory (countryImageDataRepository,
            countryImagesRepository, playMapReferenceSize);

    for (final String countryName : countryImageDataRepository.getCountryNames ())
    {
      countryNamesToActorsBuilder.put (countryName, countryFactory.create (countryName, font));
    }

    final PlayMap playMap = new DefaultPlayMap (countryNamesToActorsBuilder.build (),
            playMapInputDetection, hoveredTerritoryText, backgroundImage, mapMetadata, eventBus);

    hoveredTerritoryText.setPlayMap (playMap);

    return playMap;
  }

  @Override
  public void destroy (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    if (mapMetadata.equals (MapMetadata.NULL_MAP_METADATA)) return;

    countryAtlasesLoader.unload (mapMetadata);
    playMapBackgroundImageLoader.unload (mapMetadata);
    playMapInputDetectionFactory.destroy (mapMetadata);
    countryImagesFactory.destroy ();
  }

  @Override
  public float getAssetLoadingProgressPercent (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return isFinishedLoadingAssets (mapMetadata) ? 1.0f : assetManager.getProgressLoading ();
  }
}
