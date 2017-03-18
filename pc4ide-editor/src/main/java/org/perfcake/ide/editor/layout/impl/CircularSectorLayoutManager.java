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

package org.perfcake.ide.editor.layout.impl;

import java.awt.Graphics2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import org.perfcake.ide.editor.comparator.ViewComparator;
import org.perfcake.ide.editor.layout.AbstractLayoutManager;
import org.perfcake.ide.editor.layout.LayoutData;
import org.perfcake.ide.editor.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CircularSectorLayoutManager manages the layout of graphical part of PerfCake editor.
 */
public class CircularSectorLayoutManager extends AbstractLayoutManager {

    static final Logger logger = LoggerFactory.getLogger(CircularSectorLayoutManager.class);

    private Comparator<View> viewComparator = new ViewComparator();

    private boolean fill;

    /**
     * Creates new CircularSectorLayoutManager.
     */
    public CircularSectorLayoutManager() {
        this(false);
    }

    /**
     * Creates new CircularSectorLayoutManager.
     *
     * @param fillExtent should layout manager fill whole angle extent provided?
     */
    public CircularSectorLayoutManager(boolean fillExtent) {
        this.fill = fillExtent;
    }

    @Override
    public void layout(Graphics2D g2d) {

        Map<View, Double> extentMap = new HashMap<>();

        double requestedExtentByChildren = 0.0;

        for (View child : children) {
            double extent = child.getMinimumSize(constraints, g2d).getAngularData().getAngleExtent();
            extentMap.put(child, extent);
            requestedExtentByChildren += extent;
        }

        double startAngle = constraints.getAngularData().getStartAngle();

        double angleExtentConstraint = constraints.getAngularData().getAngleExtent();
        if (requestedExtentByChildren <= angleExtentConstraint) {
            for (View v : children) {
                final LayoutData data = new LayoutData(constraints);
                Double angleExtent = extentMap.get(v);

                if (fill) {
                    double percentage = angleExtent / requestedExtentByChildren;
                    angleExtent = percentage * angleExtentConstraint;
                }
                data.getAngularData().setAngleExtent(angleExtent);
                data.getAngularData().setStartAngle(startAngle);

                v.setLayoutData(data);

                // move startAgle by angular extent of view v
                startAngle += angleExtent;
            }
        } else {
            // TODO: inspector requested larger angular extent than is available
            // we need to somehow implement shirnking of some views!
            logger.warn("Too large angular extent. Requested: {}, Provided: {}",
                    requestedExtentByChildren,
                    constraints.getAngularData().getAngleExtent());
        }
    }

    /*
     * This layout manager computes required angular data based on radius data constraint.
     */
    @Override
    public LayoutData getMinimumSize(LayoutData constraint, Graphics2D g2d) {
        double requiredExtent = 0;

        if (children == null) {
            return constraint;
        }
        for (View child : children) {
            requiredExtent += child.getMinimumSize(constraint, g2d).getAngularData().getAngleExtent();
        }

        LayoutData requiredData = new LayoutData(constraint);
        requiredData.getAngularData().setAngleExtent(requiredExtent);

        return requiredData;
    }

    @Override
    public void add(View component) {
        super.add(component);
        Collections.sort(children, viewComparator);
    }
}