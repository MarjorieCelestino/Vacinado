package br.com.inf.vacinado.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.inf.vacinado.Controller.DoseAdapter;
import br.com.inf.vacinado.Model.Dose;
import br.com.inf.vacinado.R;

public class VacinaInfo extends AppCompatActivity {

    private List<Dose> listaDoses = new ArrayList<>();
    RecyclerView recycleDoses;
    DoseAdapter adapter;
    TextView doseVazia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacina_info);

        //doseVazia = (TextView) findViewById(R.id.carteira_vazia);
        //doseVazia.setText(R.string.dose_vazia);

        final Button novaDose = (Button) findViewById(R.id.adicionarDose_vacina_info);
        novaDose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                adicionaDose();
            }
        });

        final Button voltar = (Button) findViewById(R.id.bttnVoltar_vacina_info);
        voltar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                voltarViewCarteira();
            }
        });

        //recycle

        //Dose dose = new Dose(02, 05, 2017, "mt bom", 1);
        //listaDoses.add(dose);

        //setRecycleDoses(listaDoses);
    }

    private void adicionaDose() {
        Intent intent = new Intent(VacinaInfo.this, AdicionarDose.class);
        startActivity(intent);
    }

    private void voltarViewCarteira() {
        super.onBackPressed();
//        Intent intent = new Intent(VacinaInfo.this, Carteira.class);
//        startActivity(intent);
    }

    //configura RecycleView
    private void setRecycleDoses(List listaDoses) {
        if (listaDoses.isEmpty()) {
            doseVazia.setText(R.string.dose_vazia);
        } else
            recycleDoses = (RecyclerView) findViewById(R.id.recycle_view_doses);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recycleDoses.setLayoutManager(llm);
        adapter = new DoseAdapter(listaDoses);
        recycleDoses.setAdapter(adapter);
    }
}

