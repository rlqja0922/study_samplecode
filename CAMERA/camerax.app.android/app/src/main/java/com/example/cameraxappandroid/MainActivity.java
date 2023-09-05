package com.example.cameraxappandroid;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.*;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity
{
    private TextureView textureView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textureView = findViewById(R.id.textureView);

        textureView.post((Runnable)(new Runnable()
        {
            public final void run()
            {
                startCamera();
            }
        }));

        textureView.addOnLayoutChangeListener(new View.OnLayoutChangeListener()
        {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom)
            {
                updateTransform();
            }
        });
    }

    private void startCamera()
    {
        PreviewConfig.Builder previewConfig = new PreviewConfig.Builder();

        Preview preview = new Preview(previewConfig.build());

        preview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener()
        {
            @Override
            public void onUpdated(Preview.PreviewOutput output)
            {
                ViewGroup parent = (ViewGroup) textureView.getParent();
                parent.removeView(textureView);
                parent.addView(textureView, 0);

                textureView.setSurfaceTexture(output.getSurfaceTexture());
                updateTransform();
            }
        });

        ImageCaptureConfig.Builder imageCaptureConfig = new ImageCaptureConfig.Builder();

        final ImageCapture imageCapture = new ImageCapture(imageCaptureConfig.build());

        ImageView button = findViewById(R.id.captureImg);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                imageCapture.takePicture(new ImageCapture.OnImageCapturedListener()
                {
                    @Override
                    public void onCaptureSuccess(ImageProxy image, int rotationDegrees)
                    {
                        ImageView imageView = findViewById(R.id.imageView);
                        imageView.setImageBitmap(imageProxyToBitmap(image));
                        imageView.setRotation(rotationDegrees);

                        image.close();
                    }
                });
            }
        });

        CameraX.bindToLifecycle(this, preview, imageCapture);
    }


    private void updateTransform()
    {
        Matrix matrix = new Matrix();

        float centerX = textureView.getWidth() / 2f;
        float centerY = textureView.getHeight() / 2f;

        float rotationDegrees = 0f;
        switch (textureView.getDisplay().getRotation())
        {
            case Surface.ROTATION_0:
                rotationDegrees = 0f;
                break;

            case Surface.ROTATION_90:
                rotationDegrees = 90f;
                break;

            case Surface.ROTATION_180:
                rotationDegrees = 180f;
                break;

            case Surface.ROTATION_270:
                rotationDegrees = 270f;
                break;

            default:
                return;
        }

        matrix.postRotate(-rotationDegrees, centerX, centerY);

        textureView.setTransform(matrix);
    }


    private Bitmap imageProxyToBitmap(ImageProxy image)
    {
        ImageProxy.PlaneProxy planeProxy = image.getPlanes()[0];
        ByteBuffer buffer = planeProxy.getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}