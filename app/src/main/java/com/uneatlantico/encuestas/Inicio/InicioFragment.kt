package com.uneatlantico.encuestas.Inicio

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.uneatlantico.encuestas.R


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [InicioFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 *
 */
class InicioFragment : Fragment() {

    lateinit var text:TextView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_inicio, container, false)

        val texto = arguments!!.getString("texto")
        text = view.findViewById(R.id.textoInicio)
        text.text = texto

        return view
    }

    companion object {
        fun newInstance(): InicioFragment = InicioFragment()
    }

}
