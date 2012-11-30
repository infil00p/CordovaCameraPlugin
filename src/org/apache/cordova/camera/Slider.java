/*
 * Copyright (C) 2011 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.cordova.camera;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.apache.cordova.R;

public class Slider extends View {

	interface SliderPositionListener {
		void onPositionChange(double value);
	}

	private Drawable mIndicator;
	private Drawable mBackground;
	private double mPosition;
	private SliderPositionListener mListener;
	private boolean mVertical;

	
	void centerAround(int x, int y, Drawable d) {
        int w = d.getIntrinsicWidth();
        int h = d.getIntrinsicHeight();
        int left = x - w / 2;
        int top = y - h / 2;
        int right = left + w;
        int bottom = top + h;
        d.setBounds(left, top, right, bottom);
    }
	
	public Slider(Context context) {
		super(context);
		initSliderView(context, true);
	}

	public Slider(Context context, AttributeSet attrs) {
		super(context, attrs);
		initSliderView(context, true);
	}

	public void setSliderBackground(Drawable background) {
		mBackground = background;
		invalidate();
	}

	public void setPositionListener(SliderPositionListener listener) {
		mListener = listener;
	}

	public void setPosition(double position) {
		if (mPosition != position) {
			invalidate();
			mPosition = position;
		    mListener.onPositionChange(mPosition);
		}
	}

	public void increment()
	{
	    double position = mPosition + 0.1; 
	    if(position < 1)
	        setPosition(position);
	    else
	        setPosition(1);
	}
	
	public void decrement()
	{
	      double position = mPosition - 0.1; 
	        if(position > 0)
	            setPosition(position);
	        else
	            setPosition(0);
	}
	
	private OnTouchListener mClickListener = new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent m) {
			Rect r = new Rect();
			getDrawingRect(r);

			double position;
			if (mVertical) {
				double y = m.getY();
				position = Math.max(0, (r.bottom - y) / r.height());
			} else {
				double x = m.getX();
				position = Math.max(0, (x - r.left) / r.width());
			}
			position = Math.min(1, position);
			setPosition(position);
			return true;
		}
	};

	protected void initSliderView(Context context, boolean vertical) {
		mPosition = 0;
		mVertical = vertical;
		Resources res = context.getResources();		
		mBackground = res.getDrawable(R.drawable.slider_base);		
		mIndicator = res.getDrawable(R.drawable.slider_thumb);
		this.setOnTouchListener(mClickListener);
	}

	protected void onDraw(Canvas canvas) {
		Rect r = new Rect();
		getDrawingRect(r);
		if (mVertical) {
			int lineX = r.centerX();
			int bgW = mBackground.getIntrinsicWidth() / 2;
			if (bgW == 0) {
				bgW = 5;
			}
			mBackground.setBounds(lineX - bgW, r.top + 10, lineX + bgW,
					r.bottom - 10);
			mBackground.draw(canvas);
			final int kMargin = 48;
			int indicatorY = (int) (r.bottom - (r.height() - kMargin)
					* mPosition)
					- kMargin / 2;
			centerAround(lineX, indicatorY, mIndicator);
			mIndicator.draw(canvas);
		} else {
			int lineY = r.centerY();
			int bgH = mBackground.getIntrinsicHeight() / 2;
			if (bgH == 0) {
				bgH = 5;
			}
			mBackground.setBounds(r.left + 10, lineY - bgH, r.right - 10, lineY
					+ bgH);
			mBackground.draw(canvas);
			final int kMargin = 48;
			int indicatorX = (int) ((r.width() - kMargin) * mPosition) + r.left
					+ kMargin / 2;
			centerAround(indicatorX, lineY, mIndicator);
			mIndicator.draw(canvas);
		}
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (mVertical) {
			setMeasuredDimension(mIndicator.getIntrinsicWidth(),
					getMeasuredHeight());
		} else {
			setMeasuredDimension(getMeasuredWidth(),
					mIndicator.getIntrinsicHeight());
		}
	}

}
