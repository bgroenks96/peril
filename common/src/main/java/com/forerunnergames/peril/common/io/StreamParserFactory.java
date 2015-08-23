package com.forerunnergames.peril.common.io;

import com.forerunnergames.tools.common.io.StreamParser;

public interface StreamParserFactory
{
  StreamParser create (final String fileName);
}
