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
    EditText nombreP, apellidoP, emailP, passP;
    ListView lvPersona;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Persona personaSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Buscando el Id
        nombreP = findViewById(R.id.txt_nomPersona);
        apellidoP = findViewById(R.id.txt_apellidpPersona);
        emailP = findViewById(R.id.txt_emailPersona);
        passP = findViewById(R.id.txt_passPersona);
        lvPersona = findViewById(R.id.lv_datosPersona);

        initFirebase();
        listarDatos();

        lvPersona.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                personaSelected = (Persona) parent.getItemAtPosition(position);
                nombreP.setText(personaSelected.getNombre());
                apellidoP.setText(personaSelected.getApellido());
                emailP.setText(personaSelected.getCorreo());
                passP.setText(personaSelected.getPassword());
            }
        });
    }

    //Método para listar los datos en el LisView
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

    //Método para inicializar Firebase
    private void initFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.icon_add:{

                agregarPersona();

                break;
            }
            case R.id.icon_save:{

                actualizarPersona();

                break;
            }
            case R.id.icon_delete:{

                eliminarPersona();

                break;
            }
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Método para agregar personas
    private void agregarPersona(){

        String nombre = nombreP.getText().toString();
        String apellido = apellidoP.getText().toString();
        String correo = emailP.getText().toString();
        String pass = passP.getText().toString();

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
            Toast.makeText(this, "Agregado", Toast.LENGTH_SHORT).show();
            limpiar();
        }
    }

    //Método para actualizar personas
    private void actualizarPersona(){
        String nombre = nombreP.getText().toString();
        String apellido = apellidoP.getText().toString();
        String correo = emailP.getText().toString();
        String pass = passP.getText().toString();

        Persona p = new Persona();
        p.setUid(personaSelected.getUid());
        p.setNombre(nombre.toString().trim());
        p.setApellido(apellido.toString().trim());
        p.setCorreo(correo.toString().trim());
        p.setPassword(pass.toString().trim());
        databaseReference.child("Persona").child(p.getUid()).setValue(p);
        Toast.makeText(this,"Actualizado",Toast.LENGTH_SHORT).show();
        limpiar();
    }

    //Método para eliminar persona
    private void eliminarPersona(){

        Persona p = new Persona();
        p.setUid(personaSelected.getUid());
        databaseReference.child("Persona").child(p.getUid()).removeValue();
        Toast.makeText(this,"Eliminado",Toast.LENGTH_SHORT).show();
        limpiar();
    }

    //Método para limpiar cajas de texto
    private void limpiar() {
        nombreP.setText("");
        apellidoP.setText("");
        emailP.setText("");
        passP.setText("");
    }

    //Método para validar las casillas vacias
    public void validacion(){

        String nombre = nombreP.getText().toString();
        String apellido = apellidoP.getText().toString();
        String correo = emailP.getText().toString();
        String pass = passP.getText().toString();

        if(nombre.equals("")){
            nombreP.setError("Requerido");
        }
        else if(apellido.equals("")){
            apellidoP.setError("Requerido");
        }
        else if(correo.equals("")){
            emailP.setError("Requerido");
        }
        else if(pass.equals("")){
            passP.setError("Requerido");
        }


    }

}