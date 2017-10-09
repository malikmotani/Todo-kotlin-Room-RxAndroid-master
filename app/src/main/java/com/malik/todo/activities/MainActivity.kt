package com.malik.todo.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem

import com.malik.todo.R
import com.malik.todo.framgments.CategoryFragment
import com.malik.todo.framgments.DeletedTaskFragment
import com.malik.todo.framgments.HistoryFragment
import com.malik.todo.framgments.TaskListFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    private lateinit var handler: Handler

    lateinit var fragment: Fragment

    var fragmentClass: Class<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbarMain.title = getString(R.string.dashboard)

        setSupportActionBar(toolbarMain)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbarMain, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)

        drawer_layout.addDrawerListener(toggle)

        toggle.syncState()

        handler = Handler()

        nav_view.setNavigationItemSelectedListener(this)

        navigate(R.id.nav_dashboard)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        drawer_layout.closeDrawer(GravityCompat.START)

        handler.postDelayed({ navigate(item.itemId) }, 300)

        return true
    }

    /**
     * Navigation Drawer item clicks
     * */
    private fun navigate(id: Int) {

        when (id) {
            R.id.nav_dashboard -> {
                fragmentClass = TaskListFragment::class.java
                toolbarMain.title = getString(R.string.dashboard)
                loadFragment()
            }
            R.id.nav_category -> {
                fragmentClass = CategoryFragment::class.java
                toolbarMain.title = getString(R.string.category)
                loadFragment()
            }
            R.id.nav_history -> {
                fragmentClass = HistoryFragment::class.java
                toolbarMain.title = getString(R.string.history)
                loadFragment()
            }
            R.id.nav_deleted_task-> {
                fragmentClass = DeletedTaskFragment::class.java
                toolbarMain.title = getString(R.string.deleted_tasks)
                loadFragment()
            }
            R.id.nav_rate_us -> {
//                rateUs()
            }
            R.id.nav_share_app -> {
//                shareApp()
            }
        }

    }

    /**
     * load fragment
     */
    private fun loadFragment() {
        fragment = fragmentClass!!.newInstance() as Fragment

        val fragmentManager = supportFragmentManager

        fragmentManager
                .beginTransaction()
                .replace(R.id.framLayout, fragment).commit()

    }
}
