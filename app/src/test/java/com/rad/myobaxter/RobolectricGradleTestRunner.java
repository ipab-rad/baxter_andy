package com.rad.myobaxter;

import org.junit.runners.model.InitializationError;
import org.robolectric.AndroidManifest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.res.Fs;

public class RobolectricGradleTestRunner extends RobolectricTestRunner {

  public RobolectricGradleTestRunner(final Class<?> testClass) throws InitializationError {
    super(testClass);
  }

  @Override
  protected AndroidManifest getAppManifest(final Config config) {
    final String _manifestProperty = System.getProperty("android.manifest");
    final String _resProperty = System.getProperty("android.resources");
    final String _assetsProperty = System.getProperty("android.assets");

    final String manifestProperty = _manifestProperty == null ? "app/build/intermediates/manifests/full/debug/AndroidManifest.xml" : _manifestProperty;
    final String resProperty = _resProperty == null ? "app/build/intermediates/res/debug" : _resProperty;
    final String assetsProperty = _assetsProperty == null ? "app/build/intermediates/assets/debug" : _assetsProperty;
    final String packageProperty = BuildConfig.APPLICATION_ID; // System.getProperty("android.package");

    if (config.manifest().equals(Config.DEFAULT) && manifestProperty != null) {
      final AndroidManifestExt a = new AndroidManifestExt(
          Fs.fileFromPath(manifestProperty),
          Fs.fileFromPath(resProperty),
          Fs.fileFromPath(assetsProperty));
      a.setPackageName(packageProperty);
      return a;
    }
    return super.getAppManifest(config);
  }
}