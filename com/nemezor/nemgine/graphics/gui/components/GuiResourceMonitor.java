package com.nemezor.nemgine.graphics.gui.components;

import java.util.ArrayList;

import org.lwjgl.util.vector.Matrix4f;

import com.nemezor.nemgine.graphics.FontManager;
import com.nemezor.nemgine.graphics.GLHelper;
import com.nemezor.nemgine.graphics.ShaderManager;
import com.nemezor.nemgine.graphics.Tessellator;
import com.nemezor.nemgine.graphics.gui.Gui;
import com.nemezor.nemgine.graphics.gui.IGuiComponent;
import com.nemezor.nemgine.graphics.util.Display;
import com.nemezor.nemgine.misc.Anchors;
import com.nemezor.nemgine.misc.Color;
import com.nemezor.nemgine.misc.Data;
import com.nemezor.nemgine.misc.IGuiListener;
import com.nemezor.nemgine.misc.MouseButton;
import com.nemezor.nemgine.misc.Platform;
import com.nemezor.nemgine.misc.Registry;

public class GuiResourceMonitor implements IGuiComponent {

	private int left, right, top, bottom;
	private int x, y, width, height;
	private int fontId = FontManager.getDefaultFontID20();
	private Anchors anch = Anchors.TOP_LEFT;
	private IGuiListener listener;
	private boolean pressedLeft = false, pressedRight = false;
	private boolean hover = false;
	private boolean graph = true;
	private boolean text = true;
	private ArrayList<Float> cpuData;
	private ArrayList<Float> memData;
	private ArrayList<Float> swapData;
	private int cpuDataStr;
	private long memDataStr;
	private long swapDataStr;
	private long lastUpdate;
	private long lastStrUpdate;
	private boolean enabled = true;
	private Data scale = Data.MEBIBYTE;
	private boolean local = true;
	
	public GuiResourceMonitor(int x, int y, int width, int height, int rasterWidth, int rasterHeight, boolean local) {
		this(x, y, width, height, rasterWidth, rasterHeight);
		this.local = local;
	}
	
	public GuiResourceMonitor(int x, int y, int width, int height, int rasterWidth, int rasterHeight) {
		left = x;
		top = y;
		right = rasterWidth - (x + width);
		bottom = rasterHeight - (y + height);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		cpuData = new ArrayList<Float>();
		memData = new ArrayList<Float>();
		swapData = new ArrayList<Float>();
	}
	
	public void setDisplayMode(boolean textVisible, boolean graphVisible) {
		text = textVisible;
		graph = graphVisible;
		if (!text && !graph) {
			text = true;
		}
	}
	
	public void setFont(int id) {
		this.fontId = id;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public void setDataUnit(Data data) {
		if (data == null) {
			return;
		}
		this.scale = data;
	}
	
	@Override
	public void setListener(IGuiListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void render(Display window) {
		ShaderManager.bindShader(ShaderManager.getColorShaderID());
		ShaderManager.loadMatrix4(ShaderManager.getColorShaderID(), "transformation", window.getTransformationMatrix());
		ShaderManager.loadMatrix4(ShaderManager.getColorShaderID(), "projection", window.get2DOrthographicProjectionMatrix());
		if (!enabled) {
			ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", Gui.tertiaryColor.getColorAsVector());
		}else if (pressedLeft || pressedRight) {
			ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", Gui.primaryAccentColor.getColorAsVector());
		}else if (hover) {
			ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", Gui.primaryAccentColor.getColorAsVector());
		}else{
			ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", Gui.tertiaryColor.getColorAsVector());
		}
		
		Tessellator.start(Tessellator.QUADS);
		
		Tessellator.addVertex(x, y);
		Tessellator.addVertex(x + width, y);
		Tessellator.addVertex(x + width, y + height);
		Tessellator.addVertex(x, y + height);
		
		Tessellator.finish();

		if (graph) {
			GLHelper.enableBlending();
			ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", new Color(0xFF660040).getColorAsVector());
			Tessellator.start(Tessellator.LINES);
			
			for (int i = cpuData.size() - 1; i > -1; i--) {
				Tessellator.addVertex(x + i, y + height);
				Tessellator.addVertex(x + i, y + height - cpuData.get(i) * height);
			}
			
			Tessellator.finish();
			GLHelper.disableBlending();
			ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", new Color(0xFF6600FF).getColorAsVector());
			Tessellator.start(Tessellator.LINE_STRIP);
			
			for (int i = cpuData.size() - 1; i > -1; i--) {
				Tessellator.addVertex(x + i, y + height - cpuData.get(i) * height);
			}
			
			Tessellator.finish();
			GLHelper.enableBlending();
			ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", new Color(0x008AFF40).getColorAsVector());
			Tessellator.start(Tessellator.LINES);
			
			for (int i = memData.size() - 1; i > -1; i--) {
				Tessellator.addVertex(x + width / 2 + i, y + height);
				Tessellator.addVertex(x + width / 2 + i, y + height - memData.get(i) * height);
			}
			
			Tessellator.finish();
			GLHelper.disableBlending();
			ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", new Color(0x008AFFFF).getColorAsVector());
			Tessellator.start(Tessellator.LINE_STRIP);
			
			for (int i = memData.size() - 1; i > -1; i--) {
				Tessellator.addVertex(x + width / 2 + i, y + height - memData.get(i) * height);
			}
			
			Tessellator.finish();
			GLHelper.enableBlending();
			ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", new Color(0x15B30040).getColorAsVector());
			Tessellator.start(Tessellator.LINES);
			
			for (int i = swapData.size() - 1; i > -1; i--) {
				Tessellator.addVertex(x + width / 2 + i, y + height);
				Tessellator.addVertex(x + width / 2 + i, y + height - swapData.get(i) * height);
			}
			
			Tessellator.finish();
			GLHelper.disableBlending();
			ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", new Color(0x15B300FF).getColorAsVector());
			Tessellator.start(Tessellator.LINE_STRIP);
			
			for (int i = swapData.size() - 1; i > -1; i--) {
				Tessellator.addVertex(x + width / 2 + i, y + height - swapData.get(i) * height);
			}
			
			Tessellator.finish();
		}
		ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", Gui.quaternaryColor.getColorAsVector());

		Tessellator.start(Tessellator.LINES);
		Tessellator.addVertex(x + width / 2, y);
		Tessellator.addVertex(x + width / 2, y + height);
		Tessellator.finish();
		
		if (enabled && (pressedLeft || pressedRight)) {
			ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", Gui.secondaryAccentColor.getColorAsVector());
		}
		
		Tessellator.start(Tessellator.LINES);
		Tessellator.addVertex(x, y);
		Tessellator.addVertex(x + width, y);

		Tessellator.addVertex(x, y + height);
		Tessellator.addVertex(x + width, y + height);
		
		Tessellator.addVertex(x, y);
		Tessellator.addVertex(x, y + height);
		
		Tessellator.addVertex(x + width, y);
		Tessellator.addVertex(x + width, y + height);
		Tessellator.finish();
		ShaderManager.unbindShader();
		
		if (text) {
			FontManager.drawStringFromLeft(fontId, x + width / 2 - 4, y + FontManager.getFontHeight(fontId) + 4, cpuDataStr + "%", new Color(0xFF6600FF),  new Matrix4f(), window.get2DOrthographicProjectionMatrix(), 0, 0);
			FontManager.drawStringFromLeft(fontId, x + width - 4, y + FontManager.getFontHeight(fontId) + 4, swapDataStr + scale.suffix, new Color(0x15B300FF),  new Matrix4f(), window.get2DOrthographicProjectionMatrix(), 0, 0);
			FontManager.drawStringFromLeft(fontId, x + width - FontManager.getStringWidth(fontId, swapDataStr + scale.suffix + " ") - 4, y + FontManager.getFontHeight(fontId) + 4, memDataStr + scale.suffix, new Color(0x008AFFFF),  new Matrix4f(), window.get2DOrthographicProjectionMatrix(), 0, 0);
		}
	}

	@Override
	public void update(Display window, int mouseX, int mouseY, boolean leftButton, boolean rightButton) {
		if (mouseX > x + width || mouseX < x || mouseY < y || mouseY > y + height) {
			if (hover && listener != null) {
				listener.onExit();
			}
			pressedRight = false;
			pressedLeft = false;
			hover = false;
		}else{
			if (!hover && listener != null) {
				listener.onEnter();
			}
			hover = true;
			if (leftButton) {
				if (!pressedLeft && listener != null && enabled) {
					listener.onPressed(MouseButton.LEFT);
				}
				pressedLeft = true;
			}else{
				if (pressedLeft && listener != null && enabled) {
					listener.onReleased(MouseButton.LEFT);
				}
				pressedLeft = false;
			}
			if (rightButton) {
				if (!pressedRight && listener != null && enabled) {
					listener.onPressed(MouseButton.RIGHT);
				}
				pressedRight = true;
			}else{
				if (pressedRight && listener != null && enabled) {
					listener.onReleased(MouseButton.RIGHT);
				}
				pressedRight = false;
			}
		}
		
		if (local) {
			if (lastUpdate + Registry.PLATFORM_RESOURCES_POLL < System.currentTimeMillis()) {
				updateUsages();
				lastUpdate = System.currentTimeMillis();
			}
		}
	}

	@Override
	public void resize(Display window) {
		switch (anch) {
		case BOTTOM_LEFT_RIGHT:
			width = window.getWidth() - right - left;
		case BOTTOM:
		case BOTTOM_LEFT:
			y = window.getHeight() - bottom - height;
			break;
		case BOTTOM_RIGHT:
			y = window.getHeight() - bottom - height;
			x = window.getWidth() - right - width;
			break;
		case LEFT_RIGHT:
			width = window.getWidth() - right - left;
			break;
		case LEFT_RIGHT_TOP_BOTTOM:
			width = window.getWidth() - right - left;
			height = window.getHeight() - bottom - top;
			break;
		case RIGHT_TOP_BOTTOM:
			x = window.getWidth() - right - width;
		case LEFT_TOP_BOTTOM:
		case TOP_BOTTOM:
			height = window.getHeight() - bottom - top;
			break;
		case RIGHT:
		case TOP_RIGHT:
			x = window.getHeight() - right - width;
			break;
		case TOP_LEFT_RIGHT:
			width = window.getWidth() - right - left;
			break;
		default:
			break;
		}
	}
	
	@Override
	public void anchor(Anchors anchors) {
		this.anch = anchors;
	}
	
	private void updateUsages() {
		for (int i = cpuData.size() - 1, j = 0; i > -1; i--, j++) {
			if (j >= width / 2) {
				cpuData.remove(i);
				memData.remove(i);
				swapData.remove(i);
				i--;
			}
		}
		cpuData.add((float)Platform.getCPUUsage());
		swapData.add((float)Platform.getUsedSwapMemory() / (float)Platform.getTotalSwapMemory());
		memData.add((float)Platform.getUsedPhysicalMemory() / (float)Platform.getTotalPhysicalMemory());
		
		if (lastStrUpdate == 4) {
			lastStrUpdate = 0;
			
			cpuDataStr = (int)(Platform.getCPUUsage() * 100);
			swapDataStr = Platform.getUsedSwapMemory() / scale.amount;
			memDataStr = Platform.getUsedPhysicalMemory() / scale.amount;
		}
		lastStrUpdate++;
	}
	
	public void updateUsages(double cpu, int ram, int ramInstalled, int swap, int swapInstalled) {
		if (!local) {
			return;
		}
		for (int i = cpuData.size() - 1, j = 0; i > -1; i--, j++) {
			if (j >= width / 2) {
				cpuData.remove(i);
				memData.remove(i);
				swapData.remove(i);
				i--;
			}
		}
		cpuData.add((float)cpu);
		swapData.add((float)swap / (float)swapInstalled);
		memData.add((float)ram / (float)ramInstalled);
		
		if (lastStrUpdate == 4) {
			lastStrUpdate = 0;
			
			cpuDataStr = (int)(cpu * 100);
			swapDataStr = swap / scale.amount;
			memDataStr = ram / scale.amount;
		}
		lastStrUpdate++;
	}
}
