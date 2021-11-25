/*
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.github.caciocavallosilano.cacio.ctc;

import com.github.caciocavallosilano.cacio.peer.managed.FullScreenWindowFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.geom.AffineTransform;

public class CTCGraphicsConfiguration extends GraphicsConfiguration {
    private static final int numconfigs = BufferedImage.TYPE_BYTE_BINARY;
    private static CTCGraphicsConfiguration[] standardConfigs =
        new CTCGraphicsConfiguration[numconfigs];
    private static CTCGraphicsConfiguration[] scaledConfigs =
        new CTCGraphicsConfiguration[numconfigs];
    

    public static CTCGraphicsConfiguration getConfig(BufferedImage bImg) {
        return getConfig(bImg, 1, 1);
    }

    public static CTCGraphicsConfiguration getConfig(BufferedImage bImg,
                                                        double scaleX,
                                                        double scaleY)
    {
        CTCGraphicsConfiguration ret;
        int type = bImg.getType();

        CTCGraphicsConfiguration[] configs = (scaleX == 1 && scaleY == 1)
                ? standardConfigs : scaledConfigs;

        if (type > 0 && type < numconfigs) {
            ret = configs[type];
            if (ret != null && ret.scaleX == scaleX && ret.scaleY == scaleY) {
                return ret;
            }
        }
        ret = new CTCGraphicsConfiguration(bImg, scaleX, scaleY);
        if (type > 0 && type < numconfigs) {
            configs[type] = ret;
        }
        return ret;
    } 

    private CTCGraphicsDevice device;
    private final ColorModel model;
    private final Raster raster;
    private final double scaleX;
    private final double scaleY;

    CTCGraphicsConfiguration(BufferedImage bImg, double scaleX, double scaleY) {
        this.model  = bImg.getColorModel();
        this.raster = bImg.getRaster().createCompatibleWritableRaster(1, 1);
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    CTCGraphicsConfiguration(CTCGraphicsDevice dev) {
        device = dev;
        BufferedImage bufImg = new BufferedImage(FullScreenWindowFactory.getScreenDimension().width, FullScreenWindowFactory.getScreenDimension().height, BufferedImage.TYPE_INT_ARGB);
        this.model  = bufImg.getColorModel();
        this.raster = bufImg.getRaster().createCompatibleWritableRaster(1, 1);
        this.scaleX = 1;
        this.scaleY = 1;
    }

    @Override
    public BufferedImage createCompatibleImage(int width, int height) {
        WritableRaster wr = raster.createCompatibleWritableRaster(width, height);
        return new BufferedImage(model, wr, model.isAlphaPremultiplied(), null);
    }

    @Override
    public GraphicsDevice getDevice() {
        return device;
    }

    @Override
    public ColorModel getColorModel() {
        return ColorModel.getRGBdefault();
    }

    @Override
    public ColorModel getColorModel(int transparency) {
        return ColorModel.getRGBdefault();
    }

    @Override
    public AffineTransform getDefaultTransform() {
        return AffineTransform.getScaleInstance(scaleX, scaleY);
    }

    @Override
    public AffineTransform getNormalizingTransform() {
        return new AffineTransform();
    }

    @Override
    public Rectangle getBounds() {
        Dimension d = FullScreenWindowFactory.getScreenDimension();
        return new Rectangle(0, 0, d.width, d.height);
    }

}
