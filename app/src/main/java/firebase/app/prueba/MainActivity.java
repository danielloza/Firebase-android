package firebase.app.prueba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import firebase.app.prueba.model.Persona;

public class MainActivity extends AppCompatActivity {

    private List<Persona> listPersona = new LinkedList<Persona>();
    ArrayAdapter<Persona> arrayAdapterPer;
    EditText nomP, appP, correoP, passwordP;
    ListView lvPersona;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Persona personaSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewId();

        initFirebase();
        listarDatos();

        lvPersona.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                personaSelected = (Persona) parent.getItemAtPosition(position);
                nomP.setText(personaSelected.getNombre());
                appP.setText(personaSelected.getApellido());
                correoP.setText(personaSelected.getCorreo());
                passwordP.setText(personaSelected.getPassword());
            }
        });
    }

    private void listarDatos() {
        databaseReference.child("Persona").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listPersona.clear();
                for(DataSnapshot objDataSnap: snapshot.getChildren()){
                    Persona persona = objDataSnap.getValue(Persona.class);
                    listPersona.add(persona);

                    arrayAdapterPer = new ArrayAdapter<Persona>(MainActivity.this, android.R.layout.simple_list_item_1, listPersona);
                    lvPersona.setAdapter(arrayAdapterPer);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    public void findViewId(){

        nomP = findViewById(R.id.txt_nombrePersona);
        appP = findViewById(R.id.txt_appPersona);
        correoP = findViewById(R.id.txt_correoPersona);
        passwordP = findViewById(R.id.txt_passwordPersona);

        lvPersona = findViewById(R.id.lv_datosPersona);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        String nombre = nomP.getText().toString();
        String apellido = appP.getText().toString();
        String correo = correoP.getText().toString();
        String pass = passwordP.getText().toString();

        switch (item.getItemId()){
            case R.id.icon_add:{
                if(nombre.equals("") || apellido.equals("") || correo.equals("") || pass.equals("")){
                    validacion();
                }else {
                    Persona p = new Persona();
                    p.setUid(UUID.randomUUID().toString());
                    p.setNombre(nombre);
                    p.setApellido(apellido);
                    p.setCorreo(correo);
                    p.setPassword(pass);
                    databaseReference.child("Persona").child(p.getUid()).setValue(p);
                    Toast.makeText(this, "El dato a sido Agregado", Toast.LENGTH_SHORT).show();
                    limpiar();
                }
                break;
            }
            case R.id.icon_save:{
                Persona p = new Persona();
                p.setUid(personaSelected.getUid());
                p.setNombre(nombre.toString().trim());
                p.setApellido(apellido.toString().trim());
                p.setCorreo(correo.toString().trim());
                p.setPassword(pass.toString().trim());
                databaseReference.child("Persona").child(p.getUid()).setValue(p);
                Toast.makeText(this,"El dato a sido Actualizado",Toast.LENGTH_SHORT).show();
                limpiar();
                break;
            }
            case R.id.icon_delete:{
                Persona p = new Persona();
                p.setUid(personaSelected.getUid());
                databaseReference.child("Persona").child(p.getUid()).removeValue();
                Toast.makeText(this,"El dato a sido Eliminado",Toast.LENGTH_SHORT).show();
                limpiar();
                break;
            }
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void limpiar() {
        nomP.setText("");
        appP.setText("");
        correoP.setText("");
        passwordP.setText("");
    }

    public void validacion(){

        String nombre = nomP.getText().toString();
        String apellido = appP.getText().toString();
        String correo = correoP.getText().toString();
        String pass = passwordP.getText().toString();

        if(nombre.equals("")){
            nomP.setError("Es Requerido");
        }
        else if(apellido.equals("")){
            appP.setError("Es Requerido");
        }
        else if(correo.equals("")){
            correoP.setError("Es Requerido");
        }
        else if(pass.equals("")){
            passwordP.setError("Es Requerido");
        }


    }

}