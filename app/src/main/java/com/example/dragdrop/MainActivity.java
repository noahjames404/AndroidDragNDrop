package com.example.dragdrop;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.dragdrop.ButtonGrid.ButtonGrid;
import com.example.dragdrop.ButtonGrid.ButtonProperties;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    RelativeLayout rl_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        rl_layout = findViewById(R.id.rl_sample);

        doButtonGrid();


    }

    @Override
    public void onClick(View view) {

    }

    private void doButtonGrid() {
        rl_layout = findViewById(R.id.rl_sample);
        List<ButtonProperties> properties = new ArrayList<>();
        properties.add(
                new ButtonProperties(
                        "",
                        0,
                        "powerdered milk",
                        "#3498db",
                        "",
                        2,
                        2,
                        this
                )
        );

        properties.add(
                new ButtonProperties(
                        "",
                        0,
                        "power 2",
                        "#00FFFF",
                        "",
                        2,
                        1,
                        this
                )
        );
        properties.add(
                new ButtonProperties(
                        "",
                        0,
                        "power 3",
                        "#991111",
                        "",
                        1,
                        1,
                        this
                )
        );


        properties.add(
                new ButtonProperties(
                        "",
                        0,
                        "power 3",
                        "#991111",
                        "",
                        1,
                        1,
                        this
                )
        );

        properties.add(
                new ButtonProperties(
                        "",
                        0,
                        "power 3",
                        "#991111",
                        "",
                        1,
                        3,
                        this
                )
        );

        properties.add(
                new ButtonProperties(
                        "",
                        0,
                        "power 4",
                        "#779944",
                        "",
                        1,
                        1,
                        this
                )
        );

        properties.add(
                new ButtonProperties(
                        "",
                        0,
                        "power 5",
                        "#3498db",
                        "",
                        1,
                        1,
                        this
                )
        );


        properties.add(
                new ButtonProperties(
                        "",
                        0,
                        "power 6",
                        "#3498db",
                        "",
                        1,
                        1,
                        this
                )
        );

        final ButtonGrid buttonGrid = new ButtonGrid(3,4,properties,rl_layout,getApplicationContext());
    }


}
