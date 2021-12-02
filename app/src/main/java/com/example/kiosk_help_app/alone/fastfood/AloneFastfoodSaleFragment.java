package com.example.kiosk_help_app.alone.fastfood;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kiosk_help_app.ListViewItem;
import com.example.kiosk_help_app.ListviewAdapter;
import com.example.kiosk_help_app.R;
import com.google.android.gms.maps.MapView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AloneFastfoodSaleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AloneFastfoodSaleFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_alone_fastfood_sale, container, false);
        // Fragment에서는 onClick을 사용할 수 없기때문에,  별도로 리스너를 달아서 클릭이벤트를 지정한다.
        ImageButton sale_item_1 = (ImageButton) rootview.findViewById(R.id.FF_sale_item1);

        sale_item_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AloneFastfoodStoreActivity) getActivity()).addFFSaleMenuHandler(1);
            }
        });

        ImageButton sale_item_2 = (ImageButton) rootview.findViewById(R.id.FF_sale_item2);
        sale_item_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AloneFastfoodStoreActivity) getActivity()).addFFSaleMenuHandler(2);
            }
        });
        ImageButton sale_item_3 = (ImageButton) rootview.findViewById(R.id.FF_sale_item3);
        sale_item_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AloneFastfoodStoreActivity) getActivity()).addFFSaleMenuHandler(3);
            }
        });
        ImageButton sale_item_4 = (ImageButton) rootview.findViewById(R.id.FF_sale_item4);
        sale_item_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AloneFastfoodStoreActivity) getActivity()).addFFSaleMenuHandler(4);
            }
        });

        return rootview;
    }
    // 버튼 클릭 이벤트 리스너



//    public void onClick(View view) {
//        switch (view.getId()){
//            //출근버튼
//            case R.id.FF_sale_item:
//                //출근 관련 로직
//                Toast.makeText(getContext(),"출근 완료",Toast.LENGTH_SHORT).show();
//                break;
//            //퇴근 버튼
//
//        }
//    }

    public AloneFastfoodSaleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AloneFastfoodSaleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AloneFastfoodSaleFragment newInstance(String param1, String param2) {
        AloneFastfoodSaleFragment fragment = new AloneFastfoodSaleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


}