package com.example.gameprojectdemov1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.example.gameprojectdemov1.FrontPageFragment.OnFragmentInteractionListener
import kotlinx.android.synthetic.main.activity_navigation.*

class NavigationActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener, OnFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this,mainAct,toolbar,R.string.open_nav,R.string.close_nav)
        mainAct.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        val fragment = FrontPageFragment.newInstance("Fragment Aboutme is Created")
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.frontpagefragment,fragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.aboutme->{
                //val transaction = supportFragmentManager.beginTransaction()
                //transaction.replace(R.id.frontpagefragment,AboutMeFragment())
                //transaction.commit()
            }
            R.id.task1->{
                val intent = Intent(this,CameraActivity::class.java)
                startActivity(intent)
            }
            R.id.task2->{
                //val intent = Intent(this,RecyclerViewActivity2::class.java)
                //startActivity(intent)
            }
        }
        mainAct.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onFragmentInteraction(uri: View) {
        val transaction = supportFragmentManager.beginTransaction()
        //transaction.replace(R.id.frontpagefragment,AboutMeFragment())
        transaction.commit()
    }

}
