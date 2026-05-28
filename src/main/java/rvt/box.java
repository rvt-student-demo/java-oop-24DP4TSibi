package rvt;

import java.util.ArrayList;
import java.util.List;

public class box implements Packable {
	private double capacity;
	private List<Packable> contents;

	public box(double capacity) {
		this.capacity = capacity;
		this.contents = new ArrayList<>();
	}

	public void add(Packable item) {
		if (item == this) {
			return; 
			
		}
		if (this.weight() + item.weight() <= this.capacity) {
			this.contents.add(item);
		}
	}

	@Override
	public double weight() {
		double weight = 0;
		for (Packable p : this.contents) {
			weight += p.weight();
		}
		return weight;
	}

	@Override
	public String toString() {
		return "Box: " + this.contents.size() + " items, total weight " + this.weight() + " kg";
	}
}
