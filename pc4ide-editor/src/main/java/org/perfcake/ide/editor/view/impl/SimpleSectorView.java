/*
 *-----------------------------------------------------------------------------
 * pc4ide
 *
 * Copyright 2017 Jakub Knetl
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
 *-----------------------------------------------------------------------------
 */

package org.perfcake.ide.editor.view.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.perfcake.ide.editor.colors.NamedColor;
import org.perfcake.ide.editor.layout.LayoutData;
import org.perfcake.ide.editor.layout.RadiusData;
import org.perfcake.ide.editor.swing.icons.ResizableIcon;
import org.perfcake.ide.editor.utils.Utils2D;
import org.perfcake.ide.editor.view.Pair;
import org.perfcake.ide.editor.view.SectorView;

/**
 * Represents a view of the sector.
 *
 * @author jknetl
 */
public abstract class SimpleSectorView extends SectorView {

    /**
     * Space between header and additional text.
     */
    public static final int HEADER_BOTTOM_SPACE = 5;

    /**
     * padding between objects (e.g. header and icon)
     */
    public static final int PADDING = 10;

    protected int headerFontSize = 12;
    protected int additionalTextFontSize;

    protected ResizableIcon icon;
    protected String header;

    /**
     * creates new sector view.
     *
     * @param icon icon of the inspector in the sector
     */
    public SimpleSectorView(ResizableIcon icon) {
        this.icon = icon;
    }

    /* (non-Javadoc)
     * @see org.perfcake.ide.editor.view.View#draw(java.awt.Graphics)
     */
    @Override
    public void draw(Graphics2D g2d) {

        // if we have no layout data set, then we cannot draw
        if (layoutData == null) {
            return;
        }

        // antialiasing of the shapes
        final Map<Object, Object> hints = new HashMap<>();
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        hints.put(RenderingHints.KEY_TEXT_LCD_CONTRAST, 100);
        g2d.addRenderingHints(hints);

        // draw shape of the sector
        final Area boundArea = drawBounds();

        // draw the icon
        drawIcon(g2d);

        final Stroke defaultStorke = g2d.getStroke();
        if (isSelected()) {
            final Stroke selectedStroke = new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            g2d.setStroke(selectedStroke);
        }

        g2d.setColor(colorScheme.getColor(NamedColor.BASE_5));
        g2d.draw(boundArea);
        g2d.setStroke(defaultStorke);

        drawText(g2d);

    }

    private void drawIcon(Graphics2D g2d) {
        if (icon != null) {
            Rectangle2D iconBounds = getIconBounds(layoutData);

            Color iconColor = getIconColor();
            icon.setColor(iconColor);
            // we may pass null as inspector since our icon implementation completely ignores this argument
            icon.paintIcon(null, g2d, (int) iconBounds.getX(), (int) iconBounds.getY());
        }
    }

    protected Rectangle2D getIconBounds(LayoutData layoutData) {

        double iconCenterX = layoutData.getCenter().getX()
                + (layoutData.getRadiusData().getInnerRadius() + PADDING + icon.getIconWidth() / 2);
        double iconCenterY = layoutData.getCenter().getY();

        double theta = Utils2D.getMiddleAngle(layoutData.getAngularData());
        Point2D location = Utils2D.rotatePoint(new Point2D.Double(iconCenterX, iconCenterY), theta, layoutData.getCenter());

        Rectangle2D iconBounds = Utils2D.getUpperLeftCorner(location, icon.getIconWidth(), icon.getIconHeight());

        return iconBounds;
    }

    protected void drawText(Graphics2D g2d) {
        Font defaultFont = g2d.getFont();
        final Point2D startOuterArcPoint = getStartOuterArcPoint(layoutData);
        final Point2D endOuterArcPoint = getEndOuterArcPoint(layoutData);
        final Point2D chordCenter = getChordCenter(startOuterArcPoint, endOuterArcPoint);

        final AffineTransform defaultTransform = g2d.getTransform();
        final FontRenderContext frc = g2d.getFontRenderContext();
        final Font headerFont = getHeaderFont(g2d);
        final Rectangle2D headerBounds = headerFont.getStringBounds(header, frc);

        Rectangle2D textDimension = computeTextBounds(g2d, layoutData.getWidth());

        double chordDistanceFromOuterRadius = getChordDistanceFromOuterRadius(layoutData.getCenter(),
                chordCenter,
                layoutData.getRadiusData().getOuterRadius());

        final double textInnerBound = layoutData.getCenter().getX()
                + layoutData.getRadiusData().getInnerRadius()
                + 2 * PADDING
                + icon.getIconWidth();
        final double textOuterBound = layoutData.getCenter().getX()
                + layoutData.getRadiusData().getOuterRadius()
                - chordDistanceFromOuterRadius
                - PADDING;
        final double maximumWidthForText = textOuterBound - textInnerBound;

        //do not compute text position from inside, but rather place it near to the outer radius:
        double textCenterX =
                layoutData.getCenter().getX()
                        + layoutData.getRadiusData().getOuterRadius()
                        - chordDistanceFromOuterRadius
                        - PADDING
                        - (textDimension.getWidth() / 2);

        Point2D textCenter = new Point2D.Double(
                textCenterX,
                layoutData.getCenter().getY()
        );

        double theta = Utils2D.getMiddleAngle(layoutData.getAngularData());
        Point2D rotatedCenter = Utils2D.rotatePoint(textCenter, theta, layoutData.getCenter());

        FontMetrics headerMetrics = g2d.getFontMetrics(headerFont);
        Rectangle2D textRectangle = Utils2D.getUpperLeftCorner(rotatedCenter, textDimension.getWidth(), textDimension.getHeight());

        // if the angle is between 90 and 270, then we need to rotate the text by 180 degrees, otherwise it would be upside down
        if (theta > 90 && theta < 270) {
            theta += 180;
        }
        g2d.rotate(Math.toRadians(theta), rotatedCenter.getX(), rotatedCenter.getY());

        double y = textRectangle.getY() + 1 * headerMetrics.getAscent();

        g2d.setColor(colorScheme.getColor(NamedColor.BASE_6));
        // render header
        g2d.setFont(headerFont);
        String renderedHeader = Utils2D.computeRenderedPart(header, headerMetrics, maximumWidthForText);
        g2d.drawString(renderedHeader, (float) textRectangle.getX(), (float) y);

        y += HEADER_BOTTOM_SPACE;


        // render additional text
        List<Pair> additionalData = getAdditionalData();
        Font additionalTextFont = getAdditionalTextFont(g2d);
        FontMetrics additionalTextMetrics = g2d.getFontMetrics(additionalTextFont);
        g2d.setFont(additionalTextFont);
        g2d.setColor(colorScheme.getColor(NamedColor.BASE_4));
        for (Pair p : additionalData) {
            y += additionalTextMetrics.getHeight();
            String renderedPair = Utils2D.computeRenderedPart(p.getKey() + ": " + p.getValue(), headerMetrics, maximumWidthForText);
            g2d.drawString(renderedPair, (float) (textRectangle.getX()), (float) y);
        }
        g2d.setTransform(defaultTransform);
        g2d.setFont(defaultFont);
    }

    private Rectangle2D computeTextBounds(Graphics2D g2d, double maximumWidth) {
        final FontRenderContext frc = g2d.getFontRenderContext();
        final Font font = g2d.getFont();
        final Rectangle2D headerBounds = font.getStringBounds(header, frc);

        FontMetrics metrics = g2d.getFontMetrics(font);

        List<Pair> additionalText = getAdditionalData();

        int additionalLines = 0;
        int longestLineWidth = 0;
        for (Pair pair : additionalText) {
            int width = metrics.stringWidth(pair.getKey() + ": " + pair.getValue());
            if (width > longestLineWidth) {
                longestLineWidth = width;
            }
            additionalLines += (1 + width / maximumWidth);
        }

        int additionalHeight = additionalLines * metrics.getHeight() + metrics.getAscent();

        double totalWidth = Math.max(headerBounds.getWidth(), longestLineWidth);
        double totalHeight = headerBounds.getHeight() + HEADER_BOTTOM_SPACE + additionalHeight;

        return new Rectangle2D.Double(0, 0, totalWidth, totalHeight);
    }

    @Override
    public LayoutData getMinimumSize(LayoutData constraint, Graphics2D g2d) {

        if (constraint.getRadiusData() == null
                || constraint.getRadiusData().getInnerRadius() == 0
                || constraint.getRadiusData().getOuterRadius() == 0) {
            // it has no point to compute anything if we dont know radius
            return constraint;
        }

        Rectangle2D iconBounds = getIconBounds(constraint);

        // we assume that width constraint is positive and large enough!
        // it should be ensured by sufficient minimum size on editor as a whole
        double textMaximumWidth = computeTextMaximumWidth(constraint.getRadiusData(), Utils2D.getRectangleDiagonal(iconBounds));
        Rectangle2D textBounds = computeTextBounds(g2d, textMaximumWidth);


        Double minimumAngluarExtentForIcon = getMinimumAngle(iconBounds, constraint.getRadiusData().getInnerRadius());
        double distanceOfObject = constraint.getRadiusData().getInnerRadius() + 2 * PADDING + iconBounds.getWidth();
        Double minimumAngluarExtentForText = getMinimumAngle(textBounds, distanceOfObject);
        LayoutData minimumSize = new LayoutData(constraint);
        minimumSize.getAngularData().setAngleExtent(Math.min(minimumAngluarExtentForIcon, minimumAngluarExtentForText));
        return minimumSize;
    }

    /**
     * Compoutes text maximum width.
     *
     * @param radiusData   radius data
     * @param iconDiagonal diagonal length of icon
     * @return maximum possible width of text.
     */
    private double computeTextMaximumWidth(RadiusData radiusData, double iconDiagonal) {
        return radiusData.getOuterRadius() // outer radius
                - radiusData.getInnerRadius()   // minus inner radius
                - PADDING // minus space between inner radius and icon
                - iconDiagonal // minus icon diagonal
                - PADDING // minus space between icon and text
                - PADDING;
    }

    /**
     * Computes minimum angle required so that object will fit within the radius from the given distance.
     *
     * @param object           object
     * @param distanceOfObject distance of object
     * @return minimum angle
     */
    private Double getMinimumAngle(Rectangle2D object, double distanceOfObject) {

        // We cannot use width nor height of the object since we don't know from which direction we want to enclose the object
        // therefore we will use half of diagonal ( it effectively computes required distance of circle which encloses the object)
        double diagonal = Utils2D.getRectangleDiagonal(object);

        return Math.toDegrees(2 * Math.atan((diagonal / 2) / distanceOfObject));
    }

    /**
     * @return List of additional pairs of key value, which this view will draw into surface along with header.
     */
    protected abstract List<Pair> getAdditionalData();

    /**
     * Get font for header.
     * @param g2d graphics context
     * @return Font for the header
     */
    protected Font getHeaderFont(Graphics2D g2d) {
        Font f = g2d.getFont();
        f = f.deriveFont(Font.BOLD, headerFontSize);
        return f;
    }

    /**
     * Get font for additional text.
     * @param g2d graphics context
     * @return Font for the additionalText
     */
    protected Font getAdditionalTextFont(Graphics2D g2d) {
        Font f = g2d.getFont();
        f = f.deriveFont(additionalTextFontSize);
        return f;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * @return Color of the icon.
     */
    protected abstract Color getIconColor();
}