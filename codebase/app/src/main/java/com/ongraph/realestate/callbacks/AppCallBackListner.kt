package com.ongraph.realestate.callbacks

import android.graphics.Bitmap

interface AppCallBackListner {
    fun isSuccess(success: Boolean)

    interface ResultCallback {
        fun onResult(mObject: Any?)
    }

    interface DeleteCallback {
        fun onResult(mObject: Any?, id: Int?)
    }

    interface UploadImageListner {
        fun getImage(imageName: String, pos: Int?)
    }

    interface ImagePickerListener {
        fun getImage(image: Bitmap)
    }

    interface ItemClick {
        fun onResult(pos: Int?)
    }

    interface EidSalahGenericCallBack<T> {
        fun editClick(t: T)
        fun deleteForm(t: T)
    }

    interface DialogCallback {
        fun onClickPositiveButton()
        fun onClickNegativeButton()
    }

    interface DialogClickCallback {
        fun onButtonClick()
    }

    interface GenericItemCallBack<T> {
        fun itemClick(t: T)
    }

    interface PostSelectCallBack {
        fun onResult(mObject: Any?, flag: Boolean)
    }

    interface HomeEditCallBack {
        fun onResult(mObject: Any?, id: Int?, post: String)
    }

    interface IqamaUploadCallBack {
        fun onUpload(mObject: Any?, id: Int?, itemType: Int)
    }

    interface IqamaUploadDeleteCallBack {
        fun onUploadDelete(mObject: Any?, id: Int?, itemType: Int, action: String)
    }

    interface LocationCallBack {
        fun onSuccess(loc: String, latitude: Double, longitude: Double)
    }
}