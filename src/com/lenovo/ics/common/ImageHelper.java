package com.lenovo.ics.common;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

public class ImageHelper {
	/**
	 * 等比缩放
	 * @param source
	 * @param targetW
	 * @param targetH
	 * @param equalProportion
	 * @return
	 */
	public static BufferedImage resize(BufferedImage source,int targetW,int targetH,boolean equalProportion){  
        int type=source.getType();  
        BufferedImage target=null;  
        double sx=(double)targetW/source.getWidth();  
        double sy=(double)targetH/source.getHeight();  
        //这里想实现在targetW，targetH范围内实现等比例的缩放  
          //如果不需要等比例的缩放则下面的if else语句注释调即可  
        if(equalProportion){  
            if(sx>sy){  
                sx=sy;  
                targetW=(int)(sx*source.getWidth());  
            }else{  
                sy=sx;  
                targetH=(int)(sx*source.getHeight());  
            }  
        }  
        if(type==BufferedImage.TYPE_CUSTOM){  
            ColorModel cm=source.getColorModel();  
            WritableRaster raster=cm.createCompatibleWritableRaster(targetW,targetH);  
            boolean alphaPremultiplied=cm.isAlphaPremultiplied();  
            target=new BufferedImage(cm,raster,alphaPremultiplied,null);  
        }else{  
            target=new BufferedImage(targetW,targetH,type);  
            Graphics2D g=target.createGraphics();  
            g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);  
            g.drawRenderedImage(source,AffineTransform.getScaleInstance(sx,sy));  
            g.dispose();  
        }  
        return target;  
    } 
	
	/*
	public static void main(String argv[]) {
		try {
			byte[] bytes = toByteArray(new File("d:/test/dist/AHWS/AHWS-002.jpg"));
			// 参数1(from),参数2(to),参数3(宽),参数4(高)
			InputStream in = new ByteArrayInputStream(bytes);
			BufferedImage srcImage = ImageIO.read(in);
			BufferedImage bi = ImageHelper.resize(srcImage, 260, 110,false);
//			ImageHelper.scaleImage("d:/temp/006.png", "d:/temp/008.png", "png", 100, 100);
			
			File saveFile = new File("d:/test/dist/AHWS/AHWS-01_re.jpg");
			
			ImageIO.write(bi,"jpg",saveFile);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static byte[] toByteArray(File file) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		OutputStream stream = new BufferedOutputStream(buffer);
		FileInputStream fis = new FileInputStream(file);
		byte[] buff = new byte[8192];
		int len = -1;
		while ((len = fis.read(buff)) != -1) {
			stream.write(buff, 0, len);
		}
		fis.close();
		stream.flush();
		stream.close();
		return buffer.toByteArray();
	}
	*/
}