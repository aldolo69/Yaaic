package org.yaaic.standout;

import org.yaaic.R;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.ScrollView;

public class SoPopupUtility extends PopupWindow {

	protected View mViewPopupContent = null;
	protected Context mContext = null;
	protected PopupWindow mPw = null;

	protected SoPopupUtility() {
	}

	protected SoPopupUtility(Context ctx, View vPopupContent) {

		super(vPopupContent, WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT, true);
		setClippingEnabled(false);
		mViewPopupContent = vPopupContent;
		mContext = ctx;
		mPw = this;// new PopupWindow(vPopupContent,
	}

	protected SoPopupUtility(Context ctx, View vPopupContent, int x, int y) {
		super(vPopupContent, x, y, true);
		setClippingEnabled(false);
		mViewPopupContent = vPopupContent;
		mContext = ctx;
		mPw = this;// new PopupWindow(vPopupContent,
	}

	static public SoPopupUtility getPopupUtility(Context ctx, View vPopupContent) {

		vPopupContent.measure(View.MeasureSpec.UNSPECIFIED,
				View.MeasureSpec.UNSPECIFIED);

		int iXsize = vPopupContent.getMeasuredWidth();
		int iYsize = vPopupContent.getMeasuredHeight();

		return new SoPopupUtility(ctx, vPopupContent, iXsize, iYsize);

	}

	static public SoPopupUtility getPopupUtilityScrollable(Context ctx,
			View vPopupContent) {

		int iScreenDimensions[] = new int[2];
		SoPopupUtility.getDisplaySize(ctx, iScreenDimensions);
		// iScreenDimensions[0]=display X size
		// iScreenDimensions[1]=display Y size

		vPopupContent.measure(View.MeasureSpec.UNSPECIFIED,
				View.MeasureSpec.UNSPECIFIED);
		int iXsize = vPopupContent.getMeasuredWidth();
		int iYsize = vPopupContent.getMeasuredHeight();
		iScreenDimensions[0] = iScreenDimensions[0] * 8 / 10;
		iScreenDimensions[1] = iScreenDimensions[1] * 8 / 10;
		if (iXsize > iScreenDimensions[0])
			iXsize = iScreenDimensions[0];
		if (iYsize > iScreenDimensions[1])
			iYsize = iScreenDimensions[1];

		LayoutInflater inflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View vPopup = inflater.inflate(R.layout.so_generic_popup, null, false);
		ScrollView sv = (ScrollView) vPopup
				.findViewById(R.id.generic_popup_scroll);
		sv.addView(vPopupContent);

		return new SoPopupUtility(ctx, vPopup, iXsize, iYsize);
	}

	// public void dismiss(){mPw.dismiss();}

	public static final int FROM_TOP_SIDE_TO_UP = 1;
	public static final int FROM_BOTTOM_SIDE_TO_UP = 2;
	public static final int FROM_TOP_SIDE_TO_DOWN = 4;
	public static final int FROM_BOTTOM_SIDE_TO_DOWN = 8;
	public static final int FROM_LEFT_SIDE_TO_LEFT = 16;
	public static final int FROM_LEFT_SIDE_TO_RIGHT = 32;
	public static final int FROM_RIGHT_SIDE_TO_LEFT = 64;
	public static final int FROM_RIGHT_SIDE_TO_RIGHT = 128;
	public static final int FROM_MIDDLE_X = 256;
	public static final int FROM_MIDDLE_Y = 512;

	/*
	 * ----- | | FROM_TOP_SIDE_TO_UP -------------- ----- ---- |relativeview| |
	 * | FROM_TOP_SIDE_TO_DOWN(ok) | | ----- ---- | | | | FROM_BOTTOM_SIDE_TO_UP
	 * -------------- ----- ------ | | FROM_BOTTOM_SIDE_TO_DOWN(ok) ------
	 * 
	 * ----------------- | | | | ----------------- ---- | |
	 * FROM_LEFT_SIDE_TO_RIGHT(ok) ---- ----- | | FROM_LEFT_SIDE_TO_LEFT -----
	 * ---- | | FROM_RIGHT_SIDE_TO_LEFT ---- ----- | | FROM_RIGHT_SIDE_TO_RIGHT
	 * ----- (non-Javadoc)
	 * 
	 * @see android.widget.PopupWindow#showAsDropDown(android.view.View)
	 */

	public void showAsDropDown(View viewRelativeTo, int relativePosition) {
		// mViewRelativeTo=viewRelativeTo;
		showPopup(viewRelativeTo, relativePosition);
	}

	@Override
	public void showAsDropDown(View viewRelativeTo) {
		// mViewRelativeTo=viewRelativeTo;
		showPopup(viewRelativeTo, FROM_LEFT_SIDE_TO_RIGHT
				+ FROM_BOTTOM_SIDE_TO_DOWN);
	}

	public void showPopup(View viewRelativeTo, int relativePosition) {
		// show works using x,y of the root, not of the mTxt
		int iXrel = getRelativeLeft(viewRelativeTo);
		int iYrel = getRelativeTop(viewRelativeTo);
		// popup size

		mViewPopupContent.measure(View.MeasureSpec.UNSPECIFIED,
				View.MeasureSpec.UNSPECIFIED);
		// int iXsize = this.getWidth();//mViewPopupContent.getMeasuredWidth();
		// int iYsize =
		// this.getHeight();//mViewPopupContent.getMeasuredHeight();
		int iXsize = mPw.getWidth();// mViewPopupContent.getMeasuredWidth();
		int iYsize = mPw.getHeight();// mViewPopupContent.getMeasuredHeight();

		int iScreenDimensions[] = new int[2];
		getDisplaySize(mContext, iScreenDimensions);
		// iScreenDimensions[0]=display X size
		// iScreenDimensions[1]=display Y size
		if (iXsize > iScreenDimensions[0])
			iXsize = iScreenDimensions[0];
		if (iYsize > iScreenDimensions[1])
			iYsize = iScreenDimensions[1];

		if (0 != (relativePosition & FROM_MIDDLE_Y)) {
			iYrel = iYrel + viewRelativeTo.getHeight() / 2 - iYsize / 2;
		}
		if (0 != (relativePosition & FROM_MIDDLE_X)) {
			iXrel = iXrel + viewRelativeTo.getWidth() / 2 - iXsize / 2;
		}
		if (0 != (relativePosition & FROM_BOTTOM_SIDE_TO_DOWN)) {
			iYrel = iYrel + viewRelativeTo.getHeight();
		}
		if (0 != (relativePosition & FROM_BOTTOM_SIDE_TO_UP)) {
			iYrel = iYrel + viewRelativeTo.getHeight() - iYsize;
		}
		if (0 != (relativePosition & FROM_TOP_SIDE_TO_UP)) {
			iYrel = iYrel - iYsize;
		}
		if (0 != (relativePosition & FROM_TOP_SIDE_TO_DOWN)) {
			iYrel = iYrel;
		}

		if (0 != (relativePosition & FROM_RIGHT_SIDE_TO_LEFT)) {
			iXrel = iXrel + viewRelativeTo.getWidth() - iXsize;
		}
		if (0 != (relativePosition & FROM_RIGHT_SIDE_TO_RIGHT)) {
			iXrel = iXrel + viewRelativeTo.getWidth();
		}
		if (0 != (relativePosition & FROM_LEFT_SIDE_TO_LEFT)) {
			iXrel = iXrel - iXsize;
		}
		if (0 != (relativePosition & FROM_LEFT_SIDE_TO_RIGHT)) {
			iXrel = iXrel;
		}

		int iRootCoords[] = new int[2];
		viewRelativeTo.getRootView().getLocationOnScreen(iRootCoords);
		// iRootCoords[0]=absolute X position of the view
		// iRootCoords[1]=absolute Y position of the view

		// i don't want to exit from the top or from the left
		if ((iRootCoords[0] + iXrel) < 0) {
			iXrel = -iRootCoords[0];
		}
		if ((iRootCoords[1] + iYrel) < 0) {
			iYrel = -iRootCoords[1];
		}

		// but i don't want the box to go beyond right or bottom margin
		// of the screen
		if (iRootCoords[0] + iXrel + iXsize > iScreenDimensions[0]) {
			iXrel = iXrel
					- (iRootCoords[0] + iXrel + iXsize - iScreenDimensions[0]);
		}
		if (iRootCoords[1] + iYrel + iYsize > iScreenDimensions[1]) {
			iYrel = iYrel
					- (iRootCoords[1] + iYrel + iYsize - iScreenDimensions[1]);
		}

		mPw.showAtLocation(viewRelativeTo, Gravity.NO_GRAVITY, iXrel, iYrel);

	}

	public void showPopup() {
		mPw.showAtLocation(null, Gravity.CENTER, 0, 0);
	}

	private int getRelativeLeft(View myView) {
		if (myView.getParent() == myView.getRootView())
			return myView.getLeft();
		else
			return myView.getLeft()
					+ getRelativeLeft((View) myView.getParent());
	}

	private int getRelativeTop(View myView) {
		if (myView.getParent() == myView.getRootView())
			return myView.getTop();
		else
			return myView.getTop() + getRelativeTop((View) myView.getParent());
	}

	public static void getDisplaySize(Context context, int[] dimensions) {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		display.getMetrics(displaymetrics);
		dimensions[1] = displaymetrics.heightPixels;
		dimensions[0] = displaymetrics.widthPixels;
	}

}
