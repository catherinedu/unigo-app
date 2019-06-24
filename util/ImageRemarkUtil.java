package com.xjt.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.swing.ImageIcon;

/**
 * 图片水印工具类 
 * @author Administrator
 *
 */
public class ImageRemarkUtil {

	// 水印透明度
    private static float alpha = 0.7f;
    // 水印横向位置
    private static int positionWidth = 512;
    // 水印纵向位置
    private static int positionHeight = 336;
    // 水印文字字体
    private static Font font = new Font("宋体", Font.BOLD, 72);
    // 水印文字颜色
    private static Color color = Color.red;

    /**
     * 
     * @param alpha
     *            水印透明度
     * @param positionWidth
     *            水印横向位置
     * @param positionHeight
     *            水印纵向位置
     * @param font
     *            水印文字字体
     * @param color
     *            水印文字颜色
     */
    public static void setImageMarkOptions(float alpha, int positionWidth,
            int positionHeight, Font font, Color color) {
        if (alpha != 0.0f)
            ImageRemarkUtil.alpha = alpha;
        if (positionWidth != 0)
            ImageRemarkUtil.positionWidth = positionWidth;
        if (positionHeight != 0)
            ImageRemarkUtil.positionHeight = positionHeight;
        if (font != null)
            ImageRemarkUtil.font = font;
        if (color != null)
            ImageRemarkUtil.color = color;
    }

    /**
     * 给图片添加水印图片
     * 
     * @param iconPath
     *            水印图片路径
     * @param srcImgPath
     *            源图片路径
     * @param targerPath
     *            目标图片路径
     */
    public static void markImageByIcon(String iconPath, String srcImgPath, String targerPath) {
        markImageByIcon(iconPath, srcImgPath, targerPath, null);
    }

    /**
     * 给图片添加水印图片、可设置水印图片旋转角度
     * 
     * @param iconPath
     *            水印图片路径
     * @param srcImgPath
     *            源图片路径
     * @param targerPath
     *            目标图片路径
     * @param degree
     *            水印图片旋转角度
     */
    public static void markImageByIcon(String iconPath, String srcImgPath,
            String targerPath, Integer degree) {
        OutputStream os = null;
        try {
        	
            Image srcImg = ImageIO.read(new File(srcImgPath));
            
            BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null),
                    srcImg.getHeight(null), BufferedImage.TYPE_INT_RGB);

            // 1、得到画笔对象
            Graphics2D g = buffImg.createGraphics();

            // 2、设置对线段的锯齿状边缘处理
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null),srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0,
                    null);
            // 3、设置水印旋转
            if (null != degree) {
                g.rotate(Math.toRadians(degree),(double) buffImg.getWidth() / 2,(double) buffImg.getHeight() / 2);
            }

            // 4、水印图片的路径 水印图片一般为gif或者png的，这样可设置透明度
            ImageIcon imgIcon = new ImageIcon(iconPath);

            // 5、得到Image对象。
            Image img = imgIcon.getImage();

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,alpha));

            // 6、水印图片的位置
            g.drawImage(img, positionWidth, positionHeight, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            // 7、释放资源
            g.dispose();

            // 8、生成图片
            os = new FileOutputStream(targerPath);
            ImageIO.write(buffImg, "JPG", os);

            System.out.println("图片完成添加水印图片");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 给图片添加水印文字
     * 
     * @param logoText
     *            水印文字
     * @param srcImgPath
     *            源图片路径
     * @param targerPath
     *            目标图片路径
     */
    public static void markImageByText(String logoText, String srcImgPath, String targerPath) {
        markImageByText(logoText, srcImgPath, targerPath, null);
    }

    /**
     * 给图片添加水印文字、可设置水印文字的旋转角度
     * 
     * @param logoText
     * @param srcImgPath
     * @param targerPath
     * @param degree
     */
    public static void markImageByText(String logoText, String srcImgPath,
            String targerPath, Integer degree) {

        InputStream is = null;
        OutputStream os = null;
        try {
            // 1、源图片
            Image srcImg = ImageIO.read(new File(srcImgPath));
            BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null),
                    srcImg.getHeight(null), BufferedImage.TYPE_INT_RGB);

            // 2、得到画笔对象
            Graphics2D g = buffImg.createGraphics();
            // 3、设置对线段的锯齿状边缘处理
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(
                    srcImg.getScaledInstance(srcImg.getWidth(null),
                            srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0,
                    null);
            // 4、设置水印旋转
            if (null != degree) {
                g.rotate(Math.toRadians(degree),
                        (double) buffImg.getWidth() / 2,
                        (double) buffImg.getHeight() / 2);
            }
            // 5、设置水印文字颜色
            g.setColor(color);
            // 6、设置水印文字Font
            g.setFont(font);
            // 7、设置水印文字透明度
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
                    alpha));
            // 8、第一参数->设置的内容，后面两个参数->文字在图片上的坐标位置(x,y)
            g.drawString(logoText, positionWidth, positionHeight);
            // 9、释放资源
            g.dispose();
            // 10、生成图片
            os = new FileOutputStream(targerPath);
            ImageIO.write(buffImg, "JPG", os);

            System.out.println("图片完成添加水印文字");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != is)
                    is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (null != os)
                    os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 给图片添加水印 水印位于右下角  同比缩放水印大小
     * @param iconPath    水印图片地址
     * @param srcImgPath  添加水印的图片地址
     * @param targerPath  保存地址
     * @param degree      旋转角度
     */
    public static  void rateMarkImageByIcon(String iconPath, String comIconPath,String srcImgPath, String targerPath,Integer degree) {
        OutputStream os = null;
        try {
        	Integer iconWidth=null;  //水印的宽度
        	Integer iconHeight=null; //水印的高度
        	
            Image srcImg = ImageIO.read(new File(srcImgPath));//读取底图图片
            
            Integer positionWidth=srcImg.getWidth(null);		//获取底图图片宽度
            Integer positionHeight=srcImg.getHeight(null);		//获取底图图片高度
            
            if (positionWidth<250) {		//宽度小于250的图片不加水印
				return;
			}else  {
				iconWidth= (int)(positionWidth*0.2f);//根据底图计算水印的宽度    5分1       
	            iconHeight=(int)(positionWidth*0.2f*0.25f);//水印宽度为高度的四分1
	            
	            ImageCompressUtil.resizePng(new File(iconPath), new File(comIconPath), iconWidth, iconHeight,false);

	            positionWidth=(int) (positionWidth-(positionWidth*0.30));  // 计算水印摆放的宽度位置
	            positionHeight=(int) (positionHeight-(positionHeight*0.15)); //计算水印摆放的高度位置
	            
	            System.out.println("iconPath"+iconPath);
	            System.out.println("comIconPath="+comIconPath);
			}
          
           
            BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null),
                    srcImg.getHeight(null), BufferedImage.TYPE_INT_RGB);

            // 1、得到画笔对象
            Graphics2D g = buffImg.createGraphics();

            // 2、设置对线段的锯齿状边缘处理
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null),srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0,
                    null);
            // 3、设置水印旋转
            if (null != degree) {
                g.rotate(Math.toRadians(degree),(double) buffImg.getWidth() / 2,(double) buffImg.getHeight() / 2);
            }
            
            // 4、水印图片的路径 水印图片一般为gif或者png的，这样可设置透明度
            ImageIcon imgIcon = new ImageIcon(comIconPath);
            System.out.println("imgIcon.getIconWidth()="+imgIcon.getIconWidth());
            System.out.print("imgIcon.getIconHeight()="+imgIcon.getIconHeight());
            // 5、得到Image对象。
            Image img = imgIcon.getImage();

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,alpha));

            // 6、水印图片的位置
            g.drawImage(img, positionWidth, positionHeight, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            // 7、释放资源
            g.dispose();

            // 8、生成图片
            os = new FileOutputStream(targerPath);
            ImageIO.write(buffImg, "JPG", os);

            System.out.println("图片完成添加水印图片");
            
            new File(comIconPath).delete();//删除压缩的图片
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
    * 图片水印
    * @param pressimg 水印图片
    * @param targetimg 目标图片
    * @param location 位置：1、左上角，2、右上角，3、左下角，4、右下角，5、正中间
    * @param alpha 透明度
    */
    public static void pressImage(String pressimg, String targetimg, int location, float alpha) {
	    try {
		    //读取目标图片
		    File img = new File(targetimg);
		    Image src = ImageIO.read(img);
		    int width = src.getWidth(null);
		    int height = src.getHeight(null);
		    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		    Graphics2D g = image.createGraphics();
		    g.drawImage(src, 0, 0, width, height, null);
		    //水印文件
		    Image src_biao = ImageIO.read(new File(pressimg));
		    int width_biao = src_biao.getWidth(null);
		    int height_biao = src_biao.getHeight(null);
		    //如果水印图片高或者宽大于目标图片是做的处理,使其水印宽或高等于目标图片的宽高，并且等比例缩放
		    int new_width_biao = width_biao;
		    int new_height_biao = height_biao;
		    if(width_biao > width){
		    	new_width_biao = width;
		    	new_height_biao = (int) ((double)new_width_biao/width_biao*height);
		    }
		    if(new_height_biao > height){
		    	new_height_biao = height;
		    	new_width_biao = (int) ((double)new_height_biao/height_biao*new_width_biao);
		    }
		    //根据位置参数确定坐标位置
		    int x = 0;
		    int y = 0;
		    switch(location){
		    	case 1:
		    		break;
		    	case 2:
		    		x = width - new_width_biao;
		    		break;
		    	case 3:
		    		y = height - new_height_biao;
		    		break;
		    	case 4:
		    		x = width - new_width_biao;
		    		y = height - new_height_biao;
		    		break;
		    	case 5:
		    		x = (width - new_width_biao)/2;
		    		y = (height- new_height_biao)/2;
		    		break;
		    	default:
		    		break;
		    }
		    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
		    g.drawImage(src_biao, x, y, new_width_biao, new_height_biao, null);
		    //水印文件结束
		    g.dispose();
		    ImageIO.write( image, "png", img);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
    }

    
    public static void main(String[] args) {
    	 String srcImgPath = "e:/psb.jpg";
         String logoText = "复 印 无 效";
         String iconPath = "e:/6.png";

         String targerIconPath="e:/psb.jpg";
         
         // 给图片添加水印图片
         markImageByIcon(iconPath, srcImgPath, targerIconPath);
         
	}

}
