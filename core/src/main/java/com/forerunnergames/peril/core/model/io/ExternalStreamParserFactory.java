package com.forerunnergames.peril.core.model.io;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.io.StreamParser;

public final class ExternalStreamParserFactory implements StreamParserFactory
{
  @Override
  public StreamParser create (final String fileName)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    // @formatter:off
    return new StreamParser (fileName)
                    .withComments (StreamParser.CommentType.SLASH_SLASH, StreamParser.CommentStatus.ENABLED)
                    .withCSVSyntax ();
    // @formatter:on
  }
}
