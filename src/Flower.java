import java.awt.*;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;

public class Flower extends Plant {
    Color fColour;
    public Flower()
    {
        float randomR = (float)Math.random() * 0.75f + 0.25f;
        float randomG = 0;
        float randomB = (float)Math.random() * 0.75f + 0.25f;

        fColour = new Color(randomR, randomG, randomB);
        this.nutrition = 0.6f;
        this.name = "Flower";
        this.pColour = fColour;
        entityType = Board.Ent.flower;
        this.sprite = getFlowerImage(fColour);
    }
    public Flower (Color pColour) {
        this.pColour = pColour;
    }

    public static Image getFlowerImage(Color fColour) {
        Image ogSprite = Toolkit.getDefaultToolkit().getImage("images/flower.png");
        Flower temp = new Flower(fColour);
        ImageFilter filter = temp.new FlowerFilter();
        return Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(ogSprite.getSource(), filter));
    }

    private class FlowerFilter extends RGBImageFilter {
        public FlowerFilter() {
            canFilterIndexColorModel = true;
        }

        @Override
        public int filterRGB(int x, int y, int rgb) {
            int a = (rgb >> 24) & 0xff;
            int r = (rgb >> 16) & 0xff;
            int g = (rgb >> 8) & 0xff;
            int b = rgb & 0xff;

            r *= pColour.getRed() / 256.0;
            g *= pColour.getGreen() / 256.0;
            b *= pColour.getBlue() / 256.0;
            return (a << 24) | (r << 16) | (g << 8) | b;
        }
    }
}
