package com.example.gameprojectdemov1

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

private const val ARG_PARAM = "param"

class FrontPageFragment : androidx.fragment.app.Fragment(){
    private var mParam: String? = null
    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mParam = it.getString(ARG_PARAM)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_front_page, container, false)
        return view
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: View)
    }

    companion object {
        @JvmStatic
        fun newInstance(p: String) =
            FrontPageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM, p)
                }
            }
    }

}
