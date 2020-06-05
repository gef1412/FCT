package com.example.proyecto.Fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.proyecto.Activities.CrearGruposActivity;

import com.example.proyecto.Models.Grupos;
import com.example.proyecto.R;
import com.example.proyecto.ViewHolders.GroupViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class GruposFragment extends Fragment {

    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;

    RecyclerView recyclerView;
    static String grupo="";

    public GruposFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    //Log.i("onQueryTextChange", newText);

                    grupo=searchView.getQuery().toString();
                    onStart();
                    return false;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    //Log.i("onQueryTextSubmit", query);
                    grupo=searchView.getQuery().toString();
                    onStart();
                    return false;
                }

            };

            searchView.setOnQueryTextListener(queryTextListener);


        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                // Not implemented here
                return false;
            default:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onStart() {
        super.onStart();

        final DatabaseReference GruposRef = FirebaseDatabase.getInstance().getReference().child("Grupos");

        FirebaseRecyclerOptions<Grupos> opciones= new FirebaseRecyclerOptions.
                Builder<Grupos>().setQuery(GruposRef.orderByChild("nombre").startAt(grupo), Grupos.class).build();
        FirebaseRecyclerAdapter<Grupos, GroupViewHolder> adapter = new FirebaseRecyclerAdapter<Grupos, GroupViewHolder>(opciones) {
            @Override
            protected void onBindViewHolder(@NonNull GroupViewHolder holder, final int position, @NonNull final Grupos model) {

                //INFLAMOS LOS ELEMENTOS DE LA LISTA
                holder.txtName.setText(model.getNombre());
                holder.txtNumber.setText(model.getNumero());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final CharSequence opciones[]= new CharSequence[]{
                                "Modificar",
                                "Eliminar"
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Opciones:");
                        builder.setItems(opciones, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                //CON MODIFICAR SE CARGA EL ACTIVITY DEL PRODUCTO DONDE SE MODIFICA
                                //LA CANTIDAD DEL PRODUCTO
                                if(i==0){
                                    grupo="";
                                    Intent intent= new Intent(getContext(), CrearGruposActivity.class);
                                    intent.putExtra("IDgroup", model.getID())
                                    .putExtra("number",model.getNumero());
                                    startActivity(intent);
                                }
                                //CON ELIMINAR, SE ACCEDE A LA UBICACIÓN DE LA LISTA DE LA COMPRA,
                                //Y AHÍ SE ELIMINA EL ARTÍCULO, ACTUALIZANDO EL FRAGMENT ACTUAL PARA
                                //ACTUALIZAR LA LISTA Y EL PRECIO
                                if(i==1){

                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                                    builder1.setTitle("Alertas");
                                    builder1.setMessage("¿Seguro que quieres eliminar el grupo de la lista?");
                                    LayoutInflater inflater = getActivity().getLayoutInflater();

                                    // Botones de aceptar/cancelar
                                    builder1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            GruposRef
                                                    .child(model.getID())
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Toast.makeText(getContext(),"Grupo eliminado de la lista",Toast.LENGTH_SHORT).show();
                                                                notifyItemRemoved(position);
                                                            }
                                                        }
                                                    });
                                        }
                                    });

                                    builder1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    builder1.show();
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            //ESTE MÉTODO INDICA LA VISTA UTILIZADA PARA MOSTRAR EL PRODUCTO EN LA LISTA
            @NonNull
            @Override
            public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.groups_item_layout, parent, false);
                GroupViewHolder holder= new GroupViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_grupos, container, false);


        RecyclerView.LayoutManager layoutManager;
        recyclerView = view.findViewById(R.id.recycler_groups);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        FloatingActionButton addGroup_btn= view.findViewById(R.id.add_groups_btn);
        addGroup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grupo="";
                Intent intent= new Intent(getContext(), CrearGruposActivity.class);
                startActivity(intent);

            }
        });

        return view;
    }
}
