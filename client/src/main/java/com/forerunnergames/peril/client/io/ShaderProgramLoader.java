package com.forerunnergames.peril.client.io;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ShaderProgramLoader
        extends AsynchronousAssetLoader <ShaderProgram, ShaderProgramLoader.FragmentShaderParameter>
{
  private static final Logger log = LoggerFactory.getLogger (ShaderProgramLoader.class);
  private FileHandle fragmentShader;

  public ShaderProgramLoader (final FileHandleResolver resolver)
  {
    super (resolver);
  }

  @Override
  public void loadAsync (final AssetManager manager,
                         final String fileName,
                         final FileHandle file,
                         final FragmentShaderParameter parameter)
  {
    Arguments.checkIsNotNull (manager, "manager");
    Arguments.checkIsNotNull (fileName, "fileName");
    Arguments.checkIsNotNull (file, "file");
    Arguments.checkIsNotNull (parameter, "parameter");

    fragmentShader = resolve (parameter.getFragmentShaderFile ());

    log.debug ("Loaded fragment shader file [{}].", fragmentShader);
  }

  @Override
  public ShaderProgram loadSync (final AssetManager manager,
                                 final String fileName,
                                 final FileHandle file,
                                 final FragmentShaderParameter parameter)
  {
    Arguments.checkIsNotNull (manager, "manager");
    Arguments.checkIsNotNull (fileName, "fileName");
    Arguments.checkIsNotNull (file, "file");
    Arguments.checkIsNotNull (parameter, "parameter");
    Preconditions.checkIsTrue (fragmentShader != null, "Fragment shader was not loaded.");

    final ShaderProgram shader = new ShaderProgram (file, fragmentShader);

    if (!shader.isCompiled ())
      throw new GdxRuntimeException (Strings.format ("{} [{}] could not be compiled. Details:\n\n{}",
                                                     ShaderProgram.class.getSimpleName (), shader, shader.getLog ()));

    return shader;
  }

  @Override
  @SuppressWarnings ("rawtypes")
  public Array <AssetDescriptor> getDependencies (final String fileName,
                                                  final FileHandle file,
                                                  final FragmentShaderParameter parameter)
  {
    return null;
  }

  public static final class FragmentShaderParameter extends AssetLoaderParameters <ShaderProgram>
  {
    private final String fragmentShaderFile;

    public FragmentShaderParameter (final String fragmentShaderFile)
    {
      Arguments.checkIsNotNull (fragmentShaderFile, "fragmentShader");
      Arguments.checkIsTrue (fragmentShaderFile.endsWith (".frag"), "Fragment shader file must end in \".frag\".");

      this.fragmentShaderFile = fragmentShaderFile;
    }

    public String getFragmentShaderFile ()
    {
      return fragmentShaderFile;
    }
  }
}
