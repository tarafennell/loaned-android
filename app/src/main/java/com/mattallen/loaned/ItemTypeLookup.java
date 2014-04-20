package com.mattallen.loaned;

import java.util.ArrayList;

import android.content.Context;

public class ItemTypeLookup {
	
	public static ArrayList<String> getAllTypes(Context c){
		ArrayList<String> types = new ArrayList<String>();
		for(int i=0;i<=9;i++){
			types.add(getNameByID(c,i));
		}
		return types;
	}

	public static String getNameByID(Context c, int typeID){
		// TODO Include support for multiple languages
		switch (typeID) {
		case 0:
			return c.getResources().getString(R.string.type_electrical);
		case 1:
			return c.getResources().getString(R.string.type_dvd_game);
		case 2:
			return c.getResources().getString(R.string.type_musical_instr);
		case 3:
			return c.getResources().getString(R.string.type_tools);
		case 4:
			return c.getResources().getString(R.string.type_clothes);
		case 5:
			return c.getResources().getString(R.string.type_furniture);
		case 6:
			return c.getResources().getString(R.string.type_jewellery);
		case 7:
			return c.getResources().getString(R.string.type_stationary);
		case 8:
			return c.getResources().getString(R.string.type_kitchen);
		case 9:
			return c.getResources().getString(R.string.type_other);
		default:
			return null;
		}
	}
	
	public static int getDrawableForType(int typeID){
		switch (typeID) {
		case 0:
			return R.drawable.ic_electrical;
		case 1:
			return R.drawable.ic_disc;
		case 2:
			return R.drawable.ic_musical_instr;
		case 3:
			return R.drawable.ic_tools;
		case 4:
			return R.drawable.ic_clothes;
		case 5:
			return R.drawable.ic_furniture;
		case 6:
			return R.drawable.ic_jewellery;
		case 7:
			return R.drawable.ic_stationary;
		case 8:
			return R.drawable.ic_kitchen;
		case 9:
			return R.drawable.ic_box;
		default:
			return 0;
		}
	}
}