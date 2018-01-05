package com.example.user.ex_inmobiles;

import android.app.DialogFragment;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.ex_inmobiles.entity.ImageEntity;
import com.squareup.picasso.Picasso;

/**
 * Created by user on 1/4/2018.
 */

public class MyDialogFragment extends DialogFragment {

    private TextView desc;
    private ImageView imageView;
    private String title,descString,path;

    public static MyDialogFragment newInstance(String title,String descString,String path) {
        Bundle bundle = new Bundle();
        bundle.putString("title",title);
        bundle.putString("desc",descString);
        bundle.putString("path",path);
        MyDialogFragment fragment = new MyDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            title = bundle.getString("title");
            descString = bundle.getString("desc");
            path = bundle.getString("path");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sample_dialog, container, false);
        readBundle(getArguments());
        getDialog().setTitle(title);
        desc = (TextView) rootView.findViewById(R.id.description);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);

        desc.setText(descString);
        try{
            Picasso.with(getActivity()).load(path).placeholder(R.mipmap.logo)
                    .error(R.mipmap.logo).resize(500, 500).into(imageView);
        }catch(Exception e){
            Toast.makeText(getActivity(),"Broken URL or failed to connect",Toast.LENGTH_LONG).show();

        }

        Button dismiss = (Button) rootView.findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return rootView;
    }


}