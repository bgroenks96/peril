package com.forerunnergames.peril.core.model.map.io;

import com.forerunnergames.peril.common.io.ExternalStreamParserFactory;
import com.forerunnergames.peril.core.model.io.InternalStreamParserFactory;
import com.forerunnergames.peril.common.io.StreamParserFactory;
import com.forerunnergames.peril.common.map.MapType;
import com.forerunnergames.peril.common.map.PlayMapLoadingException;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Strings;

public final class ContinentModelDataLoaderFactory
{
  public static ContinentModelDataLoader create (final MapType mapType, final CountryIdResolver countryIdResolver)
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
                Strings.format ("Cannot create {} for {}: [{}].", ContinentModelDataLoader.class.getSimpleName (),
                                MapType.class.getSimpleName (), mapType));
      }
    }

    return new ContinentModelDataLoader (streamParserFactory, countryIdResolver);
  }

  private ContinentModelDataLoaderFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
