package com.example.kiosk_help_app.alone.Transfer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.kiosk_help_app.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AloneTransferTwoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AloneTransferTwoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AloneTransferTwoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AloneTransferTwoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AloneTransferTwoFragment newInstance(String param1, String param2) {
        AloneTransferTwoFragment fragment = new AloneTransferTwoFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_alone_transfer_two, container, false);
        // Fragment에서는 onClick을 사용할 수 없기때문에,  별도로 리스너를 달아서 클릭이벤트를 지정한다.
        Button btn_1 = rootview.findViewById(R.id.transfer_item1);
        Button btn_2 = rootview.findViewById(R.id.transfer_item1);
        Button btn_3 = rootview.findViewById(R.id.transfer_item1);
        Button btn_4 = rootview.findViewById(R.id.transfer_item1);
        btn_1.setOnClickListener(this::onClick);
        btn_2.setOnClickListener(this::onClick);
        btn_3.setOnClickListener(this::onClick);
        btn_4.setOnClickListener(this::onClick);


        return rootview;
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.transfer_item1:
                ((AloneTransferOnsiteActivity) getActivity()).addTransferTwoMenuHandler(1);
                break;
            case R.id.transfer_item2:
                ((AloneTransferOnsiteActivity) getActivity()).addTransferTwoMenuHandler(2);
                break;
            case R.id.transfer_item3:
                ((AloneTransferOnsiteActivity) getActivity()).addTransferTwoMenuHandler(3);
                break;
            case R.id.transfer_item4:
                ((AloneTransferOnsiteActivity) getActivity()).addTransferTwoMenuHandler(4);
                break;


        }
    }
}