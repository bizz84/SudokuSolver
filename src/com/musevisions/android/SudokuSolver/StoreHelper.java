/**
 * JSON Sudoku solver is covered under the Creative Commons Attribution 3.0 Unported License
 * http://creativecommons.org/licenses/by/3.0/
 * 
 * @author: Andrea Bizzotto {@link www.musevisions.com}, {@link www.bizzotto.biz}
 * @email: bizz84dev@gmail.com
 */
package com.musevisions.android.SudokuSolver;

import android.content.Context;

public class StoreHelper {

	static public enum Store {
		GOOGLE_PLAY_STORE,
		AMAZON_APP_STORE,
		SLIDE_ME,
	}
	Store mStore;
	String mLink;
	
	public StoreHelper(Context context, int storeId, int... id) {
	
		String str = context.getString(storeId);
		if (str.equals("Google")) {
			mStore = Store.GOOGLE_PLAY_STORE;
		}
		else if (str.equals("Amazon")) {
			mStore = Store.AMAZON_APP_STORE;
		}
		else if (str.equals("SlideMe")) {
			mStore = Store.SLIDE_ME;
		}
		else
			mStore = Store.GOOGLE_PLAY_STORE;
		int index = mStore.ordinal() < id.length ? mStore.ordinal() : 0;
		mLink = context.getString(id[index]);
	}
	public Store getStore() { 
		return mStore;
	}
	public String getLink() {
		return mLink;
	}
}
