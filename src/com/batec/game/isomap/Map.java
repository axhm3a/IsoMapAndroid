package com.batec.game.isomap;

import android.graphics.Bitmap;

public class Map {
	public Bitmap heightMap;
	public Bitmap objectMap;
	public byte[][] heightArray;
	public byte[][] objectArray;
	public int sizeX;
	public int sizeY;
	
	public Map(Bitmap hMap, Bitmap objMap){
		heightMap = hMap;
		objectMap = objMap;
		
		sizeX = heightMap.getWidth();
		sizeY = heightMap.getHeight();
		
		heightArray = new byte[sizeX][sizeY];
		objectArray = new byte[sizeX][sizeY];
		
		for(int x = 0; x < heightMap.getWidth(); x++){
			for(int y = 0; y < heightMap.getHeight(); y++){
				heightArray[x][y] = (byte)(heightMap.getPixel(x, y) & 0xFF);				
			}
		}
		
		for(int x = 0; x < objectMap.getWidth(); x++){
			for(int y = 0; y < objectMap.getHeight(); y++){
				objectArray[x][y] = (byte)(objectMap.getPixel(x, y) & 0xFF);				
			}
		}
		
	}
	
	public int getHeight(int x, int y){
		
		if(x < 0 || x >= sizeX || y < 0 || y >= sizeX){
			return 0;
		}
		else{
			return heightArray[x][y];
			//return (byte)(heightMap.getPixel(x, y) & 0xFF);
		}
	}
	
	public int getObject(int x, int y){
		if(x < 0 || x >= sizeX || y < 0 || y >= sizeX){
			return 0;
		}
		else{
			//return (byte)(objectMap.getPixel(x, y) & 0xFF);
			return objectArray[x][y];
		}
	}
}
