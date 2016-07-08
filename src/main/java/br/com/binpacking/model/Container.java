package br.com.binpacking.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.binpacking.restrictions.Restrictions;

public class Container {

	private Restrictions restrictions;
	private Measures measures;
	private List<Item> items;
	private List<Point> entryPoints;

	private Measures measuresFake;
	private Double volumeFake;

	public Container(Restrictions restrictions) {
		this.restrictions = restrictions;
		this.measures = new Measures();
		this.measuresFake = new Measures();
		this.items = new ArrayList<Item>();
		this.entryPoints = new ArrayList<Point>();
		this.entryPoints.add(new Point(new Line(0D, restrictions.getMaxWidth()), 
				new Line(0D, restrictions.getMaxLength()),
				new Line(0D, restrictions.getMaxHeight())));
	}

	public boolean add(Item item) {
		Integer index = getBestEntryPoint(item);
		if (index == null) {
			return false;
		}

		add(item, entryPoints.get(index).clone());
		Point reference = entryPoints.get(index).clone();

		entryPoints.remove(index.intValue());
		createEntryPoints(item, reference);
		Collections.sort(entryPoints);
		updateEntryPoints();

		return true;
	}

	private Integer getBestEntryPoint(Item item) {
		this.volumeFake = 0D;
		Integer index = calculateBestEntryPoint(item);

		item.switchWidthLength();
		Integer i = calculateBestEntryPoint(item);
		if (i == null) {
			item.switchWidthLength();
		} else {
			index = i;
		}

		return index;
	}

	private Integer calculateBestEntryPoint(Item item) {
		Integer index = null;

		for (int i = 0; i < entryPoints.size(); i++) {
			Point point = entryPoints.get(i);

			if (item.fits(point)) {
				updateMeasuresFake(item, point);
				if (!restrictions.isMaxRestrictionsViolated(this)) {
					if (measuresFake.getVolume() < this.volumeFake || this.volumeFake == 0) {
						this.volumeFake = measuresFake.getVolume();
						index = i;
					}
				}
			}
		}

		return index;
	}

	private void updateMeasuresFake(Item item, Point point) {
		if (point.getWidth().getBegin() + item.getWidth() > measures.getWidth()) {
			measuresFake.setWidth(point.getWidth().getBegin() + item.getWidth());
		} else {
			measuresFake.setWidth(measures.getWidth());
		}

		if (point.getLength().getBegin() + item.getLength() > measures.getLength()) {
			measuresFake.setLength(point.getLength().getBegin() + item.getLength());
		} else {
			measuresFake.setLength(measures.getLength());
		}

		if (point.getHeight().getBegin() + item.getHeight() > measures.getHeight()) {
			measuresFake.setHeight(point.getHeight().getBegin() + item.getHeight());
		} else {
			measuresFake.setHeight(measures.getHeight());
		}
	}

	private void add(Item item, Point point) {
		point.getWidth().setEnd(point.getWidth().getBegin() + item.getWidth());
		point.getLength().setEnd(point.getLength().getBegin() + item.getLength());
		point.getHeight().setEnd(point.getHeight().getBegin() + item.getHeight());

		item.setPoint(point);
		items.add(item);
		updateContainerSize(point);
	}

	private void updateContainerSize(Point point) {
		if (point.getWidth().getEnd() > measures.getWidth()) {
			measures.setWidth(point.getWidth().getEnd());
		}

		if (point.getLength().getEnd() > measures.getLength()) {
			measures.setLength(point.getLength().getEnd());
		}

		if (point.getHeight().getEnd() > measures.getHeight()) {
			measures.setHeight(point.getHeight().getEnd());
		}
	}

	private void createEntryPoints(Item item, Point reference) {
		if (reference.isInBaseOfContainer()) {
			createPointsInBaseOfContainer(item);
		} else {
			createPointsAboveBaseOfContainer(item, reference);
		}
		updatePointLineHeight(item);
	}

	private void createPointsInBaseOfContainer(Item item) {
		Line w = new Line(item.getPoint().getWidth().getEnd(), restrictions.getMaxWidth());
		Line l = new Line(item.getPoint().getLength().getBegin(), restrictions.getMaxLength());
		Line h = new Line(item.getPoint().getHeight().getBegin(), restrictions.getMaxHeight());
		Point point = new Point(w, l, h);
		entryPoints.add(point);

		w = new Line(item.getPoint().getWidth().getBegin(), restrictions.getMaxWidth());
		l = new Line(item.getPoint().getLength().getEnd(), restrictions.getMaxLength());
		h = new Line(item.getPoint().getHeight().getBegin(), restrictions.getMaxHeight());
		point = new Point(w, l, h);
		entryPoints.add(point);
	}

	private void createPointsAboveBaseOfContainer(Item item, Point reference) {
		Line w = new Line(item.getPoint().getWidth().getEnd(), reference.getWidth().getEnd());
		Line l = new Line(item.getPoint().getLength().getBegin(), reference.getLength().getEnd());
		Line h = new Line(item.getPoint().getHeight().getBegin(), restrictions.getMaxHeight());
		Point point = new Point(w, l, h);
		entryPoints.add(point);

		w = new Line(item.getPoint().getWidth().getBegin(), reference.getWidth().getEnd());
		l = new Line(item.getPoint().getLength().getEnd(), reference.getLength().getEnd());
		h = new Line(item.getPoint().getHeight().getBegin(), restrictions.getMaxHeight());
		point = new Point(w, l, h);
		entryPoints.add(point);
	}

	private void updatePointLineHeight(Item item) {
		Line w = new Line(item.getPoint().getWidth().getBegin(), item.getPoint().getWidth().getEnd());
		Line l = new Line(item.getPoint().getLength().getBegin(), item.getPoint().getLength().getEnd());
		Line h = new Line(item.getPoint().getHeight().getEnd(), restrictions.getMaxHeight());
		Point point = new Point(w, l, h);
		entryPoints.add(point);
	}

	private void updateEntryPoints() {
		for (Point entryPoint : entryPoints) {
			for (Item item : items) {
				if (entryPoint.isInBaseOfContainer() || entryPoint.isSameHeight(item)) {
					updateEntryPoints(entryPoint, item);
				}
			}
		}
	}

	private void updateEntryPoints(Point entryPoint, Item item) {
		if (item.getPoint().isIntersectLineWidth(entryPoint)) {
			entryPoint.getWidth().setEnd(item.getPoint().getWidth().getBegin());
		}
		if (item.getPoint().isIntersectLineLength(entryPoint)) {
			entryPoint.getLength().setEnd(item.getPoint().getLength().getBegin());
		}
	}

	public Measures getMeasures() {
		return this.measures;
	}

	public Measures getUpdatedMeasures() {
		return this.measuresFake;
	}

	public List<Item> getItems() {
		return this.items;
	}

	public List<Point> getEntryPoints() {
		return this.entryPoints;
	}

	public Restrictions getRestrictions() {
		return this.restrictions;
	}

	@Override
	public String toString() {
		return items.size() + " items \n" + "Volume: " + measures.getVolume() + "\n" + "Measures: [W: "
				+ measures.getWidth() + ", L: " + measures.getLength() + ", H: " + measures.getHeight() + "]\n";
	}

}