package com.nuevapartida.images;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.apache.log4j.Logger;

import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;

public class ImageConversion {
	private static Logger logger = Logger.getLogger(ImageConversion.class);

	public static void resizeImage(File image, File destDir, String fileName, int size)
			throws Exception {
		BufferedImage bi = read(image);
		if (bi != null) {
			int w = 0, h = 0;
			// Don't resize small images
			if(bi.getHeight() >= size || bi.getWidth() >= size) {
				// Proportional
				if (bi.getHeight() >= bi.getWidth()) {
					h = size;
					w = (int) (((float) bi.getWidth() / (float) bi.getHeight()) * (float) size);
				} else {
					w = size;
					h = (int) (((float) bi.getHeight() / (float) bi.getWidth()) * (float) size);
				}
				resize(bi, w, h, new File(destDir, fileName + size + ".jpg"));

				// Square: crop image first
				if (bi.getHeight() != bi.getWidth()) {
					w = bi.getWidth();
					h = bi.getHeight();
					int s = Math.min(w, h);
					int c = (int) Math.abs(Math.round(((w - h) / 2.0)));
					BufferedImage cropped = new BufferedImage(s, s, bi.getType());
					Graphics2D g = cropped.createGraphics();
					g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
					g.setComposite(AlphaComposite.Src);
					g.drawImage(bi, 0, 0, s, s, (s == w ? 0 : c), (s == w ? c : 0), (s == w ? s : s + c), (s == w ? s + c : s), null);
			        g.dispose();
			        bi = cropped;
				}
				resize(bi, size, size, new File(destDir, fileName + size + "s.jpg"));
			}
			bi.flush();
		}
	}

	private static BufferedImage read(File image) {
		BufferedImage bi = null;
		try {
			BufferedImage tmp = ImageIO.read(image);
			if (tmp.getType() != BufferedImage.TYPE_3BYTE_BGR) {
				bi = new BufferedImage(tmp.getWidth(), tmp.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
				bi.getGraphics().drawImage(tmp, 0, 0, null);
			} else {
				bi = tmp;
			}
		} catch (Exception e) {
			logger.error("Error cargando " + image.getAbsolutePath());
		}
		return bi;
	}

	private static void resize(BufferedImage bi, int w, int h, File dest) throws Exception {
		ResampleOp ro = new ResampleOp(w, h);
		ro.setFilter(ResampleFilters.getLanczos3Filter());
		BufferedImage biscaled = ro.filter(bi, null);
		ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
		ImageOutputStream ios = ImageIO.createImageOutputStream(dest);
		writer.setOutput(ios);
		ImageWriteParam param = writer.getDefaultWriteParam();
		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(0.8f);
		writer.write(biscaled);
	}
}
