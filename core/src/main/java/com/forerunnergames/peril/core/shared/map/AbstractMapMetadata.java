package com.forerunnergames.peril.core.shared.map;

import com.forerunnergames.tools.common.Strings;

public abstract class AbstractMapMetadata implements MapMetadata
{
  @Override
  public final int hashCode ()
  {
    int result = getName ().hashCode ();
    result = 31 * result + getType ().hashCode ();
    result = 31 * result + getMode ().hashCode ();
    return result;
  }

  @Override
  public final boolean equals (final Object obj)
  {
    if (this == obj) return true;
    if (obj == null || !MapMetadata.class.isInstance (obj.getClass ())) return false;

    final MapMetadata mapMetaData = (MapMetadata) obj;

    return getName ().equalsIgnoreCase (mapMetaData.getName ()) && getType () == mapMetaData.getType ()
            && getMode () == mapMetaData.getMode ();
  }

  @Override
  public final String toString ()
  {
    return Strings.format ("{}: Name: {} | Type: {} | Mode: {}", getClass ().getSimpleName (), getName (), getType (),
                           getMode ());
  }
}
