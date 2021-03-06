import com.google.common.base.Strings;
import org.gdal.gdal.gdal;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;

public class WorldPopTiler {

  public static void main(String[] args) {

    gdal.AllRegister();

    File sourceDir = findSourceDir();

    int baseZoomLevel = 10;
    Tiling tiling = new Tiling(baseZoomLevel);
    Optional<GcsUploader> uploader = GcsUploader.fromEnvironment();
    TileStore tileStore = new TileStore(new File("tiles"));

    renderBaseLayer(sourceDir, tiling, tileStore, uploader);

    downsample(tileStore, uploader, baseZoomLevel);

    uploader.ifPresent(up -> up.close());
  }

  static File findSourceDir() {
    String sourceDirName = System.getenv("SOURCE_DIR");
    if(Strings.isNullOrEmpty(sourceDirName)) {
      System.err.println("The SOURCE_DIR environment variable is not set.");
      System.exit(-1);
      return null;
    }

    File sourceDir = new File(sourceDirName);
    if(!sourceDir.exists()) {
      System.err.println("The SOURCE_DIR (" + sourceDir.getAbsolutePath() + ") does not exist.");
      System.exit(-1);
      return null;
    }
    return sourceDir;
  }

  private static void renderBaseLayer(File sourceDir, Tiling tiling, TileStore tileStore, Optional<GcsUploader> uploader) {

    TileRenderQueue renderQueue = new TileRenderQueue();

    for (File file : sourceDir.listFiles()) {
      if (file.getName().endsWith(".tif") && !file.getName().startsWith("ata")) {

        Country country = new Country(tiling, file);

        for (TileRect rect : country.divideIntoBatches()) {
          renderQueue.enqueue(new TileBatch(country, tileStore, rect));
        }
      }
    }
    renderQueue.run();
    uploader.ifPresent(up -> up.uploadZoomDirectory(tileStore.getZoomDirectory(tiling.zoomLevel)));
  }

  private static void downsample(TileStore store, Optional<GcsUploader> uploader, int baseZoomLevel) {
    for (int zoom = baseZoomLevel - 1; zoom >= 0; zoom--) {
      Tiling tiling = new Tiling(zoom);
      System.out.println("Downsampling zoom level " + zoom + "...");
      ForkJoinPool.commonPool().invoke(new DownsampleTask(tiling, store, 0, 0, Tiling.tileCount(zoom)));

      File zoomDirectory = store.getZoomDirectory(zoom);
      uploader.ifPresent(up -> up.uploadZoomDirectory(zoomDirectory));
    }
  }
}
