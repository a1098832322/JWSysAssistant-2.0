package com.wishes.assistant.myapplication.activity;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnDrawListener;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.listener.OnPageChangeListener;
import com.wishes.assistant.myapplication.R;

import skin.support.app.SkinCompatActivity;

/**
 * Created by 郑龙 on 2017/4/28.
 */

public class PDF extends SkinCompatActivity implements OnDrawListener, OnLoadCompleteListener, OnPageChangeListener {
    private ImageView mImgBack;
    private PDFView mPdfView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        mImgBack = (ImageView) findViewById(R.id.title_btn_back);
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mPdfView = findViewById(R.id.pdfview);
                mPdfView.fromAsset("test.pdf")
                        .pages(0, 1, 2, 3)
                        .defaultPage(1)
                        .showMinimap(false)
                        .enableSwipe(true)
                        .onDraw(this)
                        .onLoad(this)
                        .onPageChange(this)
                        .load();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

    }

    @Override
    public void loadComplete(int nbPages) {

    }

    @Override
    public void onPageChanged(int page, int pageCount) {

    }
}
