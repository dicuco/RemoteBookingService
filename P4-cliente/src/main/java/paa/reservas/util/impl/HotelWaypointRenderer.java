package paa.reservas.util.impl;

/*
 * WaypointRenderer.java
 *
 * Created on March 30, 2006, 5:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.WaypointRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;


/**
 * A fancy waypoint painter
 *
 * @author Martin Steiger
 * @author Daniel Berjón
 */
public class HotelWaypointRenderer implements WaypointRenderer<HotelWaypoint> {

	final static int pickerWidth = 5;
	final static int pickerHeight = 10;
	final static int padding = 5;

	public HotelWaypointRenderer() {
		super();
	}

	private static Color availabilityColor(int available, int total) {
		Color c;
		final float availabilityRatio = (float) available / (float) total;
		if (0.5 < availabilityRatio) {
			c = Color.GREEN;
		} else if (0.2 < availabilityRatio) {
			c = Color.ORANGE;
		} else {
			c = Color.RED;
		}
		return c;
	}

	private static String colorToHTML(Color c) {
		return String.format("#%06X", 0xFFFFFF & c.getRGB());
	}

	private static JLabel makeLabel(HotelWaypoint w) {
		final int singleAvl = w.getAvailableSingleRooms();
		final int singleTot = w.getTotalSingleRooms();
		final String sColor = colorToHTML(availabilityColor(singleAvl, singleTot));
		final int doubleAvl = w.getAvailableDoubleRooms();
		final int doubleTot = w.getTotalDoubleRooms();
		final String dColor = colorToHTML(availabilityColor(doubleAvl, doubleTot));

		// <div style='text-align:center'><div style='display:inline-block;text-align:left'>
		String labelText = String.format("<html><body>" +
						"<p>%s</p>" +
						"<p style='text-align:center' color='orange'>%s</p>" +
						"<p>S: <span color='%s'>%d/%d</span><br>D: <span color='%s'>%d/%d</span></p>" +
						"</body></html>",
				w.getName(), "\u2605".repeat(w.getStars()), sColor, singleAvl, singleTot, dColor, doubleAvl, doubleTot);
		JLabel label = new JLabel(labelText);
		label.setSize(label.getPreferredSize());
		return label;
	}

	private static Image makeLabelImage(JLabel label) {
		BufferedImage bi = new BufferedImage(
				label.getWidth(),
				label.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		label.paint(bi.createGraphics());
		return bi;
	}

	private static RoundRectangle2D makeRectangle(JLabel label, Point2D locationPixel) {
		RoundRectangle2D rr = new RoundRectangle2D.Float();
		int x = (int) locationPixel.getX();
		int y = (int) locationPixel.getY();
		rr.setRoundRect(x - label.getWidth() / 2 - padding, y - (label.getHeight() + 2 * padding) - pickerHeight, label.getWidth() + 2 * padding, label.getHeight() + 2 * padding, 2 * padding, 2 * padding);
		return rr;
	}

	private static Polygon makePicker(Point2D locationPixel) {
		Polygon picker = new Polygon();
		int x = (int) locationPixel.getX();
		int y = (int) locationPixel.getY();
		picker.addPoint(x, y);
		picker.addPoint(x + pickerWidth / 2, y - pickerHeight);
		picker.addPoint(x - pickerWidth / 2, y - pickerHeight);
		return picker;
	}

	@Override
	public void paintWaypoint(Graphics2D g, JXMapViewer viewer, HotelWaypoint w) {
		g = (Graphics2D) g.create();

		Point2D point = viewer.getTileFactory().geoToPixel(w.getPosition(), viewer.getZoom());

		int x = (int) point.getX();
		int y = (int) point.getY();

		JLabel label = makeLabel(w);

		RoundRectangle2D rectangle = makeRectangle(label, point);
		g.setColor(Color.WHITE);
		g.fill(rectangle);
		g.setColor(Color.BLACK);
		g.draw(rectangle);

		g.drawImage(makeLabelImage(label), x - label.getWidth() / 2, y - label.getHeight() - pickerHeight - padding, null);

		Polygon picker = makePicker(point);
		g.setBackground(Color.BLACK);
		g.drawPolygon(picker);
		g.fillPolygon(picker);

		g.dispose();
	}

	// Esto sirve para determinar si hemos clicado dentro, eliminar para los estudiantes
	public static boolean isMouseInside(JXMapViewer viewer, HotelWaypoint wp, Point2D mousePoint) {
		Point2D parkingPx = viewer.getTileFactory().geoToPixel(wp.getPosition(), viewer.getZoom());
		JLabel label = makeLabel(wp);
		RoundRectangle2D rr = makeRectangle(label, parkingPx);
		return rr.contains(mousePoint);
	}
}
