package com.group2.handwritingrecognition;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;


//CREDIT TO:
//http://stackoverflow.com/questions/4552045/copy-bufferedimage-to-clipboard

class CopyImage implements Transferable{
	BufferedImage img;
	
	public CopyImage(BufferedImage arg_img){
		img = arg_img;
	}
	
	@Override
	public Object getTransferData(DataFlavor arg_f) throws UnsupportedFlavorException, IOException {
		if(img != null && arg_f.equals(DataFlavor.imageFlavor) ){
			return img;
		}else{
			throw new UnsupportedFlavorException( arg_f );
		}
	}

	@Override
	public DataFlavor[] getTransferDataFlavors(){
		DataFlavor[] flavors = new DataFlavor[1];
        flavors[ 0 ] = DataFlavor.imageFlavor;
        return flavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor arg_f){
		boolean supportsFlavor = false;
		DataFlavor[] allFlavors = getTransferDataFlavors();
        for ( int i = 0; i < allFlavors.length; i++ ) {
            if ( arg_f.equals(allFlavors[i])) {
            	supportsFlavor = true;
                break;
            }
        }
        return supportsFlavor;
	}
}