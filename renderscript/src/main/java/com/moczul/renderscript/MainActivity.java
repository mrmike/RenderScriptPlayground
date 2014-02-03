package com.moczul.renderscript;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v8.renderscript.*;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

    private ImageView mImageView;
    private ImageView mRSImageView;
    private Bitmap mInBitmap;
    private Bitmap mOutBitmap;
    private TextView mSupportInfo;

    private RenderScript mRenderScript;

    private boolean mIsBlur = true;

    static {
        System.loadLibrary("renderscript");
    }

    public native String getProcessorType();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        mImageView = (ImageView) findViewById(R.id.image_view);
        mRSImageView = (ImageView) findViewById(R.id.rs_image_view);
        mSupportInfo = (TextView) findViewById(R.id.support_info);

        mInBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_photo);
        mOutBitmap = Bitmap.createBitmap(mInBitmap.getWidth(), mInBitmap.getHeight(), mInBitmap.getConfig());

        mImageView.setImageBitmap(mInBitmap);

        final boolean hasRSSupport = hasRenderScriptSupport();
        mRSImageView.setVisibility(hasRSSupport ? View.VISIBLE : View.GONE);
        mSupportInfo.setVisibility(hasRSSupport ? View.GONE : View.VISIBLE);

        if (hasRSSupport) {
            mRenderScript = RenderScript.create(this);
            loadBlurScript();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_change_item) {
            if (!hasRenderScriptSupport()) {
                Toast.makeText(this, R.string.render_support_info, Toast.LENGTH_SHORT).show();
                return true;
            }

            if (mIsBlur) {
                loadMonoScript();
            } else {
                loadBlurScript();
            }

            mIsBlur = !mIsBlur;
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void loadBlurScript() {
        final Allocation input = Allocation.createFromBitmap(mRenderScript, mInBitmap);
        final Allocation output = Allocation.createTyped(mRenderScript, input.getType());

        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(mRenderScript,
                Element.U8_4(mRenderScript));
        script.setRadius(25f);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(mOutBitmap);

        mRSImageView.setImageBitmap(mOutBitmap);
    }

    private void loadMonoScript() {
        final Allocation input = Allocation.createFromBitmap(mRenderScript, mInBitmap,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
        final Allocation output = Allocation.createFromBitmap(mRenderScript, mOutBitmap,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);

        ScriptC_greyscale script = new ScriptC_greyscale(mRenderScript, getResources(), R.raw.greyscale);
        script.forEach_root(input, output);
        output.copyTo(mOutBitmap);

        mRSImageView.setImageBitmap(mOutBitmap);
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
        if (mRenderScript != null) {
            mRenderScript.destroy();
        }
        mImageView.setImageBitmap(null);
        mRSImageView.setImageBitmap(null);
    }
}
