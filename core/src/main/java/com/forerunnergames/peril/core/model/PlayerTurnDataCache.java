package com.forerunnergames.peril.core.model;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Exceptions;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PlayerTurnDataCache
{
  private static final Logger log = LoggerFactory.getLogger (PlayerTurnDataCache.class);

  private final Map <CacheKey, Object> dataCache = Maps.newConcurrentMap ();

  boolean isSet (final CacheKey key)
  {
    Arguments.checkIsNotNull (key, "key");

    return dataCache.containsKey (key);
  }

  boolean isNotSet (final CacheKey key)
  {
    Arguments.checkIsNotNull (key, "key");

    return !dataCache.containsKey (key);
  }

  void put (final CacheKey key, final Object value)
  {
    Arguments.checkIsNotNull (key, "key");
    Arguments.checkIsNotNull (value, "value");

    if (dataCache.containsKey (key))
    {
      log.warn ("Overwriting pre-existing value for [{}] in data cache | oldValue=[{}] | newValue=[{}]", key,
                dataCache.get (key), value);
    }

    dataCache.put (key, value);
  }

  <T> T get (final CacheKey key, final Class <T> valueType)
  {
    Arguments.checkIsNotNull (key, "key");
    Arguments.checkIsNotNull (valueType, "valueType");
    Preconditions.checkIsTrue (dataCache.containsKey (key), "No value for [{}] currently in cache.");

    return valueType.cast (dataCache.get (key));
  }

  void clear (final CacheKey key)
  {
    Arguments.checkIsNotNull (key, "key");

    dataCache.remove (key);
  }

  <T> Optional <T> clear (final CacheKey key, final Class <T> valueType)
  {
    Arguments.checkIsNotNull (key, "key");
    Arguments.checkIsNotNull (valueType, "valueType");

    final Object value = dataCache.remove (key);
    if (value == null) return Optional.absent ();

    if (!value.getClass ().equals (valueType))
    {
      Exceptions.throwIllegalArg ("Type [{}] does not match value type [{}].", valueType, value.getClass ());
    }

    return Optional.of (valueType.cast (dataCache.remove (key)));
  }

  void clearAll ()
  {
    dataCache.clear ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: [{}]", dataCache);
  }

  enum CacheKey
  {
    BATTLE_PENDING_ATTACK_ORDER,
    BATTLE_ATTACKER_DATA,
    BATTLE_DEFENDER_DATA,
    OCCUPY_SOURCE_COUNTRY,
    OCCUPY_DEST_COUNTRY,
    OCCUPY_MIN_ARMY_COUNT
  }
}
