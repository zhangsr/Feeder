/*
 * Copyright (C) 2013 Antarix Tandon
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

package org.sufficientlysecure.htmltextview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html.ImageGetter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import me.zsr.feeder.R;

public class UrlImageGetter implements ImageGetter {
    private Context mContext;
    private TextView mContainerTextView;

    /**
     * Construct the URLImageParser which will execute AsyncTask and refresh the container
     *
     * @param container
     */
    public UrlImageGetter(TextView container, Context context) {
        mContext = context;
        mContainerTextView = container;
    }

    public Drawable getDrawable(String source) {
        UrlDrawable urlDrawable = new UrlDrawable();

        new ImageGetterAsyncTask(urlDrawable).execute(source);

        // return reference to URLDrawable which will asynchronously load the image specified in the src tag
        return urlDrawable;
    }

    public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
        UrlDrawable mmUrlDrawable;

        public ImageGetterAsyncTask(UrlDrawable d) {
            this.mmUrlDrawable = d;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            String source = params[0];
            return new BitmapDrawable(mContext.getResources(), ImageLoader.getInstance().loadImageSync(source));
        }

        @Override
        protected void onPostExecute(Drawable result) {
            // change the reference of the current drawable to the result from the HTTP call
            mmUrlDrawable.setDrawable(result);

            mContainerTextView.setText(mContainerTextView.getText());
        }
    }

    @SuppressWarnings("deprecation")
    public class UrlDrawable extends BitmapDrawable {
        protected Drawable mmDrawable;

        public UrlDrawable() {
            // Set default
            mmDrawable = mContext.getResources().getDrawable(R.drawable.ic_add_selector);
            adjustDrawableBounds(mmDrawable, mmDrawable.getIntrinsicWidth(), mmDrawable.getIntrinsicHeight());
        }

        public void setDrawable(Drawable drawable) {
            adjustDrawableBounds(this, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            mmDrawable = drawable;
            adjustDrawableBounds(drawable, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        }

        @Override
        public void draw(Canvas canvas) {
            // override the draw to facilitate refresh function later
            mmDrawable.draw(canvas);
        }
    }

    private void adjustDrawableBounds(Drawable drawable, int oldWidth, int oldHeight) {
        int width;
        int height;

        if (oldWidth * 2 < mContainerTextView.getWidth()) {
            width = oldWidth * 2;
            height = oldHeight * 2;
        } else {
            width = mContainerTextView.getWidth();
            height = width * oldHeight / oldWidth;
        }

        drawable.setBounds(0, 0, width, height);
    }
} 
