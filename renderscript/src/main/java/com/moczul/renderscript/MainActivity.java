package com.moczul.renderscript;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

    private ImageView mImageView;
    private ImageView mBlurImageView;
    private TextView mSupportInfo;

    static {
        System.loadLibrary("renderscript");
    }

    public native String getProcessorType();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        mImageView = (ImageView) findViewById(R.id.image_view);
        mBlurImageView = (ImageView) findViewById(R.id.blur_image_view);
        mSupportInfo = (TextView) findViewById(R.id.support_info);

        final Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.sample_photo);
        Bitmap outImage = image.copy(image.getConfig(), false);
        mImageView.setImageBitmap(image);

        final boolean hasRSSupport = hasRenderScriptSupport();
        mBlurImageView.setVisibility(hasRSSupport ? View.VISIBLE : View.GONE);
        mSupportInfo.setVisibility(hasRSSupport ? View.GONE : View.VISIBLE);

        if (hasRSSupport) {
            final RenderScript renderScript = RenderScript.create(this);
            final Allocation input = Allocation.createFromBitmap(renderScript, image);
            final Allocation output = Allocation.createTyped(renderScript, input.getType());

            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(renderScript,
                    Element.U8_4(renderScript));
            script.setRadius(25f);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(outImage);

            mBlurImageView.setImageBitmap(outImage);

            renderScript.destroy();
        }
    }

    private boolean hasRenderScriptSupport() {
        final String processorType = getProcessorType();

        if ("unknown".equals(processorType)
                || "armeabi".equals(processorType)) {
            return false;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageView.setImageBitmap(null);
    }
}
