package com.forerunnergames.peril.core.shared.net.kryonet;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import com.google.common.collect.ImmutableSet;

/**
 * Kryo {@link Serializer} for Guava's {@link ImmutableSet}.
 */
public class KryonetImmutableSetSerializer extends Serializer <ImmutableSet <Object>>
{
  private static final boolean DOES_NOT_ACCEPT_NULL = false;
  private static final boolean IMMUTABLE = true;

  public KryonetImmutableSetSerializer ()
  {
    super (DOES_NOT_ACCEPT_NULL, IMMUTABLE);
  }

  @Override
  public void write (final Kryo kryo, final Output output, final ImmutableSet <Object> object)
  {
    output.writeInt (object.size (), true);
    for (final Object elm : object)
    {
      kryo.writeClassAndObject (output, elm);
    }
  }

  @Override
  public ImmutableSet <Object> read (final Kryo kryo, final Input input, final Class <ImmutableSet <Object>> type)
  {
    final int size = input.readInt (true);
    final Object[] list = new Object [size];
    for (int i = 0; i < size; ++i)
    {
      list [i] = kryo.readClassAndObject (input);
    }
    return ImmutableSet.copyOf (list);
  }
}
