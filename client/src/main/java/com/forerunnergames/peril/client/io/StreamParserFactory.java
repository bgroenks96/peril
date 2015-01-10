package com.forerunnergames.peril.client.io;

import com.badlogic.gdx.Gdx;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.io.StreamParser;

public final class StreamParserFactory
{
  // @formatter:off
  public static StreamParser create (final String fileName)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    return new StreamParser (Gdx.files.internal (fileName).reader ())
                    .withComments (StreamParser.CommentType.SLASH_SLASH, StreamParser.CommentStatus.ENABLED)
                    .withCSVSyntax ();
  }

  private StreamParserFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
  // @formatter:on
}
