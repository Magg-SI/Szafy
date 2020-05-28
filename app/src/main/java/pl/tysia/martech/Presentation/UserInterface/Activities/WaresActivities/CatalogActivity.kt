package pl.tysia.martech.Presentation.UserInterface.Activities.WaresActivities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_colect_wares.*
import kotlinx.android.synthetic.main.app_bar.*
import pl.tysia.martech.Presentation.PresentationLogic.Catalogs.CatalogAdapter
import pl.tysia.martech.Presentation.PresentationLogic.Catalogs.ICatalogable
import pl.tysia.martech.Presentation.PresentationLogic.Filterer.StringFilter

import pl.tysia.martech.R
import java.util.ArrayList

abstract class CatalogActivity<T : ICatalogable> : AppCompatActivity(),
        CatalogAdapter.ItemSelectedListener<T>, TextWatcher, CatalogAdapter.EmptyListListener {
    protected lateinit var catalogItemsList: ArrayList<T>
    protected lateinit var adapter: CatalogAdapter<T>
    private var filter: StringFilter? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText

    protected abstract fun getCatalogItems(): ArrayList<T>
    protected abstract fun getCatalogAdapter(items : ArrayList <T>): CatalogAdapter<T>

    protected abstract fun onFinishClicked()
    protected abstract fun onScanClicked()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_colect_wares)

        catalogItemsList = getCatalogItems()
        adapter = getCatalogAdapter(catalogItemsList)
        adapter.addItemSelectedListener(this)
        adapter.addEmptyListener(this)

        val filterer = adapter.filterer
        filter = StringFilter()
        filterer.addFilter(filter)

        recyclerView = findViewById(R.id.recycler_view)
        searchEditText = findViewById(R.id.search_edit_text)

        recyclerView.adapter = adapter

        searchEditText.addTextChangedListener(this)

        val linearLayoutManager = LinearLayoutManager(this@CatalogActivity, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager


        adapter.filter()
        adapter.notifyDataSetChanged()

        finish_button.setOnClickListener { onFinishClicked() }
        scanButton.setOnClickListener { onScanClicked() }

        setSupportActionBar(themedToolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return true

    }


    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable) {
        filter?.filteredString = s.toString()
        adapter.filter()
        adapter.notifyDataSetChanged()

    }


    protected fun okDialog(title : String, message : String){
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle(title)
        dialogBuilder.setMessage(message)
        dialogBuilder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, whichButton ->
            //get what you need here!
        })
        val b = dialogBuilder.create()
        b.show()
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    protected fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            orders_container.visibility = if (show) View.GONE else View.VISIBLE
            orders_container.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 0 else 1).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            orders_container.visibility = if (show) View.GONE else View.VISIBLE
                        }
                    })

            orders_progress_bar.visibility = if (show) View.VISIBLE else View.GONE
            orders_progress_bar.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            orders_progress_bar.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            orders_progress_bar.visibility = if (show) View.VISIBLE else View.GONE
            orders_container.visibility = if (show) View.GONE else View.VISIBLE
        }
    }


}
