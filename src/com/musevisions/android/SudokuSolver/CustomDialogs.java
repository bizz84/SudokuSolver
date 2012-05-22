/**
 * JSON Sudoku solver is covered under the Creative Commons Attribution 3.0 Unported License
 * http://creativecommons.org/licenses/by/3.0/
 * 
 * @author: Andrea Bizzotto {@link www.musevisions.com}, {@link www.bizzotto.biz}
 * @email: bizz84dev@gmail.com
 */
package com.musevisions.android.SudokuSolver;


import android.app.AlertDialog;
import android.content.Context;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

public class CustomDialogs {

	public static AlertDialog createAbout(Context context) {
		final TextView message = new TextView(context);
		// i.e.: R.string.dialog_message =>
		// "Test this dialog following the link to dtmilano.blogspot.com"
		final String about = context.getString(R.string.about_text);
		StoreHelper helper = new StoreHelper(context, R.string.store,
				R.string.more_from_dev_google, R.string.more_from_dev_amazon);
		final String link = helper.getLink();
		final SpannableString s = new SpannableString(about + " " + link);

		Linkify.addLinks(s, Linkify.WEB_URLS);
		message.setText(s);
		message.setMovementMethod(LinkMovementMethod.getInstance());
		
		return new AlertDialog.Builder(context)
			.setTitle(R.string.about_title)
			.setCancelable(true)
			.setIcon(android.R.drawable.ic_dialog_info)
			.setPositiveButton(R.string.about_dismiss, null)
			.setView(message)
			.create();
	}
	
}