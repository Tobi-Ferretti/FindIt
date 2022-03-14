package com.FindIt.finditpflanzenfinden;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.FindIt.finditpflanzenfinden.R;

import java.util.HashMap;
import java.util.Map;

public class MoreActivity extends AppCompatActivity {

    private TableLayout flowerCard;
    private TableLayout knCard;
    private TableLayout wCard;
    private TableLayout treeRel;
    private HashMap<String, String> searchMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchMap= (HashMap<String, String>) getIntent().getSerializableExtra("ADA");


        flowerCard=(TableLayout) findViewById(R.id.flowRelLay);
        knCard=(TableLayout) findViewById(R.id.knRelLay);
        wCard=(TableLayout) findViewById(R.id.wRelLay);
        treeRel=(TableLayout) findViewById(R.id.treeRelLay);


        setupBtns();


    }

    private void setupBtns(){
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            Button button;
        TableRow rowTree=new TableRow(this);
        TableRow rowFl=new TableRow(this);
        TableRow rowKn=new TableRow(this);
        TableRow rowRo=new TableRow(this);
        RelativeLayout spaceing;

        for(Map.Entry<String, String> entry : searchMap.entrySet()) {
            String name = entry.getKey();
            String cat=entry.getValue();
            button = new Button(this);
            spaceing =new RelativeLayout(this);
            spaceing.setLayoutParams(new TableRow.LayoutParams(5, TableRow.LayoutParams.WRAP_CONTENT));

            if (cat!=null) {
                switch (cat) {
                    case "Baum":
                        button = (Button) getLayoutInflater().inflate(R.layout.custom_more_button, (ViewGroup) treeRel.getParent(), false);
                        button.setText(name);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Button b = (Button) view;
                                searchItem(b.getText().toString());
                            }
                        });
                        if (rowTree.getChildCount()==1){
                            rowTree.addView(spaceing);
                            rowTree.addView(button, layoutParams);
                            treeRel.removeView(rowTree);
                        }else {
                            rowTree=new TableRow(this);
                            rowTree.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                            rowTree.addView(button,layoutParams);
                        }
                        treeRel.addView(rowTree);
                        break;
                    case "Blüte":
                        button = (Button) getLayoutInflater().inflate(R.layout.custom_more_button, (ViewGroup) flowerCard.getParent(), false);
                        button.setText(name);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Button b = (Button) view;
                                searchItem(b.getText().toString());
                            }
                        });
                        if (rowFl.getChildCount()==1){
                            rowFl.addView(spaceing);
                            rowFl.addView(button, layoutParams);
                            flowerCard.removeView(rowFl);
                        }else {
                            rowFl=new TableRow(this);
                            rowFl.addView(button, layoutParams);
                        }
                        flowerCard.addView(rowFl);
                        break;
                    case "Knospe":
                        button = (Button) getLayoutInflater().inflate(R.layout.custom_more_button, (ViewGroup) knCard.getParent(), false);
                        button.setText(name);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Button b = (Button) view;
                                searchItem(b.getText().toString());
                            }
                        });
                        if (rowKn.getChildCount()==1){
                            rowKn.addView(spaceing);
                            rowKn.addView(button, layoutParams);
                            knCard.removeView(rowKn);
                        }else {
                            rowKn=new TableRow(this);
                            rowKn.addView(button, layoutParams);
                        }
                        knCard.addView(rowKn);
                        break;
                    case "Wurzel":
                        button = (Button) getLayoutInflater().inflate(R.layout.custom_more_button, (ViewGroup) wCard.getParent(), false);
                        button.setText(name);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Button b = (Button) view;
                                searchItem(b.getText().toString());
                            }
                        });
                        if (rowRo.getChildCount()==1){
                            rowRo.addView(spaceing);
                            rowRo.addView(button, layoutParams);
                            wCard.removeView(rowRo);
                        }else {
                            rowRo=new TableRow(this);
                            rowRo.addView(button, layoutParams);
                        }
                        wCard.addView(rowRo);
                        break;
                }
            }

        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

    public void searchItem(String name){
        Intent intent=new Intent(this, MapsActivity.class);
        intent.putExtra("Search", name);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}