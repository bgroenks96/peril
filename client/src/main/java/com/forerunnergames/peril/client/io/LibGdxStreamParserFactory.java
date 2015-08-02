package com.forerunnergames.peril.client.io;

import com.badlogic.gdx.Gdx;

import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.core.shared.io.StreamParserFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.io.StreamParser;

public final class LibGdxStreamParserFactory implements StreamParserFactory
{
  public StreamParser create (final String fileName)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    // @formatter:off
    return new StreamParser (Gdx.files.external (AssetSettings.RELATIVE_EXTERNAL_ASSETS_DIRECTORY + "/" + fileName).reader ())
                    .withComments (StreamParser.CommentType.SLASH_SLASH, StreamParser.CommentStatus.ENABLED)
                    .withCSVSyntax ();
    // @formatter:on
  }
}
