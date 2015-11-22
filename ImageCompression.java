package pkg;

/*
 * Author      : Rakesh Sharad Navale.
 * University  : University of SOuthern California : COmputer Science
 * Subject     : Multi-media Systems [CSCI 576]
 */

import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.image.*;

public class ImageCompression 
{
   @SuppressWarnings({ "resource", "unused" })
   public static void main(String[] args) 
   {
	   try
	   {
		   String fileName = args[0];						//################# Input Image file-path reading
		   int width = Integer.parseInt(args[1]);			//################# Input  image width
		   int height = Integer.parseInt(args[2]);			//################# Input  image height
		   int resamplingMethod = Integer.parseInt(args[3]);//################# Image Sampling method options
		   String outputFormat = args[4];					//################# Output-Format to display image
		   int w2 = 0 , h2 = 0;
		   
		   if(outputFormat.equals("O1"))
		   {	w2 = 1920;	h2 = 1080;	}
		   else if (outputFormat.equals("O2"))
		   {	w2 = 1280;	h2 = 720;	}
		   else if(outputFormat.equals("O3"))
		   {	w2 = 640;	h2 = 480;	}
		   else
		   {
			   System.out.println("Error!, Output format provided is not correct");
			   return;
		   }
	
		   BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		   BufferedImage img1 = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_RGB);
    
		   File file = new File(fileName);
		   InputStream is = new FileInputStream(file);

		   long len = file.length();
		   byte[] bytes = new byte[(int)len];	    
		   int[] pixels = new int[width*height];
		   int offset = 0, numRead = 0, ind = 0;;
		   while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) 
			   offset += numRead;

		   for(int y = 0; y < height; y++)
		   {
			   for(int x = 0; x < width; x++)
			   {		 
				   byte a = 0;
				   byte r = bytes[ind];
				   byte g = bytes[ind+height*width];
				   byte b = bytes[ind+height*width*2]; 
				
				   int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
				   pixels[ind] = pix;
				   img.setRGB(x,y,pix);
				   ind++;
			   }
		   }
		
//------------------------ DOWN SAMPLING ------------------------------------
		   if(width > 1920 && height >1080)
		   {
			   if(resamplingMethod==1)
			   {
				   img1 = downSampling1(img,width, height, w2,h2);
			   }
			   else if(resamplingMethod==2)
			   {
				   int finalpix[];
				   finalpix = downSampling2(pixels,width, height, w2,h2);
	    			    		
				   ind = 0;
				   for(int y = 0; y < h2; y++)
				   {
					   for(int x = 0; x < w2; x++)
					   {		 
						   img1.setRGB(x,y,finalpix[ind]);
						   ind++;
					   }
				   }	    
			   }
			   else
			   {
				   System.out.println("Error!, Method option provided is not correct");
				   return;
			   }
		   }
//------------------------ UP SAMPLING ------------------------------------
		   else if(width < 640 && height < 480)
		   {
			   if(resamplingMethod==1)
			   {
				   img1 = upSampling1(img,width, height, w2,h2);
			   }
			   else if(resamplingMethod==2)
			   {
				   int finalpix[];
				   finalpix = upSampling2(pixels,width, height, w2,h2);
				   ind = 0;
				   for(int y = 0; y < h2; y++)
				   {
					   for(int x = 0; x < w2; x++)
					   {		 
						   img1.setRGB(x,y,finalpix[ind]);
						   ind++;
					   }
				   }	    		
			   }
			   else
			   {
				   System.out.println("Error!, Method option provided is not correct");
				   return;
			   }
		   }		
		   
//------------------------ DISPLAY IMAGE ------------------------------------

		   // Use a panel and label to display the image
		   JPanel  panel = new JPanel ();
		   panel.add (new JLabel (new ImageIcon (img1)));
		   JScrollPane scroller = new JScrollPane(panel);

		   JFrame frame = new JFrame("Display images");
		   panel.setPreferredSize(new Dimension(w2, h2));
		   frame.getContentPane().add (scroller, BorderLayout.CENTER);
		   frame.pack();
		   frame.setVisible(true);
		   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	   } 
	   catch (FileNotFoundException e) 
	   {	e.printStackTrace();	} 
	   catch (ArrayIndexOutOfBoundsException e) 
	   {	e.printStackTrace();	}
	   catch (IOException e) 
	   {	e.printStackTrace();	}
   }
 //###################################################################################################################

	private static BufferedImage downSampling1(BufferedImage img, int width, int height, int w2, int h2) 
	{
	    BufferedImage img2 = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_RGB);
		float x_ratio = ((float)(width)/w2);
		float y_ratio = ((float)(height)/h2);
		for(int y = 0; y < h2; y++)
		{
			for(int x = 0; x < w2; x++)
			{
				img2.setRGB(x,y,img.getRGB((int)(x_ratio*x), (int)(y_ratio*y)));
			}
		}
		return img2;
	}
//###################################################################################################################

	private static int[] downSampling2(int[] pixels , int width, int height, int w2, int h2) 
	{
		int[] temp = new int[w2*h2];
	    int x, y, index,offset = 0  ;
	    float x_ratio = ((float)(width-1))/w2 ;
	    float y_ratio = ((float)(height-1))/h2 ;
	    float  blue = 0, red=0, green=0 ;
        int win[] = new int[ (int) (Math.ceil(x_ratio*y_ratio)) - 1];

        for (int i=0;i<h2;i++) 
	    {
	        for (int j=0;j<w2;j++) 
	        {
	        	int  count=0;
	        	x = (int)(x_ratio * j);
	            y = (int)(y_ratio * i);
	            index = (y*width+x);
	            while(count < (int)x_ratio* (int)y_ratio)
	            {
	            	for(int k = 0; k < (int)x_ratio; k++)
	            	{
	            			win[count++] = pixels[index++];
	            	}
	            	index = index + width - (int)x_ratio;//// bound check
	            }
	            count = 0;
	            blue = (win[count]&0xff);
	            green = ((win[count]>>8)&0xff);
	            red = ((win[count]>>16)&0xff);
	            count++;
	            while(count< (int)x_ratio * (int)y_ratio)
	            {
	            	//blue element
	            	blue = (blue + (win[count]&0xff))/2;
		            // green element
	            	green = (green + ((win[count]>>8)&0xff))/2;
		            // red element
	            	red = (red+((win[count]>>16)&0xff))/2;
	            	count++;
	            }
	           	            
	            temp[offset++] = 
	                    0xff000000 | // hardcode alpha
	                    ((((int)red)<<16)&0xff0000) |
	                    ((((int)green)<<8)&0xff00) |
	                    ((int)blue) ;
	        }
	    }
        return temp;	
	}
//###################################################################################################################

	private static BufferedImage upSampling1(BufferedImage img, int width, int height, int w2, int h2) 
	{
		BufferedImage img2 = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_RGB);
		
		double x_ratio = (double)w2 / width;
	    double y_ratio = (double)h2 / height;
		
		for(int y = 0; y < h2; y++)
		{
			for(int x = 0; x < w2; x++)
			{
			    int m = (int) Math.min( Math.floor(x / x_ratio), width);
				int n = (int) (Math.min( Math.floor(y / y_ratio), height));
				img2.setRGB(x,y, img.getRGB(m,n));
			}
		}
		return img2;
	}
//###################################################################################################################

	private static int[] upSampling2(int[] pixels, int width, int height, int w2, int h2) 
	{
		int[] temp = new int[w2*h2] ;
		int a, b, c, d, x, y, index ;
		float x_ratio = ((float)(width-1))/w2 ;
		float y_ratio = ((float)(height-1))/h2 ;
		float x_diff, y_diff, blue, red, green ;
		int offset = 0 ;
		for (int i=0;i<h2;i++) 
		{
			for (int j=0;j<w2;j++) 
			{
				x = (int)(x_ratio * j);
		        y = (int)(y_ratio * i);
		        x_diff = (x_ratio * j) - x ;
		        y_diff = (y_ratio * i) - y ;
		        index = (y*width+x) ;                
		        a = pixels[index] ;
		        b = pixels[index+1] ;
		        c = pixels[index+width] ;
		        d = pixels[index+width+1] ;

		        // blue element
		        // Yb = Ab(1-w)(1-h) + Bb(w)(1-h) + Cb(h)(1-w) + Db(wh)
		        blue = (a&0xff)*(1-x_diff)*(1-y_diff) + (b&0xff)*(x_diff)*(1-y_diff) + (c&0xff)*(y_diff)*(1-x_diff)   + (d&0xff)*(x_diff*y_diff);

		        // green element
		        // Yg = Ag(1-w)(1-h) + Bg(w)(1-h) + Cg(h)(1-w) + Dg(wh)
		        green = ((a>>8)&0xff)*(1-x_diff)*(1-y_diff) + ((b>>8)&0xff)*(x_diff)*(1-y_diff) + ((c>>8)&0xff)*(y_diff)*(1-x_diff)   + ((d>>8)&0xff)*(x_diff*y_diff);

		        // red element
		        // Yr = Ar(1-w)(1-h) + Br(w)(1-h) + Cr(h)(1-w) + Dr(wh)
		        red = ((a>>16)&0xff)*(1-x_diff)*(1-y_diff) + ((b>>16)&0xff)*(x_diff)*(1-y_diff) +((c>>16)&0xff)*(y_diff)*(1-x_diff)   + ((d>>16)&0xff)*(x_diff*y_diff);

		        				// hardcode alpha
		        temp[offset++] =  0xff000000 | ((((int)red)<<16)&0xff0000) | ((((int)green)<<8)&0xff00) | ((int)blue) ;
			}
		}
		return temp;	
	}

/*###################################################################################################################
										THE END
###################################################################################################################*/
}
