package com.nemezor.nemgine.debug;

import com.nemezor.nemgine.misc.Color;

public class DebugColorizer {

	private int currColor = 0;
	private boolean reversed = false;
	
	public DebugColorizer(int start) {
		currColor = start;
	}
	
	public Color getNext(Color c) {
		int r = c.getRedAsInt();
		int g = c.getGreenAsInt();
		int b = c.getBlueAsInt();
		
		switch (currColor) {
		case 0: // red
			r += reversed ? -1 : 1;
			g = 0;
			b = 0;
			if (r > 255) {
				r--;
				reversed = true;
			}
			if (r == 0 && reversed) {
				reversed = false;
				currColor++;
			}
			break;
		case 1: // green
			r = 0;
			g += reversed ? -1 : 1;
			b = 0;
			if (g > 255) {
				g--;
				reversed = true;
			}
			if (g == 0 && reversed) {
				reversed = false;
				currColor++;
			}
			break;
		case 2: // blue
			r = 0;
			g = 0;
			b += reversed ? -1 : 1;
			if (b > 255) {
				b--;
				reversed = true;
			}
			if (b == 0 && reversed) {
				reversed = false;
				currColor++;
			}
			break;
		case 3: // magenta
			r += reversed ? -1 : 1;
			g = 0;
			b += reversed ? -1 : 1;
			if (r > 255) {
				r--;
				b--;
				reversed = true;
			}
			if (r == 0 && reversed) {
				reversed = false;
				currColor++;
			}
			break;
		case 4: // cyan
			r = 0;
			g += reversed ? -1 : 1;
			b += reversed ? -1 : 1;
			if (g > 255) {
				g--;
				b--;
				reversed = true;
			}
			if (g == 0 && reversed) {
				reversed = false;
				currColor++;
			}
			break;
		case 5: // yellow
			r += reversed ? -1 : 1;
			g += reversed ? -1 : 1;
			b = 0;
			if (r > 255) {
				r--;
				g--;
				reversed = true;
			}
			if (r == 0 && reversed) {
				reversed = false;
				currColor++;
			}
			break;
		case 6: // white
			r += reversed ? -1 : 1;
			g += reversed ? -1 : 1;
			b += reversed ? -1 : 1;
			if (r > 255) {
				r--;
				g--;
				b--;
				reversed = true;
			}
			if (r == 0 && reversed) {
				reversed = false;
				currColor++;
			}
			break;
		}
		
		if (currColor > 6) {
			currColor = 0;
		}
		
		return new Color(r, g, b);
	}
}
