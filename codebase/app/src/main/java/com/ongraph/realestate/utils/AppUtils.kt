package com.ongraph.realestate.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.ongraph.realestate.callbacks.AppCallBackListner
import com.ongraph.realestate.callbacks.DateCallBacks
import java.io.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object AppUtils {

    fun showToast(v: View, msg: String) {
        Snackbar.make(v, msg, Snackbar.LENGTH_LONG).show()
    }

    fun logoutDialog(
        context: Context,
        title: String,
        message: String,
        appCallBackListner: AppCallBackListner
    ) {
        val builder = AlertDialog.Builder(context)
        //        context.setTheme(R.style.AlertDialogCustom)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setCancelable(true)
        builder.setPositiveButton("Yes") { dialog, _ ->
            dialog.cancel()
            appCallBackListner.isSuccess(true)
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
            appCallBackListner.isSuccess(false)
        }
        builder.show()
    }

    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val netInfo = cm!!.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    fun hideKeyboard(mContext: Context) {
        val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if ((mContext as Activity).window.currentFocus != null)
            imm.hideSoftInputFromWindow(mContext.window.currentFocus!!.windowToken, 0)
        else
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    fun setLocale(lang: String, context: Context) {
        val locale = Locale(lang)
        val dm = context.resources.displayMetrics
        val conf = context.resources.configuration
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                conf.setLocale(locale)
                context.createConfigurationContext(conf)
            } else {
                conf.locale = locale
                context.resources.updateConfiguration(conf, dm)
            }
            context.resources.updateConfiguration(conf, dm)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isLocationAccessPermitted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            (context as Activity), Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocationAccessPermission(context: Context) {
        ActivityCompat.requestPermissions(
            (context as Activity), arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), GlobalVariable.LOC_REQ_CODE
        )
    }


    private var progressDialog: ProgressDialog? = null

    fun setFragment(
        containerViewId: Int,
        fragmentManager: FragmentManager,
        fragment: Fragment,
        fragmentTag: String,
        addBackStack: Boolean,
        replace: Boolean
    ) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        if (replace) {
            fragmentTransaction.replace(containerViewId, fragment, fragmentTag)
        } else {
            fragmentTransaction.add(containerViewId, fragment, fragmentTag)
        }
        if (addBackStack) {
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.commit()
    }

    @SuppressLint("SimpleDateFormat")
    fun openDatePickerDialog(
        context: Context,
        mDateListener: DateCallBacks.DateSelectedListener,
        selected_date: String
    ) {
        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                mDateListener.onDateSet(pad(dayOfMonth) + "-" + pad((monthOfYear + 1)) + "-" + year.toString())
            }
        if (selected_date.isNotEmpty()) {
            val df = SimpleDateFormat("dd-MM-yyyy")
            var d1: Date? = null
            try {
                d1 = df.parse(selected_date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            if (d1 != null) {
                cal.time = d1
            }
        }
        val mDatePicker = DatePickerDialog(
            context,
            dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        mDatePicker.datePicker.minDate = System.currentTimeMillis() - 1000
        mDatePicker.show()
    }

    /*fun openTimePickerDialog(context: Context, mTimeListener: DateCallBacks.TimeSelectedListener, selectedTime: String, date: String) {
        val calTime = Calendar.getInstance()
        val currenTime = Calendar.getInstance()
        var selectedDate = date
        //set previously selected date
        if (selectedDate.isEmpty()) {
            selectedDate = getCurrentDate()
        }

        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        val convertSelectedDate = dateFormat.parse(selectedDate)
        val convertCurrentDate = dateFormat.parse(getCurrentDate())

        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            val time1 = SimpleDateFormat("HH:mm").parse("${currenTime.get(Calendar.HOUR_OF_DAY)}:${currenTime.get(Calendar.MINUTE)}")
            val time2 = SimpleDateFormat("HH:mm").parse("$hour:$minute")

            if (convertSelectedDate.before(convertCurrentDate)) {
                mTimeListener.onTimeSet("")
                showToast((context as Activity).findViewById(android.R.id.content), context.getString(R.string.cant_change_prev_date))
            } else if (convertSelectedDate.equals(convertCurrentDate) && time2.before(time1)) {
                mTimeListener.onTimeSet("")
                showToast((context as Activity).findViewById(android.R.id.content), context.getString(R.string.current_time))
            } else {
                mTimeListener.onTimeSet(StringBuilder().append(pad(hour)).append(':').append(pad(minute)).toString())
            }
        }

        //set previously selected time
        if (selectedTime.isNotEmpty()) {
            val df = SimpleDateFormat("HH:mm")
            var d: Date? = null
            try {
                d = df.parse(selectedTime)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            if (d != null) {
                calTime.time = d
            }
        }

        TimePickerDialog(context, timeSetListener, calTime.get(Calendar.HOUR_OF_DAY), calTime.get(Calendar.MINUTE),
                true).show()
    }
*/

    fun openAnyTimePickerDialog(
        context: Context,
        mTimeListener: DateCallBacks.TimeSelectedListener,
        selectedTime: String
    ) {
        val calTime = Calendar.getInstance()

        //set previously selected time
        if (selectedTime.isNotEmpty()) {
            val df = SimpleDateFormat("hh:mm a"/*, Locale.getDefault()*/)
            var d: Date? = null
            try {
                d = df.parse(selectedTime)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            if (d != null) {
                calTime.time = d
            }
        }

        val timePickerDialog = TimePickerDialog(
            context,
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                try {
                    val datetime = Calendar.getInstance()
                    datetime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    datetime.set(Calendar.MINUTE, minute)

                    var am_pm = "AM"
                    if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                        am_pm = "AM"
                    else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                        am_pm = "PM"

                    var tempMinute = "00"
                    if (minute < 10) {
                        tempMinute = "0" + minute
                    } else {
                        tempMinute = "" + minute
                    }
                    val strHrsToShow =
                        if (datetime.get(Calendar.HOUR) == 0) "12" else datetime.get(Calendar.HOUR).toString() + ""
                    mTimeListener.onTimeSet("$strHrsToShow:$tempMinute $am_pm")

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            calTime.get(Calendar.HOUR_OF_DAY),
            calTime.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }

    /*private fun updateTime(hours: Int, mins: Int): String {
        var hours = hours
        var timeSet = ""
        if (hours > 12) {
            hours -= 12
            timeSet = "PM"
        } else if (hours == 0) {
            hours += 12
            timeSet = "AM"
        } else if (hours == 12)
            timeSet = "PM"
        else
            timeSet = "AM"

        var minutes = ""
        if (mins < 10)
            minutes = "0$mins"
        else
            minutes = mins.toString()
        val aTime = StringBuilder().append(hours).append(':').append(minutes).append(" ").append(timeSet).toString()
        return aTime
    }*/

    /*@SuppressLint("SimpleDateFormat")
    fun convertDateWithoutDay(mdateStr: String): String? {
        val pattern = "yyyy-MM-dd"
        var date: String? = null
        val sdf = SimpleDateFormat("EEE, dd MMM yyyy")
        try {
            val d = sdf.parse(mdateStr)
            val simpleDateFormat = SimpleDateFormat(pattern)
            date = simpleDateFormat.format(d)
        } catch (ex: ParseException) {
            ex.printStackTrace()
        }
        return date
    }*/

    @SuppressLint("SimpleDateFormat")
    fun convertDateWithDay(mdateStr: String): String? {
        val pattern = "EEE, dd MMM yyyy"
        var date: String? = null
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        try {
            val d = sdf.parse(mdateStr)
            val simpleDateFormat = SimpleDateFormat(pattern)
            date = simpleDateFormat.format(d)
        } catch (ex: ParseException) {
            ex.printStackTrace()
        }
        return date
    }

    @SuppressLint("SimpleDateFormat")
    fun timeConversion(time: String): String { //24h to 12h
        val pattern = "hh:mm a"
        var date: String? = null
        val sdf = SimpleDateFormat("HH:mm")
        try {
            val d = sdf.parse(time)
            val simpleDateFormat = SimpleDateFormat(pattern/*, Locale.getDefault()*/)
            date = simpleDateFormat.format(d)
            date = date.replace("a.m.", "AM").replace("p.m.", "PM")
        } catch (ex: ParseException) {
            ex.printStackTrace()
        }
        return date ?: time
    }

    fun convert12to24h(time: String): String {
        try {
            if (time.equals("00:00"))
                return "00:00"
            var d: Date? = null
            d = SimpleDateFormat("hh:mm a"/*, Locale.getDefault()*/).parse(time)
            val sdf = SimpleDateFormat("HH:mm")
            return sdf.format(d)

        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

    @SuppressLint("SimpleDateFormat")
    fun timeDifference(time1: String): Long {
        val format = SimpleDateFormat("HH:mm")
        val date1 = format.parse(time1)
        val date2 = format.parse(getCurrentTime())
        val difference = date1.time - date2.time

        return (difference)
    }

    fun getImageFile(bm: Bitmap, filename: String): File {
        val imageFile =
            File(Environment.getExternalStorageDirectory().toString() + "/RealEstate", filename)
        imageFile.parentFile.mkdirs()
        try {
            val bos = ByteArrayOutputStream()
            val resizedBitmap =
                Bitmap.createScaledBitmap(
                    bm, (bm.width * 0.8).toInt(),
                    (bm.height * 0.8).toInt(), true
                )
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 80, bos)

            val bitmapdata = bos.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(imageFile)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return imageFile
    }

    fun getLogoFile(bm: Bitmap, filename: String): File {
        val imageFile =
            File(Environment.getExternalStorageDirectory().toString() + "/RealEstate", filename)
        imageFile.parentFile.mkdirs()

        val bos = ByteArrayOutputStream()
        val resizedBitmap =
            Bitmap.createScaledBitmap(bm, (bm.width * 0.3).toInt(), (bm.height * 0.3).toInt(), true)
        resizedBitmap.compress(Bitmap.CompressFormat.PNG, 50, bos)

        val bitmapdata = bos.toByteArray()
        try {
            val fos = FileOutputStream(imageFile)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return imageFile
    }

    private fun pad(c: Int): String {
        return if (c >= 10)
            c.toString()
        else
            "0$c"
    }

    fun convertDateTime(timestamp: Long): String {
        val timestamp1 = timestamp * 1000

        val messageTime: Long = System.currentTimeMillis() - timestamp1

        if (messageTime < 60000) {
            return "Just now"
        } else if (messageTime < 3600000) {
            val daysAgo: Long = messageTime / 60000
            return if (daysAgo > 1) {
                "$daysAgo minutes ago"
            } else
                "$daysAgo minute ago"
        } else if (messageTime < 86400000) {
            val hoursAgo = messageTime / 3600000
            return if (hoursAgo > 1) {
                "$hoursAgo hours ago"
            } else
                "$hoursAgo hour ago"
        } else {
            return convertLongToTime(timestamp1)
        }
    }

    private fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("EEE, dd MMM yyyy")
        return format.format(date)
    }

    fun showLocationOnMap(context: Context, latitude: Double, longitude: Double) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("geo:<$latitude>,<$longitude>?q=<$latitude>,<$longitude>")
        )
        context.startActivity(intent)
    }

    fun hideDialogKeyboard(context: Context, dialog: Dialog) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if ((context as Activity).window.currentFocus != null)
            imm.hideSoftInputFromWindow(dialog.window!!.currentFocus!!.windowToken, 0)
        else
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    /*  fun showImages(context: Context, index: Int, mImageList: ArrayList<String>) {
          val intent = Intent(context, MyImageViewPagerActivity::class.java)
          intent.putExtra("LIST", mImageList as Serializable)
          intent.putExtra("MY_PHOTOS", false)
          intent.putExtra("INDEX", index)
          context.startActivity(intent)
      }*/

    fun isValidMobile(phone: String): Boolean {
        if (!Pattern.matches("[+/^#_.a-zA-Z]+", phone)) {
//            if (phone.length < 10 || phone.length > 11) {
            return if (phone.length > 10) {
                if (phone.length == 11) {
                    if (phone.startsWith("0", true)) {
                        true
                    } else {
                        false
                    }
                } else {
                    false
                }
            } else {
                true
            }
//            }
        } else {
            return false
        }
    }

    fun getCurrentDate(): String {
        return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    }

    fun getCurrentTime(): String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Calendar.getInstance().time)
    }

    fun getExpiryDate(): String {
        var dt = Date()
        val c = Calendar.getInstance()
        c.setTime(dt)
        c.add(Calendar.DATE, 3)
        dt = c.getTime()
        return dt.toString()
    }

    fun convertStringToDate(dateStr: String, salahTime: String, takbirTime: String): Date {
        val df = SimpleDateFormat("dd-MM-yyyy")
        var startDate: Date? = null
        try {
            startDate = df.parse(dateStr)

            if (salahTime.isNotEmpty()) {
                val salahTimeArr = salahTime.split(":")
                startDate.setHours(salahTime[0].toInt())
                startDate.setMinutes(salahTime[1].toInt())
            } else {
                val takbirTimeArr = takbirTime.split(":")
                startDate.hours = takbirTime[0].toInt()
                startDate.minutes = takbirTime[1].toInt()
            }

//            val newDateString = df.format(startDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return startDate!!
    }

    fun replaceSpecialCharaters(input: String): String {
        var replacedStr = input
        try {
            replacedStr = input.replace("[@-_=:?.,/+'^#_a-zA-Z0-9]", "")
//            replacedStr = input.replace("\\W", "")
        } catch (e: Exception) {
            replacedStr = input
            e.printStackTrace()
        }
        return replacedStr
    }

    fun spannableText(view: TextView, fulltext: String, subtext: String, color: Int) {
        view.setText(fulltext, TextView.BufferType.SPANNABLE)
        val str = view.text as Spannable
        val i = fulltext.indexOf(subtext)
        str.setSpan(
            ForegroundColorSpan(color), i,
            i + subtext.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    fun getDateTime(timestamp: Long): String? {
        try {
            val neededTime = Calendar.getInstance()
            neededTime.timeInMillis = timestamp * 1000
            val sdf = SimpleDateFormat(AppConstants.DateFormat)
            return sdf.format(neededTime.time)
        } catch (e: Exception) {
            return e.toString()
        }
    }

    fun getTime(timestamp: Long): String? {
        try {
            val neededTime = Calendar.getInstance()
            neededTime.timeInMillis = timestamp * 1000
            val sdf = SimpleDateFormat(AppConstants.DateTimeFormat)
            return sdf.format(neededTime.time)
        } catch (e: Exception) {
            return e.toString()
        }
    }


}