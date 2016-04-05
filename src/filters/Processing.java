package filters;


import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_BYTE_INDEXED;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.sound.midi.SysexMessage;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Borys
 */
public class Processing {
    private final int id;
    private int getR(int in) {
	   return (int)((in << 8) >> 24) & 0xff;
    }
   private int getG(int in) {
	   return (int)((in << 16) >> 24) & 0xff;
    }
   private int getB(int in) {
	   return (int)((in << 24) >> 24) & 0xff;
   }
   private int toRGB(int r,int g,int b) {
	   return (int)((((r << 8)|g) << 8)|b);
   }
   public Processing(int identifier){
       this.id=identifier;
   }
   protected BufferedImage changeBrightness(int value, BufferedImage image){
        System.out.println("Brightness function");
//        BufferedImage out=new BufferedImage(image.getWidth(),image.getHeight(),TYPE_BYTE_INDEXED);
        BufferedImage out=image;
        int width = image.getWidth(), height = image.getHeight(),p=0,r=0,g=0,b=0;
        for (int x=0; x<width; x++){
            for (int y=0; y<height;y++){
                p = image.getRGB(x, y);
                r = getR(p);
                r+=value;
                g = getG(p);
                g+=value;
                b = getB(p);
                b+=value;
                if(r>255) r=255;
                if(r<0) r=0;
                if(g>255) g=255;
                if(g<0) g=0;
                if(b>255) b=255;
                if(b<0) b=0;
                out.setRGB(x, y, toRGB(r,g,b)); 
            }
//            System.out.println("Working on X = "+x);
        }
        System.out.println(value);
        return out;
   }
   protected BufferedImage inversion(BufferedImage image){
        System.out.println("Inversion function");
//        BufferedImage out=new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_ARGB);
        BufferedImage out=image;
        int width = image.getWidth(), height = image.getHeight(),p=0,r=0,g=0,b=0;
        for (int x=0; x<width; x++){
            for (int y=0; y<height;y++){
                p = image.getRGB(x, y);
                r = getR(p);
                r=Math.abs(255-r);
                g = getG(p);
                g=Math.abs(255-g);
                b = getB(p);
                b=Math.abs(255-b);
                out.setRGB(x, y, toRGB(r,g,b)); 
            }
//            System.out.println("Working on X = "+x);
        }
        return out;
   }
   protected BufferedImage changeContrast(int value, BufferedImage image){
        System.out.println("Contrast function");
//        BufferedImage out=new BufferedImage(image.getWidth(),image.getHeight(),TYPE_BYTE_INDEXED);
        BufferedImage out=image;
        int width = image.getWidth(), height = image.getHeight(),p=0,r=0,g=0,b=0;
        double val = value, factor = (259 * (val + 255)) / (255 * (259 - val));
//        System.out.println("Factor 1: "+factor);
        for (int x=0; x<width; x++){
            for (int y=0; y<height;y++){
                p = image.getRGB(x, y);
                r = getR(p);
                g = getG(p);
                b = getB(p);
                
                r=(int) Math.round((factor*(r-128)+128));
                g=(int)Math.round((factor*(g-128)+128));
                b=(int)Math.round((factor*(b-128)+128));
                
//                if(r>=128) r+=value;
//                else r-=value;
//                if(g>=128) g+=value;
//                else g-=value;
//                if(b>=128) b+=value;
//                else b-=value;
                
                if(r>255) r=255;
                if(r<0) r=0;
                if(g>255) g=255;
                if(g<0) g=0;
                if(b>255) b=255;
                if(b<0) b=0;
                out.setRGB(x, y, toRGB(r,g,b)); 
            }
        }
        System.out.println(value);
        return out;
   }
   protected BufferedImage changeGamma(double value, BufferedImage image){
       System.out.println("Gamma correction");
        BufferedImage out=new BufferedImage(image.getWidth(),image.getHeight(),TYPE_BYTE_INDEXED);
        int width = image.getWidth(), height = image.getHeight(),p=0,r=0,g=0,b=0;
        Hashtable<Integer,Integer> map = new Hashtable<Integer,Integer>();
        double pixel=0,tmp=0;
        while(pixel<=255){
            int pix;
            pix=(int)(255*Math.pow(pixel/255, value));
            map.put((int)pixel, pix);
            pixel++;
        }
        for (int x=0; x<width; x++){
            for (int y=0; y<height;y++){
                p = image.getRGB(x, y);
                r = getR(p);
                g = getG(p);
                b = getB(p);
//                System.out.println(b);
                r=map.get(r);
                g=map.get(g);
                b=map.get(b);
                out.setRGB(x, y, toRGB(r,g,b)); 
            }
//            System.out.println("Working on X = "+x);
        }
        System.out.println(value);
        return out;
   }
   protected BufferedImage getInitial(BufferedImage image){
        BufferedImage out=new BufferedImage(image.getWidth(),image.getHeight(),TYPE_BYTE_INDEXED);
        int width = image.getWidth(), height = image.getHeight(),p=0,r=0,g=0,b=0;
        for (int x=0; x<width; x++){
            for (int y=0; y<height;y++){
                p = image.getRGB(x, y);
                r = getR(p);
                g = getG(p);
                b = getB(p);
                out.setRGB(x, y, toRGB(r,g,b)); 
            }
        }
        return out;
   }

    protected BufferedImage filterng(int offset, int divisor,int dimy,int dimx, int[][] blur,BufferedImage image,BufferedImage original) {
        //BufferedImage out=new BufferedImage(image.getWidth(),image.getHeight(),TYPE_BYTE_INDEXED);
        BufferedImage out=original;
        int width = image.getWidth(), height = image.getHeight(),p=0,r=0,g=0,b=0,r1=0,g1=0,b1=0;
        for (int x=dimx/2; x<width-dimx/2; x++){
            for (int y=dimy/2; y<height-dimy/2;y++){
                p = image.getRGB(x, y);
                r = getR(p);
                g = getG(p);
                b = getB(p);
                int k=(dimy/2)*(-1); //WRITE PROPER FORMULA HERE!
                for(int i=0;i<dimy;i++){
                    int l=(dimx/2)*(-1);
                    for(int j=0;j<dimx;j++){
                        p=image.getRGB(x+l, y+k);
                        r1+=getR(p)*blur[i][j];
                        g1+=getG(p)*blur[i][j];
                        b1+=getB(p)*blur[i][j];
                        l++;
                    }
                    k++;
                }
                r=offset+r1/divisor;
                //System.out.println(r+" - "+ r1);
                g=offset+g1/divisor;
                b=offset+b1/divisor;
                if(r>255) r=255;
                if(r<0) r=0;
                if(g>255) g=255;
                if(g<0) g=0;
                if(b>255) b=255;
                if(b<0) b=0;
                out.setRGB(x, y, toRGB(r,g,b)); 
                r1=0;
                g1=0;
                b1=0;
            }
        }
        return out;
    }
    //K - Means algorithm! 
    protected BufferedImage kMean(int k, BufferedImage image, BufferedImage original){
        BufferedImage out=original;
        int width = image.getWidth(), height = image.getHeight(),p=0,r=0,g=0,b=0,r1=0,g1=0,b1=0;
        boolean changing = true;
        ArrayList<ArrayList<ArrayList<Integer>>> main = new ArrayList<ArrayList<ArrayList<Integer>>>();
        ArrayList<ArrayList<Integer>> initialClassification = new ArrayList<ArrayList<Integer>>();
        //Choosing random points
        ArrayList<ArrayList<Integer>> colors = new ArrayList<ArrayList<Integer>>(k);
        for(int i=0;i<k;i++){
            Random rand = new Random();
            int coordX=0, coordY=0;
            ArrayList<Integer> coords = new ArrayList<Integer>();
            coordX = rand.nextInt(width);
            coordY = rand.nextInt(height);
//            System.out.println("Random point number "+(i+1)+" X - "+coordX+" Y - "+coordY);
            coords.add(coordX);
            coords.add(coordY);
            colors.add(coords);
            
            p = image.getRGB(coordX, coordY);
            r = getR(p);
            g = getG(p);
            b = getB(p);
            ArrayList<Integer> cl = new ArrayList<Integer>();
            cl.add(r);
            cl.add(g);
            cl.add(b);
            initialClassification.add(cl);
            
            ArrayList<ArrayList<Integer>> singleClass = new ArrayList<ArrayList<Integer>>();
            main.add(singleClass);
        }
//..................................
        int h = 0;
        while(changing){
            h++;
            //Distriuting pixels through classes....
            for (int x=0; x<width; x++){    
                for (int y=0; y<height;y++){
                    ArrayList<Integer> coords = new ArrayList<Integer>();
                    int c = 0;
                    p = image.getRGB(x, y);
                    c = calculateDist(p,initialClassification);
                    coords.add(x);
                    coords.add(y);
                    main.get(c).add(coords);
                }
            }
            //Calculate average to all classes and set those as new centroids.
            ArrayList<ArrayList<Integer>> newCentroids = new ArrayList<>(k);
            for(int i=0;i<main.size();i++){
                ArrayList<Integer> centr = new ArrayList<Integer>();
                int rAv=0, gAv=0, bAv=0;
                int size = main.get(i).size();
                for(int j=0;j<size;j++){
                    p = image.getRGB(main.get(i).get(j).get(0), main.get(i).get(j).get(1));
                    rAv += getR(p);
                    gAv += getG(p);
                    bAv += getB(p);
                }
                rAv = rAv/size;
                gAv = gAv/size;
                bAv = bAv/size;
                centr.add(rAv);
                centr.add(gAv);
                centr.add(bAv);
                
                newCentroids.add(centr);
            }
            changing=compareCentroids(initialClassification,newCentroids);
            initialClassification = newCentroids;
        }
        for(int i=0;i<main.size();i++){
            int size = main.get(i).size();
            for(int j=0;j<size;j++){
                int x=main.get(i).get(j).get(0),y=main.get(i).get(j).get(1);
                out.setRGB(x, y, toRGB(initialClassification.get(i).get(0),initialClassification.get(i).get(1),initialClassification.get(i).get(2)));   
            }
        }
        
        
        return out;
    }

    private int calculateDist(int p, ArrayList<ArrayList<Integer>> initialClassification) {
        int r = getR(p), g = getG(p), b = getB(p), c;
        ArrayList<Double> distances = new ArrayList<>(initialClassification.size());
        double d = 0, min=0;
        for(int i=0;i<initialClassification.size();i++){
            int d1=initialClassification.get(i).get(0)-r,
                d2=initialClassification.get(i).get(1)-g,
                d3=initialClassification.get(i).get(2)-b;
            
            d1 = (int) Math.pow(d1, 2);
            d2 = (int) Math.pow(d2, 2);
            d3 = (int) Math.pow(d3, 2);
            
            d=d1+d2+d3;
            d=Math.sqrt(d);
            distances.add(d);
        }
        min = distances.get(0);
        c=0;
        for(int i=0;i<initialClassification.size();i++){
            double dist = distances.get(i);
            if(dist<min) {
                min = dist;
                c=i;
            }
        }
        return c; 
    }
    private boolean compareCentroids(ArrayList<ArrayList<Integer>> old, ArrayList<ArrayList<Integer>> notOld ){
        boolean result = true;
        int count =0;
        for(int i=0;i<old.size();i++){
            for(int j=0;j<old.size();j++){
                if(old.get(i).get(0)==notOld.get(j).get(0)||old.get(i).get(1)==notOld.get(j).get(1)||old.get(i).get(2)==notOld.get(j).get(2)) 
                count++;
            }
        }
        System.out.println("It is - "+count+" Should be - "+ old.size());
        if(count==old.size())
            result = false;
        return result;
    }
    //Dithering......................
    protected BufferedImage randomDithering(int k, BufferedImage image, BufferedImage original){
        BufferedImage out=original;
        int width = image.getWidth(), height = image.getHeight(),p=0,r=0,g=0,b=0,r1=0,g1=0,b1=0;
        for (int x=0; x<width; x++){    
            for (int y=0; y<height;y++){
                int pix = 0, ran = 0;
                Random rand = new Random();
                p = image.getRGB(x,y);
                r = getR(p);
                g = getG(p);
                b = getB(p);
                pix = (int)(0.299*r+0.587*g + 0.114*b);
                ArrayList<Integer> randomArr = new ArrayList<Integer>();
                for(int j=0;j<k/2;j++){
                    ran = rand.nextInt(256);
                    randomArr.add(ran);
                }
                /*
                int step=256/k;
                int initial = 0, num=0;
                boolean assigned = false;
                for(int l=0;l<k-1;l++){
                    ran = rand.nextInt(256);
                    if(ran>pix){
                        pix=initial;
                        assigned=true;
                        break;
                    }
                    initial+=step;
                    if(l%2==0) num++;
                }
                if(!assigned) pix = 255;*/
                ran = rand.nextInt(256);
                if(ran>pix){
                    pix = 0;
                }
                else pix=255;
                
                out.setRGB(x, y, toRGB(pix,pix,pix)); 
            }
        }
        return out;
    }
}
