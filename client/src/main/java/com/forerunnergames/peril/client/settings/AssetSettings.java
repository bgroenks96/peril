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

package com.forerunnergames.peril.client.settings;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.forerunnergames.peril.client.io.MultiAtlasSkinLoader;
import com.forerunnergames.peril.client.io.ShaderProgramLoader;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.NetworkTools;

import com.google.common.collect.ImmutableList;

import java.util.regex.Pattern;

public final class AssetSettings
{
  public static final String INITIAL_S3_ASSETS_DOWNLOAD_SUBDIRECTORY = "";
  public static final String RELATIVE_EXTERNAL_ASSETS_PARENT_DIRECTORY = "peril/";
  // External assets directory is relative to:
  // - the home directory of the current user on desktop (Windows / OS X / Linux)
  // - the SD card root on mobile (Android / iOS)
  // and is accessed via Gdx.files.external.
  // It is used for assets that do not ship with the game executable,
  // and must be downloaded separately to this directory.
  //
  // Prepend: Relative external assets parent directory (compile-time)
  public static final String RELATIVE_EXTERNAL_ASSETS_DIRECTORY = RELATIVE_EXTERNAL_ASSETS_PARENT_DIRECTORY + "assets/";
  // Prepend: Relative countries directory (compile-time)
  // Append: Country atlases (runtime)
  public static final String RELATIVE_COUNTRY_ATLASES_DIRECTORY = "atlases/";
  // Play map resource files
  public static final String PLAY_MAP_BACKGROUND_IMAGE_FILENAME = "background.png";
  public static final Class <Texture> PLAY_MAP_BACKGROUND_IMAGE_TYPE = Texture.class;
  public static final String PLAY_MAP_INPUT_DETECTION_IMAGE_FILENAME = "inputDetection.png";
  public static final Class <Pixmap> PLAY_MAP_INPUT_DETECTION_IMAGE_TYPE = Pixmap.class;
  public static final String CONTINENT_INPUT_DETECTION_DATA_FILENAME = "inputDetection.txt";
  public static final String COUNTRY_IMAGE_DATA_FILENAME = "imageData.txt";
  public static final String COUNTRY_INPUT_DETECTION_DATA_FILENAME = "inputDetection.txt";
  public static final String VALID_COUNTRY_ATLAS_FILENAME_DESCRIPTION = "1) Must begin with the word \'countries\' (all lowercase, without quotes)\n"
          + "2) The word \'countries\' must be followed by a consecutive, increasing, whole number index, starting from \'1\' for the first country atlas.\n"
          + "3) The index number must be followed by \'.atlas\' (all lowercase, without quotes)\n"
          + "4) Each .atlas file must be accompanied by a corresponding .png file with the same name, in the same folder.\n\n"
          + "Examples of valid .atlas filenames: countries1.atlas, countries2.atlas, countries3.atlas\n"
          + "Examples of valid .png filenames: countries1.png, countries2.png, countries3.png\n";
  // Splash Screen
  public static final AssetDescriptor <TextureAtlas> SPLASH_SCREEN_SKIN_ATLAS_ASSET_DESCRIPTOR = new AssetDescriptor <> (
          "screens/splash/skin.atlas", TextureAtlas.class);
  public static final AssetDescriptor <Skin> SPLASH_SCREEN_SKIN_ASSET_DESCRIPTOR = new AssetDescriptor <> (
          "screens/splash/skin.json", Skin.class, new MultiAtlasSkinLoader.SkinParameter (
                  SPLASH_SCREEN_SKIN_ATLAS_ASSET_DESCRIPTOR));
  // General
  public static final AssetDescriptor <ShaderProgram> SPRITE_BATCH_SHADER_PROGRAM_ASSET_DESCRIPTOR = new AssetDescriptor <> (
          "shaders/spriteBatch.vert", ShaderProgram.class, new ShaderProgramLoader.FragmentShaderParameter (
                  "shaders/spriteBatch.frag"));
  public static final AssetDescriptor <Pixmap> NORMAL_CURSOR_ASSET_DESCRIPTOR = new AssetDescriptor <> (
          "cursors/normalCursor.png", Pixmap.class);
  // Menu Screens
  public static final AssetDescriptor <TextureAtlas> MENU_SCREEN_SKIN_ATLAS_1_ASSET_DESCRIPTOR = new AssetDescriptor <> (
          "screens/menus/shared/skin1.atlas", TextureAtlas.class);
  public static final AssetDescriptor <TextureAtlas> MENU_SCREEN_SKIN_ATLAS_2_ASSET_DESCRIPTOR = new AssetDescriptor <> (
          "screens/menus/shared/skin2.atlas", TextureAtlas.class);
  public static final AssetDescriptor <Skin> MENU_SCREEN_SKIN_ASSET_DESCRIPTOR = new AssetDescriptor <> (
          "screens/menus/shared/skin.json", Skin.class, new MultiAtlasSkinLoader.SkinParameter (
                  MENU_SCREEN_SKIN_ATLAS_1_ASSET_DESCRIPTOR, MENU_SCREEN_SKIN_ATLAS_2_ASSET_DESCRIPTOR));
  public static final AssetDescriptor <Music> MENU_SCREEN_MUSIC_ASSET_DESCRIPTOR = new AssetDescriptor <> (
          "screens/menus/shared/music/peril.ogg", Music.class);
  // Loading Screen
  public static final AssetDescriptor <TextureAtlas> LOADING_SCREEN_SKIN_ATLAS_ASSET_DESCRIPTOR = new AssetDescriptor <> (
          "screens/loading/skin.atlas", TextureAtlas.class);
  public static final AssetDescriptor <Skin> LOADING_SCREEN_SKIN_ASSET_DESCRIPTOR = new AssetDescriptor <> (
          "screens/loading/skin.json", Skin.class, new MultiAtlasSkinLoader.SkinParameter (
                  LOADING_SCREEN_SKIN_ATLAS_ASSET_DESCRIPTOR));
  // Play Screen
  public static final AssetDescriptor <Music> PLAY_SCREEN_MUSIC_ASSET_DESCRIPTOR = new AssetDescriptor <> (
          "screens/game/play/modes/shared/music/escalation.ogg", Music.class);
  // Classic Mode Play Screen
  public static final AssetDescriptor <TextureAtlas> CLASSIC_MODE_PLAY_SCREEN_SKIN_ATLAS_1_ASSET_DESCRIPTOR = new AssetDescriptor <> (
          "screens/game/play/modes/classic/skin.atlas", TextureAtlas.class);
  public static final AssetDescriptor <Skin> CLASSIC_MODE_PLAY_SCREEN_SKIN_ASSET_DESCRIPTOR = new AssetDescriptor <> (
          "screens/game/play/modes/classic/skin.json", Skin.class, new MultiAtlasSkinLoader.SkinParameter (
                  CLASSIC_MODE_PLAY_SCREEN_SKIN_ATLAS_1_ASSET_DESCRIPTOR));
  public static final AssetDescriptor <Sound> CLASSIC_MODE_PLAY_SCREEN_BATTLE_SINGLE_EXPLOSION_SOUND_ASSET_DESCRIPTOR = new AssetDescriptor <> (
          "screens/game/play/modes/classic/sounds/artillery_explosion07_48k.wav", Sound.class);
  public static final AssetDescriptor <Music> CLASSIC_MODE_PLAY_SCREEN_BATTLE_AMBIENCE_SOUND_EFFECT_ASSET_DESCRIPTOR = new AssetDescriptor <> (
          "screens/game/play/modes/classic/sounds/war_ambience_distant_01_120_48k_eq.ogg", Music.class);
  // Peril Mode Play Screen
  public static final AssetDescriptor <TextureAtlas> PERIL_MODE_ATLAS_ASSET_DESCRIPTOR = new AssetDescriptor <> (
          "screens/game/play/modes/peril/atlases/skin.atlas", TextureAtlas.class);
  public static final String PERIL_MODE_GRIDLINES_ATLAS_NINEPATCH_NAME = "gridMiddle";
  // Distance Field Font Shaders
  public static final String DISTANCE_FIELD_FONT_FRAGMENT_SHADER_FILENAME = "fonts/distancefield/shaders/font.frag";
  public static final String DISTANCE_FIELD_FONT_VERTEX_SHADER_FILENAME = "fonts/distancefield/shaders/font.vert";
  // TODO Java 8: Generalized target-type inference: Remove unnecessary explicit generic type casts.
  public static final ImmutableList <AssetDescriptor <?>> LOAD_BEFORE_SPLASH_SCREEN_ASSET_DESCRIPTORS = ImmutableList
          .<AssetDescriptor <?>> of (SPRITE_BATCH_SHADER_PROGRAM_ASSET_DESCRIPTOR, NORMAL_CURSOR_ASSET_DESCRIPTOR,
                                     SPLASH_SCREEN_SKIN_ATLAS_ASSET_DESCRIPTOR, SPLASH_SCREEN_SKIN_ASSET_DESCRIPTOR);
  // TODO Java 8: Generalized target-type inference: Remove unnecessary explicit generic type casts.
  public static final ImmutableList <AssetDescriptor <?>> UNLOAD_AFTER_SPLASH_SCREEN_ASSET_DESCRIPTORS = ImmutableList
          .<AssetDescriptor <?>> of (SPLASH_SCREEN_SKIN_ATLAS_ASSET_DESCRIPTOR, SPLASH_SCREEN_SKIN_ASSET_DESCRIPTOR);
  // TODO Java 8: Generalized target-type inference: Remove unnecessary explicit generic type casts.
  public static final ImmutableList <AssetDescriptor <?>> LOADING_SCREEN_ASSET_DESCRIPTORS = ImmutableList
          .<AssetDescriptor <?>> of (LOADING_SCREEN_SKIN_ATLAS_ASSET_DESCRIPTOR, LOADING_SCREEN_SKIN_ASSET_DESCRIPTOR);
  // TODO Java 8: Generalized target-type inference: Remove unnecessary explicit generic type casts.
  public static final ImmutableList <AssetDescriptor <?>> ALWAYS_LOADED_ASSET_DESCRIPTORS = new ImmutableList.Builder <AssetDescriptor <?>> ()
          .add (SPRITE_BATCH_SHADER_PROGRAM_ASSET_DESCRIPTOR).add (NORMAL_CURSOR_ASSET_DESCRIPTOR)
          .addAll (LOADING_SCREEN_ASSET_DESCRIPTORS).add (MENU_SCREEN_MUSIC_ASSET_DESCRIPTOR)
          .add (PLAY_SCREEN_MUSIC_ASSET_DESCRIPTOR).build ();
  // TODO Java 8: Generalized target-type inference: Remove unnecessary explicit generic type casts.
  public static final ImmutableList <AssetDescriptor <?>> MENU_SCREEN_ASSET_DESCRIPTORS = ImmutableList
          .<AssetDescriptor <?>> of (MENU_SCREEN_SKIN_ATLAS_1_ASSET_DESCRIPTOR,
                                     MENU_SCREEN_SKIN_ATLAS_2_ASSET_DESCRIPTOR, MENU_SCREEN_SKIN_ASSET_DESCRIPTOR);
  public static final ImmutableList <AssetDescriptor <?>> INITIAL_ASSET_DESCRIPTORS = new ImmutableList.Builder <AssetDescriptor <?>> ()
          .addAll (ALWAYS_LOADED_ASSET_DESCRIPTORS).addAll (MENU_SCREEN_ASSET_DESCRIPTORS).build ();
  // TODO Java 8: Generalized target-type inference: Remove unnecessary explicit generic type casts.
  public static final ImmutableList <AssetDescriptor <?>> CLASSIC_MODE_PLAY_SCREEN_ASSET_DESCRIPTORS = ImmutableList
          .<AssetDescriptor <?>> of (CLASSIC_MODE_PLAY_SCREEN_SKIN_ATLAS_1_ASSET_DESCRIPTOR,
                                     CLASSIC_MODE_PLAY_SCREEN_SKIN_ASSET_DESCRIPTOR,
                                     CLASSIC_MODE_PLAY_SCREEN_BATTLE_SINGLE_EXPLOSION_SOUND_ASSET_DESCRIPTOR,
                                     CLASSIC_MODE_PLAY_SCREEN_BATTLE_AMBIENCE_SOUND_EFFECT_ASSET_DESCRIPTOR);
  // TODO Java 8: Generalized target-type inference: Remove unnecessary explicit generic type casts.
  public static final ImmutableList <AssetDescriptor <?>> PERIL_MODE_PLAY_SCREEN_ASSET_DESCRIPTORS = ImmutableList
          .<AssetDescriptor <?>> of (PERIL_MODE_ATLAS_ASSET_DESCRIPTOR);
  private static final TextureLoader.TextureParameter GENERAL_TEXTURE_PARAMETER = new TextureLoader.TextureParameter ();
  public static final TextureLoader.TextureParameter PLAY_MAP_BACKGROUND_IMAGE_PARAMETER = GENERAL_TEXTURE_PARAMETER;
  private static final TextureLoader.TextureParameter FONT_TEXTURE_PARAMETER = new TextureLoader.TextureParameter ();
  private static final String S3_BUCKET_PATH_PREFIX = "s3://";
  // Amazon S3
  public static final String DEFAULT_S3_BUCKET_PATH = S3_BUCKET_PATH_PREFIX + "assets.peril.forerunner.games";
  public static final String VALID_S3_BUCKET_PATH_DESCRIPTION = "1) Must begin with " + S3_BUCKET_PATH_PREFIX + "\n"
          + "2) Must be 3 to 63 characters in length.\n" + "3) Must be all lowercase.\n"
          + "4) Can contain letters, numbers, and hypens.\n"
          + "5) Must be a series of one or more labels. (Adjacent labels are separated by periods.)\n"
          + "6) Each label must begin and end with a lowercase letter or number.\n"
          + "7) Must not be formatted as an ip address (e.g., 192.168.5.4).\n\n"
          + "Examples of valid S3 bucket paths: s3://mybucketname, s3://my.bucket.name, s3://mybucketname.1\n\n"
          + "See https://docs.aws.amazon.com/AmazonS3/latest/dev/BucketRestrictions.html for more information.\n";
  private static final Pattern S3_BUCKET_PATH_PREFIX_PATTERN = Pattern.compile (S3_BUCKET_PATH_PREFIX);
  public static String ABSOLUTE_UPDATED_ASSETS_LOCATION = DEFAULT_S3_BUCKET_PATH;
  public static boolean UPDATE_ASSETS = true;

  static
  {
    GENERAL_TEXTURE_PARAMETER.genMipMaps = GraphicsSettings.TEXTURE_MIPMAPPING;
    GENERAL_TEXTURE_PARAMETER.minFilter = GraphicsSettings.TEXTURE_MINIFICATION_FILTER;
    GENERAL_TEXTURE_PARAMETER.magFilter = GraphicsSettings.TEXTURE_MAGNIFICATION_FILTER;

    FONT_TEXTURE_PARAMETER.genMipMaps = GraphicsSettings.FONT_TEXTURE_MIPMAPPING;
    FONT_TEXTURE_PARAMETER.minFilter = GraphicsSettings.FONT_TEXTURE_MINIFICATION_FILTER;
    FONT_TEXTURE_PARAMETER.magFilter = GraphicsSettings.FONT_TEXTURE_MAGNIFICATION_FILTER;
  }

  public static boolean isValidCountryAtlasPackFileName (final String fileName, final int expectedAtlasIndex)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    if (expectedAtlasIndex < 1) return false;

    return ("countries" + expectedAtlasIndex + ".atlas").equals (fileName);
  }

  public static boolean isValidCountryAtlasImageFileName (final String fileName, final int expectedAtlasIndex)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    if (expectedAtlasIndex < 1) return false;

    return ("countries" + expectedAtlasIndex + ".png").equals (fileName);
  }

  public static String getValidCountryAtlasPackFileName (final int expectedAtlasIndex)
  {
    Arguments.checkLowerInclusiveBound (expectedAtlasIndex, 1, "expectedAtlasIndex");

    return "countries" + expectedAtlasIndex + ".atlas";
  }

  public static boolean isAtlasPackFileType (final String fileName)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    return fileName.endsWith (".atlas");
  }

  public static boolean isValidS3BucketPath (final String bucketPath)
  {
    Arguments.checkIsNotNull (bucketPath, "bucketPath");

    return bucketPath.startsWith (S3_BUCKET_PATH_PREFIX)
            && NetworkTools.isValidDomainName (getS3BucketName (bucketPath));
  }

  public static String getS3BucketName (final String bucketPath)
  {
    Arguments.checkIsNotNull (bucketPath, "bucketPath");

    return S3_BUCKET_PATH_PREFIX_PATTERN.matcher (bucketPath).replaceAll ("");
  }

  public static ImmutableList <AssetDescriptor <?>> fromGameMode (final GameMode mode)
  {
    Arguments.checkIsNotNull (mode, "mode");

    final ScreenId playScreenId = ScreenId.fromGameMode (mode);

    switch (playScreenId)
    {
      case PLAY_CLASSIC:
      {
        return CLASSIC_MODE_PLAY_SCREEN_ASSET_DESCRIPTORS;
      }
      case PLAY_PERIL:
      {
        return PERIL_MODE_PLAY_SCREEN_ASSET_DESCRIPTORS;
      }
      default:
      {
        throw new UnsupportedOperationException (Strings.format ("Unsupported {}: [{}].", playScreenId.getClass ()
                .getSimpleName (), playScreenId));
      }
    }
  }

  private AssetSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
