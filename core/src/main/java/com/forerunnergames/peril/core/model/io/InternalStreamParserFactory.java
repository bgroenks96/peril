package com.forerunnergames.peril.core.model.io;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.io.StreamParser;

import java.io.FileNotFoundException;
import java.io.InputStream;

public final class InternalStreamParserFactory implements StreamParserFactory
{
  @Override
  public StreamParser create (final String fileName)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    final InputStream inputStream = InternalStreamParserFactory.class.getResourceAsStream (fileName);

    if (inputStream == null) throw new RuntimeException (
            new FileNotFoundException ("Could not find model resource file: [" + fileName + "]."));

    // @formatter:off
    return new StreamParser (inputStream)
                    .withComments (StreamParser.CommentType.SLASH_SLASH, StreamParser.CommentStatus.ENABLED)
                    .withCSVSyntax ();
    // @formatter:on
  }
}
