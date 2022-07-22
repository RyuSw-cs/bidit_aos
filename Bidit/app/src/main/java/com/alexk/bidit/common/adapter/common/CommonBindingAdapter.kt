package com.alexk.bidit.common.adapter.common

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.alexk.bidit.GetBiddingInfoQuery
import com.alexk.bidit.GetItemListQuery
import com.alexk.bidit.R
import com.alexk.bidit.common.adapter.common.CommonBindingAdapter.loadImageListUrl
import com.alexk.bidit.common.util.addComma
import com.alexk.bidit.type.JoinPath
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

object CommonBindingAdapter {
    @JvmStatic
    @BindingAdapter("imageItemUrlList")
    fun ImageView.loadImageListUrl(
        imageUrlList: List<GetItemListQuery.Image?>?
    ) {
        if (!imageUrlList.isNullOrEmpty()) {
            val img = imageUrlList[0]?.url
            Glide.with(this.context)
                .load(img)
                .centerInside()
                .into(this)
        }
        else{
            this.setImageResource(R.drawable.bg_rect_transparent_white_smoke_radius4_stroke0)
        }
    }

    @JvmStatic
    @BindingAdapter("imageBidUrlList")
    fun ImageView.loadBidImageList(
        imageUrlList: List<GetBiddingInfoQuery.Image?>?
    ) {
        if (!imageUrlList.isNullOrEmpty()) {
            val img = imageUrlList[0]?.url
            Glide.with(this.context)
                .load(img)
                .centerInside()
                .into(this)
        }
        else{
            this.setImageResource(R.drawable.bg_rect_transparent_white_smoke_radius4_stroke0)
        }
    }

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun ImageView.loadImage(imageUrl: String?) {
        if (imageUrl != null) {
            Glide.with(this.context)
                .load(imageUrl)
                .into(this)
        }
        else{
            this.setImageResource(R.drawable.bg_rect_transparent_white_smoke_radius4_stroke0)
        }
    }

    @SuppressLint("SetTextI18n")
    @JvmStatic
    @BindingAdapter("sPrice","cPrice")
    fun TextView.changePriceType(sPrice: Int?, cPrice : Int?) {
        if (sPrice != null) {
            if(sPrice > 0 && cPrice == null){
                this.text = "${addComma(sPrice)}원"
            }
            else{
                this.text = "${addComma(cPrice!!)}원"
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @JvmStatic
    @BindingAdapter("buyNow")
    fun TextView.changeBuyNowPriceType(price: Int?) {
        if(price != null){
            this.text = "${addComma(price)}"
        }
    }

    @SuppressLint("SetTextI18n")
    @JvmStatic
    @BindingAdapter("peopleCount")
    fun TextView.changePeopleCountType(count: Int?) {
        if (count == null) {
            this.text = "0명"
        } else {
            this.text = "${addComma(count)}명"
        }
    }

    @JvmStatic
    @BindingAdapter("nickname")
    fun TextView.changeNickName(nickname : String?) {
        if(nickname == null){
            this.text = nickname
        }
    }

    @JvmStatic
    @BindingAdapter("number")
    fun TextView.changeNumberType(number: Int?) {
        if (number == null) {
            this.text = "0"
        } else {
            this.text = addComma(number)
        }
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    @JvmStatic
    @BindingAdapter("calcDate")
    fun TextView.calcDate(date: String?) {
        //텍스트 날짜 형식으로 변환 필요
        val dateTime = date?.split("T")
        if (dateTime?.size == 2) {
            val yearDate = dateTime[0]
            val time = dateTime[1]
            val simpleDataFormat = SimpleDateFormat("yyyy-MM-ddHH:mm:SS")
            try {
                val parseData = simpleDataFormat.parse(yearDate + time)
                val currentDate = Calendar.getInstance()

                //밀리초
                val calcDate = (parseData?.time?.minus(currentDate.time.time))

                val hour = calcDate?.div((60 * 60 * 24 * 1000))
                if (hour != null) {
                    if (hour > 0) {
                        this.text = "${hour}일 후 마감"
                    } else {
                        this.text = "${hour * 1000 * 60 * 60}시간 후 마감"
                    }
                }

            } catch (e: Exception) {
                Log.d("error", "time error")
            }
        }
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    @JvmStatic
    @BindingAdapter("date")
    fun TextView.changeDateType(date: String?) {
        this.text = ""
        val year = date?.substring(0, 4)
        val month = date?.substring(5, 7)
        val day = date?.substring(8, 10)
        val hour = date?.substring(11, 13)
        val minute = date?.substring(14, 16)
        this.text = "${year}년 ${month}월 ${day}일 ${hour}:${minute}"
    }

    @JvmStatic
    @BindingAdapter("status")
    fun TextView.changeStatusType(status: Int?) {
        //텍스트 날짜 형식으로 변환 필요
        val statusText: String = when (status) {
            0,1 -> {
                "판매중"
            }
            2 -> {
                "예약중"
            }
            3 -> {
                "판매완료"
            }
            4 -> {
                "판매종료"
            }
            else -> {
                ""
            }
        }
        this.text = statusText
    }

    @JvmStatic
    @BindingAdapter("reservation")
    fun TextView.checkReservation(status: Int?) {
        if (status == 1) {
            this.visibility = View.VISIBLE
        }
    }

    @JvmStatic
    @BindingAdapter("socialType")
    fun ImageView.setSocialType(type: JoinPath?) {
        when(type){
            JoinPath.GOOGLE -> {
                Log.d("Not Implements","Login")
            }
            JoinPath.KAKAO -> {
                this.setImageResource(R.drawable.ic_my_page_login_kakao)
            }
            JoinPath.APPLE -> {
                Log.d("Not Implements","Login")
            }
            else -> {
                Log.d("Not Implements","Login")
            }
        }
    }
}