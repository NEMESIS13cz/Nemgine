package com.nemezor.nemgine.graphics.util;

import java.util.ArrayList;

public class LoaderSegment {

	private String label;
	private ArrayList<LoaderSegment> subsegments = new ArrayList<LoaderSegment>();
	private int segIndex = 0;
	private int progress, maxProgress;
	
	public LoaderSegment(int maxProgress) {
		this.label = "";
		this.maxProgress = maxProgress;
	}
	
	public LoaderSegment(String label, int maxProgress) {
		this.label = label;
		this.maxProgress = maxProgress;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		if (subsegments.size() == 0) {
			this.label = label;
		}else{
			LoaderSegment segment = getSubsegment();
			if (segment == null) {
				return;
			}
			segment.setLabel(label);
		}
	}
	
	public LoaderSegment getSubsegment() {
		if (segIndex >= subsegments.size()) {
			return null;
		}
		return subsegments.get(segIndex);
	}
	
	public void addSubsegment(LoaderSegment subsegment) {
		this.subsegments.add(subsegment);
	}
	
	public boolean nextSubsegment() {
		segIndex++;
		if (segIndex == subsegments.size()) {
			return true;
		}
		return false;
	}
	
	public void stepProgress() {
		if (subsegments.size() == 0) {
			progress++;
		}else{
			LoaderSegment segment = getSubsegment();
			if (segment == null) {
				return;
			}
			segment.stepProgress();
		}
	}
	
	public float getProgress() {
		if (subsegments.size() == 0) {
			float perc = (float)progress / (float)maxProgress;
			return perc < 0 ? 0 : perc > 1 ? 1 : perc;
		}else{
			return (float)segIndex / (float)subsegments.size();
		}
	}
}
