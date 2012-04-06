import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;

//This is intended to use for digit recognizing.
//We give an input image and we downnsample it so the input size is smaller
//and the calculations are faster
public class PatternRecognizer implements ImageObserver{

	int[] pixelMap;	//pixel map of the image
	Image img;	//image
	int width;	//width of image
	int height;	//height of image
	int downLeft;	//used to crop the white spaces of image
	int downRight;	//used to crop the white spaces of image
	int downTop;	//used to crop the white spaces of image
	int downBottom;	//used to crop the white spaces of image
	
	double ratioX;
	double ratioY;
	
	public PatternRecognizer(Image img){
		this.img=img;		
		width=img.getWidth(this);
		height=img.getHeight(this);
		
	}

	//get the downsampled image.
	public double[] getPattern(int w,int h){
		return downSample(w, h);
	}
	
	//go through the given horizontal line and find out whether it is a completely white spaced line
	//if so return true 
	private boolean hLineClear(int y)
	{
		int w = img.getWidth(this);
		for ( int i=0;i<w;i++ ) {
			if ( pixelMap[(y*w)+i] !=-1 )
				return false;
		}
		return true;
	} 
	//go through the given vertical line and find out whether it is a completely white spaced line.
	private boolean vLineClear(int x)
	{
		int h = img.getHeight(this);
		for ( int i=0;i<h;i++ ) {
			if ( pixelMap[(x*h)+i] !=-1 )
				return false;
		}
		return true;
	} 

	//set boundaries to crop the image.
	private void setBounds(){
		downBottom=height;
		downTop=0;
		downLeft=0;
		downRight=width;
		
		while(hLineClear(downBottom)){
			downBottom--;
		}
		while(hLineClear(downTop)){
			downTop++;
		}
		while(vLineClear(downLeft)){
			downLeft++;
		}
		while(vLineClear(downRight)){
			downRight--;
		}
		
	}
	
	//downsampling algorithm
	public double[] downSample(int w,int h)
	{
		setBounds();
		double[] result=new double[w*h];
		PixelGrabber pixGrab=new PixelGrabber(img, 0, 0, w, h, true);
		try {
			pixGrab.grabPixels();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pixelMap=(int[]) pixGrab.getPixels();
		
		ratioX=(double) (downRight-downLeft)*1.0/w;
		ratioY=(double) (downBottom-downTop)*1.0/h;
		
		int index = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				result[index++]=downSampleRegion(x, y);
			}
		}
		
		return result;
	} 
	
	private double downSampleRegion(int x,int y){
		int startX=(int)(downLeft+x*ratioX);
		int startY=(int)(downTop+y*ratioY);
		int endX=(int) (startX+(x*ratioX));
		int endY=(int) (startY+(y*ratioX));
		
		int redTotal = 0;
		int greenTotal = 0;
		int blueTotal = 0;

		int total = 0;
		
		for (int yy = startY; yy <= endY; yy++) {
			for (int xx = startX; xx <= endX; xx++) {
				final int loc = xx + (yy * width);
				int pixel = this.pixelMap[loc];
				int red = (pixel >> 16) & 0xff;
				int green = (pixel >> 8) & 0xff;
				int blue = (pixel) & 0xff;

				redTotal += red;
				greenTotal += green;
				blueTotal += blue;
				total++;

				if (this.pixelMap[loc] != -1) {
					return 1.0;
				}
			}
		}
		
		return (redTotal+greenTotal+blueTotal)/(total*3);
	}
	@Override
	public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3,
			int arg4, int arg5) {
		// TODO Auto-generated method stub
		return false;
	}
}
