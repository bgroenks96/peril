package com.forerunnergames.peril.core.shared.net.kryonet;

/*
 * Copyright 2014 Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.MapSerializer;

import com.google.common.collect.ImmutableMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Kryo {@link Serializer} for Guava's {@link ImmutableMap}.
 */
public class KryonetImmutableMapSerializer extends Serializer <ImmutableMap <?, ?>>
{
  private static final boolean DOES_NOT_ACCEPT_NULL = false;
  private static final boolean IMMUTABLE = true;
  private final MapSerializer mapSerializer = new MapSerializer ();

  public KryonetImmutableMapSerializer ()
  {
    super (DOES_NOT_ACCEPT_NULL, IMMUTABLE);
  }

  @Override
  public void write (final Kryo kryo, final Output output, final ImmutableMap <?, ?> object)
  {
    // wrapping with unmodifiableMap proxy
    // to avoid Kryo from writing only the reference marker of this instance,
    // which will be embedded right before this method call.
    kryo.writeObject (output, Collections.unmodifiableMap (object), mapSerializer);
  }

  @Override
  public ImmutableMap <?, ?> read (final Kryo kryo, final Input input, final Class <ImmutableMap <?, ?>> type)
  {
    final Map <?, ?> map = kryo.readObject (input, HashMap.class, mapSerializer);
    return ImmutableMap.copyOf (map);
  }
}
