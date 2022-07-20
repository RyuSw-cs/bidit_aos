package com.alexk.bidit.presentation.ui.selling

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexk.bidit.R
import com.alexk.bidit.common.adapter.selling.SellingItemImgListAdapter
import com.alexk.bidit.common.util.view.EditTextAutoCommaWatcher
import com.alexk.bidit.databinding.FragmentSellingBinding
import com.alexk.bidit.di.ViewState
import com.alexk.bidit.domain.entity.merchandise.MerchandiseImgEntity
import com.alexk.bidit.domain.entity.selling.SellingCalendarEntity
import com.alexk.bidit.domain.entity.selling.SellingTimeEntity
import com.alexk.bidit.presentation.base.BaseFragment
import com.alexk.bidit.presentation.ui.selling.dialog.SellingCalendarDialog
import com.alexk.bidit.presentation.ui.selling.dialog.SellingEssentialRequiredItemDialog
import com.alexk.bidit.presentation.ui.selling.dialog.SellingTimePickerDialog
import com.alexk.bidit.presentation.viewModel.ItemImgViewModel
import com.alexk.bidit.presentation.viewModel.MerchandiseViewModel
import com.alexk.bidit.type.ItemAddInput
import com.alexk.bidit.type.ItemDetailInput
import com.apollographql.apollo3.api.Optional
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream


@AndroidEntryPoint
@ExperimentalCoroutinesApi
class SellingFragment : BaseFragment<FragmentSellingBinding>(R.layout.fragment_selling) {

    private lateinit var resultLauncherActivityInfo: ActivityResultLauncher<Intent>
    private val itemImgViewModel by viewModels<ItemImgViewModel>()
    private val merchandiseViewModel by viewModels<MerchandiseViewModel>()
    private val itemUrlImgList = mutableListOf<MerchandiseImgEntity>()
    private val itemImgAdapter by lazy { SellingItemImgListAdapter() }
    private val itemTimeIdx = SellingTimeEntity(0, 6, 3)
    private val itemCalendarIdx = SellingCalendarEntity(0, 7, 3)
    private val args: SellingFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initEvent()
    }

    override fun init() {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        observeMerchandise()
        observeImgUrl()
        resultLauncherActivityInfo =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data

                    //uri
                    val filePath = data?.data
                    val inputStream: InputStream? =
                        requireContext().contentResolver.openInputStream(filePath!!)

                    //file
                    val file = File.createTempFile("image", filePath.lastPathSegment)
                    val outStream: OutputStream = FileOutputStream(file)
                    outStream.write(inputStream!!.readBytes())

                    Log.d("test file path", filePath.lastPathSegment!!)

                    itemImgViewModel.uploadItemImg(filePath.lastPathSegment!! + ".png", file)
                }
            }

        binding.apply {
            if (args.category == "") {
                tvCategory.text = "카테고리"
            } else {
                tvCategory.setTextColor(ResourcesCompat.getColor(resources, R.color.nero, null))
                tvCategory.text = args.category
            }
            rvMerchandiseImgList.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            rvMerchandiseImgList.adapter = itemImgAdapter
        }
    }

    override fun initEvent() {
        binding.apply {
            tvBiddingEndingDate.setOnClickListener {
                val sellingDateDialog = SellingCalendarDialog(itemCalendarIdx) {
                    val getDate =
                        "${resources.getStringArray(R.array.category_number_picker_year)[it.yearIdx]}년 ${
                            resources.getStringArray(R.array.category_number_picker_zero_to_twelve)[it.monthIdx]
                        }월 ${resources.getStringArray(R.array.category_number_picker_one_to_thirty_one)[it.dayIdx]}일"
                    tvBiddingEndingDate.text = getDate
                    tvBiddingEndingDate.setTextColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.nero,
                            null
                        )
                    )
                }
                ivBiddingEndingDateDelete.visibility = View.VISIBLE
                sellingDateDialog.show(childFragmentManager, sellingDateDialog.tag)
            }

            ivBiddingEndingDateDelete.setOnClickListener {
                tvBiddingEndingDate.text = "경매 마감 날짜"
                tvBiddingEndingDate.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.nobel,
                        null
                    )
                )
                ivBiddingEndingDateDelete.visibility = View.INVISIBLE
            }

            ivBiddingEndingTimeDelete.setOnClickListener {
                tvBiddingEndingTime.text = "경매 마감 시간"
                tvBiddingEndingTime.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.nobel,
                        null
                    )
                )
                ivBiddingEndingTimeDelete.visibility = View.INVISIBLE
            }

            tvBiddingEndingTime.setOnClickListener {
                val sellingTimePickerDialog = SellingTimePickerDialog(itemTimeIdx) {
                    itemTimeIdx.dateIdx = it.dateIdx
                    itemTimeIdx.hourIdx = it.hourIdx
                    itemTimeIdx.minuteIdx = it.minuteIdx
                    val getDate =
                        "${resources.getStringArray(R.array.category_number_picker_day)[it.dateIdx]} ${
                            resources.getStringArray(R.array.category_number_picker_zero_to_twelve)[it.hourIdx]
                        }시 ${resources.getStringArray(R.array.category_number_zero_to_fifty_10)[it.minuteIdx]}분"
                    tvBiddingEndingTime.text = getDate
                    tvBiddingEndingTime.setTextColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.nero,
                            null
                        )
                    )
                    ivBiddingEndingTimeDelete.visibility = View.VISIBLE
                }
                sellingTimePickerDialog.show(childFragmentManager, sellingTimePickerDialog.tag)
            }

            tvCategory.setOnClickListener {
                navigate(
                    SellingFragmentDirections.actionSellingFragmentToSellingCategoryFragment(
                        tvCategory.text.toString()
                    )
                )
            }

            editBiddingImmediatePrice.apply {
                val immediatePriceTextWatcher = EditTextAutoCommaWatcher(this)
                immediatePriceTextWatcher.wonTextView = tvImmediatePriceWon
                addTextChangedListener(immediatePriceTextWatcher)
            }
            editBiddingStartPrice.apply {
                val startPriceTextWatcher = EditTextAutoCommaWatcher(this)
                startPriceTextWatcher.wonTextView = tvStartPriceWon
                addTextChangedListener(startPriceTextWatcher)
            }

            ivMerchandiseImg.setOnClickListener {
                resultLauncherActivityInfo.launch(getImageResource())
            }

            btnPostRegistration.setOnClickListener {
                if (tvCategory.text == "" ||
                    editBiddingStartPrice.text.toString() == "" ||
                    editPostTitle.text.toString() == "" ||
                    editBiddingImmediatePrice.text.toString() == "" ||
                    tvBiddingEndingDate.text == "" ||
                    tvBiddingEndingTime.text == "" ||
                    editPostContent.text.toString().length < 10
                ) {
                    val dialog =
                        SellingEssentialRequiredItemDialog(requireContext())
                    dialog.setCanceledOnTouchOutside(true)
                    dialog.show()
                    dialog.window?.setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT
                    )
                } else {
                    val imgList = mutableListOf<String>()

                    for (dataIdx in itemUrlImgList.indices) {
                        imgList.add(itemUrlImgList[dataIdx].imgUrl!!)
                    }

                    //수정 필요
                    merchandiseViewModel.addItemInfo(
                        ItemAddInput(
                            categoryId = 1,
                            sPrice = 2,
                            buyNow = Optional.Present(3000),
                            title = "123",
                            name = "123",
                            dueDate = "2022-07-26T23:58:00.000Z",
                            deliveryType = 1,
                            sCondition = 1,
                            aCondition = 1,
                        ),
                        description = "123123",
                        images = imgList
                    )
                }
            }
        }
    }


    private fun observeImgUrl() {
        itemImgViewModel.itemUrl.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ViewState.Loading -> {
                    Log.d("img upload", "Loading img upload")
                }
                is ViewState.Success -> {
                    Log.d("img upload", "Success img upload")
                    itemUrlImgList.add(response.value!!)
                    itemImgAdapter.submitList(itemUrlImgList.toList())
                    binding.tvImgCount.text = "${itemUrlImgList.size}/10"
                    itemImgAdapter.onItemClicked = {
                        itemUrlImgList.removeAt(it!!)
                        binding.tvImgCount.text = "${itemUrlImgList.size}/10"
                        itemImgAdapter.submitList(itemUrlImgList.toList())
                    }
                }
                else -> {
                    Log.d("img error", "Error img upload")
                    Log.d("why error", response.message.toString())
                }
            }
        }
    }

    private fun observeMerchandise() {
        merchandiseViewModel.addItemStatus.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ViewState.Loading -> {
                    Log.d("Add item", "Loading")
                }
                is ViewState.Success -> {
                    Log.d("Add item", "Success")
                    Toast.makeText(requireContext(), "성공", Toast.LENGTH_LONG).show()
                    activity?.finish()
                }
                else -> {
                    Log.d("Add item", "Error")
                }
            }
        }
    }

    private fun getImageResource(): Intent {
        return Intent().apply {
            action = Intent.ACTION_OPEN_DOCUMENT
            type = "image/*"
        }
    }
}
