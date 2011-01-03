/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ndsr.chart;

import java.awt.Paint;
import org.jfree.chart.renderer.category.StackedBarRenderer3D;

/**
 *
 * @author lkufel
 */
public class ColorRenderer extends StackedBarRenderer3D {

    /** The colors. */
    private Paint[] colors;

    /**
     * Creates a new renderer.
     *
     * @param colors  the colors.
     */
    public ColorRenderer(final Paint[] colors) {
        this.colors = colors;
    }

    /**
     * Returns the paint for an item.  Overrides the default behaviour inherited from
     * AbstractSeriesRenderer.
     *
     * @param row  the series.
     * @param column  the category.
     *
     * @return The item color.
     */
    @Override
    public Paint getItemPaint(final int row, final int column) {
        return this.colors[column % this.colors.length];
    }
}
