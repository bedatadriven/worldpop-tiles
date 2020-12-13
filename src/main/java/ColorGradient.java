import java.awt.*;
import java.util.Arrays;

public class ColorGradient {

  public static final Color TRANSPARENT_COLOR = new Color(255, 255, 255, 0);

  public static final int TRANSPARENT = TRANSPARENT_COLOR.getRGB();

  private final int maxColor;
  private final int maxPop;
  private final int[] rgb;

  public ColorGradient() {
    maxPop = 600;
    rgb = new int[maxPop];
    rgb[0] = new Color(255, 255, 240).getRGB();
    Arrays.fill(rgb, 1, 25, new Color(255, 255, 204).getRGB());
    Arrays.fill(rgb, 25, 50, new Color(255, 237, 160).getRGB());
    Arrays.fill(rgb, 50, 100, new Color(254, 217, 118).getRGB());
    Arrays.fill(rgb, 100, 150, new Color(254, 178, 76).getRGB());
    Arrays.fill(rgb, 150, 200, new Color(253, 141, 60).getRGB());
    Arrays.fill(rgb, 200, 400, new Color(252, 78, 42).getRGB());
    Arrays.fill(rgb, 400, 600, new Color(227, 26, 28).getRGB());
    maxColor = new Color(177, 0, 38).getRGB();
  }

  public ColorGradient(int scale) {
    maxPop = 3000;
    rgb = new int[maxPop];
    rgb[0] = new Color(255, 255, 240).getRGB();
    Arrays.fill(rgb, 1, 4, new Color(255, 255, 204).getRGB());
    Arrays.fill(rgb, 4, 8, new Color(255, 237, 160).getRGB());
    Arrays.fill(rgb, 8, 12, new Color(254, 217, 118).getRGB());
    Arrays.fill(rgb, 12, 20, new Color(254, 178, 76).getRGB());
    Arrays.fill(rgb, 20, 50, new Color(253, 141, 60).getRGB());
    Arrays.fill(rgb, 50, 100, new Color(252, 78, 42).getRGB());
    Arrays.fill(rgb, 100, 3000, new Color(227, 26, 28).getRGB());
    maxColor = new Color(177, 0, 38).getRGB();
  }

  public int color(int pop) {
    if(pop < 0) {
      return TRANSPARENT;
    } else if(pop >= maxPop) {
      return maxColor;
    } else {
      return rgb[pop];
    }
  }
}
