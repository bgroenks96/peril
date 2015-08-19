package com.forerunnergames.peril.core.model.map.io;

import com.forerunnergames.peril.core.model.io.ExternalStreamParserFactory;
import com.forerunnergames.peril.core.model.io.InternalStreamParserFactory;
import com.forerunnergames.peril.core.shared.io.StreamParserFactory;
import com.forerunnergames.peril.core.shared.map.MapType;
import com.forerunnergames.peril.core.shared.map.PlayMapLoadingException;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Strings;

public final class CountryModelDataLoaderFactory
{
  public static CountryModelDataLoader create (final MapType mapType)
  {
    Arguments.checkIsNotNull (mapType, "mapType");

    final StreamParserFactory streamParserFactory;

    switch (mapType)
    {
      case STOCK:
      {
        streamParserFactory = new InternalStreamParserFactory ();
        break;
      }
      case CUSTOM:
      {
        streamParserFactory = new ExternalStreamParserFactory ();
        break;
      }
      default:
      {
        throw new PlayMapLoadingException (
                Strings.format ("Cannot create {} for {}: [{}].", CountryModelDataLoader.class.getSimpleName (),
                                MapType.class.getSimpleName (), mapType));
      }
    }

    return new CountryModelDataLoader (streamParserFactory);
  }

  private CountryModelDataLoaderFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
